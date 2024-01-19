package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class editar_perfilActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar, btn_cambiar, btn_verHorario;
    ImageView btn_atras, foto;
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

        db = FirebaseFirestore.getInstance();
        usuarioID = obtenerId();

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
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_to_main = new Intent(editar_perfilActivity.this, mainActivity.class);
                startActivity(edit_to_main);
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
                Intent edit_to_main = new Intent(editar_perfilActivity.this, mainActivity.class);
                startActivity(edit_to_main);
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
                mostrarHorarioController.mostrarDialogoHorario(editar_perfilActivity.this, usuarioID);
            }
        });
    }

    public static class mostrarHorarioController {
        public static void mostrarDialogoHorario(Context context, String usuarioID) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_fecha_ver, null);

            // Propiedades de la vista
            Button btn_editar = view.findViewById(R.id.botonEditarHorario);
            CheckBox caja_lunes = view.findViewById(R.id.lunes);
            CheckBox caja_martes = view.findViewById(R.id.martes);
            CheckBox caja_miercoles = view.findViewById(R.id.miercoles);
            CheckBox caja_jueves = view.findViewById(R.id.jueves);
            CheckBox caja_viernes = view.findViewById(R.id.viernes);
            CheckBox caja_sabado = view.findViewById(R.id.sabado);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setTitle("Días de trabajo");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            DocumentReference drPersonal = FirebaseFirestore.getInstance().collection("Personal").document(usuarioID);
            drPersonal.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    List<Object> diasLaborablesObject = (List<Object>) documentSnapshot.get("dias_laborables");
                    Log.d("MostrarHorarioController", "Días laborables: " + diasLaborablesObject);

                    // Verifica el estado de cada día y actualiza los CheckBox correspondientes
                    cargarCheckBox(caja_lunes, diasLaborablesObject, 0);
                    cargarCheckBox(caja_martes, diasLaborablesObject, 1);
                    cargarCheckBox(caja_miercoles, diasLaborablesObject, 2);
                    cargarCheckBox(caja_jueves, diasLaborablesObject, 3);
                    cargarCheckBox(caja_viernes, diasLaborablesObject, 4);
                    cargarCheckBox(caja_sabado, diasLaborablesObject, 5);
                }
            });

            btn_editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    editarHorarioController.editarDialogoHorario(context, usuarioID);
                }
            });
        }
    }

    private static void cargarCheckBox(CheckBox checkBox, List<Object> diasLaborables, int dia) {
        if (diasLaborables != null) {
            Log.d("CheckBoxLog", "Días laborables: " + diasLaborables);
            Log.d("CheckBoxLog", "Día a verificar: " + dia);

            if (diasLaborables.contains((long) dia)) {
                // Día laborable, marcar CheckBox
                Log.d("CheckBoxLog", "Día " + dia + " marcado como laborable");
                checkBox.setChecked(true);
            } else {
                // Día no laborable, desmarcar CheckBox
                Log.d("CheckBoxLog", "Día " + dia + " marcado como no laborable");
                checkBox.setChecked(false);
            }
        } else {
            Log.d("CheckBoxLog", "La lista de días laborables es nula");
        }
    }

    public static class editarHorarioController {
        public static void editarDialogoHorario(Context context, String usuarioID) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_fecha_editar, null);

            //Propiedades de la vista
            CheckBox[] cajas = new CheckBox[]{
                    view.findViewById(R.id.lunes),
                    view.findViewById(R.id.martes),
                    view.findViewById(R.id.miercoles),
                    view.findViewById(R.id.jueves),
                    view.findViewById(R.id.viernes),
                    view.findViewById(R.id.sabado)
            };

            List<Object> diasLaborables = new ArrayList<>();
            List<Object> diasNoLaborables = new ArrayList<>();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setTitle("Editar días de trabajo");
            builder.setPositiveButton("Guardar", (dialog, which) -> {

                for (int i = 0; i < cajas.length; i++) {
                    CheckBox checkBox = cajas[i];
                    if (checkBox.isChecked()) {
                        diasLaborables.add(i);
                    } else {
                        diasNoLaborables.add(i);
                    }
                }

                if (!diasLaborables.isEmpty()) {
                    if (!diasNoLaborables.contains(6)) {
                        diasNoLaborables.add(6);
                    }

                    Object[] arrayDiasLaborables = diasLaborables.toArray(new Object[0]);
                    Object[] arrayDiasNoLaborables = diasNoLaborables.toArray(new Object[0]);

                    if (arrayDiasLaborables.length > 0) {
                        // Actualiza solo si hay días laborables seleccionados
                        actualizar(usuarioID, arrayDiasLaborables, arrayDiasNoLaborables);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Debes seleccionar al menos un día laborable", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            DocumentReference drPersonal = FirebaseFirestore.getInstance().collection("Personal").document(usuarioID);

            drPersonal.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // Obtiene los días laborables
                    List<Object> diasLaborables = (List<Object>) documentSnapshot.get("dias_laborables");

                    // Carga el estado de cada día y actualiza los CheckBox
                    for (int i = 0; i < cajas.length; i++) {
                        CheckBox checkBox = cajas[i];
                        checkBox.setChecked(diasLaborables != null && diasLaborables.contains((long) (i)));
                    }
                }
            });
        }
    }

    private static void actualizar(String usuarioID, Object[] diasLaborables, Object[] diasNoLaborables) {
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("dias_laborables", Arrays.asList(diasLaborables));
        datosActualizados.put("dias_no_laborables", Arrays.asList(diasNoLaborables));

        FirebaseFirestore.getInstance().collection("Personal").document(usuarioID)
                .set(datosActualizados, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(e -> {

                });
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

            // Obtiene la URL de la imagen y subir al Storage
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
                        urlImagen = uri.toString();

                        // Mostrar imagen
                        Picasso.get().load(urlImagen).into(foto);

                        // Guarda la URL
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
        String usuarioID = obtenerId();

        if (!usuarioID.isEmpty()) {
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

                                // Obtener la URL de la imagen y mostrarla
                                String urlImagen = document.getString("Foto");
                                if (urlImagen != null && !urlImagen.isEmpty()) {
                                    Picasso.get().load(urlImagen).into(foto);
                                }
                            } else {
                                Toast.makeText(editar_perfilActivity.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Manejar el caso cuando no se puede obtener el ID del usuario
            Toast.makeText(editar_perfilActivity.this, "Error al obtener ID del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarCambios() {
        // Obtener los valores actuales
        String nuevoNombre = nombreET.getText().toString().trim();
        String nuevoApellido = apellidoET.getText().toString().trim();
        String nuevoCelular = celularET.getText().toString().trim();

        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("Nombre", nuevoNombre);
        datosActualizados.put("Apellido", nuevoApellido);
        datosActualizados.put("Telefono", nuevoCelular);


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

    private String obtenerId() {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("userId","");
    }

}