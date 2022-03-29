package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private EditText EditTextemail;
    private EditText EditTextpassword;
    private Button ButtonLogin;
    private Button ButtonRegistrarse;

    private String email;
    private String password;

    FirebaseAuth firebase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_ParkingAlert);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        EditTextemail = (EditText) findViewById(R.id.editTextEmail);
        EditTextpassword = (EditText) findViewById(R.id.editTextPassword);
        ButtonLogin = (Button) findViewById(R.id.ButtonLogin);
        ButtonRegistrarse = (Button) findViewById(R.id.ButtonRegisterActivity);


        firebase = FirebaseAuth.getInstance();

        ButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = EditTextemail.getText().toString();
                password = EditTextpassword.getText().toString();


                if(!email.isEmpty() && !password.isEmpty()){
                        login();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Complete todos los campos",Toast.LENGTH_LONG).show();
                }

            }
        });
        Log.i("mensaje", "registrarse pulsado");

        /*TODO ver si hace falta finish o no de la activity*/
        ButtonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.i("mensaje", "registrarse pulsado1");
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

/*TODO PONER LOGO Y TEXTO DE INICIAR SESION*/
/*TODO VER SI ES NECESARIO PONER EN UNA VARIABLE EL USUARIO LOGEADO*/
    public void login(){
        firebase.signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, AllParkingsActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Email o contraseña invalidos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}