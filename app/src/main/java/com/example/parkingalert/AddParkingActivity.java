package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


/**
 * Actividad encargada de llevar el proceso de anyadir una plaza nueva
 */
public class AddParkingActivity extends AppCompatActivity {

    private Button buttonAccept;
    private Button buttonConfirm;
    private TextView textViewRadioGroup;
    private RadioGroup radioGroup;
    private double latitude;
    private double longitude;
    private String encodedbitmap;
    FirebaseAuth firebase;
    FirebaseFirestore db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MapsFragment.class, null)
                    .commit();
        }


        setContentView(R.layout.activity_add_parking);

        buttonAccept = (Button) findViewById(R.id.ButtonAccept);
        buttonConfirm = (Button) findViewById(R.id.ButtonConfirm);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        textViewRadioGroup = (TextView) findViewById(R.id.textViewRadioGroup);

        firebase = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        //Boton de aceptar
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkRadioGroup()){
                    Toast.makeText(AddParkingActivity.this, "seleccione si es de pago o no", Toast.LENGTH_LONG).show();
                    return;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,CameraFragment.class,null).commit();
                longitude = MapsFragment.getLong();
                latitude = MapsFragment.getLat();
                buttonAccept.setVisibility(View.GONE);
                textViewRadioGroup.setVisibility(View.GONE);
                radioGroup.setVisibility(View.GONE);
                buttonConfirm.setVisibility(View.VISIBLE);
            }
        });

        //Boton de confirmar
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encodedbitmap = CameraFragment.getBitmapPhoto();
                if(encodedbitmap != null){
                    String User = firebase.getUid();
                    Map<String,Object> parkingData = new HashMap<>();
                    parkingData.put("UserID", User);
                    parkingData.put("longitude", longitude);
                    parkingData.put("latitude", latitude);
                    parkingData.put("encodedBitmapPhoto", encodedbitmap);
                    parkingData.put("timeStamp", System.currentTimeMillis());
                    parkingData.put("paymentArea", getSelectedRadioButton());

                    db.collection("Parkings").add(parkingData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            encodedbitmap = null;
                            Toast.makeText(AddParkingActivity.this, "plaza publicada con exito", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddParkingActivity.this, AllParkingsActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddParkingActivity.this, "Error al intentar conectar con la base de datos", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else{
                    Toast.makeText(AddParkingActivity.this,"haga una foto de la plaza",Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    /**
     * Funcion para comprobar si se ha seleccionado alguna de las opciones del RadioGroup
     * @return true en caso de haberse seleccionado alguna opcion y false en caso contrario
     */
    private boolean checkRadioGroup() {
        if(radioGroup.getCheckedRadioButtonId()==-1){
            return false;
        }
        return true;
    }


    /**
     * Funcion que devuelve la opcion del RadioGroup seleccionada
     * @return true si la opcion seleccionada es si es de pago y false en caso contrario
     */
    private boolean getSelectedRadioButton(){
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);
        if (idx == 0){
            return true;
        }
        else{
            return false;
        }
    }


}