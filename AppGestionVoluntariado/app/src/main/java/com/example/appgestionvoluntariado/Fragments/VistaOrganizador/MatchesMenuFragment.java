package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.MatchesAPIService;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;

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

    private MatchesAPIService apiService;



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

        apiService = APIClient.getMAtchesAPIService();
        Call<List<Match>> call = apiService.getMatches();
        call.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful()){
                    matches = response.body();
                }else {
                    Log.e("API", "Error: " + response.code());

                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e("API", "Error de conexión", t);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // 3. DARLES VIDA: Navegar a otros Fragments

        // --- IR A PENDIENTES ---
        pendientes.setOnClickListener(v -> {
            // "Oye Activity padre, cámbiame por el fragmento de Pendientes"
            filtrarLista("Pendiente");
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

    private void filtrarLista(String estado) {
        if (estado.equals("Pendiente")) {

        }
        //if (estado.equals(""))
    }
}