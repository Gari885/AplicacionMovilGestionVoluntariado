package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.StatAdapter;
import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;

public class OrgDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private StatAdapter statAdapter;

    private ArrayList<Stat> stats;

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_org_dashboard);

        recyclerView = findViewById(R.id.statsRecycler);

        stats = new ArrayList<>();
        stats.add(new Stat("VOLUNTARIOS", 2, "+2 Pendientes", "voluntarios"));
        stats.add(new Stat("ORGANIZACIONES", 2, "+2 Pendientes", "organizacion"));
        stats.add(new Stat("TOTAL MATCHES", 2, "", "matches"));
        stats.add(new Stat("PENDIENTES", 4, "", "pendientes"));

        statAdapter = new StatAdapter(stats);

        recyclerView.setAdapter(statAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        logo = findViewById(R.id.ivLogo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrgDashboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}