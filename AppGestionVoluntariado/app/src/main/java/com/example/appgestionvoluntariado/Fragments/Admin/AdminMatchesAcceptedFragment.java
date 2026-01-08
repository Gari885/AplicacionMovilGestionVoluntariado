package com.example.appgestionvoluntariado.Fragments.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.MatchesAdapter;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;

public class AdminMatchesAcceptedFragment extends Fragment {

    private Button btnBack;
    private TextView tabInProgress, tabCompleted;
    private MatchesAdapter matchesAdapter;
    private RecyclerView recyclerView;

    private List<Match> matches = new ArrayList<>();
    private List<Match> filteredMatches = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_matches_accepted, container, false);
        tabInProgress = view.findViewById(R.id.tabStatusInProgress);
        tabCompleted = view.findViewById(R.id.tabStatusCompleted);
        recyclerView = view.findViewById(R.id.rvMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null){
            matches = (ArrayList<Match>) getArguments().getSerializable("KEY_LIST");
            String type = getArguments().getString("MATCH_TYPE");

            if ("EN CURSO".equals(type)){
                filterMatches("En Curso");
            } else if ("COMPLETADOS".equals(type)){
                filterMatches("Completado");
            }
        }

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AdminMatchesMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        tabInProgress.setOnClickListener(v -> showInProgress());
        tabCompleted.setOnClickListener(v -> showCompleted());

        return view;
    }

    private void filterMatches(String statusMatch) {
        if ("En Curso".equals(statusMatch)) {
            showInProgress();
        } else if ("Completado".equals(statusMatch)) {
            showCompleted();
        }
    }

    private void showInProgress() {
        filterListInProgress();
        updateTabsVisuals(true);
    }

    private void showCompleted() {
        filterListCompleted();
        updateTabsVisuals(false);
    }

    private void filterListCompleted() {
        filteredMatches.clear();
        if (matches != null) {
            for (Match m : matches) {
                if ("Completado".equalsIgnoreCase(m.getStatus())) {
                    filteredMatches.add(m);
                }
            }
        }
        updateAdapter();
    }

    private void filterListInProgress() {
        filteredMatches.clear();
        if (matches != null) {
            for (Match m : matches) {
                if ("En Curso".equalsIgnoreCase(m.getStatus())) {
                    filteredMatches.add(m);
                }
            }
        }
        updateAdapter();
    }

    private void updateAdapter() {
        if (matchesAdapter == null) {
            matchesAdapter = new MatchesAdapter(filteredMatches, true);
            recyclerView.setAdapter(matchesAdapter);
        } else {
            // Need to recreate or update logic if different? Default Adapter works for list updates
            // But we might need to notify change.
            // MatchesAdapter expects `matches` list. Since we passed `filteredMatches` in constructor,
            // referencing the same object and clearing/adding works if we didn't reassign filteredMatches.
            // But here I did `filteredMatches.clear()`.
            // So notifyDataSetChanged works.
            matchesAdapter.notifyDataSetChanged();
            // Actually, if the adapter reference to lista is the same object, it works.
            // But here `filteredMatches` is my local list.
            // If I passed it to constructor, it's fine.
            // Wait, previous code `new MatchesAdapter(filteredMatches, true)` passed the reference.
            // So if I modify `filteredMatches` logic above, it affects adapter.
            
            // Re-instantiating adapter is also safe for fragments logic usually, though less efficient.
            // I'll stick to:
             matchesAdapter = new MatchesAdapter(filteredMatches, true);
             recyclerView.setAdapter(matchesAdapter);
        }
    }

    private void updateTabsVisuals(boolean isInProgress) {
        if (isInProgress) {
            tabInProgress.setBackgroundResource(R.drawable.background_tab_selected);
            tabInProgress.setTextColor(Color.WHITE);
            tabCompleted.setBackgroundResource(R.drawable.background_tab_unselected);
            tabCompleted.setTextColor(Color.parseColor("#1A3B85"));
        } else {
            tabCompleted.setBackgroundResource(R.drawable.background_tab_selected);
            tabCompleted.setTextColor(Color.WHITE);
            tabInProgress.setBackgroundResource(R.drawable.background_tab_unselected);
            tabInProgress.setTextColor(Color.parseColor("#1A3B85"));
        }
    }
}
