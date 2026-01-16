package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgProfileEditFragment extends Fragment {

    private EditText etNombre, etSector, etZona, etDescripcion;
    private Button btnGuardar, btnBack;
    private ProgressBar loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_org_profile_edit, container, false);

        initViews(v);
        loadCurrentProfile();

        btnGuardar.setOnClickListener(view -> updateProfile());
        btnBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());

        return v;
    }

    private void initViews(View v) {
        etNombre = v.findViewById(R.id.etNombre);
        etSector = v.findViewById(R.id.etSector);
        etZona = v.findViewById(R.id.etZona);
        etDescripcion = v.findViewById(R.id.etDescripcion);
        btnGuardar = v.findViewById(R.id.btnGuardar);
        btnBack = v.findViewById(R.id.btnBack);
        loading = v.findViewById(R.id.pbLoading);
    }

    private void loadCurrentProfile() {
        loading.setVisibility(View.VISIBLE);
        // GET /organization/profile - El servidor identifica a la ONG por el Token
        APIClient.getOrganizationService().getProfile().enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Organization org = response.body();
                    etNombre.setText(org.getName());
                    etSector.setText(org.getSector());
                    etZona.setText(org.getZone());
                    etDescripcion.setText(org.getDescription());
                }
            }

            @Override
            public void onFailure(Call<Organization> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String nombre = etNombre.getText().toString().trim();
        String sector = etSector.getText().toString().trim();
        String zona = etZona.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        loading.setVisibility(View.VISIBLE);
        Organization updatedOrg = new Organization(nombre, sector, zona, desc);

        // PUT /organization/update - No enviamos CIF ni DNI, solo el cuerpo con datos
        APIClient.getOrganizationService().updateProfile(updatedOrg).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fallo al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}