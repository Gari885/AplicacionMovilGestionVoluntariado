package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class VolunteerProfileHubFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_profile_hub, container, false);

        TextView tvName = view.findViewById(R.id.tvHubUserName);
        if (GlobalSession.getVolunteer() != null) {
            tvName.setText(GlobalSession.getVolunteer().getName());
        }

        // Navegación a Editar Datos
        view.findViewById(R.id.btnNavEditData).setOnClickListener(v ->
                replaceFragment(new VolunteerEditDataFragment()));

        // Navegación a Seguridad
        view.findViewById(R.id.btnNavSecurity).setOnClickListener(v ->
                replaceFragment(new VolunteerChangePasswordFragment()));

        // Logout
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> performLogout());

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        SessionManager.getInstance(getContext()).logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }
}