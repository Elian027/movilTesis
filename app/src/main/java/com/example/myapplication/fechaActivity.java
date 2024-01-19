package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fechaActivity extends AppCompatActivity {
    Button btn_guardar;
    CheckBox caja_lunes, caja_martes, caja_miercoles, caja_jueves, caja_viernes, caja_sabado;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha);

        btn_guardar = findViewById(R.id.guardar);

        caja_lunes = findViewById(R.id.lunes);
        caja_martes = findViewById(R.id.martes);
        caja_miercoles = findViewById(R.id.miercoles);
        caja_jueves = findViewById(R.id.jueves);
        caja_viernes = findViewById(R.id.viernes);
        caja_sabado = findViewById(R.id.sabado);

        usuarioID = obtenerId();

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDias();
            }
        });
        cargarDias();
    }

    private void mostrarAlerta(String titulo, String mensaje, Runnable onAceptar) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fechaActivity.this);
            builder.setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        if (onAceptar != null) {
                            onAceptar.run();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void mostrarAlertaX(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, null);
    }

    private void mostrarAlertaExito() {
        mostrarAlerta("Éxito", "Los días seleccionados se han guardado correctamente", () -> {
            Intent irMain = new Intent(fechaActivity.this, mainActivity.class);
            startActivity(irMain);
        });
    }

    private void cargarDias() {
        // Obtener los días desde la base de datos y marcar las CheckBox correspondientes
        if (usuarioID != null) {

            DocumentReference drPersonal = db.collection("Personal").document(usuarioID);
            drPersonal.get().addOnSuccessListener(documentSnapshot -> {
                // Obtener el array de días laborables desde el documento
                List<Integer> diasLaborables = documentSnapshot.get("dias_laborables", List.class);

                // Verificar si la lista de días laborables no es nula
                if (diasLaborables != null) {
                    // Iterar sobre los días laborables y marcar los CheckBox correspondientes
                    for (Integer diaLaborable : diasLaborables) {
                        marcarCheckBoxSegunDia(diaLaborable);
                    }
                }
            });
        }
    }

    private void marcarCheckBoxSegunDia(int diaLaborable) {
        switch (diaLaborable) {
            case 0:
                caja_lunes.setChecked(true);
                break;
            case 1:
                caja_martes.setChecked(true);
                break;
            case 2:
                caja_miercoles.setChecked(true);
                break;
            case 3:
                caja_jueves.setChecked(true);
                break;
            case 4:
                caja_viernes.setChecked(true);
                break;
            case 5:
                caja_sabado.setChecked(true);
                break;
            default:
                break;
        }
    }

    private void guardarDias() {
        if (usuarioID != null) {

            Map<String, Object> empleadoData = obtenerDiasSeleccionados();

            if (!verificarDias(empleadoData)) {
                mostrarAlertaX("Error", "Debes seleccionar al menos un día");
                return;
            }
            Log.d("GuardarDias", "Días laborables antes de actualizar: " + empleadoData.get("dias_laborables"));

            // Actualizar la base de datos con los nuevos valores
            actualizar(usuarioID, empleadoData);
        }
    }

    private Map<String, Object> obtenerDiasSeleccionados() {
        Map<String, Object> diasSeleccionados = new HashMap<>();

        List<Integer> diasLaborables = new ArrayList<>();
        List<Integer> diasNoLaborables = new ArrayList<>();

        if (caja_lunes.isChecked()) {
            diasLaborables.add(0);
        } else {
            diasNoLaborables.add(0);
        }

        if (caja_martes.isChecked()) {
            diasLaborables.add(1);
        } else {
            diasNoLaborables.add(1);
        }

        if (caja_miercoles.isChecked()) {
            diasLaborables.add(2);
        } else {
            diasNoLaborables.add(2);
        }

        if (caja_jueves.isChecked()) {
            diasLaborables.add(3);
        } else {
            diasNoLaborables.add(3);
        }
        if (caja_viernes.isChecked()) {
            diasLaborables.add(4);
        } else {
            diasNoLaborables.add(4);
        }

        if (caja_sabado.isChecked()) {
            diasLaborables.add(5);
        } else {
            diasNoLaborables.add(5);
        }

        diasNoLaborables.add(6);

        diasSeleccionados.put("dias_laborables", diasLaborables);
        diasSeleccionados.put("dias_no_laborables", diasNoLaborables);

        diasSeleccionados.put("fecha_trabajo", true);

        Log.d("ObtenerDias", "Días laborables antes de devolver el mapa: " + diasLaborables);

        return diasSeleccionados;
    }

    private void actualizar(String usuarioID, Map<String, Object> empleadoData) {
        DocumentReference drPersonal = db.collection("Personal").document(usuarioID);

        drPersonal.update(empleadoData)
                .addOnSuccessListener(aVoid -> mostrarAlertaExito())
                .addOnFailureListener(e -> mostrarAlertaX("Error", "Error al actualizar los datos: " + e.getMessage()));
    }

    private boolean verificarDias(Map<String, Object> empleadoData) {
        List<Integer> diasLaborables = (List<Integer>) empleadoData.get("dias_laborables");
        Log.d("VerificarDias", "Días laborables antes de la verificación: " + diasLaborables);

        return (diasLaborables != null && !diasLaborables.isEmpty());
    }

    private String obtenerId() {
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return preferences.getString("userId","");
    }
}