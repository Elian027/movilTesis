package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class contrasenaNuevaActivity extends AppCompatActivity {
    ImageView btn_atras;
    TextInputEditText nuevaPass, confirmarPass;
    Button btn_guardar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena_nueva);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_atras = findViewById(R.id.atras);
        nuevaPass = findViewById(R.id.contrasenaNueva);
        confirmarPass = findViewById(R.id.confirmarContrasena);
        btn_guardar = findViewById(R.id.guardar);

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

        // Validaciones pasadas, proceder a actualizar la contraseña en Firebase Authentication
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(nuevaPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Contraseña actualizada con éxito
                            // Ahora, eliminar el campo "Password" en la base de datos
                            eliminarCampo(user.getUid());
                        } else {
                            Toast.makeText(contrasenaNuevaActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void eliminarCampo(String usuarioID) {
        DocumentReference empleadoRef = db.collection("Personal").document(usuarioID);
        // Crear un Map sin el campo "Password"
        Map<String, Object> updates = new HashMap<>();
        updates.put("Password", FieldValue.delete());

        // Actualizar el documento en Firestore
        empleadoRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mostrarAlertaExito();
                    } else {
                        // Error al eliminar el campo "Password"
                        Toast.makeText(contrasenaNuevaActivity.this, "Error al eliminar el campo \"Password\"", Toast.LENGTH_SHORT).show();
                    }
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

    private void mostrarAlertaExito() {
        mostrarAlerta("Éxito", "Contraseña actualizada con éxito", () -> {
            Intent irMain = new Intent(contrasenaNuevaActivity.this, fechaActivity.class);
            startActivity(irMain);
        });
    }
}