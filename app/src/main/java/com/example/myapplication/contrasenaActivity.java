package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class contrasenaActivity extends AppCompatActivity {
    TextInputEditText contrasenaActual, contrasenaNueva, contrasenaConf;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ImageView btn_atras = findViewById(R.id.atras);
        Button btn_cancelar = findViewById(R.id.cancelar);
        Button btn_guardar = findViewById(R.id.guardar);
        contrasenaActual = findViewById(R.id.pass_actual);
        contrasenaNueva = findViewById(R.id.pass_nueva);
        contrasenaConf = findViewById(R.id.pass_confirmar);

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pass_to_main = new Intent(contrasenaActivity.this, mainActivity.class);
                startActivity(pass_to_main);
                finish();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pass_to_main = new Intent(contrasenaActivity.this, mainActivity.class);
                startActivity(pass_to_main);
                finish();
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passActual = contrasenaActual.getText().toString();
                String passNueva = contrasenaNueva.getText().toString();
                String passConfirmar = contrasenaConf.getText().toString();

                if (passNueva.equals(passConfirmar)) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        // Verificar la contraseña actual directamente en la autenticación
                        mAuth.signInWithEmailAndPassword(currentUser.getEmail(), passActual)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        // La contraseña actual es correcta, actualizar la contraseña
                                        currentUser.updatePassword(passNueva)
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        Intent irMain = new Intent(contrasenaActivity.this, exitoContrasenaActivity.class);
                                                        startActivity(irMain);
                                                        finish();
                                                    } else {
                                                        mostrarError("Error", "No se pudo actualizar la contraseña de autenticación");
                                                    }
                                                });
                                    } else {
                                        mostrarError("Error", "La contraseña actual es incorrecta");
                                    }
                                });
                    } else {
                        mostrarError("Error", "Usuario no autenticado");
                    }
                } else {
                    mostrarError("Error", "Las nuevas contraseñas no coinciden");
                }
            }
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

}