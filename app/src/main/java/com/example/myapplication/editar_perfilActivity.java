package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.app.AlertDialog;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class editar_perfilActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar, btn_cambiar, btn_verHorario;
    ImageView btn_atras, foto;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText nombreET, apellidoET, emailET, celularET;
    String usuarioID;
    final int cod_imagen=300;
    String ruta_foto = "empleados/*";
    String urlImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usuarioID = mAuth.getCurrentUser().getUid();

        nombreET = findViewById(R.id.nombre);
        apellidoET = findViewById(R.id.apellido);
        emailET = findViewById(R.id.email);
        celularET = findViewById(R.id.telefono);

        btn_atras = findViewById(R.id.atras);
        btn_cancelar = findViewById(R.id.botonCancelar);
        btn_guardar = findViewById(R.id.botonGuardar);
        btn_cambiar = findViewById(R.id.botonFoto);
        btn_verHorario = findViewById(R.id.botonHorario);

        foto = findViewById(R.id.fotoEmpleado);

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

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
                Intent edit_to_main = new Intent(editar_perfilActivity.this, mainActivity.class);
                startActivity(edit_to_main);
                finish();
            }
        });

        btn_cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();
            }
        });

        btn_verHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarHorarioController.mostrarDialogoHorario(editar_perfilActivity.this);
            }
        });
    }

    public static class mostrarHorarioController {
        public static void mostrarDialogoHorario(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_fecha_ver, null);

            // Propiedades de la vista
            Button btn_editar = view.findViewById(R.id.botonEditarHorario);


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setTitle("Días de trabajo");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            btn_editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarHorarioController.editarDialogoHorario(context);
                }
            });
        }
    }

    public static class editarHorarioController {
        public static void editarDialogoHorario(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_fecha_editar, null);

            //Propiedades de la vista

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setTitle("Editar días de trabajo");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, cod_imagen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == cod_imagen && resultCode == RESULT_OK && data != null) {
            // Obtiene la Uri de la imagen seleccionada
            Uri uriImagen = data.getData();

            // Obtiene la URL de la imagen y la sube al Storage de Firebase
            subirImagen(uriImagen);
        }
    }

    private void subirImagen(Uri uriImagen) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagenRef = storageRef.child(ruta_foto + usuarioID + ".jpg");

        // Sube la imagen al Storage
        UploadTask uploadTask = imagenRef.putFile(uriImagen);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Obtiene la URL de la imagen subida
                imagenRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Puedes utilizar la URL de la imagen como sea necesario
                        urlImagen = uri.toString();

                        // Muestra la imagen en tu ImageView usando Picasso
                        Picasso.get().load(urlImagen).into(foto);

                        // Guarda la URL en Firestore
                        guardarUrl(urlImagen);
                        Toast.makeText(editar_perfilActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void guardarUrl(String urlImagen) {
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("Foto", urlImagen);

        db.collection("Personal").document(usuarioID)
                .set(datosActualizados, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }

    private void obtenerInformacionUsuario() {
        db.collection("Personal").document(usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            String nombre = document.getString("Nombre");
                            String apellido = document.getString("Apellido");
                            String email = document.getString("Email");
                            String celular = document.getString("Telefono");

                            // Cargar la información en los EditText
                            nombreET.setText(nombre);
                            apellidoET.setText(apellido);
                            emailET.setText(email);
                            celularET.setText(celular);

                            // Obtener la URL de la imagen y mostrarla en el ImageView
                            String urlImagen = document.getString("Foto");
                            if (urlImagen != null && !urlImagen.isEmpty()) {
                                Picasso.get().load(urlImagen).into(foto);
                            }
                        } else {
                            Toast.makeText(editar_perfilActivity.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarCambios() {
        // Obtener los valores actuales de los EditText
        String nuevoNombre = nombreET.getText().toString().trim();
        String nuevoApellido = apellidoET.getText().toString().trim();
        String nuevoEmail = emailET.getText().toString().trim();
        String nuevoCelular = celularET.getText().toString().trim();

        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("Nombre", nuevoNombre);
        datosActualizados.put("Apellido", nuevoApellido);
        datosActualizados.put("Email", nuevoEmail);
        datosActualizados.put("Telefono", nuevoCelular);

        String nuevoCorreo = emailET.getText().toString().trim();
        actualizarCorreo(nuevoCorreo);

        db.collection("Personal").document(usuarioID)
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

    private void actualizarCorreo(String nuevoCorreo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updateEmail(nuevoCorreo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DocumentReference usuarioRef = FirebaseFirestore.getInstance().collection("Personal").document(user.getUid());

                                usuarioRef.update("Email", nuevoCorreo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        }
                    });
        }
    }

}