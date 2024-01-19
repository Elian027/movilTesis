package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class restaurarContrasenaActivity extends AppCompatActivity {

    TextInputLayout correoTextInputLayout;
    Button btnRecuperarContrasena;
    ImageView btn_atras;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurar_contrasena);

        db = FirebaseFirestore.getInstance();


        correoTextInputLayout = findViewById(R.id.correo);
        btnRecuperarContrasena = findViewById(R.id.btnRecuperarContrasena);
        btn_atras = findViewById(R.id.atras);

        btnRecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = correoTextInputLayout.getEditText().getText().toString().trim();

                if (email.isEmpty()) {
                    correoTextInputLayout.setError("Ingrese su correo electrónico");
                    return;
                }
                verificarCorreo(email);
            }
        });

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irLogin = new Intent(restaurarContrasenaActivity.this, loginActivity.class);
                startActivity(irLogin);
            }
        });
    }

    private void verificarCorreo(String email) {
        db.collection("Personal")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Usuario encontrado en la base de datos
                                String contrasenaTemporal = "STemporal01"; // Puedes generar una contraseña temporal
                                String hashTemp = hashContrasenia.hashPassword(contrasenaTemporal);

                                // Actualizar la contraseña en Firestore
                                String userId = task.getResult().getDocuments().get(0).getId();
                                actualizarContrasena(userId, hashTemp);

                                // Mostrar alerta con la nueva contraseña temporal
                                mostrarAlertaNC(contrasenaTemporal);
                            } else {
                                mostrarAlertaX("Correo no encontrado", "El correo no está registrado");
                            }
                        } else {
                            // Manejar errores de la consulta
                            mostrarAlertaX("Error", "Error al verificar el correo");
                        }
                    }
                });
    }

    private void mostrarAlertaNC(String nuevaContrasenaTemporal) {
        mostrarAlerta("Nueva Contraseña", "Tu nueva contraseña temporal es: " + nuevaContrasenaTemporal, new Runnable() {
            @Override
            public void run() {
                // Puedes realizar acciones adicionales después de mostrar la alerta
                Intent irMain = new Intent(restaurarContrasenaActivity.this, loginActivity.class);
                startActivity(irMain);
                finish();
            }
        });
    }

    private void actualizarContrasena(String userId, String nuevaContrasenaTemporal) {
        // Actualizar el campo "Contrasenia" a la nueva contraseña temporal
        db.collection("Personal")
                .document(userId)
                .update("Contrasenia", nuevaContrasenaTemporal)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Manejar éxito o error en la actualización
                        if (task.isSuccessful()) {
                            // Éxito al actualizar la contraseña
                        } else {
                            // Error al actualizar la contraseña
                        }
                    }
                });
    }

    private void mostrarAlerta(String titulo, String mensaje, Runnable onAceptar) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(restaurarContrasenaActivity.this);
            builder.setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        if (onAceptar != null) {
                            onAceptar.run();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void mostrarAlertaX(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, null);
    }

}
