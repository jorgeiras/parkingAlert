package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class StripeCheckOutActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private PaymentSheet.CustomerConfiguration customerConfig;
    private String PUBLIC_KEY = "pk_test_51Kkx1CBCrHClQlFN1tLA5bl7RHLJ49Hv27AvEdYIk5XaGIxPfvjMIVQaVJUeERAsTc7ziJbMUmvZWTtatA96X7gs00zL0ACOXx";
    private String SERCRET_KEY = "sk_test_51Kkx1CBCrHClQlFNgiXisJF2D89Vx7Kla7aj3lZBfkzOMoJy1y9ri0BpBINbu2qU859nnkjWJE8kjoFtLfJUH6ct00aekAw5v8";
    private String customerID;
    private String ephemeralKey;
    private String clientSecret;
    private double ticketPrice = 0.05;
    private Button buttonPay;
    private NumberPicker numberPickerMin;
    private NumberPicker numberPickerHours;
    private TextView textViewTicketPrice;
    FirebaseFirestore db;
    Intent i;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_check_out);

        db = FirebaseFirestore.getInstance();

        PaymentConfiguration.init(this,PUBLIC_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            paymentResult(paymentSheetResult);
        });


        numberPickerMin = findViewById(R.id.numberPickerMinutes);
        numberPickerHours = findViewById(R.id.numberPickerHours);
        buttonPay = findViewById(R.id.buttonPay);
        textViewTicketPrice = findViewById(R.id.ticketPrice);

        i = getIntent();
        db.collection("users").document(i.getStringExtra("UserID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    UserInfo us = task.getResult().toObject(UserInfo.class);
                    score = us.getScore();
                }
            }
        });


        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ticketPrice < 0.50){
                    Toast.makeText(StripeCheckOutActivity.this,"precio minimo 0.50 €",Toast.LENGTH_LONG).show();
                }
                else{
                    getCustomerData();
                }

            }
        });

        numberPickerMin.setMinValue(1);
        numberPickerMin.setMaxValue(59);

        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(4);

        numberPickerHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                ticketPrice = 0.05 * numberPickerMin.getValue() + 0.05 * numberPickerHours.getValue() * 60;
                textViewTicketPrice.setText("Precio: " + new DecimalFormat("#.##").format(ticketPrice) + "€");
            }
        });

        numberPickerMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                ticketPrice = 0.05 * numberPickerMin.getValue() + 0.05 * numberPickerHours.getValue() * 60;
                textViewTicketPrice.setText("Precio: " + new DecimalFormat("#.##").format(ticketPrice) + "€");

            }
        });



    }

    private void getCustomerData(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    customerID = json.getString("id");
                    getEphemeralKey(customerID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SERCRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(StripeCheckOutActivity.this);
        requestQueue.add(stringRequest);
    }




    private void getEphemeralKey(String customer) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    ephemeralKey = json.getString("id");
                    getClient(ephemeralKey, customer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SERCRET_KEY);
                header.put("Stripe-Version","2020-08-27");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer",customer);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(StripeCheckOutActivity.this);
        requestQueue.add(stringRequest);
    }





    private void getClient(String ephemeralKey, String customer) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    clientSecret = json.getString("client_secret");
                    execPayment();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SERCRET_KEY);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer",customer);
                DecimalFormat dec = new DecimalFormat("00.00");
                String amount = dec.format(ticketPrice).toString();
                String amountWithoutDot = amount.replace(".","");
                amountWithoutDot = amountWithoutDot.replace(",","");
                params.put("amount", amountWithoutDot);
                params.put("currency","eur");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(StripeCheckOutActivity.this);
        requestQueue.add(stringRequest);
    }

    private void paymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this,"pago completado", Toast.LENGTH_LONG).show();
            updateUserScore();
        }
    }

    private void execPayment(){
        paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration("ParkingAlert",new PaymentSheet.CustomerConfiguration(customerID,ephemeralKey)));
    }


    private void updateUserScore(){
       db.collection("users").document(i.getStringExtra("UserID")).update("score",score + 1);
       db.collection("Parkings").document(i.getStringExtra("docID")).delete();
       Intent intent = new Intent();
       setResult(RESULT_OK, intent);
       finish();
    }


}