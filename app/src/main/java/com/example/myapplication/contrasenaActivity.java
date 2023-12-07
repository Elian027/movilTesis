package com.example.myapplication;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class contrasenaActivity extends AppCompatActivity {
    EditText contrasenaActual, contrasenaNueva, contrasenaConf;
    TextView nombreTextView, apellidoTextView, emailTextView, celularTextView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena);

        // Inicializa Firebase Authentication y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Obtén el usuario actual
        FirebaseUser usuarioActual = mAuth.getCurrentUser();

        ImageView imageViewAtras = findViewById(R.id.atras);
        Button btn_cancelar = findViewById(R.id.cancelar);
        Button btn_guardar = findViewById(R.id.guardar);
        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        emailTextView = findViewById(R.id.email);
        celularTextView = findViewById(R.id.celular);
        contrasenaActual = findViewById(R.id.pass_actual);
        contrasenaNueva = findViewById(R.id.pass_nueva);
        contrasenaConf = findViewById(R.id.pass_confirmar);

        if (usuarioActual != null) {
            String usuarioId = usuarioActual.getUid();
            DocumentReference userRef = db.collection("usuarios").document("wSaPsMKjV1k2FRnAfZFS");
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre_cliente");
                        String apellido = documentSnapshot.getString("apellido_cliente");
                        String email = documentSnapshot.getString("email_cliente");
                        String celular = documentSnapshot.getString("telefono_cliente");

                        // Actualiza los TextView con los datos obtenidos
                        nombreTextView.setText("Nombre: " + nombre);
                        apellidoTextView.setText("Apellido: " + apellido);
                        emailTextView.setText("Email: " + email);
                        celularTextView.setText("Celular: " + celular);
                    } else {
                        Log.d("contrasenaActivity", "El documento no existe");
                    }
                }
            });
        }

        imageViewAtras.setOnClickListener(new View.OnClickListener() {
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

                // Verificar la contraseña actual con Firebase Authentication
                String userUid = mAuth.getCurrentUser().getUid();
                DocumentReference userDocRef = db.collection("usuarios").document("wSaPsMKjV1k2FRnAfZFS");

                userDocRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String contrasenaBD = task.getResult().getString("pass_cliente");
                        if (contrasenaBD != null && contrasenaBD.equals(passActual)) {
                            // Contraseña actual correcta, verificar las nuevas contraseñas
                            if (passNueva.equals(passConfirmar)) {
                                // Cambiar la contraseña en Firebase Authentication
                                mAuth.getCurrentUser().updatePassword(passNueva);

                                // Actualizar la contraseña en Cloud Firestore
                                userDocRef.update("pass_cliente", passNueva);

                                // Mostrar mensaje de éxito
                                Toast.makeText(contrasenaActivity.this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();

                                // Redirigir a la actividad de éxito
                                Intent pass_to_ex = new Intent(contrasenaActivity.this, exitoContrasenaActivity.class);
                                startActivity(pass_to_ex);
                                finish();
                            } else {
                                // Mostrar mensaje de error si las nuevas contraseñas no coinciden
                                Toast.makeText(contrasenaActivity.this, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Mostrar mensaje de error si la contraseña actual es incorrecta
                            Toast.makeText(contrasenaActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Manejar el caso en el que la verificación de la contraseña en la BD no fue exitosa
                        Toast.makeText(contrasenaActivity.this, "Error al verificar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}