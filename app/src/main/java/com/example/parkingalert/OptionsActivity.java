package com.example.parkingalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Actividad para la opción de cambio de distancia del radio de busqueda
 */
public class OptionsActivity extends AppCompatActivity {

    EditText editText;
    Spinner dropdown;
    Button buttonAccept;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        dropdown = findViewById(R.id.spinnerOptions);
        String[] dropdownItems = new String[]{"m", "km"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dropdownItems);
        dropdown.setAdapter(adapterSpinner);

        sharedPreferences = getApplicationContext().getSharedPreferences("options", Context.MODE_PRIVATE);
        String measure = sharedPreferences.getString("measure","m");
        int distance = sharedPreferences.getInt("distance",50);

        editText = findViewById(R.id.editTextOptions);
        editText.setText(String.valueOf(distance));

        buttonAccept = findViewById(R.id.buttonOption);

        int position;
        if(measure.equals("m")){
            position = 0;
        }else{
            position = 1;
        }
        dropdown.setSelection(position);

        //Boton aceptar
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("distance",Integer.parseInt(editText.getText().toString()));
                editor.apply();
                editor.putString("measure", dropdown.getSelectedItem().toString());
                editor.apply();
                finish();
            }
        });

    }
}