package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Map;

public class ParkingItemActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewStreet;
    private TextView textViewCity;
    private TextView textViewUser;
    private TextView textViewTimeStamp;
    private Button buttonServido;
    private Button buttonNoServido;
    private double latitude;
    private double longitude;
    FirebaseFirestore db;

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
        buttonServido = findViewById(R.id.ButtonServido);
        buttonNoServido = findViewById(R.id.ButtonNoServido);

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




    }


    private Bitmap decodeImage(String s){
        byte[] decodeStringBytes = android.util.Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeStringBytes,0,decodeStringBytes.length);
    }
}