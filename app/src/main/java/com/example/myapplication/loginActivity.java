package com.example.myapplication;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginActivity extends AppCompatActivity {
    Button btn_login;
    EditText email, password;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email = findViewById(R.id.correo);
        password = findViewById(R.id.pass);
        btn_login = findViewById(R.id.ingresar);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUsuario = email.getText().toString().trim();
                String passUsuario = password.getText().toString().trim();

                if (emailUsuario.isEmpty() && passUsuario.isEmpty()) {
                    Toast.makeText(loginActivity.this, "Los campos deben estar llenos", Toast.LENGTH_SHORT).show();
                } else {
                    loginUsuario(emailUsuario, passUsuario);
                }
            }

            private void loginUsuario(String emailUser, String passUser) {
                mAuth.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkFechaTrabajo();
                        } else {
                            Toast.makeText(loginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void checkFechaTrabajo() {
                String usuarioID = mAuth.getCurrentUser().getUid();
                DocumentReference empleadoRef = db.collection("Empleados").document(usuarioID);

                empleadoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.contains("fecha_trabajo")) {
                                startActivity(new Intent(loginActivity.this, mainActivity.class));
                                Toast.makeText(loginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                startActivity(new Intent(loginActivity.this, fechaActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(loginActivity.this, "Error al consultar la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
