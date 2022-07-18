package com.example.parkingalert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        Spinner dropdown = findViewById(R.id.spinnerOptions);
        String[] dropdownItems = new String[]{"m", "km"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dropdownItems);
        dropdown.setAdapter(adapterSpinner);
    }
}