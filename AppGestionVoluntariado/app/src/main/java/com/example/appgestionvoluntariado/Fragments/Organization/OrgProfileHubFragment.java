package com.example.appgestionvoluntariado.Fragments.Organization;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Activities.MainActivity;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Response.ProfileResponse;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.SessionManager;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hub for Organization profile.
 * Fetches generic ProfileResponse and decodes it based on user type [cite: 2026-01-16].
 */
public class OrgProfileHubFragment extends Fragment {

    private TextView tvName, tvVat;
    private View loadingLayout;
    private ImageView logoSpinner;
    private android.view.animation.Animation rotateAnimation;
    private Organization organization;

    private final Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_profile_hub, container, false);

        initViews(view);
        fetchProfileData();
        setupNavigation(view);

        return view;
    }

    private void initViews(View v) {
        tvName = v.findViewById(R.id.tvHubOrgName);
        tvVat = v.findViewById(R.id.tvHubOrgCif);
        loadingLayout = v.findViewById(R.id.layoutLoading);
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);

        rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        if (logoSpinner != null) {
            logoSpinner.startAnimation(rotateAnimation);
        }
    }

    private void setupNavigation(View view) {
        view.findViewById(R.id.btnNavEditOrgData).setOnClickListener(v ->
                replaceFragment(new OrgProfileEditFragment()));

        view.findViewById(R.id.btnNavOrgSecurity).setOnClickListener(v ->
                replaceFragment(new OrgChangePasswordFragment()));

        view.findViewById(R.id.btnLogoutOrg).setOnClickListener(v -> performLogout());
    }

    private void fetchProfileData() {
        toggleLoading(true);

        // Uses the Auth API to get the generic profile wrapper [cite: 2026-01-16]
        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                toggleLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    handleProfileResponse(response.body());
                } else {
                    StatusHelper.showStatus(getContext(), "Error", "No se pudo obtener el perfil", true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                toggleLoading(false);
                StatusHelper.showStatus(getContext(), "Error de red", "Sin conexión con el servidor", true);
            }
        });
    }

    private void handleProfileResponse(ProfileResponse wrapper) {
        if ("organizacion".equalsIgnoreCase(wrapper.getType())) {
            // Decode the nested data into the Organization model [cite: 2026-01-16]
            organization = gson.fromJson(wrapper.getData(), Organization.class);
            updateUI();
        } else {
            StatusHelper.showStatus(getContext(), "Error de Tipo", "El perfil no pertenece a una organización", true);
        }
    }

    private void updateUI() {
        if (organization != null) {
            tvName.setText(organization.getName());
            tvVat.setText("CIF: " + (organization.getCif() != null ? organization.getCif() : "N/A"));
        }
    }

    private void toggleLoading(boolean isLoading) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.organization_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void performLogout() {
        // 0. Sign out from Google Client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        mGoogleSignInClient.revokeAccess().addOnCompleteListener(requireActivity(), task -> {
            if (getActivity() == null) return;

            FirebaseAuth.getInstance().signOut();
            com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).clearToken();
            SessionManager.getInstance(getContext()).logout();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            if (getActivity() != null) getActivity().finish();
        });
    }
}