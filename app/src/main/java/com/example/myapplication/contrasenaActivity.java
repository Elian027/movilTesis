package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class contrasenaActivity extends AppCompatActivity {
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
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("usuarios").document(userId);
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

        ImageView imageViewAtras = findViewById(R.id.atras);
        Button btn_cancelar = findViewById(R.id.cancelar);
        Button btn_guardar = findViewById(R.id.guardar);
        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        emailTextView = findViewById(R.id.email);
        celularTextView = findViewById(R.id.celular);

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
                Intent pass_to_ex = new Intent(contrasenaActivity.this, exitoContrasenaActivity.class);
                startActivity(pass_to_ex);
                finish();
            }
        });


    }
}