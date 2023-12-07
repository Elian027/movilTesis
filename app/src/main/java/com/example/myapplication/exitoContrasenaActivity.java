package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class exitoContrasenaActivity extends AppCompatActivity {
    Button btn_continuar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exito_contrasena);
        btn_continuar = findViewById(R.id.continuar);
        mAuth = FirebaseAuth.getInstance();

        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent ex_to_login = new Intent(exitoContrasenaActivity.this, loginActivity.class);
                startActivity(ex_to_login);
                finish();
            }
        });


    }

}