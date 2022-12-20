package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.UserService;
import com.example.smartorders.service.UserServiceImpl;

public class VerifyPasswordToUpdatePhoneActivity extends AppCompatActivity {

    private final int requestCodePhone = 3;
    private String passwordExisting;
    private EditText phoneNewField;
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_password_to_update_phone);
        Button saveBtn = findViewById(R.id.saveButton);
        phoneNewField = findViewById(R.id.phoneTextNew);
        saveBtn.setOnClickListener(view -> {
            if(phoneNewField.getText().toString().length() > 0) {
                String phoneNew = phoneNewField.getText().toString();
                Intent intent = new Intent(getApplicationContext(), UpdatePhoneActivity.class);
                intent.putExtra("phoneNumber", phoneNew);
                intent.putExtra("passwordEnteredByUser", passwordExisting);
                startActivityForResult(intent, requestCodePhone);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 3) {
            if (data.hasExtra("verifiedPhone")) {
                System.out.println("VerifyPasswordToUpdatePhoneActivity "+data.getStringExtra("verifiedPhone"));
                userService.updateUserPhoneInFirebase(VerifyPasswordToUpdatePhoneActivity.this, data);
            }
        }
    }
}
