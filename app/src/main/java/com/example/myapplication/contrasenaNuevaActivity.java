package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.mindrot.jbcrypt.BCrypt;
import java.util.HashMap;
import java.util.Map;

public class contrasenaNuevaActivity extends AppCompatActivity {
    ImageView btn_atras;
    TextInputEditText nuevaPass, confirmarPass;
    MaterialButton btn_guardar;
    FirebaseFirestore db;
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena_nueva);

        db = FirebaseFirestore.getInstance();

        btn_atras = findViewById(R.id.atras);
        nuevaPass = findViewById(R.id.contrasenaNueva);
        confirmarPass = findViewById(R.id.confirmarContrasena);
        btn_guardar = findViewById(R.id.guardar);
        usuarioID = obtenerId();

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irLogin = new Intent(contrasenaNuevaActivity.this, loginActivity.class);
                startActivity(irLogin);
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarPassword();
            }
        });
    }

    private void validarPassword() {
        String nuevaPassword = nuevaPass.getText().toString().trim();
        String confirmarPassword = confirmarPass.getText().toString().trim();

        if (TextUtils.isEmpty(nuevaPassword) || TextUtils.isEmpty(confirmarPassword)) {
            Toast.makeText(this, "Ambos campos de contraseña deben estar llenos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevaPassword.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!usuarioID.isEmpty()) {
            String contraseniaHash = hashPassword(nuevaPassword);
            actualizarContrasenia(usuarioID, contraseniaHash);
        }
    }

    private String hashPassword(String password) {
        // Genera el hash de la contraseña
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void actualizarContrasenia(String usuarioID, String nuevaPassword) {
        DocumentReference docRef = db.collection("Personal").document(usuarioID);

        Map<String, Object> updates = new HashMap<>();
        updates.put("Contrasenia", nuevaPassword);
        updates.put("contrasenaCambiada", true);

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    obtenerFecha(usuarioID);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(contrasenaNuevaActivity.this, "Error al actualizar la contraseña en la base de datos", Toast.LENGTH_SHORT).show();
                });
    }

    private void obtenerFecha(String usuarioID) {
        DocumentReference docRef = db.collection("Personal").document(usuarioID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                boolean fechaTrabajo = documentSnapshot.getBoolean("fecha_trabajo");
                if (fechaTrabajo) {
                    mostrarAlertaExito2();
                } else {
                    mostrarAlertaExito1();
                }
            } else {
                Toast.makeText(contrasenaNuevaActivity.this, "Documento no encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(contrasenaNuevaActivity.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
        });
    }


    private void mostrarAlerta(String titulo, String mensaje, Runnable onAceptar) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(contrasenaNuevaActivity.this);
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

    private void mostrarAlertaExito1() {
        mostrarAlerta("Éxito", "Contraseña actualizada con éxito", () -> {
            Intent irMain = new Intent(contrasenaNuevaActivity.this, fechaActivity.class);
            startActivity(irMain);
        });
    }

    private void mostrarAlertaExito2() {
        mostrarAlerta("Éxito", "Contraseña actualizada con éxito", () -> {
            Intent irMain = new Intent(contrasenaNuevaActivity.this, mainActivity.class);
            startActivity(irMain);
        });
    }

    private String obtenerId() {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("userId","");
    }

}