package com.example.smartorders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartorders.EnterPasswordOrCodeFragment;
import com.example.smartorders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateAccountActivity extends AppCompatActivity {

    private EditText firstNameField,lastNameField,phoneNumberField,emailField,passwordField;
    private Button saveButton;
    private DatabaseReference urlReferenceToId;
    private FirebaseAuth mAuth;
    private final int requestCodePhone = 3;
    private final int requestCodePassword = 5;
    private static final String TAG = "UpdateAccountActivity";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String intentLayout = intent.getStringExtra("intentLayout");
        SharedPreferences prefers = PreferenceManager.getDefaultSharedPreferences(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        switch(intentLayout){

            case "firstNameIntent":
            setContentView(R.layout.edit_firstname);
                firstNameField = findViewById(R.id.firstNameText);
                getFieldValueAndUpdateInDB(prefers,firstNameField, "firstName", "first name","firstNameUpdated");
            break;
            case "surNameIntent":
            setContentView(R.layout.edit_lastname);
                lastNameField = findViewById(R.id.lastNameText);
                getFieldValueAndUpdateInDB(prefers,lastNameField, "lastName", "last name","lastNameUpdated");
            break;
            case "phoneNumberIntent":
            setContentView(R.layout.edit_phonenumber);
                phoneNumberField = findViewById(R.id.phoneNumberText);
                getFieldValueAndUpdateInDB(prefers,phoneNumberField, "phoneNumber", "phone","phoneUpdated");
                break;
            case "emailIntent":
            setContentView(R.layout.edit_email);
                emailField = findViewById(R.id.emailText);
                getFieldValueAndUpdateInDB(prefers,emailField, "email", "email","emailUpdated");
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
        if(prefersKey.equals("password")){
            field.setText("");
        }
        else {
            field.setText(prefersValue);
        }

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(field.getText().toString().length() > 0){
                    updateFieldInDB(childKeyInDB,field.getText().toString(),prefersKey,updateTag);

                }
            }
        });
    }

    private void updateFieldInDB(String childKeyInDB, String childValueInDB, String sharedPrefsKey,String updateTag) {

        /*Update more children in DB if phone or email are to be updated*/
        if (childKeyInDB.equals("phone")){
            Intent intent = new Intent(getApplicationContext(), UpdatePhoneActivity.class);
            intent.putExtra("phoneNumber", childValueInDB);
            startActivityForResult(intent,requestCodePhone);
        }
        else if (childKeyInDB.equals("email")){

            updateUserEmail(childValueInDB);
        }
        else if (childKeyInDB.equals("password")){
            /*childValueInDB is the password that the user entered, the current password*/
            Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
            intent.putExtra("passwordEnteredByUser", childValueInDB);
            startActivityForResult(intent,requestCodePassword);
        }
        else {
            urlReferenceToId = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            urlReferenceToId.child(childKeyInDB).setValue(childValueInDB);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(sharedPrefsKey, childValueInDB);
            editor.apply();
            Intent data = new Intent();
            data.putExtra(updateTag, childValueInDB);
            // Activity finished ok, return the data
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void updateUserEmail(final String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        // Get auth credentials from the user for re-authentication
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        AuthCredential credential = EmailAuthProvider
                .getCredential(sp.getString("email",""), sp.getString("password","")); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User email address updated.");
                                            Toast.makeText(getApplicationContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                                            /*Update real time database*/
                                            String user_id = mAuth.getCurrentUser().getUid();
                                            DatabaseReference current_user_db = mDatabase.child(user_id);
                                            current_user_db.child("email").setValue(newEmail);
                                            current_user_db.child(sp.getString("phoneNumber","")).setValue(newEmail);
                                            /*Update Preferences */
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("email",newEmail);
                                            editor.apply();
                                            Intent data = new Intent();
                                            data.putExtra("emailUpdated", newEmail);
                                            // Activity finished ok, return the data
                                            setResult(RESULT_OK, data);
                                            finish();
                                        }
                                    }
                                });
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == requestCodePhone) {
            if(data.hasExtra("verifiedPhone")) {
                Log.d(TAG, data.getExtras().getString("verifiedPhone"));
                /*Create a new ID in the Database and add the new phone and the current data email,first/last name*/
                String user_id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user_db = mDatabase.child(user_id);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                current_user_db.child("email").setValue(sp.getString("email", ""));
                current_user_db.child("first name").setValue(sp.getString("firstName", ""));
                current_user_db.child("last name").setValue(sp.getString("lastName", ""));
                current_user_db.child(data.getExtras().getString("verifiedPhone")).setValue(sp.getString("email", ""));
                current_user_db.child("phone").setValue(data.getExtras().getString("verifiedPhone"));
                /*Update in Prefs. Store in preferences the new phone*/
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("phoneNumber", data.getExtras().getString("verifiedPhone"));
                editor.apply();
                Intent dataIntent = new Intent();
                dataIntent.putExtra("phoneUpdated", phoneNumberField.getText().toString());
                // Activity finished ok, return the data
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else if (resultCode == RESULT_OK && requestCode == requestCodePassword){
            if(data.hasExtra("passwordUpdated")){
                /*Updated shared prefs with the new password */
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
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
    }
}
