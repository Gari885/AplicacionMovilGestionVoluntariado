package com.example.appgestionvoluntariado.Fragments.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.MatchesAdapter;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Request.StatusRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.InscriptionsService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMatchesListFragment extends Fragment {

    private TextView tabInProgress, tabCompleted;
    private RecyclerView recyclerView;
    private MatchesAdapter adapter;
    private View loadingLayout;
    private EditText etSearch;
    private List<Match> fullList = new ArrayList<>();


    private ImageView logoSpinner;
    private Animation rotateAnimation;
    private InscriptionsService inscriptionsService;

    private FloatingActionButton fabAddMatch;

    private String currentFilter = "PENDIENTE"; // PENDIENTE o EN CURSO / CONFIRMADO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_matches_list, container, false);

        initViews(v);
        setupTabs();
        setupSearch();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabsVisuals(currentFilter.equals("PENDIENTE"));
    }

    public void initViews(View v){
        loadingLayout = v.findViewById(R.id.layoutLoading);
        tabInProgress = v.findViewById(R.id.tabStatusPending);
        tabCompleted = v.findViewById(R.id.tabStatusCompleted);
        etSearch = v.findViewById(R.id.etSearchMatch);
        recyclerView = v.findViewById(R.id.rvMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inscriptionsService = APIClient.getInscriptionService();
        fabAddMatch = v.findViewById(R.id.fabAddMatch);
        fabAddMatch.setVisibility(View.INVISIBLE);
        fabAddMatch.bringToFront();
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        logoSpinner.startAnimation(rotateAnimation);
    }
    private void setupTabs() {
        tabInProgress.setOnClickListener(v -> {
            currentFilter = "PENDIENTE"; // Mostramos primero los que hay que validar
            updateTabsVisuals(true);
            loadMatches();
        });

        tabCompleted.setOnClickListener(v -> {
            currentFilter = "CONFIRMADO"; // O "COMPLETADO" según prefieras
            updateTabsVisuals(false);
            loadMatches();
        });

        fabAddMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.admin_fragment_container, new AdminCreateMatchFragment()).
                        addToBackStack(null).commit();
            }
        });
    }

    private void loadMatches() {
        loadingLayout.setVisibility(View.VISIBLE);
        if (fabAddMatch != null) fabAddMatch.setVisibility(View.INVISIBLE);
        
        inscriptionsService.getMatches(currentFilter.toUpperCase())
                .enqueue(new Callback<List<Match>>() {
                    @Override
                    public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                        loadingLayout.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            fabAddMatch.setVisibility(View.VISIBLE);
                            fullList = response.body();
                            updateAdapter(fullList);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Match>> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapter(List<Match> list) {
        adapter = new MatchesAdapter(list, new MatchesAdapter.OnMatchActionListener() {
            @Override public void onAccept(Match m) { updateMatchStatus(m, "confirmado"); }
            @Override public void onReject(Match m) { updateMatchStatus(m, "rechazado"); }
            @Override public void onFinish(Match m) { updateMatchStatus(m, "finalizado"); }
            @Override public void onCancel(Match m) { updateMatchStatus(m, "rechazado"); }
        });
        recyclerView.setAdapter(adapter);
    }

    private void updateMatchStatus(Match match, String newStatus) {
        loadingLayout.setVisibility(View.VISIBLE);
       inscriptionsService.updateStatus(match.getRegistrationId(), new StatusRequest(newStatus))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            com.example.appgestionvoluntariado.Utils.StatusHelper.showStatus(getContext(), "Éxito", "Match " + newStatus, false);
                            loadMatches();
                        }
                        else {
                            loadingLayout.setVisibility(View.GONE);
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                                com.example.appgestionvoluntariado.Utils.StatusHelper.showStatus(getContext(), "Error " + response.code(), errorBody, true);
                            } catch (Exception e) {
                                com.example.appgestionvoluntariado.Utils.StatusHelper.showStatus(getContext(), "Error", "Fallo al procesar respuesta", true);
                            }
                        }
                    }
                    @Override public void onFailure(Call<Void> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        com.example.appgestionvoluntariado.Utils.StatusHelper.showStatus(getContext(), "Error de Conexión", t.getMessage(), true);
                    }
                });
    }

    private void updateTabsVisuals(boolean isFirstTab) {
        int selected = R.drawable.background_tab_selected;
        int unselected = R.drawable.background_tab_unselected;
        int white = Color.WHITE;
        int blue = Color.parseColor("#1A3B85");

        tabInProgress.setBackgroundResource(isFirstTab ? selected : unselected);
        tabInProgress.setTextColor(isFirstTab ? white : blue);
        tabCompleted.setBackgroundResource(isFirstTab ? unselected : selected);
        tabCompleted.setTextColor(isFirstTab ? blue : white);
    }
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String text) {
        List<Match> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();
        for (Match m : fullList) {
            String volName = m.getVolunteerName() != null ? m.getVolunteerName().toLowerCase() : "";
            String actName = m.getActivityName() != null ? m.getActivityName().toLowerCase() : "";
            
            if (volName.contains(query) || actName.contains(query)) {
                filtered.add(m);
            }
        }
        updateAdapter(filtered);
    }
}