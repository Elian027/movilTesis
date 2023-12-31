package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.TextUtils;
import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.TimePicker;
import java.util.Calendar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class fechaActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar;
    ImageView btn_atras;
    CheckBox caja_lunes, caja_martes, caja_miercoles, caja_jueves, caja_viernes, caja_sabado;

    EditText iLunes, iMartes, iMiercoles, iJueves, iViernes, iSabado;
    EditText fLunes, fMartes, fMiercoles, fJueves, fViernes, fSabado;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha);

        btn_guardar = findViewById(R.id.guardar);
        btn_cancelar = findViewById(R.id.cancelar);
        btn_atras = findViewById(R.id.atras);

        caja_lunes = findViewById(R.id.lunes);
        caja_martes = findViewById(R.id.martes);
        caja_miercoles = findViewById(R.id.miercoles);
        caja_jueves = findViewById(R.id.jueves);
        caja_viernes = findViewById(R.id.viernes);
        caja_sabado = findViewById(R.id.sabado);

        iLunes = findViewById(R.id.inicioLunes);
        iMartes = findViewById(R.id.inicioMartes);
        iMiercoles = findViewById(R.id.inicioMiercoles);
        iJueves = findViewById(R.id.inicioJueves);
        iViernes = findViewById(R.id.inicioViernes);
        iSabado = findViewById(R.id.inicioSabado);

        fLunes = findViewById(R.id.finLunes);
        fMartes = findViewById(R.id.finMartes);
        fMiercoles = findViewById(R.id.finMiercoles);
        fJueves = findViewById(R.id.finJueves);
        fViernes = findViewById(R.id.finViernes);
        fSabado = findViewById(R.id.finSabado);

        //Deshabilita los campos al iniciar en la pantalla
        iLunes.setEnabled(false);
        fLunes.setEnabled(false);
        iMartes.setEnabled(false);
        fMartes.setEnabled(false);
        iMiercoles.setEnabled(false);
        fMiercoles.setEnabled(false);
        iJueves.setEnabled(false);
        fJueves.setEnabled(false);
        iViernes.setEnabled(false);
        fViernes.setEnabled(false);
        iSabado.setEnabled(false);
        fSabado.setEnabled(false);

        iLunes.setOnClickListener(v -> mostrarReloj(iLunes));
        fLunes.setOnClickListener(v -> mostrarReloj(fLunes));
        iMartes.setOnClickListener(v -> mostrarReloj(iMartes));
        fMartes.setOnClickListener(v -> mostrarReloj(fMartes));
        iMiercoles.setOnClickListener(v -> mostrarReloj(iMiercoles));
        fMiercoles.setOnClickListener(v -> mostrarReloj(fMiercoles));
        iJueves.setOnClickListener(v -> mostrarReloj(iJueves));
        fJueves.setOnClickListener(v -> mostrarReloj(fJueves));
        iViernes.setOnClickListener(v -> mostrarReloj(iViernes));
        fViernes.setOnClickListener(v -> mostrarReloj(fViernes));
        iSabado.setOnClickListener(v -> mostrarReloj(iSabado));
        fSabado.setOnClickListener(v -> mostrarReloj(fSabado));

        caja_lunes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iLunes.setEnabled(isChecked);
            fLunes.setEnabled(isChecked);
            if (!isChecked) {
                iLunes.getText().clear();
                fLunes.getText().clear();
            }
        });

        caja_martes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iMartes.setEnabled(isChecked);
            fMartes.setEnabled(isChecked);
            if (!isChecked) {
                iMartes.getText().clear();
                fMartes.getText().clear();
            }
        });

        caja_miercoles.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iMiercoles.setEnabled(isChecked);
            fMiercoles.setEnabled(isChecked);
            if (!isChecked) {
                iMiercoles.getText().clear();
                fMiercoles.getText().clear();
            }
        });

        caja_jueves.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iJueves.setEnabled(isChecked);
            fJueves.setEnabled(isChecked);
            if (!isChecked) {
                iJueves.getText().clear();
                fJueves.getText().clear();
            }
        });

        caja_viernes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iViernes.setEnabled(isChecked);
            fViernes.setEnabled(isChecked);
            if (!isChecked) {
                iViernes.getText().clear();
                fViernes.getText().clear();
            }
        });

        caja_sabado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iSabado.setEnabled(isChecked);
            fSabado.setEnabled(isChecked);
            if (!isChecked) {
                iSabado.getText().clear();
                fSabado.getText().clear();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fecha_to_log = new Intent(fechaActivity.this, loginActivity.class);
                startActivity(fecha_to_log);
                finish();
            }
        });

        btn_atras.setOnClickListener(new View.OnClickListener() {
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

    private void mostrarAlerta(String titulo, String mensaje) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fechaActivity.this);
            builder.setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private Map<String, Boolean> verificarEstados() {
        Map<String, Boolean> estadosCheckBox = new HashMap<>();
        estadosCheckBox.put("lunes", caja_lunes.isChecked());
        estadosCheckBox.put("martes", caja_martes.isChecked());
        estadosCheckBox.put("miercoles", caja_miercoles.isChecked());
        estadosCheckBox.put("jueves", caja_jueves.isChecked());
        estadosCheckBox.put("viernes", caja_viernes.isChecked());
        estadosCheckBox.put("sabado", caja_sabado.isChecked());
        return estadosCheckBox;
    }

    private boolean guardarDias() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            String usuarioID = usuario.getUid();
            // Referencia al documento del usuario en Firestore
            final Map<String, Object> empleadoData = obtenerDiasSeleccionados();

            if (!verificarDias(empleadoData)) {
                mostrarAlerta("Error", "Debes seleccionar al menos un día");
                return false;
            }
          if (!verificar_campos_horas(empleadoData)) {
                mostrarAlerta("Error", "Los campos de hora inicio y fin no deben estar vacíos");
                return false;
            }

            empleadoData.put("fecha_trabajo", true);
            db.collection("Empleados")
                    .document(usuarioID)
                    .update(empleadoData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(fechaActivity.this, "Días seleccionados guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Navegar solo si no hay errores
                        Intent irMain = new Intent(fechaActivity.this, mainActivity.class);
                        startActivity(irMain);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        mostrarAlerta("Error", "Error al guardar los días seleccionados");
                    });
        }
        return true;
    }

    private Map<String, Object> obtenerDiasSeleccionados() {
        Map<String, Object> diasSeleccionados = new HashMap<>();
        agregarDiaSeleccionado(diasSeleccionados, "lunes", caja_lunes, iLunes, fLunes);
        agregarDiaSeleccionado(diasSeleccionados, "martes", caja_martes, iMartes, fMartes);
        agregarDiaSeleccionado(diasSeleccionados, "miercoles", caja_miercoles, iMiercoles, fMiercoles);
        agregarDiaSeleccionado(diasSeleccionados, "jueves", caja_jueves, iJueves, fJueves);
        agregarDiaSeleccionado(diasSeleccionados, "viernes", caja_viernes, iViernes, fViernes);
        agregarDiaSeleccionado(diasSeleccionados, "sabado", caja_sabado, iSabado, fSabado);

        if (btn_guardar.isPressed()) {
            diasSeleccionados.put("fecha_trabajo", true);
        }
        return diasSeleccionados;
    }

    private void agregarDiaSeleccionado(Map<String, Object> map, String key, CheckBox checkBox, EditText inicio, EditText fin) {
        if (checkBox.isChecked()) {
            map.put(key, true);
            String horaInicio = inicio.getText().toString();
            String horaFin = fin.getText().toString();
            if (!horaInicio.isEmpty()) {
                map.put("inicio" + key.substring(0, 1).toUpperCase() + key.substring(1), horaInicio);
            }
            if (!horaFin.isEmpty()) {
                map.put("fin" + key.substring(0, 1).toUpperCase() + key.substring(1), horaFin);
            }
        }
    }

    private boolean campoLleno(EditText editText) {
        // Obtiene el texto del EditText
        String texto = editText.getText().toString().trim();
        // Verifica si el texto está vacío
        return !TextUtils.isEmpty(texto);
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

    private boolean verificar_campos_horas(Map<String, Object> empleadoData) {
        Map<String, Boolean> estados = verificarEstados();
        Boolean estadoLunes = estados.get("lunes");
        Boolean estadoMartes = estados.get("martes");
        Boolean estadoMiercoles = estados.get("miercoles");
        Boolean estadoJueves = estados.get("jueves");
        Boolean estadoViernes = estados.get("viernes");
        Boolean estadoSabado = estados.get("sabado");
        if ((estadoLunes != null && estadoLunes) && (!campoLleno(iLunes) || !campoLleno(fLunes))) {
            return false;
        }
        if ((estadoMartes != null && estadoMartes) && (!campoLleno(iMartes) || !campoLleno(fMartes))) {
            return false;
        }
        if ((estadoMiercoles != null && estadoMiercoles) && (!campoLleno(iMiercoles) || !campoLleno(fMiercoles))) {
            return false;
        }
        if ((estadoJueves != null && estadoJueves) && (!campoLleno(iJueves) || !campoLleno(fJueves))) {
            return false;
        }
        if ((estadoViernes != null && estadoViernes) && (!campoLleno(iViernes) || !campoLleno(fViernes))) {
            return false;
        }
        if ((estadoSabado != null && estadoSabado) && (!campoLleno(iSabado) || !campoLleno(fSabado))) {
            return false;
        }
        return true;
    }

    private void mostrarReloj(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                    editText.setText(selectedTime);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

}