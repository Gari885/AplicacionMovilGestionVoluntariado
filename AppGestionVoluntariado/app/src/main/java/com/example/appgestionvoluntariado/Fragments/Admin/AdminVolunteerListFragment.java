package com.example.appgestionvoluntariado.Fragments.Admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.VolunteerAdapter;
import com.example.appgestionvoluntariado.Fragments.Auth.OrganizationRegisterFragment;
import com.example.appgestionvoluntariado.Fragments.Auth.VolunteerRegisterFragment;
import com.example.appgestionvoluntariado.Models.Request.StatusRequest;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.AdminService;
import com.example.appgestionvoluntariado.Services.VolunteerService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    private ImageView logoSpinner;
    private Animation rotateAnimation;

    private List<Volunteer> fullList = new ArrayList<>();
    private VolunteerAdapter adapter;
    private String currentStatus = "PENDIENTE";
    private AdminService adminService;

    private FloatingActionButton fabAddVolunteer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_volunteer_list, container, false);


        initViews(v);
        setupTabs();
        setupSearch();
        fetchVolunteers();

        return v;
    }

    private void initViews(View v) {
        rvVolunteers = v.findViewById(R.id.rvVolunteers);
        rvVolunteers.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearch = v.findViewById(R.id.etSearchVolunteer);
        tabPending = v.findViewById(R.id.tabStatusPending);
        tabAccepted = v.findViewById(R.id.tabStatusAccepted);
        loadingLayout = v.findViewById(R.id.layoutLoading);
        fabAddVolunteer = v.findViewById(R.id.fabAddVolunteer);
        fabAddVolunteer.setVisibility(View.INVISIBLE);
        adminService = APIClient.getAdminService();
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        logoSpinner.startAnimation(rotateAnimation);
    }

    private void setupTabs() {
        tabPending.setOnClickListener(v -> {
            currentStatus = "PENDIENTE";
            updateTabUI(tabPending, tabAccepted);
            fetchVolunteers();
        });

        tabAccepted.setOnClickListener(v -> {
            // CORRECCIÓN: El backend usa 'ACEPTADO', no 'ACTIVO'
            currentStatus = "ACEPTADO";
            updateTabUI(tabAccepted, tabPending);
            fetchVolunteers();
        });

        fabAddVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.admin_fragment_container, new VolunteerRegisterFragment())
                        .addToBackStack(null).commit();
            }
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
        adminService.getVolunteers(currentStatus).enqueue(new Callback<List<Volunteer>>() {
            @Override
            public void onResponse(Call<List<Volunteer>> call, Response<List<Volunteer>> response) {
                loadingLayout.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    fabAddVolunteer.setVisibility(View.VISIBLE);
                    fullList = response.body();
                    updateAdapter(fullList);
                } else {
                    // Es buena práctica limpiar la lista si no hay resultados o falla
                    fullList.clear();
                    updateAdapter(fullList);
                }
            }

            @Override
            public void onFailure(Call<List<Volunteer>> call, Throwable t) {
                Log.e("API_ERROR", "Error al llamar a getVolunteers", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void updateAdapter(List<Volunteer> list) {
        if (adapter == null) {
            adapter = new VolunteerAdapter(list, new VolunteerAdapter.OnVolunteerActionListener() {
                @Override
                public void onAccept(Volunteer volunteer) {
                    processStatusChange(volunteer, new StatusRequest("ACEPTADO")); // Asegura mayúsculas si el backend lo requiere
                }

                @Override
                public void onReject(Volunteer volunteer) {
                    processStatusChange(volunteer, new StatusRequest("RECHAZADO"));
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
        APIClient.getVolunteerService().updateStatus(vol.getDni(), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loadingLayout.setVisibility(View.GONE); // Importante ocultarlo aquí también
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                            fetchVolunteers();
                        } else {
                            Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
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
                .setMessage("¿Estás seguro de que quieres rechazar a " + volunteer.getFirstName() + "?")
                .setPositiveButton("Confirmar", (dialog, which) -> processStatusChange(volunteer, new StatusRequest("RECHAZADO")))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showVolunteerDetails(Volunteer v) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_volunteer_info, null);
        bottomSheetDialog.setContentView(view);

        ChipGroup cgHabilidades = view.findViewById(R.id.chipGroupHabilidades);
        TextView tvDisp = view.findViewById(R.id.tvDisponibilidadVal);
        Button btnCerrar = view.findViewById(R.id.btnCerrarPopup);

        View parent = (View) view.getParent();
        parent.setBackgroundResource(android.R.color.transparent);

        // --- CORRECCIÓN INTEGRAL DEL BUCLE ---
        if (v.getSkills() != null) {
            // Ahora iteramos sobre CategoryItem, no String
            for (Volunteer.CategoryItem skillItem : v.getSkills()) {
                Chip chip = new Chip(getContext());
                chip.setText(skillItem.nombre); // Accedemos al nombre del objeto
                chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light);
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
            if (v.getFirstName().toLowerCase().contains(query) ||
                    v.getDni().toLowerCase().contains(query)) {
                filtered.add(v);
            }
        }
        updateAdapter(filtered);
    }
}