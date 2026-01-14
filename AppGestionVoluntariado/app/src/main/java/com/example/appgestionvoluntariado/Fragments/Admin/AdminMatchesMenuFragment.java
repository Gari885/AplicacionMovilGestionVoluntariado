package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdminMatchesMenuFragment extends Fragment {

    private Button btnPending;
    private Button btnCompleted;
    private Button btnInProgress;

    private List<Match> matches = new ArrayList<>();
    private List<Match> filteredMatches = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_matches_menu, container, false);

        btnPending = view.findViewById(R.id.btnPending);
        btnInProgress = view.findViewById(R.id.btnInProgress);
        btnCompleted = view.findViewById(R.id.btnCompleted);
        matches = GlobalData.getInstance().matches;

        // --- GO TO PENDING ---
        btnPending.setOnClickListener(v -> {
            filterPending();
            Bundle args = new Bundle();
            args.putSerializable("KEY_LIST", (Serializable) filteredMatches);
            Fragment fragment = new AdminMatchesPendingFragment();
            fragment.setArguments(args);
            
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null) 
                    .commit();
        });

        // --- GO TO IN PROGRESS ---
        btnInProgress.setOnClickListener(v -> {
            Fragment fragment = new AdminMatchesAcceptedFragment();
            Bundle args = new Bundle();
            args.putString("MATCH_TYPE", "EN CURSO");
            args.putSerializable("KEY_LIST", (Serializable) matches);
            fragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // --- GO TO COMPLETED ---
        btnCompleted.setOnClickListener(v -> {
            Fragment fragment = new AdminMatchesAcceptedFragment();
            Bundle args = new Bundle();
            args.putString("MATCH_TYPE", "COMPLETADOS");
            args.putSerializable("KEY_LIST", (Serializable) matches);
            fragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void filterPending() {
        filteredMatches.clear();
        if (matches != null) {
            for (Match mat : matches) {
                if ("Pendiente".equalsIgnoreCase(mat.getStatus())) {
                    filteredMatches.add(mat);
                }
            }
        }
    }
}
