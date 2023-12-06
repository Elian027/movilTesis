package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class mainActivity extends AppCompatActivity {
    Button btn_cerrar;
    FirebaseAuth mAuth;
    TextView nombreTextView, apellidoTextView, emailTextView, celularTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btn_cerrar = findViewById(R.id.cerrar);
        FirebaseFirestore db;
        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        emailTextView = findViewById(R.id.email);
        celularTextView = findViewById(R.id.celular);

        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cierra sesión en Firebase
                mAuth.signOut();
                Intent intent = new Intent(mainActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            }

        });
        db = FirebaseFirestore.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            String usuarioId = usuario.getUid();
            DocumentReference userRef = db.collection("usuarios").document(usuarioId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        String nombre = document.getString("nombre_cliente");
                        String apellido = document.getString("apellido_cliente");
                        String email = document.getString("email_cliente");
                        String celular = document.getString("telefono_cliente");

                        nombreTextView.setText("Nombre: " + nombre);
                        apellidoTextView.setText("Apellido: " + apellido);
                        emailTextView.setText("Email: " + email);
                        celularTextView.setText("Celular: " + celular);
                    } else {
                        Log.d("Firestore", "El documento no existe");
                    }
                }
            });

        }

    }
}

