package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appgestionvoluntariado.Adapters.AdaptadorMatches;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;


public class MatchesAceptadosFragment extends Fragment {

    private Button volver;

    private TextView tabEnCurso, tabCompletados;

    private AdaptadorMatches adaptadorMatches;
    private RecyclerView recyclerView;

    private List<Match> matches = new ArrayList<Match>();
    private List<Match> matchesFiltrados = new ArrayList<Match>();
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
        recyclerView = view.findViewById(R.id.rvMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null){
            matches = (ArrayList<Match>) getArguments().getSerializable("CLAVE_LISTA");

            String tipo = getArguments().getString("TIPO_MATCH");

            if (tipo.equals("EN CURSO")){
                filtrarMatches("En Curso");

            }else if (tipo.equals("COMPLETADOS")){
                filtrarMatches("Completado");
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

    private void filtrarMatches(String estadoMatch) {
        if (estadoMatch.equals("En Curso")) {
            mostrarEnCurso();
        }else if (estadoMatch.equals("Completado")) {
            mostrarCompletados();
        }
    }


    private void mostrarEnCurso() {

        filtrarListaEnCurso();
        marcarPestañaEnCurso();
    }

    private void mostrarCompletados() {
        filtrarListaCompletados();
        marcarPestañaCompletados();

    }

    private void filtrarListaCompletados() {
        if (matches != null) {
            for (Match m : matches) {
                String estadoMatch = m.getEstado();
                if (estadoMatch.equalsIgnoreCase("Completado")) {
                    matchesFiltrados.add(m);
                }
            }
        }
    }


    private void filtrarListaEnCurso() {
        if (matches != null) {
            for (Match m : matches) {
                String estadoMatch = m.getEstado();
                if (estadoMatch.equalsIgnoreCase("En Curso")) {
                    matchesFiltrados.add(m);
                }
            }
        }
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