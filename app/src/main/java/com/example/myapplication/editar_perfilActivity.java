package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class editar_perfilActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText nombreET, apellidoET, emailET, celularET;
    ActivityResultLauncher<Intent> cameraLauncher;
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usuarioID = mAuth.getCurrentUser().getUid();

        ImageView btn_atras = findViewById(R.id.atras);
        ImageView btn_camara = findViewById(R.id.camara);

        nombreET = findViewById(R.id.editTextNombre);
        apellidoET = findViewById(R.id.editTextApellido);
        emailET = findViewById(R.id.editTextEmail);
        celularET = findViewById(R.id.editTextTelefono);

        btn_cancelar = findViewById(R.id.botonCancelar);
        btn_guardar = findViewById(R.id.botonGuardar);

        obtenerInformacionUsuario();

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_to_main = new Intent(editar_perfilActivity.this, mainActivity.class);
                startActivity(edit_to_main);
                finish();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No hagas nada o vuelve a cargar la información original
            }
        });

        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camara();
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            onActivityResult(result.getResultCode(), result.getData());
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambiosEnFirestore();
                Intent edit_to_main = new Intent(editar_perfilActivity.this, mainActivity.class);
                startActivity(edit_to_main);
                finish();
            }
        });
    }

    private void obtenerInformacionUsuario() {
        db.collection("usuarios").document(usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre_cliente");
                            String apellido = documentSnapshot.getString("apellido_cliente");
                            String email = documentSnapshot.getString("email_cliente");
                            String celular = documentSnapshot.getString("telefono_cliente");

                            // Cargar la información en los EditText
                            nombreET.setText(nombre);
                            apellidoET.setText(apellido);
                            emailET.setText(email);
                            celularET.setText(celular);
                        } else {
                            Toast.makeText(editar_perfilActivity.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void camara() {
        Intent abrirCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (abrirCamara.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(abrirCamara);
        }
    }

    private void onActivityResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            // Manejar la imagen capturada si es necesario
        }
    }

    private void guardarCambiosEnFirestore() {
        // Obtener los valores actuales de los EditText
        String nuevoNombre = nombreET.getText().toString().trim();
        String nuevoApellido = apellidoET.getText().toString().trim();
        String nuevoEmail = emailET.getText().toString().trim();
        String nuevoCelular = celularET.getText().toString().trim();

        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("nombre_cliente", nuevoNombre);
        datosActualizados.put("apellido_cliente", nuevoApellido);
        datosActualizados.put("email_cliente", nuevoEmail);
        datosActualizados.put("telefono_cliente", nuevoCelular);

        // Actualizar los datos en Firestore
        db.collection("usuarios").document(usuarioID)
                .set(datosActualizados, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(editar_perfilActivity.this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(editar_perfilActivity.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                });
    }
}