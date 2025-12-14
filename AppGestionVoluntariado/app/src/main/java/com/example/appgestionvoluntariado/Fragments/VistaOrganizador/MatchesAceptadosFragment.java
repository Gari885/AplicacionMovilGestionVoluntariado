package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appgestionvoluntariado.R;


public class MatchesAceptadosFragment extends Fragment {

    private Button volver;

    private TextView tabEnCurso, tabCompletados;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matches_aceptados, container, false);
        tabEnCurso = view.findViewById(R.id.tabOrgEnCurso);
        tabCompletados = view.findViewById(R.id.tabOrgCompletados);

        if (getArguments() != null){
            String tipo = getArguments().getString("TIPO_MATCH");

            if (tipo.equals("EN CURSO")){
                marcarPestañaEnCurso();
                
            }else if (tipo.equals("COMPLETADOS")){
                marcarPestañaCompletados();
            }
        }



        volver = view.findViewById(R.id.btnVolver);
        volver.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MatchesMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        tabEnCurso.setOnClickListener(v-> marcarPestañaEnCurso());
        tabCompletados.setOnClickListener(v-> marcarPestañaCompletados());



        return view;
    }

    private void marcarPestañaCompletados() {
        tabCompletados.setBackgroundResource(R.drawable.background_tab_selected);
        tabCompletados.setTextColor(Color.WHITE);

        tabEnCurso.setBackgroundResource(R.drawable.background_tab_unselected);
        tabEnCurso.setTextColor(Color.parseColor("#1A3B85"));
    }

    private void marcarPestañaEnCurso() {
        tabEnCurso.setBackgroundResource(R.drawable.background_tab_selected);
        tabEnCurso.setTextColor(Color.WHITE);

        tabCompletados.setBackgroundResource(R.drawable.background_tab_unselected);
        tabCompletados.setTextColor(Color.parseColor("#1A3B85"));
    }
}