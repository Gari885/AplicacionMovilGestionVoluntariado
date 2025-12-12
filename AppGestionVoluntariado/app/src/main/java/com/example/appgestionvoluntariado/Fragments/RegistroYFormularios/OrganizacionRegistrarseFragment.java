package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.R;

public class OrganizacionRegistrarseFragment extends Fragment {

    private Button registrado;

    private Button volver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrarse_organizacion, container, false);

        registrado = view.findViewById(R.id.btnRegistrar);

        registrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarFormulario()){
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerFragments, new LogInFragment())
                            .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                            .commit();
                }

            }
        });

        volver = view.findViewById(R.id.btnVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new MenuRegistrarseFragment())
                        .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                        .commit();

            }
        });

        return view;
    }

    private boolean verificarFormulario() {
        return true;
    }
}