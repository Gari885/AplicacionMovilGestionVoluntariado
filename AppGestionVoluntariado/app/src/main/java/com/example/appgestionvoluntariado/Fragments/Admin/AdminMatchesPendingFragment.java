package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.MatchesAdapter;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;

public class AdminMatchesPendingFragment extends Fragment {

    private Button btnBack;
    private RecyclerView recyclerView;
    private List<Match> matchesToShow = new ArrayList<>();
    private MatchesAdapter matchesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_matches_pending, container, false);
        recyclerView = view.findViewById(R.id.rvMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            matchesToShow = (ArrayList<Match>) getArguments().getSerializable("KEY_LIST");
        }

        if (matchesToShow != null) {
            showMatches();
        }

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AdminMatchesMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void showMatches() {
        if (matchesAdapter == null){
            matchesAdapter = new MatchesAdapter(matchesToShow, false);
            recyclerView.setAdapter(matchesAdapter);
        } else {
            matchesAdapter.notifyDataSetChanged();
        }
    }
}
