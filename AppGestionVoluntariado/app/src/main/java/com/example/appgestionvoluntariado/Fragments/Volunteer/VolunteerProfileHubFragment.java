package com.example.appgestionvoluntariado.Fragments.Volunteer;

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

import com.example.appgestionvoluntariado.Activities.MainActivity;
import com.example.appgestionvoluntariado.Models.Response.ProfileResponse;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.AuthAPIService;
import com.example.appgestionvoluntariado.Utils.SessionManager;
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

public class VolunteerProfileHubFragment extends Fragment {

    private TextView tvName;
    private androidx.appcompat.widget.SwitchCompat switchNotifications;
    private AuthAPIService authAPIService;

    private Volunteer volunteer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el diseño corregido sin el icono blanco
        View view = inflater.inflate(R.layout.fragment_volunteer_profile_hub, container, false);

        tvName = view.findViewById(R.id.tvHubUserName);
        authAPIService = APIClient.getAuthAPIService();

        // Cargamos el nombre real desde la API usando el Token de Firebase
        fetchProfileName();

        // Navegación a Editar Datos Personales (ahora con el nombre de clase correcto)
        view.findViewById(R.id.btnNavEditData).setOnClickListener(v ->
                replaceFragment(new VolunteerProfileEditDataFragment()));

        // Navegación a Seguridad y Contraseña
        view.findViewById(R.id.btnNavSecurity).setOnClickListener(v ->
                replaceFragment(new VolunteerProfileChangePasswordFragment()));

        // Logout
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> performLogout());

        // Configurar Switch de Notificaciones
        switchNotifications = view.findViewById(R.id.switchNotificaciones);
        setupNotificationSwitch();

        return view;
    }

    private void fetchProfileName() {
        // La API sabe quién es el usuario gracias al AuthInterceptor
        authAPIService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Usamos el helper getFullName() que definimos en el modelo
                    ProfileResponse wrapper = response.body();
                    Gson gson = new Gson();
                    if ("voluntario".equalsIgnoreCase(wrapper.getType())) {
                        volunteer = gson.fromJson(wrapper.getData(), Volunteer.class);
                        updateUI();
                    }

                } else {
                    tvName.setText("Usuario");
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                tvName.setText("Error al cargar");
            }
        });
    }

    private void updateUI() {
        if (volunteer != null) {
            tvName.setText(volunteer.getFirstName());
        }
    }

    private void setupNotificationSwitch() {
        // 1. Obtener preferencia guardada (Default: true)
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        boolean areNotificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        
        switchNotifications.setChecked(areNotificationsEnabled);

        // 2. Guardar preferencia al cambiar
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            
            String msg = isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Permite volver al Hub con el botón atrás
                .commit();
    }

    private void performLogout() {
        // 0. Sign out from Google Client to force account picker next time
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        mGoogleSignInClient.revokeAccess().addOnCompleteListener(requireActivity(), task -> {
            if (getActivity() == null) return;

            // 1. Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();
            com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).clearToken();
            
            // 2. Limpiar preferencias locales (token, etc.)
            SessionManager.getInstance(getContext()).logout();

            // 3. Volver al Login limpiando el historial de actividades
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }

            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });
    }
}