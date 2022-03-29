package com.example.parkingalert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ParkingItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_item);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MapsItemFragment.class, null)
                    .commit();
        }
    }
}