package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.EscaparateFragment;
import com.example.appgestionvoluntariado.Fragments.RegistroYFormularios.LogInFragment;
import com.example.appgestionvoluntariado.R;

public class MainActivity extends AppCompatActivity {
    private Button botonlogIn;
    private ImageView logoImagen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        botonlogIn = findViewById(R.id.logIn);
        botonlogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentSeleccionado = null;
                fragmentSeleccionado = new LogInFragment();
                if (fragmentSeleccionado != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.containerFragments, fragmentSeleccionado)
                            .commit();
                }
            }
        });

        logoImagen = findViewById(R.id.ivLogo);
        logoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new EscaparateFragment()) // <--- Aquí cargas el Dashboard
                        .commit();
            }
        });




        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerFragments, new LogInFragment()) // <--- Aquí cargas el Dashboard
                    .commit();
        }

    }
}