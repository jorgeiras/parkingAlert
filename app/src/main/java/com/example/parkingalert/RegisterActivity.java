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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText EditTextname;
    private EditText EditTextemail;
    private EditText EditTextpassword;
    private Button Buttonregistrarse;

    private String name;
    private String email;
    private String password;

    FirebaseAuth firebase;
    //DatabaseReference db;
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
        //db = FirebaseDatabase.getInstance().getReference();
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

//TODO integrar la base de datos real time
    //TODO controlar los fallos por usuario ya creado devolviendo un mensaje de error

    public void register(){
        firebase.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String User = firebase.getUid();
                    Map<String,Object> UserDataRegister = new HashMap<>();
                    //UserDataRegister.put("UserID", User);
                    UserDataRegister.put("name", name);
                    UserDataRegister.put("email", name);

                    db.collection("users").add(UserDataRegister).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            startActivity(new Intent(RegisterActivity.this, AllParkingsActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error al intentar conectar con la base de datos", Toast.LENGTH_LONG).show();
                        }
                    });
                    /*
                    db.child("Users").child(User).setValue(UserDataRegister).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                startActivity(new Intent(RegisterActivity.this, AllParkingsActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Error al intentar conectar con la base de datos", Toast.LENGTH_LONG).show();
                            }
                        }
                    });*/

                }
                else{
                    Toast.makeText(RegisterActivity.this,"El usuario ya existe", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}