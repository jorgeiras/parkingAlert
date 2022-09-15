package com.example.parkingalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Actividad usada como pantalla de inicio para logearse o regsitrarse
 */
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

        //Boton de login
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

        //Boton de registrar un nuevo usuario
        ButtonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }


    /**
     *  Función login para acceder como usuario a la aplicación
     */
    public void login(){
        firebase.signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(!firebase.getCurrentUser().isEmailVerified()){
                        Toast.makeText(LoginActivity.this,"Debe verificar su email",Toast.LENGTH_LONG).show();
                    }else{
                        startActivity(new Intent(LoginActivity.this, AllParkingsActivity.class));
                        finish();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this,"Email o contraseña invalidos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}