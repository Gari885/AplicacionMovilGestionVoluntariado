package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.StatAdapter;
import com.example.appgestionvoluntariado.Adapters.VolunteerAdapter;
import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;

public class OrgVoluntariosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private VolunteerAdapter volunteerAdapter;

    private ArrayList<Voluntario> voluntarios;

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_org_voluntarios);


        recyclerView = findViewById(R.id.rvVoluntarios);

        voluntarios = new ArrayList<>();
        voluntarios.add(new Voluntario("Ana Garcia", "ana.garcia@gmail.com"));
        voluntarios.add(new Voluntario("Pedro", "pedro@gmail.com"));
        voluntarios.add(new Voluntario("Miguel Jimenez", "miguelito@gmail.com"));
        voluntarios.add(new Voluntario("Maria", "maria@gmail.com"));


        volunteerAdapter = new VolunteerAdapter(voluntarios);

        recyclerView.setAdapter(volunteerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logo = findViewById(R.id.ivLogo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrgVoluntariosActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}