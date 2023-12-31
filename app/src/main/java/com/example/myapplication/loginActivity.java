package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginActivity extends AppCompatActivity {
    Button btn_login;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextInputLayout email ,password;

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
                String emailUsuario = email.getEditText().getText().toString().trim();
                String passUsuario = password.getEditText().getText().toString().trim();

                if (emailUsuario.isEmpty() && passUsuario.isEmpty()) {
                    mostrarAlerta("Error", "Los campos de correo y contraseña deben estar llenos");
                } else {
                    loginUsuario(emailUsuario, passUsuario);
                }
            }

            private void mostrarAlerta(String titulo, String mensaje) {
                AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                builder.setTitle(titulo)
                        .setMessage(mensaje)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            private void loginUsuario(String emailUser, String passUser) {
                if (passUser.length() < 6) {
                    mostrarAlerta("Error de inicio de sesión", "La contraseña debe tener al menos 6 caracteres");
                    return;
                }
                mAuth.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verificarCampo();
                        } else {
                            mostrarAlerta("Error de inicio de sesión", "Correo o contraseña incorrectos");
                            password.getEditText().setText("");
                        }
                    }
                });
            }

            private void verificarCampo() {
                String usuarioID = mAuth.getCurrentUser().getUid();
                DocumentReference empleadoRef = db.collection("Empleados").document(usuarioID);

                empleadoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documento = task.getResult();
                            if (documento.exists()) {
                                if (documento.contains("fecha_trabajo") && documento.getBoolean("fecha_trabajo")) {
                                    startActivity(new Intent(loginActivity.this, mainActivity.class));
                                    Toast.makeText(loginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    startActivity(new Intent(loginActivity.this, fechaActivity.class));
                                    finish();
                                }
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