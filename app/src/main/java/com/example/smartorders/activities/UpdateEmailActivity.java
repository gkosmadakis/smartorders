package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.UserService;
import com.example.smartorders.service.UserServiceImpl;

public class UpdateEmailActivity extends AppCompatActivity {

    private String passwordExisting;
    private EditText emailNewField;
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        Button saveBtn = findViewById(R.id.saveButton);
        emailNewField = findViewById(R.id.emailTextNew);
        saveBtn.setOnClickListener(view -> {
            if(emailNewField.getText().toString().length() > 0) {
                String emailNew = emailNewField.getText().toString();
                userService.updateUserEmailInFirebase(UpdateEmailActivity.this, emailNew, passwordExisting);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Get the password entered on the previous activity UpdateAccountActivity. This is the existing password*/
        Intent intent = getIntent();
        passwordExisting = intent.getStringExtra("passwordEnteredByUser");
    }
}
