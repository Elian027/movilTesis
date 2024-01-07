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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class loginActivity extends AppCompatActivity {
    Button btn_login;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextInputEditText email ,password;
    TextView btn_recuperarPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email = findViewById(R.id.correo);
        password = findViewById(R.id.pass);
        btn_login = findViewById(R.id.ingresar);
        btn_recuperarPass = findViewById(R.id.olvidarContrasena);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUsuario = email.getText().toString().trim();
                String passUsuario = password.getText().toString().trim();

                if (emailUsuario.isEmpty() && passUsuario.isEmpty()) {
                    mostrarAlerta("Error", "Los campos de correo y contraseña deben estar llenos");
                } else {
                    loginUsuario(emailUsuario, passUsuario);
                }
            }


        });

        btn_recuperarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irReccuperar = new Intent(loginActivity.this, restaurarContrasenaActivity.class);
                startActivity(irReccuperar);
                finish();
            }
        });
    }
    private void verificarCampo() {
        String usuarioID = mAuth.getCurrentUser().getUid();
        DocumentReference empleadoRef = db.collection("Personal").document(usuarioID);

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
                }
            }
        });
    }

    private void loginUsuario(String emailUser, String passUser) {
        if (passUser.length() < 6) {
            mostrarAlerta("Error de inicio de sesión", "La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // Realizar consulta en Firestore
        db.collection("Personal")
                .whereEqualTo("Email", emailUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                mAuth.signInWithEmailAndPassword(emailUser, passUser)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> authTask) {
                                                if (authTask.isSuccessful()) {
                                                    verificarCampo();
                                                } else {
                                                    mostrarAlerta("Error de inicio de sesión", "Correo o contraseña incorrectos");
                                                    password.setText("");
                                                }
                                            }
                                        });
                            } else {
                                mostrarAlerta("Error de inicio de sesión", "El usuario no está registrado");
                                password.setText("");
                            }
                        } else {
                            mostrarAlerta("Error de inicio de sesión", "Error al verificar el correo en la base de datos");
                            password.setText("");
                        }
                    }
                });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}