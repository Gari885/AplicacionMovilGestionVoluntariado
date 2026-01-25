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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.OrganizationAdapter;
import com.example.appgestionvoluntariado.Fragments.Auth.OrganizationRegisterFragment;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Request.StatusRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.OrganizationService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrganizationListFragment extends Fragment {
    private RecyclerView rvOrgs;
    private EditText etSearch;
    private TextView tabPending, tabAccepted;
    private View loadingLayout;
    private FloatingActionButton fabAddOrganization;

    private ImageView logoSpinner;
    private Animation rotateAnimation;

    private List<Organization> fullList = new ArrayList<>();
    private OrganizationAdapter adapter;
    private String currentFilter = "PENDIENTE";

    private OrganizationService organizationService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_organization_list, container, false);



        initViews(v);

        setupTabs();
        setupSearch();
        loadData();

        return v;
    }

    private void initViews(View v) {
        rvOrgs = v.findViewById(R.id.rvOrganizations);
        rvOrgs.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearch = v.findViewById(R.id.etSearchOrganization);
        tabPending = v.findViewById(R.id.tabStatusPending);
        tabAccepted = v.findViewById(R.id.tabStatusAccepted);
        loadingLayout = v.findViewById(R.id.layoutLoading);
        fabAddOrganization = v.findViewById(R.id.fabAddOrganization);
        organizationService = APIClient.getOrganizationService();
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        logoSpinner.startAnimation(rotateAnimation);

        fabAddOrganization.setVisibility(View.INVISIBLE);
    }

    private void setupTabs() {
        tabPending.setOnClickListener(v -> {
            currentFilter = "PENDIENTE";
            updateTabUI(tabPending, tabAccepted);
            loadData();
        });

        tabAccepted.setOnClickListener(v -> {
            currentFilter = "ACEPTADA";
            updateTabUI(tabAccepted, tabPending);
            loadData();
        });

        fabAddOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.admin_fragment_container, new OrganizationRegisterFragment())
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

    private void loadData() {
        loadingLayout.setVisibility(View.VISIBLE);
        organizationService.getOrganizations(currentFilter).enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
                loadingLayout.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    fabAddOrganization.setVisibility(View.VISIBLE);
                    fullList = response.body();
                    updateAdapter(fullList);
                }
            }
            @Override
            public void onFailure(Call<List<Organization>> call, Throwable t) {
                Log.e("API_ERROR", "Error al llamar a getOrganizations", t); // <--- Mira el Logcat
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateAdapter(List<Organization> list) {
        if (adapter == null) {
            adapter = new OrganizationAdapter(list, new OrganizationAdapter.OnOrgActionListener() {
                @Override
                public void onAccept(Organization org) {
                    // Solo enviamos el estado, el token identifica a la entidad
                    processStatusChange(org, new StatusRequest("Aceptada"));
                }

                @Override
                public void onReject(Organization org) {
                    processStatusChange(org, new StatusRequest("rechazado"));
                }

                @Override
                public void onDelete(Organization org) {
                    showConfirmDeleteDialog(org);
                }

                @Override
                public void onDetails(Organization org) {
                    showDetailsPopup(org);
                }
            });
            rvOrgs.setAdapter(adapter);
        } else {
            rvOrgs.setAdapter(adapter);
            adapter.updateList(list);
        }
    }

    private void showConfirmDeleteDialog(Organization org) {
        new AlertDialog.Builder(getContext())
                .setTitle("¿Dar de baja?")
                .setMessage("¿Estás seguro de que quieres dar de baja a " + org.getName() + "?")
                .setPositiveButton("Dar de baja", (dialog, which) -> {
                    processStatusChange(org, new StatusRequest("rechazado"));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDetailsPopup(Organization org) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_organization_details, null);
        bottomSheetDialog.setContentView(view);

        // Ajuste para esquinas redondeadas transparentes
        View parent = (View) view.getParent();
        if (parent != null) {
            parent.setBackgroundResource(android.R.color.transparent);
        }

        // Mapeo de vistas según los campos de tu registro
        TextView tvCif = view.findViewById(R.id.tvCifVal);
        TextView tvCorreo = view.findViewById(R.id.tvCorreoVal);
        TextView tvSector = view.findViewById(R.id.tvSectorVal);
        TextView tvZona = view.findViewById(R.id.tvZonaVal);
        TextView tvMision = view.findViewById(R.id.tvMisionVal);
        View btnClose = view.findViewById(R.id.btnCerrarPopup);

        // Datos Obligatorios (Nombre ya sale en la tarjeta, aquí ponemos el resto)
        tvCif.setText(org.getCif());
        tvCorreo.setText(org.getEmail());

        // Datos Opcionales con validación de "Sin datos"
        validateAndSetText(tvSector, org.getSector());
        validateAndSetText(tvZona, org.getAddress());
        // Recuerda que en tu registro 'etDescripcion' es la Misión
        validateAndSetText(tvMision, org.getDescription());

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    /**
     * Método auxiliar para gestionar campos vacíos
     * Si no hay datos, muestra un aviso en gris e itálica.
     */
    private void validateAndSetText(TextView tv, String data) {
        if (data == null || data.trim().isEmpty()) {
            tv.setText("Información no proporcionada");
            tv.setTextColor(Color.parseColor("#9E9E9E")); // Gris
            tv.setTypeface(null, android.graphics.Typeface.ITALIC);
        } else {
            tv.setText(data);
            tv.setTextColor(Color.parseColor("#333333")); // Negro/Gris oscuro
            tv.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    public void processStatusChange( Organization org,StatusRequest request) {
        loadingLayout.setVisibility(View.VISIBLE);
        APIClient.getOrganizationService().updateStatus(org.getCif() , request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            loadData(); // Refrescamos la lista para mover el item de pestaña
                        } else {
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
                    }
                });
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
        List<Organization> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();

        for (Organization org : fullList) {
            if (org.getName().toLowerCase().contains(query) ||
                    org.getCif().toLowerCase().contains(query)) {
                filtered.add(org);
            }
        }
        updateAdapter(filtered);
    }
}