package com.example.appgestionvoluntariado.Fragments.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.appgestionvoluntariado.Activities.MainActivity;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class AdminProfileHubFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile_hub, container, false);

        // Botón Seguridad
        view.findViewById(R.id.btnAdminSecurity).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, new AdminChangePasswordFragment())
                    .addToBackStack(null) // Permite volver al Hub con el botón atrás
                    .commit();
        });

        // Botón Logout
        view.findViewById(R.id.btnAdminLogout).setOnClickListener(v -> performLogout());

        return view;
    }

    private void performLogout() {
        // 1. Cerrar en Firebase
        FirebaseAuth.getInstance().signOut();

        // 2. Limpiar sesión local
        SessionManager.getInstance(getContext()).logout();

        // 3. Ir al Login y limpiar el historial de activities
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) getActivity().finish();
    }
}