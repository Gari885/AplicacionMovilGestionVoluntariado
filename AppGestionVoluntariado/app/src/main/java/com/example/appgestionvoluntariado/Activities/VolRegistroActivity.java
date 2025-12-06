package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appgestionvoluntariado.R;

public class VolRegistroActivity extends AppCompatActivity {

    private ImageView logo;

    private TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vol_registro);

        logo = (ImageView) findViewById(R.id.ivLogo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VolRegistroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        titulo = findViewById(R.id.tvTitulo);

        Bundle extras = getIntent().getExtras();
        String tipo = "";
        if (extras != null){
            tipo = extras.get("tipo").toString();
        }
        if (tipo != ""){
            titulo.setText("Añadir Voluntario");
        }
    }
}