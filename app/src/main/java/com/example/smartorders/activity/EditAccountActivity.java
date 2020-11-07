package com.example.smartorders.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartorders.R;
import com.google.android.material.snackbar.Snackbar;

public class EditAccountActivity extends AppCompatActivity {

    private EditText firstNameField,lastNameField,phoneNumberField,emailField,passwordField;
    private String firstName, lastName,phoneNumber,email,password;
    private String firstNameIntent,lastNameIntent,phoneNumberIntent,emailIntent,passwordIntent;
    private final int requestCodeFirstName = 1;
    private final int requestCodeLastName = 2;
    private final int requestCodePhone = 3;
    private final int requestCodeEmail = 4;
    private final int requestCodePassword = 5;
    private static final String TAG = "EditAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        /*Fields*/
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.surnameField);
        phoneNumberField = findViewById(R.id.phoneNumField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        /*Get first name and last name from shared preferences and use it in the fullname view*/
        SharedPreferences prefers = PreferenceManager.getDefaultSharedPreferences(this);
        firstName = prefers.getString("firstName", "");
        lastName = prefers.getString("lastName","");
        phoneNumber = prefers.getString("phoneNumber","");
        email = prefers.getString("email","");
        password = prefers.getString("password","");
        /*Set the values from preferences to the fields */
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        phoneNumberField.setText(phoneNumber);
        emailField.setText(email);
        passwordField.setText(password);

        firstNameField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                firstNameIntent = "firstNameIntent";
                Intent intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
                intent.putExtra("intentLayout", firstNameIntent);
                startActivityForResult(intent, requestCodeFirstName);

            }

        });
        lastNameField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                lastNameIntent = "surNameIntent";
                Intent intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
                intent.putExtra("intentLayout", lastNameIntent);
                startActivityForResult(intent,requestCodeLastName);

            }

        });
        phoneNumberField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                phoneNumberIntent = "phoneNumberIntent";
                Intent intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
                intent.putExtra("intentLayout", phoneNumberIntent);
                startActivityForResult(intent,requestCodePhone);

            }

        });
        emailField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                emailIntent = "emailIntent";
                Intent intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
                intent.putExtra("intentLayout", emailIntent);
                startActivityForResult(intent,requestCodeEmail);

            }

        });
        passwordField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                passwordIntent = "passwordIntent";
                Intent intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
                intent.putExtra("intentLayout", passwordIntent);
                startActivityForResult(intent,requestCodePassword);

            }

        });
    }// end of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == requestCodeFirstName) {
            updateFieldAndShowSnackBar(data, "firstNameUpdated", firstNameField, "Name Updated");
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodeLastName){
            updateFieldAndShowSnackBar(data, "lastNameUpdated", lastNameField, "SurName Updated");
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodePhone){
            updateFieldAndShowSnackBar(data, "phoneUpdated", phoneNumberField, "Phone Updated");
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodeEmail){
            updateFieldAndShowSnackBar(data, "emailUpdated", emailField, "Email Updated");
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodePassword){
            updateFieldAndShowSnackBar(data, "passwordUpdated", passwordField, "Password Updated");
        }
    }

    private void updateFieldAndShowSnackBar(Intent data, String dataReceived, EditText field, String s) {
        if (data.hasExtra(dataReceived)) {
            Log.d(TAG, "Data Received: "+data.getExtras().getString(dataReceived));
            field.setText(data.getExtras().getString(dataReceived));
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.editAccountlayout), s, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(getResources().getColor(R.color.green_success));
            snackbar.show();
        }
    }
}
