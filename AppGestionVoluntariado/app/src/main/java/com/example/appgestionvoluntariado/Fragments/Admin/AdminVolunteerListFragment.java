package com.example.appgestionvoluntariado.Fragments.Admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.VolunteerAdapter;
import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVolunteerListFragment extends Fragment {

    private RecyclerView rvVolunteers;
    private EditText etSearch;
    private TextView tabPending, tabAccepted;
    private View loadingLayout;

    private List<Volunteer> fullList = new ArrayList<>();
    private VolunteerAdapter adapter;
    private String currentStatus = "PENDIENTE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Asegúrate de que este sea el nombre de tu XML de fragmento
        View v = inflater.inflate(R.layout.fragment_admin_volunteer_list, container, false);

        // Inicialización
        rvVolunteers = v.findViewById(R.id.rvVolunteers);
        rvVolunteers.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearch = v.findViewById(R.id.etSearchVolunteer);
        tabPending = v.findViewById(R.id.tabStatusPending);
        tabAccepted = v.findViewById(R.id.tabStatusAccepted);
        loadingLayout = v.findViewById(R.id.layoutLoading);

        setupTabs();
        setupSearch();
        fetchVolunteers();

        return v;
    }

    private void setupTabs() {
        tabPending.setOnClickListener(v -> {
            currentStatus = "PENDIENTE";
            updateTabUI(tabPending, tabAccepted);
            fetchVolunteers();
        });

        tabAccepted.setOnClickListener(v -> {
            currentStatus = "ACTIVO";
            updateTabUI(tabAccepted, tabPending);
            fetchVolunteers();
        });
    }

    private void updateTabUI(TextView selected, TextView unselected) {
        selected.setBackgroundResource(R.drawable.background_tab_selected);
        selected.setTextColor(Color.WHITE);
        unselected.setBackgroundResource(R.drawable.background_tab_unselected);
        unselected.setTextColor(Color.parseColor("#1A3B85"));
    }

    private void fetchVolunteers() {
        loadingLayout.setVisibility(View.VISIBLE);
        APIClient.getVolunteerService().getVolunteers(currentStatus)
                .enqueue(new Callback<List<Volunteer>>() {
                    @Override
                    public void onResponse(Call<List<Volunteer>> call, Response<List<Volunteer>> response) {
                        loadingLayout.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            fullList = response.body();
                            updateAdapter(fullList);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Volunteer>> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error al cargar voluntarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapter(List<Volunteer> list) {
        if (adapter == null) {
            adapter = new VolunteerAdapter(list, new VolunteerAdapter.OnVolunteerActionListener() {
                @Override
                public void onAccept(Volunteer volunteer) {
                    processStatusChange(volunteer,new StatusRequest("aprobado"));
                }

                @Override
                public void onReject(Volunteer volunteer) {
                    processStatusChange(volunteer,new StatusRequest("rechazado"));
                }

                @Override
                public void onDelete(Volunteer volunteer) {
                    showConfirmDeleteDialog(volunteer);
                }

                @Override
                public void onDetails(Volunteer volunteer) {
                    showVolunteerDetails(volunteer);
                }
            });
            rvVolunteers.setAdapter(adapter);
        } else {
            adapter.updateList(list);
        }
    }

    private void processStatusChange(Volunteer vol, StatusRequest request) {
        loadingLayout.setVisibility(View.VISIBLE);
        // Usamos el token de Firebase, no enviamos identificadores en la URL/Body
        APIClient.getVolunteerService().updateStatus(vol.getDni(),request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            fetchVolunteers(); // Refrescamos la lista completa
                        } else {
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error en el cambio de estado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showConfirmDeleteDialog(Volunteer volunteer) {
        new AlertDialog.Builder(getContext())
                .setTitle("¿Dar de baja?")
                .setMessage("¿Estás seguro de que quieres desactivar a " + volunteer.getFullName() + "?")
                .setPositiveButton("Confirmar", (dialog, which) -> processStatusChange(volunteer,new StatusRequest("rechazado")))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showVolunteerDetails(Volunteer v) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_volunteer_info, null);
        bottomSheetDialog.setContentView(view);

        // Referencias
        ChipGroup cgHabilidades = view.findViewById(R.id.chipGroupHabilidades);
        TextView tvDisp = view.findViewById(R.id.tvDisponibilidadVal);
        Button btnCerrar = view.findViewById(R.id.btnCerrarPopup);


        // TRUCO: Quitar el fondo por defecto del contenedor para que se vean las esquinas redondeadas
        View parent = (View) view.getParent();
        parent.setBackgroundResource(android.R.color.transparent);

        // Crear chips dinámicamente para que no se corten
        if (v.getSkills() != null) {
            for (String skill : v.getSkills().split(",")) {
                Chip chip = new Chip(getContext());
                chip.setText(skill.trim());
                chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light); // Define un azul claro en colors
                cgHabilidades.addView(chip);
            }
        }

        btnCerrar.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
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
        List<Volunteer> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();
        for (Volunteer v : fullList) {
            // Buscamos por nombre o por DNI
            if (v.getFullName().toLowerCase().contains(query) ||
                    v.getDni().toLowerCase().contains(query)) {
                filtered.add(v);
            }
        }
        updateAdapter(filtered);
    }
}