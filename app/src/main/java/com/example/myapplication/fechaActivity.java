package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class fechaActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar;
    CheckBox caja_lunes, caja_martes, caja_miercoles, caja_jueves, caja_viernes, caja_sabado;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha);

        btn_guardar = findViewById(R.id.guardar);
        btn_cancelar = findViewById(R.id.cancelar);

        caja_lunes = findViewById(R.id.lunes);
        caja_martes = findViewById(R.id.martes);
        caja_miercoles = findViewById(R.id.miercoles);
        caja_jueves = findViewById(R.id.jueves);
        caja_viernes = findViewById(R.id.viernes);
        caja_sabado = findViewById(R.id.sabado);

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fecha_to_log = new Intent(fechaActivity.this, loginActivity.class);
                startActivity(fecha_to_log);
                finish();
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDias();
            }
        });
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
            finish();
        });
    }

    private boolean guardarDias() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            String usuarioID = usuario.getUid();

            final Map<String, Object> empleadoData = obtenerDiasSeleccionados();

            if (!verificarDias(empleadoData)) {
                mostrarAlertaX("Error", "Debes seleccionar al menos un día");
                return false;
            }
            empleadoData.put("fecha_trabajo", true);
            db.collection("Personal")
                    .document(usuarioID)
                    .update(empleadoData)
                    .addOnSuccessListener(aVoid -> {
                        mostrarAlertaExito();
                    });
        }
        return true;
    }

    private Map<String, Object> obtenerDiasSeleccionados() {
        Map<String, Object> diasSeleccionados = new HashMap<>();
        agregarDiaSeleccionado(diasSeleccionados, "lunes", caja_lunes);
        agregarDiaSeleccionado(diasSeleccionados, "martes", caja_martes);
        agregarDiaSeleccionado(diasSeleccionados, "miercoles", caja_miercoles);
        agregarDiaSeleccionado(diasSeleccionados, "jueves", caja_jueves);
        agregarDiaSeleccionado(diasSeleccionados, "viernes", caja_viernes);
        agregarDiaSeleccionado(diasSeleccionados, "sabado", caja_sabado);
        return diasSeleccionados;
    }

    private void agregarDiaSeleccionado(Map<String, Object> map, String key, CheckBox checkBox) {
        if (checkBox.isChecked()) {
            map.put(key, true);
        }
    }

    private boolean verificarDias(Map<String, Object> empleadoData) {
        // Verificar al menos un día seleccionado
        for (String key : empleadoData.keySet()) {
            if (key.equals("lunes") || key.equals("martes") || key.equals("miercoles") ||
                    key.equals("jueves") || key.equals("viernes") || key.equals("sabado")) {
                return true;
            }
        }
        return false;
    }
}