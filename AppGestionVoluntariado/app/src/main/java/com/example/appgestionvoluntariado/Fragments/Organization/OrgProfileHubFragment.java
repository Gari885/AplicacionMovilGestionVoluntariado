package com.example.appgestionvoluntariado.Fragments.Organization;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Activities.LoginActivity;
import com.example.appgestionvoluntariado.Activities.MainActivity;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.OrganizationService; // Deberás crear este Service
import com.example.appgestionvoluntariado.Utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgProfileHubFragment extends Fragment {

    private TextView tvName, tvCif;
    private OrganizationService organizationService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_profile_hub, container, false);

        initViews(view);
        fetchOrganizationData();

        // Navegación
        view.findViewById(R.id.btnNavEditOrgData).setOnClickListener(v ->
                replaceFragment(new OrgProfileEditFragment()));

        view.findViewById(R.id.btnNavOrgSecurity).setOnClickListener(v ->
                replaceFragment(new OrgChangePasswordFragment()));

        view.findViewById(R.id.btnLogoutOrg).setOnClickListener(v -> performLogout());

        return view;
    }

    private void initViews(View v) {
        tvName = v.findViewById(R.id.tvHubOrgName);
        tvCif = v.findViewById(R.id.tvHubOrgCif);
        organizationService = APIClient.getOrganizationService();
    }

    private void fetchOrganizationData() {
        // El servidor identifica a la Org por el Token
        organizationService.getProfile().enqueue(new Callback<Organization>() {
            @Override
            public void onResponse(Call<Organization> call, Response<Organization> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Organization org = response.body();
                    tvName.setText(org.getName());
                    tvCif.setText("CIF: " + org.getCif());
                }
            }

            @Override
            public void onFailure(Call<Organization> call, Throwable t) {
                tvName.setText("Error al cargar");
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.organization_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        SessionManager.getInstance(getContext()).logout();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) getActivity().finish();
    }
}