package com.example.smartorders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartorders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private String passwordExisting;
    private Button saveBtn;
    private EditText passwordNewField;
    private FirebaseAuth mAuth;
    private static final String TAG = "UpdatePasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        mAuth = FirebaseAuth.getInstance();

        saveBtn = findViewById(R.id.saveButton);
        passwordNewField = findViewById(R.id.passwordTextNew);
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordNewField.getText().toString().length() > 0) {
                    String newPassword = passwordNewField.getText().toString();
                    updateUserPassword(newPassword);
                }
            }
        });
    }

    private void updateUserPassword(final String passwordNew) {
        final FirebaseUser user = mAuth.getCurrentUser();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sp.getString("email","");

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, passwordExisting);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                        Intent data = new Intent();
                                        data.putExtra("passwordUpdated", passwordNew);
                                        // Activity finished ok, return the data
                                        setResult(RESULT_OK, data);
                                        finish();
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                        }
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
