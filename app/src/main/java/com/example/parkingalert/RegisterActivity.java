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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * Actividad usada como pantalla de registro de un nuevo usuario
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText EditTextname;
    private EditText EditTextemail;
    private EditText EditTextpassword;
    private Button Buttonregistrarse;

    private String name;
    private String email;
    private String password;

    FirebaseAuth firebase;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditTextname = (EditText) findViewById(R.id.editTextName);
        EditTextemail = (EditText) findViewById(R.id.editTextEmail);
        EditTextpassword = (EditText) findViewById(R.id.editTextPassword);
        Buttonregistrarse = (Button) findViewById(R.id.ButtonRegistrarse);


        firebase = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Buttonregistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = EditTextname.getText().toString();
                email = EditTextemail.getText().toString();
                password = EditTextpassword.getText().toString();


                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){

                    if(password.length()>=6){
                        register();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Complete todos los campos",Toast.LENGTH_LONG).show();
                }

            }
        });

    }



    /**
     * función para registrar un nuevo usuario
     */
    public void register(){
        firebase.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String User = firebase.getUid();
                    Map<String,Object> UserDataRegister = new HashMap<>();
                    UserDataRegister.put("name", name);
                    UserDataRegister.put("email", email);
                    UserDataRegister.put("score", 0);

                    db.collection("users").document(User).set(UserDataRegister).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            firebase.getCurrentUser().sendEmailVerification();
                            Toast.makeText(RegisterActivity.this, "Debe verificar su email con el correo que acabamos de enviarle", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error al intentar conectar con la base de datos", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else{
                    Toast.makeText(RegisterActivity.this,"El usuario ya existe", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}