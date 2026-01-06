package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.Adapters.AdaptadorMatches;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class MatchesPendientesFragment extends Fragment {


    private Button volver;

    private RecyclerView recyclerView;

    private List<Match> matchesMostrar = new ArrayList<>();

    private AdaptadorMatches adaptadorMatches;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matches_pendientes, container, false);
        recyclerView = view.findViewById(R.id.rvMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            matchesMostrar =  (ArrayList<Match>) getArguments().getSerializable("CLAVE_LISTA");
        }

        if (matchesMostrar != null) {
            mostrarMatches();
        }


        volver = view.findViewById(R.id.btnVolver);
        volver.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MatchesMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void mostrarMatches() {
        if (adaptadorMatches == null){
            adaptadorMatches = new AdaptadorMatches(matchesMostrar,false);
            recyclerView.setAdapter(adaptadorMatches);
        }else {
            adaptadorMatches.notifyDataSetChanged();
        }
    }
}