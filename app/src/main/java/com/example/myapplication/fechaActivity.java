package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class fechaActivity extends AppCompatActivity {
    Button btn_guardar, btn_cancelar;
    ImageView btn_atras;
    CheckBox caja_lunes, caja_martes, caja_miercoles, caja_jueves, caja_viernes, caja_sabado;
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
                guardarDiasSeleccionados();
                Intent irMain = new Intent(fechaActivity.this, mainActivity.class);
                startActivity(irMain);
                finish();
            }
        });
    }

    private void guardarDiasSeleccionados() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            String usuarioID = usuario.getUid();
            // Referencia al documento del usuario en Firestore
            final Map<String, Object> empleadoData = obtenerDiasSeleccionados();

            // Actualizar solo los campos necesarios en Firestore
            db.collection("Empleados")
                    .document(usuarioID)
                    .update(empleadoData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(fechaActivity.this, "Días seleccionados guardados correctamente", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(fechaActivity.this, "Error al guardar los días seleccionados", Toast.LENGTH_SHORT).show());
        }
    }

    private Map<String, Object> obtenerDiasSeleccionados() {
        Map<String, Object> diasSeleccionados = new HashMap<>();
        diasSeleccionados.put("lunes", caja_lunes.isChecked());
        diasSeleccionados.put("martes", caja_martes.isChecked());
        diasSeleccionados.put("miercoles", caja_miercoles.isChecked());
        diasSeleccionados.put("jueves", caja_jueves.isChecked());
        diasSeleccionados.put("viernes", caja_viernes.isChecked());
        diasSeleccionados.put("sabado", caja_sabado.isChecked());

        if (btn_guardar.isPressed()) {
            diasSeleccionados.put("fecha_trabajo", true);
        }

        return diasSeleccionados;
    }

}
