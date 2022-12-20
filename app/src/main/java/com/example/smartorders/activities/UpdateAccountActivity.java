package com.example.smartorders.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.smartorders.R;
import com.example.smartorders.service.UserService;
import com.example.smartorders.service.UserServiceImpl;

public class UpdateAccountActivity extends AppCompatActivity {

    private EditText phoneNumberField;
    private EditText passwordField;
    private final int requestCodePhone = 3;
    private final int requestCodeEmail = 4;
    private final int requestCodePassword = 5;
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String intentLayout = intent.getStringExtra("intentLayout");
        SharedPreferences prefers = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);
        switch(intentLayout){
            case "firstNameIntent":
            setContentView(R.layout.edit_firstname);
                EditText firstNameField = findViewById(R.id.firstNameText);
                getFieldValueAndUpdateInDB(prefers, firstNameField, "firstName", "first name","firstNameUpdated");
            break;
            case "surNameIntent":
            setContentView(R.layout.edit_lastname);
                EditText lastNameField = findViewById(R.id.lastNameText);
                getFieldValueAndUpdateInDB(prefers, lastNameField, "lastName", "last name","lastNameUpdated");
            break;
            case "phoneNumberIntent":
            setContentView(R.layout.edit_password);
                passwordField = findViewById(R.id.passwordText);
                getFieldValueAndUpdateInDB(prefers,passwordField, "phoneNumber", "phone","phoneUpdated");
                break;
            case "emailIntent":
            setContentView(R.layout.edit_password);
                passwordField = findViewById(R.id.passwordText);
                getFieldValueAndUpdateInDB(prefers, passwordField, "email", "email","emailUpdated");
            break;
            case "passwordIntent":
            setContentView(R.layout.edit_password);
                passwordField = findViewById(R.id.passwordText);
                getFieldValueAndUpdateInDB(prefers,passwordField, "password", "password","passwordUpdated");
            break;
            default:
            setContentView(R.layout.activity_update_account);
            break;
        }
    }

    private void getFieldValueAndUpdateInDB(SharedPreferences prefers, final EditText field, final String prefersKey, final String childKeyInDB, final String updateTag) {
        String prefersValue = prefers.getString(prefersKey, "");
        if(prefersKey.equals("password") || prefersKey.equals("email") || prefersKey.equals("phoneNumber")){
            field.setText("");
        }
        else {
            field.setText(prefersValue);
        }
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            if(field.getText().toString().length() > 0){
                updateFieldInDB(childKeyInDB,field.getText().toString(),prefersKey,updateTag);
            }
        });
    }

    private void updateFieldInDB(String childKeyInDB, String childValueInDB, String sharedPrefsKey, String updateTag) {
        /*Update more children in DB if phone or email are to be updated*/
        if (childKeyInDB.equals("phone")){
            Intent intent = new Intent(getApplicationContext(), VerifyPasswordToUpdatePhoneActivity.class);
            intent.putExtra("passwordEnteredByUser", childValueInDB);
            startActivityForResult(intent,requestCodePhone);
        }
        else if (childKeyInDB.equals("email")){
            Intent intent = new Intent(getApplicationContext(), UpdateEmailActivity.class);
            intent.putExtra("passwordEnteredByUser", childValueInDB);
            startActivityForResult(intent,requestCodeEmail);
        }
        else if (childKeyInDB.equals("password")){
            /*childValueInDB is the password that the user entered in the Verify Password screen, the current password*/
            Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
            intent.putExtra("passwordEnteredByUser", childValueInDB);
            startActivityForResult(intent,requestCodePassword);
        }
        else {
            userService.updateUserFirstOrLastName(UpdateAccountActivity.this, childKeyInDB, childValueInDB, sharedPrefsKey, updateTag);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == requestCodePhone) {
            if(data.hasExtra("phoneUpdated")) {
                System.out.println("UpdateAccountActivity "+data.getStringExtra("verifiedPhone"));
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodePassword){
            if(data.hasExtra("passwordUpdated")){
                /*Updated shared prefs with the new password */
                SharedPreferences sp = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("password", data.getExtras().getString("passwordUpdated"));
                editor.apply();
                Intent dataIntent = new Intent();
                dataIntent.putExtra("passwordUpdated", passwordField.getText().toString());
                // Activity finished ok, return the data
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodeEmail){
            if(data.hasExtra("emailUpdated")){
                /*Updated shared prefs with the new password */
                SharedPreferences sp = this.getSharedPreferences("user_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("email", data.getExtras().getString("emailUpdated"));
                editor.apply();
                Intent dataIntent = new Intent();
                dataIntent.putExtra("emailUpdated", data.getStringExtra("emailUpdated"));
                // Activity finished ok, return the data
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
