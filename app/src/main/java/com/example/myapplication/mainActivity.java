package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class mainActivity extends AppCompatActivity {
    Button btn_cerrar, btn_cambiar, btn_editar;
    FirebaseAuth mAuth;
    TextView nombreTextView, apellidoTextView, emailTextView, celularTextView;
    String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btn_cerrar = findViewById(R.id.cerrar);
        btn_cambiar = findViewById(R.id.cambiar);
        btn_editar = findViewById(R.id.editar);
        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        emailTextView = findViewById(R.id.email);
        celularTextView = findViewById(R.id.celular);

        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            usuarioId = usuario.getUid();
            DocumentReference userRef = db.collection("usuarios").document(usuarioId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        // Acciones si el documento existe
                        String nombre = document.getString("nombre_cliente");
                        String apellido = document.getString("apellido_cliente");
                        String email = document.getString("email_cliente");
                        String celular = document.getString("telefono_cliente");

                        nombreTextView.setText("Nombre: " + nombre);
                        apellidoTextView.setText("Apellido: " + apellido);
                        emailTextView.setText("Email: " + email);
                        celularTextView.setText("Celular: " + celular);
                    }
                }
            });
        }

        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_to_edit = new Intent(mainActivity.this, editar_perfilActivity.class);
                startActivity(main_to_edit);
                finish();
            }
        });

        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cierra sesión en Firebase
                mAuth.signOut();
                Intent main_to_log = new Intent(mainActivity.this, loginActivity.class);
                startActivity(main_to_log);
                finish();
            }

        });

        btn_cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_to_pass = new Intent(mainActivity.this, contrasenaActivity.class);
                startActivity(main_to_pass);
                finish();
            }
        });
    }
}
