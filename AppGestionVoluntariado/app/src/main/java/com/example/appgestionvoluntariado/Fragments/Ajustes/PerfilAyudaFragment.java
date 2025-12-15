package com.example.appgestionvoluntariado.Fragments.Ajustes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.R;


public class PerfilAyudaFragment extends Fragment {

    private Button volver, contactar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_ayuda, container, false);

        volver = view.findViewById(R.id.btnVolver);
        contactar = view.findViewById(R.id.btnContactarEmail);


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilUsuarioFragment())
                        .commit();
            }
        });

        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logica para contactar con Irina o lo que sea falta por implementar preguntar irina
            }
        });

        return view;
    }
}