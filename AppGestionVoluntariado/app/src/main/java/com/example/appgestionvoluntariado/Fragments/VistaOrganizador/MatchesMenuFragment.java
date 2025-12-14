package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.R;

public class MatchesMenuFragment extends Fragment {

    private Button pendientes;

    private Button completados;

    private Button enCurso;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matches_menu, container, false);

        pendientes = view.findViewById(R.id.btnPendientes);
        enCurso = view.findViewById(R.id.btnEnCurso);
        completados = view.findViewById(R.id.btnCompletados);

        // 3. DARLES VIDA: Navegar a otros Fragments

        // --- IR A PENDIENTES ---
        pendientes.setOnClickListener(v -> {
            // "Oye Activity padre, cámbiame por el fragmento de Pendientes"
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MatchesPendientesFragment())
                    .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                    .commit();
        });

        // --- IR A EN CURSO ---
        enCurso.setOnClickListener(v -> {
            Fragment fragment = new MatchesAceptadosFragment();

            Bundle args = new Bundle();
            args.putString("TIPO_MATCH", "EN CURSO");

            fragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // --- IR A COMPLETADOS ---
        completados.setOnClickListener(v -> {

            Fragment fragment = new MatchesAceptadosFragment();

            Bundle args = new Bundle();
            args.putString("TIPO_MATCH", "COMPLETADOS");

            fragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}