package com.example.smartorders.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartorders.R;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class GetStartedActivity extends Activity {

    private Button skipBtn;
    private Button continueBtn;
    private ImageView backgroundImage;
    private TextView logo;
    private TextView getStartedText;
    private View bottomBackgroundView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        skipBtn = findViewById(R.id.skipBtn);
        continueBtn = findViewById(R.id.continueBtn);
        backgroundImage = findViewById(R.id.backgroundImageView);
        logo = findViewById(R.id.logoTextView);
        bottomBackgroundView = findViewById(R.id.bottomBackgroundview);
        SharedPreferences sp = getSharedPreferences("user_logged_in", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Here we check if the user has logged in at least once to the app and is a registered firebase user
                * if it is then we proceed to HomeActivity */
                if(sp.getString("is_user_logged_in","").equals("true")
                    && sp.getString("firebase_user_id","").equals(mAuth.getUid())){
                    Intent intent = new Intent(view.getContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                else {
                    Intent intent = new Intent(view.getContext(), EnterPhoneActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        });
    }
}
