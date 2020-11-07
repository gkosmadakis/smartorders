package com.example.smartorders.activity.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.MySimpleArrayAdapter;
import com.example.smartorders.R;
import com.example.smartorders.activity.HomeActivity;
import com.example.smartorders.activity.PaymentInfoActivity;
import com.example.smartorders.activity.SettingsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

public class AccountFragment extends Fragment {

    private AccountViewModel notificationsViewModel;
    private ListView listView;
    private TextView textView;
    private String[] listItem;
    private String firstName,lastName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        /* Set the account name in the title of the action bar. Set the font to black, background to grey_100*/
        /*Get first name and last name from shared preferences and use it in the fullname view*/
        ((HomeActivity)getActivity()).getSupportActionBar().show();
        SharedPreferences prefers = PreferenceManager.getDefaultSharedPreferences(getContext());
        firstName = prefers.getString("firstName", "");
        lastName = prefers.getString("lastName","");

        ((HomeActivity)getActivity()).setActionBarTitle(firstName +" "+lastName);

        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();

        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey_100)));

        /*List view with items*/
        listView= root.findViewById(R.id.listView);
        textView= root.findViewById(R.id.textView);
        listItem = getResources().getStringArray(R.array.array_on_account);
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getContext(), listItem,null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String value= adapter.getItem(position);
                Toast.makeText(getContext(),value,Toast.LENGTH_SHORT).show();
                switch (value) {
                    case "Your favourites":

                        break;
                    case "Payment":
                        Intent paymentIntent = new Intent(getContext(), PaymentInfoActivity.class);
                        startActivity(paymentIntent);
                        break;
                    case "Help":

                        break;
                    case "Promotions":

                        break;
                    case "Settings":
                        Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                }
            }
        });
        return root;
    }
}
