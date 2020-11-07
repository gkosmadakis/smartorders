package com.example.smartorders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.MySimpleArrayAdapter;
import com.example.smartorders.PaymentInfoAdapter;
import com.example.smartorders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaymentInfoActivity extends AppCompatActivity {
    private ListView listView;
    private FirebaseAuth mAuth;
    private final static String TAG = "PaymentInfoActivity ";
    LinkedHashMap<String, String> last4DigitsToBrandMap = new LinkedHashMap<>();
    private  ProgressDialog mProgressDialog;
    private TextView addPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);

        addPaymentMethod = findViewById(R.id.addPaymentMethod);
        mAuth = FirebaseAuth.getInstance();
        findSourcesOnFirestore();

        addPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent paymentIntent = new Intent(getApplicationContext(), AddPaymentMethodActivity.class);
                startActivity(paymentIntent);
            }
        });

    }

    public void findSourcesOnFirestore(){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
        FirebaseFirestore.getInstance().collection("stripe_customers").
                document(mAuth.getUid()).collection("sources")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully retrieved sources", Toast.LENGTH_LONG).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                getLast4DigitsAndBrand(document.getData());
                            }
                            /*List view with items*/
                            listView = findViewById(R.id.listView);
                            final PaymentInfoAdapter adapter = new PaymentInfoAdapter(getApplicationContext(), last4DigitsToBrandMap);
                            listView.setAdapter(adapter);
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        for(Map.Entry<String,String> cardDetails : last4DigitsToBrandMap.entrySet()) {
                            Log.i(TAG,cardDetails.getKey()+" "+cardDetails.getValue());
                        }
                    }

        });

    }

    private void getLast4DigitsAndBrand(Map<String, Object> data) {

        String brand = "";
        String last4 = "";
        for(Map.Entry<String,Object> cardDetails : data.entrySet()) {
            if(cardDetails.getKey().equals("brand")){
               brand = (String) cardDetails.getValue();
            }
            if(cardDetails.getKey().equals("last4")){
               last4 = (String) cardDetails.getValue();
            }
        }
        last4DigitsToBrandMap.put(last4,brand);
    }
}
