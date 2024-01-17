package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.AlertDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import android.view.Gravity;

public class mainActivity extends AppCompatActivity {
    Button btn_cerrar, btn_cambiar, btn_editar;
    FirebaseAuth mAuth;
    TextView nombreTextView, apellidoTextView, emailTextView, celularTextView;
    String usuarioId;
    ImageView foto;
    TableLayout tabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        btn_cerrar = findViewById(R.id.cerrar);
        btn_cambiar = findViewById(R.id.cambiar);
        btn_editar = findViewById(R.id.editar);

        nombreTextView = findViewById(R.id.nombre);
        apellidoTextView = findViewById(R.id.apellido);
        emailTextView = findViewById(R.id.email);
        celularTextView = findViewById(R.id.celular);
        tabla = findViewById(R.id.tablaCitas);

        foto = findViewById(R.id.fotoEmpleado);

        cargarInformacion();
        cargarCitas();

        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_to_edit = new Intent(mainActivity.this, editar_perfilActivity.class);
                startActivity(main_to_edit);
            }
        });

        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                Intent main_to_log = new Intent(mainActivity.this, loginActivity.class);
                startActivity(main_to_log);
            }
        });

        btn_cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_to_pass = new Intent(mainActivity.this, contrasenaActivity.class);
                startActivity(main_to_pass);
            }
        });
    }

    private void cargarInformacion() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            usuarioId = usuario.getUid();
            DocumentReference userRef = db.collection("Personal").document(usuarioId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        String urlImagen = document.getString("Foto");
                        if (urlImagen != null && !urlImagen.isEmpty()) {
                            Picasso.get().load(urlImagen).into(foto);
                        }

                        String nombre = document.getString("Nombre");
                        String apellido = document.getString("Apellido");
                        String email = document.getString("Email");
                        String celular = document.getString("Telefono");

                        nombreTextView.setText("Nombre: " + nombre);
                        apellidoTextView.setText("Apellido: " + apellido);
                        emailTextView.setText("Email: " + email);
                        celularTextView.setText("Celular: " + celular);

                    }
                }
            });
        }
    }

    private void cargarCitas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Citas")
                .whereEqualTo("IDEmpleado", usuarioId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Llama a agregarFila y pasa el documento
                            agregarFila(document);
                        }
                    }
                });
    }

    private void agregarFila(QueryDocumentSnapshot document) {
        TableRow fila = new TableRow(this);

        // Extrayendo valores
        String servicio = document.getString("Titulo");
        String fecha = document.getString("Fecha");
        String hora = document.getString("Hora");
        String idUsuario = document.getString("IDUsuario");
        String costo = document.getString("Precio");
        String estado = document.getString("Estado");

        TextView servicioTextView = new TextView(this);
        servicioTextView.setText(servicio);
        servicioTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        servicioTextView.setGravity(Gravity.CENTER);
        fila.addView(servicioTextView);

        TextView fechaTextView = new TextView(this);
        fechaTextView.setText(fecha);
        fechaTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        fechaTextView.setGravity(Gravity.CENTER);
        fila.addView(fechaTextView);

        TextView horaTextView = new TextView(this);
        horaTextView.setText(hora);
        horaTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        horaTextView.setGravity(Gravity.CENTER);
        fila.addView(horaTextView);

        TextView costoTextView = new TextView(this);
        costoTextView.setText(costo);
        costoTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        costoTextView.setGravity(Gravity.CENTER);
        fila.addView(costoTextView);

        Button verButton = new Button(this);
        verButton.setText("Ver");
        verButton.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        verButton.setGravity(Gravity.CENTER);
        verButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pasar el documento a la función obtenerIdDocumentoCita
                obtenerIdDocumentoCita(document, servicio, fecha, hora, idUsuario, costo, estado);
            }
        });

        fila.addView(verButton);
        tabla.addView(fila);
    }

    private void obtenerIdDocumentoCita(QueryDocumentSnapshot document, String servicio, String fecha, String hora, String idUsuario, String costo, String estado) {
        // Obtener el ID del documento en la colección "Citas"
        String idDocumento = document.getId();

        // Ahora puedes usar este idDocumento para realizar actualizaciones o referencias específicas a esta cita
        verCitaController.mostrarDialogoCita(mainActivity.this, servicio, fecha, hora, idUsuario, costo, estado, idDocumento);
    }

    private static void actualizarEstadoCita(String idDocumento) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Actualizar el campo "Estado" a "cancelado" para la cita específica
        db.collection("Citas").document(idDocumento)
                .update("Estado", "Cancelado")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al actualizar el estado
                        // Puedes realizar acciones adicionales si es necesario
                    }
                });
    }

    public static class verCitaController {
        public static void mostrarDialogoCita(Context context, String servicio, String fecha, String hora, String idUsuario, String costo, String estado, String idDocumento) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            View view = LayoutInflater.from(context).inflate(R.layout.activity_cita_ver, null);
            // Propiedades de la vista
            Button btn_cancelar = view.findViewById(R.id.botonCancelar);
            EditText etServicio = view.findViewById(R.id.servicio);
            EditText etFecha = view.findViewById(R.id.fecha);
            EditText etHora = view.findViewById(R.id.hora);
            EditText etCliente = view.findViewById(R.id.cliente);
            EditText etCosto = view.findViewById(R.id.costo);
            EditText etEstado = view.findViewById(R.id.estado);
            final String[] correo = {""};

            etServicio.setText(servicio);
            etFecha.setText(fecha);
            etHora.setText(hora);

            DocumentReference userRef = db.collection("UsuariosLogin").document(idUsuario);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        String nombreUsuario = document.getString("Nombre");
                        String apellidoUsuario = document.getString("Apellido");
                        correo[0] = document.getString("Email");

                        etCliente.setText(nombreUsuario + " " + apellidoUsuario);
                    }
                }
            });

            etCosto.setText(costo);
            etEstado.setText(estado);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setTitle("Ver información de cita");
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            btn_cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarCitaController.mostrarEditarCita(context, correo[0], idDocumento);
                    alertDialog.dismiss();
                }
            });
        }
    }

    public static class editarCitaController {
        public static void mostrarEditarCita(Context context, String correo, String idDocumento) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_cita_cancelar, null);

            // Propiedades de la vista
            Button btn_enviar = view.findViewById(R.id.btnEnviarCorreo);
            TextInputEditText correoC = view.findViewById(R.id.correoCan);
            TextInputEditText asuntoC = view.findViewById(R.id.asuntoCan);
            TextInputEditText mensajeC = view.findViewById(R.id.mensajeCan);

            correoC.setText(correo);
            asuntoC.setText("Cancelación de cita");

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setTitle("Cancelación de cita");
            builder.setPositiveButton("Cerrar", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            btn_enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String correoDestino = correoC.getText().toString();
                    String asunto = asuntoC.getText().toString();
                    String msj = mensajeC.getText().toString();

                    // Crear instancia de la tarea asíncrona para enviar correo
                    enviarCorreo enviarCorreoTask = new enviarCorreo(context, new enviarCorreo.CorreoCallback() {
                        @Override
                        public void onCorreoEnviado(boolean enviado) {
                            if (enviado) {
                                Toast.makeText(context, "Correo enviado correctamente", Toast.LENGTH_SHORT).show();
                                // Actualizar el estado de la cita solo cuando el correo se ha enviado correctamente
                                actualizarEstadoCita(idDocumento);
                                alertDialog.dismiss();
                            } else {
                                // Manejar el caso donde el correo no se envió correctamente
                                Toast.makeText(context, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // Ejecutar la tarea asíncrona
                    enviarCorreoTask.execute(correoDestino, asunto, msj);
                }
            });
        }
    }
}