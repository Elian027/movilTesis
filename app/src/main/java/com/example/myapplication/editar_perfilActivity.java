package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class editar_perfilActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar, btn_seleccionar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText nombreET, apellidoET, emailET, celularET;
    ActivityResultLauncher<Intent> cameraLauncher;
    String usuarioID;
    TextView archivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usuarioID = mAuth.getCurrentUser().getUid();

        ImageView btn_atras = findViewById(R.id.atras);
        //ImageView btn_camara = findViewById(R.id.camara);

        nombreET = findViewById(R.id.editTextNombre);
        apellidoET = findViewById(R.id.editTextApellido);
        emailET = findViewById(R.id.editTextEmail);
        celularET = findViewById(R.id.editTextTelefono);

        btn_cancelar = findViewById(R.id.botonCancelar);
        btn_guardar = findViewById(R.id.botonGuardar);
        btn_seleccionar = findViewById(R.id.seleccionar);

        archivo = findViewById(R.id.textViewArchivo);

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
                Intent edit_to_main = new Intent(editar_perfilActivity.this, mainActivity.class);
                startActivity(edit_to_main);
                finish();
            }
        });

        /*btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camara();
            }
        });*/

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

        btn_seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para seleccionar un archivo
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
            }
        });

    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imagenSeleccionada = data.getData();
                        String nombreImg = obtenerNombreArchivo(imagenSeleccionada);
                        archivo.setText(nombreImg);
                    }
                }
            }
    );

    private String obtenerNombreArchivo(Uri uri) {
        String nombre = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nombreIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    nombre = cursor.getString(nombreIndex);
                }
            }
        } else if (uri.getScheme().equals("file")) {
            nombre = new File(uri.getPath()).getName();
        }
        return nombre;
    }

    private void obtenerInformacionUsuario() {
        db.collection("Empleados").document(usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("Nombre");
                            String apellido = documentSnapshot.getString("Apellido");
                            String email = documentSnapshot.getString("correo");
                            String celular = documentSnapshot.getString("telefono");

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
        datosActualizados.put("Nombre", nuevoNombre);
        datosActualizados.put("Apellido", nuevoApellido);
        datosActualizados.put("correo", nuevoEmail);
        datosActualizados.put("telefono", nuevoCelular);

        // Actualizar los datos en Firestore
        db.collection("Empleados").document(usuarioID)
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