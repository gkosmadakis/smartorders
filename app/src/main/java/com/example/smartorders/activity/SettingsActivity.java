package com.example.smartorders.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.MySimpleArrayAdapter;
import com.example.smartorders.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private TextView fullNameView,editAccountLink,signOutLink;
    private String firstName,lastName;
    private FirebaseAuth mAuth;
    private ListView listView;
    private String[] listItem;
    private String homePlace;
    private String workPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();

        /*Views*/
        fullNameView = findViewById(R.id.fullNameView);
        editAccountLink = findViewById(R.id.editAccountLink);
        signOutLink = findViewById(R.id.signOutLink);
        /*Get first name and last name from shared preferences and use it in the fullname view*/
        SharedPreferences prefers = PreferenceManager.getDefaultSharedPreferences(this);
        firstName = prefers.getString("firstName", "");
        lastName = prefers.getString("lastName","");

        fullNameView.setText(firstName + " "+ lastName);

        listView = findViewById(R.id.placesListView);
        listItem = getResources().getStringArray(R.array.array_on_saved_places);

        final MySimpleArrayAdapter adapter = getSavedPlacesAndSetAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String value= adapter.getItem(position);
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
                switch (value) {
                    case "Home":
                        Intent intentHome = new Intent(getApplicationContext(), FindAddressActivity.class);
                        intentHome.putExtra("savedPlace", "home");
                        startActivity(intentHome);
                        break;
                    case "Work":
                        Intent intentWork = new Intent(getApplicationContext(), FindAddressActivity.class);
                        intentWork.putExtra("savedPlace", "work");
                        startActivity(intentWork);
                        break;
                }
            }
        });

        /*Click listener for the edit account link*/
        editAccountLink.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), EditAccountActivity.class);
                startActivity(intent);
            }
        });

        signOutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                Intent i = new Intent(getApplicationContext(), GetStartedActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        });
    }


    private void signOut() {
        mAuth.signOut();
    }

    @Override
    public void onResume(){
        super.onResume();
        final MySimpleArrayAdapter adapter = getSavedPlacesAndSetAdapter();
        adapter.notifyDataSetChanged();
    }

    private MySimpleArrayAdapter getSavedPlacesAndSetAdapter() {
        SharedPreferences sp = getSharedPreferences("home", Context.MODE_PRIVATE);
        homePlace = sp.getString("placeName","");
        SharedPreferences sprefs = getSharedPreferences("work", Context.MODE_PRIVATE);
        workPlace = sprefs.getString("placeName","");
        String [] savedPlacesArray = new String[2];
        if(homePlace != ""){
            savedPlacesArray[0] = homePlace;
        }
        else {
            savedPlacesArray[0] = "Add home";
        }
        if(workPlace != ""){
            savedPlacesArray[1] = workPlace;
        }
        else {
            savedPlacesArray[1] = "Add work";
        }
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, listItem,savedPlacesArray);
        listView.setAdapter(adapter);
        return adapter;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}