package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        // Verificar si el usuario ya está autenticado
        if (mAuth.getCurrentUser() != null) {
            // El usuario ya está autenticado, redirigir a la actividad principal
            startActivity(new Intent(loginActivity.this, mainActivity.class));
            finish();
        }

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
            }
        });
    }
    private void verificarCampo() {
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            String usuarioID = mAuth.getCurrentUser().getUid();
            DocumentReference empleadoRef = db.collection("Personal").document(usuarioID);
            empleadoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento.exists()) {
                            if (!documento.contains("ContrasenaCambiada")) {
                                boolean contrasenaNueva = documento.getBoolean("contrasenaCambiada");
                                if (!contrasenaNueva) {
                                    Intent irContrasena = new Intent(loginActivity.this, contrasenaNuevaActivity.class);
                                    startActivity(irContrasena);
                                    finish();
                                } else {
                                    // El usuario existe y la contraseña ha sido cambiada
                                    startActivity(new Intent(loginActivity.this, mainActivity.class));
                                    Toast.makeText(loginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                // El usuario existe y tiene el campo "ContrasenaCambiada"
                                startActivity(new Intent(loginActivity.this, mainActivity.class));
                                Toast.makeText(loginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            // El usuario no existe, redirigir a la pantalla de inicio de sesión
                            startActivity(new Intent(loginActivity.this, loginActivity.class));
                            finish();
                        }
                    }
                }
            });
        } else {
            // No hay usuario autenticado, redirigir a la pantalla de inicio de sesión
            startActivity(new Intent(loginActivity.this, loginActivity.class));
            finish();
        }
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
                                                    FirebaseUser usuarioX = mAuth.getCurrentUser();
                                                    saveUserCredentials(usuarioX.getEmail(), usuarioX.getUid());
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

    private void saveUserCredentials(String userEmail, String userId) {
        // Almacenar las credenciales en SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("email", userEmail);
        editor.putString("userId", userId);

        editor.apply();
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