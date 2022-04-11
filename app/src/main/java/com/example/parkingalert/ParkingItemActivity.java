package com.example.parkingalert;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class ParkingItemActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewStreet;
    private TextView textViewCity;
    private TextView textViewUser;
    private TextView textViewTimeStamp;
    private Button buttonPayParking;
    private Button buttonParkingBusy;
    private double latitude;
    private double longitude;
    FirebaseFirestore db;
    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getData() != null){
                finish();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_item);

        db = FirebaseFirestore.getInstance();
        Intent i = getIntent();
        latitude = i.getDoubleExtra("latitude",0);
        longitude = i.getDoubleExtra("longitude",0);

        if(savedInstanceState==null){
            Fragment mapFrag = new MapsItemFragment();
            Bundle arguments= new Bundle();
            arguments.putDouble("latitude",latitude);
            arguments.putDouble("longitude",longitude);
            mapFrag.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, mapFrag, null)
                    .commit();
        }

        imageView = findViewById(R.id.itemImage);
        textViewStreet = findViewById(R.id.itemInfoStreet);
        textViewCity = findViewById(R.id.itemInfoCity);
        textViewUser = findViewById(R.id.itemInfoUser);
        textViewTimeStamp = findViewById(R.id.itemTimeStamp);
        buttonPayParking = findViewById(R.id.ButtonPayParking);
        buttonParkingBusy = findViewById(R.id.ButtonParkingBusy);

        imageView.setImageBitmap(decodeImage(i.getStringExtra("encodedBitMap")));
        textViewStreet.setText(i.getStringExtra("streetAdress"));
        textViewCity.setText(i.getStringExtra("cityName"));
        long l = i.getLongExtra("timeStamp",0);
        Date d = new Date(l * 1000);
        textViewTimeStamp.setText(d.toString());

        db.collection("users").document(i.getStringExtra("UserID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    UserInfo us = task.getResult().toObject(UserInfo.class);
                    textViewUser.setText(us.getName());
                }
            }
        });


        buttonPayParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stripeIntent = new Intent(ParkingItemActivity.this, StripeCheckOutActivity.class);
                stripeIntent.putExtra("UserID",i.getStringExtra("UserID"));
                stripeIntent.putExtra("docID",i.getStringExtra("docID"));
                mGetContent.launch(stripeIntent);
            }
        });


        buttonParkingBusy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Parkings").document(i.getStringExtra("docID")).delete();
                Toast.makeText(ParkingItemActivity.this,"plaza borrada",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }


    private Bitmap decodeImage(String s){
        byte[] decodeStringBytes = android.util.Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeStringBytes,0,decodeStringBytes.length);
    }
}