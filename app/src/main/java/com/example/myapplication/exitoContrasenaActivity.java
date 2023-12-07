package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class exitoContrasenaActivity extends AppCompatActivity {
    Button btn_continuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exito_contrasena);
        btn_continuar = findViewById(R.id.continuar);

        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ex_to_main = new Intent(exitoContrasenaActivity.this, mainActivity.class);
                startActivity(ex_to_main);
                finish();
            }
        });


    }

}