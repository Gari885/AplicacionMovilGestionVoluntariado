package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.MatchesAPIService;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchesMenuFragment extends Fragment {

    private Button pendientes;

    private Button completados;

    private Button enCurso;

    private List<Match> matches = new ArrayList<>();
    private List<Match> matchesFiltradas = new ArrayList<>();


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
        matches = DatosGlobales.getInstance().matches;




        // --- IR A PENDIENTES ---
        pendientes.setOnClickListener(v -> {
            filtrarPendientes();
            Bundle args = new Bundle();
            args.putSerializable("CLAVE_LISTA", (Serializable) matchesFiltradas);
            Fragment fragment = new MatchesPendientesFragment();
            fragment.setArguments(args);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                    .commit();
        });

        // --- IR A EN CURSO ---
        enCurso.setOnClickListener(v -> {
            Fragment fragment = new MatchesAceptadosFragment();

            Bundle args = new Bundle();
            args.putString("TIPO_MATCH", "EN CURSO");
            args.putSerializable("CLAVE_LISTA", (Serializable) matches);


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
            args.putSerializable("CLAVE_LISTA", (Serializable) matches);


            fragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });



        return view;
    }


    private void filtrarPendientes() {
        if (matches != null) {
            for (Match mat : matches) {
                String estadoMatch = mat.getEs();
                if (estadoMatch.equalsIgnoreCase("Pendiente")) {
                    matchesFiltradas.add(mat);
                }
            }
        }
    }
}