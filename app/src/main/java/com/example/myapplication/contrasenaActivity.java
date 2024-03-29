package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Window;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.mindrot.jbcrypt.BCrypt;
import java.util.HashMap;
import java.util.Map;

public class contrasenaActivity extends AppCompatActivity {
    TextInputEditText contrasenaActual, contrasenaNueva, contrasenaConf;
    FirebaseFirestore db;
    ImageView btn_atras;
    Button btn_cancelar, btn_guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena);

        db = FirebaseFirestore.getInstance();

        btn_atras = findViewById(R.id.atras);
        btn_cancelar = findViewById(R.id.cancelar);
        btn_guardar = findViewById(R.id.guardar);
        contrasenaActual = findViewById(R.id.pass_actual);
        contrasenaNueva = findViewById(R.id.pass_nueva);
        contrasenaConf = findViewById(R.id.pass_confirmar);

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pass_to_main = new Intent(contrasenaActivity.this, mainActivity.class);
                startActivity(pass_to_main);
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pass_to_main = new Intent(contrasenaActivity.this, mainActivity.class);
                startActivity(pass_to_main);
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passActual = contrasenaActual.getText().toString();
                String passNueva = contrasenaNueva.getText().toString();
                String passConfirmar = contrasenaConf.getText().toString();

                if (passNueva.equals(passConfirmar)) {
                    String userId = obtenerId();

                    if (userId != null) {
                        verificarContrasenaActual(userId, passActual, passNueva);
                    } else {
                        mostrarError("Error", "No se pudo obtener el ID del usuario");
                    }
                } else {
                    mostrarError("Error", "Las nuevas contraseñas no coinciden");
                }
            }
        });
    }

    private void verificarContrasenaActual(String userId, String passActual, String passNueva) {
        DocumentReference usuarioRef = db.collection("Personal").document(userId);

        usuarioRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Trae la contraseña de la BD
                String contrasenaAlmacenada = documentSnapshot.getString("Contrasenia");

                if (contrasenaAlmacenada != null && BCrypt.checkpw(passActual, contrasenaAlmacenada)) {
                    actualizarContrasena(userId, passNueva);
                } else {
                    mostrarError("Error", "La contraseña actual es incorrecta");
                }
            }
        });
    }

    private void actualizarContrasena(String userId, String nuevaContrasena) {
        // Genera el hash para la nueva contraseña
        String contraseniaHash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());

        DocumentReference usuarioRef = db.collection("Personal").document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("Contrasenia", contraseniaHash);

        usuarioRef.update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(contrasenaActivity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
            Intent irMain = new Intent(contrasenaActivity.this, exitoContrasenaActivity.class);
            startActivity(irMain);
            finish();
        }).addOnFailureListener(e -> {
            mostrarError("Error", "No se pudo actualizar la contraseña en la base de datos");
        });
    }

    private void mostrarError(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contrasenaActivity.this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String obtenerId() {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("userId","");
    }

}
