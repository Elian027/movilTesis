package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.mindrot.jbcrypt.BCrypt;

public class loginActivity extends AppCompatActivity {
    Button btn_login;
    FirebaseFirestore db;
    TextInputEditText email ,password;
    TextView btn_recuperarPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        email = findViewById(R.id.correo);
        password = findViewById(R.id.pass);
        btn_login = findViewById(R.id.ingresar);
        btn_recuperarPass = findViewById(R.id.olvidarContrasena);

        verificarSesion();

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
                                // Usuario encontrado en la BD
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String usuarioId = document.getId();
                                    Log.e("ID DE USUARIO", "Este es el id del usuario ingresado: " + usuarioId);

                                    // Verificar si existe el campo "contrasenaCambiada"
                                    if (document.contains("contrasenaCambiada")) {
                                        boolean contrasenaCambiada = document.getBoolean("contrasenaCambiada");

                                        if (contrasenaCambiada) {
                                            // La contraseña ha sido cambiada, verificar el hash
                                            String contraseniaAlmacenada = document.getString("Contrasenia");

                                            if (contraseniaAlmacenada != null && BCrypt.checkpw(passUser, contraseniaAlmacenada)) {
                                                // Contraseña correcta
                                                startActivity(new Intent(loginActivity.this, mainActivity.class));
                                                Toast.makeText(loginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                // Contraseña o correo incorrectos
                                                password.setText("");
                                                mostrarAlerta("Error de inicio de sesión", "Contraseña o correo incorrectos");
                                            }
                                        } else {
                                            // La contraseña no ha sido cambiada, redirigir a la pantalla de cambio de contraseña
                                            Intent irContrasena = new Intent(loginActivity.this, contrasenaNuevaActivity.class);
                                            startActivity(irContrasena);
                                            finish();
                                        }
                                    } else {
                                        String contraseniaAlmacenada = document.getString("Contrasenia");
                                        if (contraseniaAlmacenada != null && contraseniaAlmacenada.equals(passUser)) {
                                            Intent irContrasena = new Intent(loginActivity.this, contrasenaNuevaActivity.class);
                                            startActivity(irContrasena);
                                            finish();
                                        } else {
                                            password.setText("");
                                            mostrarAlerta("Error de inicio de sesión", "Contraseña o correo incorrectos");
                                        }
                                    }

                                    // Guardar el ID del usuario
                                    guardarID(usuarioId);
                                }
                            } else {
                                // Usuario no encontrado en la base de datos
                                mostrarAlerta("Error de inicio de sesión", "El usuario no está registrado");
                                password.setText("");
                            }
                        } else {
                            mostrarAlerta("Error de inicio de sesión", "Error al verificar el correo en la base de datos");
                        }
                    }
                });
    }




    private void guardarID(String usuarioId) {
        // Almacenar las credenciales
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", usuarioId);
        editor.apply();
    }

    private void verificarSesion() {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");

        if (!userId.isEmpty()) {
            // Iniciar directamente con el usuario almacenado
            Intent irMain = new Intent(loginActivity.this, mainActivity.class);
            startActivity(irMain);
        } else {
            //
        }
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