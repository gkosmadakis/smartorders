package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.UserService;
import com.example.smartorders.service.UserServiceImpl;

public class UpdatePasswordActivity extends AppCompatActivity {

    private String passwordExisting;
    private EditText passwordNewField;
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        Button saveBtn = findViewById(R.id.saveButton);
        passwordNewField = findViewById(R.id.passwordTextNew);
        saveBtn.setOnClickListener(view -> {
            if(passwordNewField.getText().toString().length() > 0) {
                String passwordNew = passwordNewField.getText().toString();
                userService.updateUserPassword(UpdatePasswordActivity.this, passwordExisting, passwordNew);
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
