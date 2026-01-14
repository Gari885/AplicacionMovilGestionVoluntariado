package com.example.appgestionvoluntariado.Fragments.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Activities.MainActivity;
import com.example.appgestionvoluntariado.Fragments.Organization.OrganizationMyProjectsFragment;
import com.example.appgestionvoluntariado.Fragments.Volunteer.VolunteerMyProjectsFragment;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;

public class ProfileUserFragment extends Fragment {

    private LinearLayout btnPersonalInfo, btnSecurity, btnHelp;
    private Button btnLogout, btnBack;
    private TextView userName, userEmail;
    private SwitchCompat switchNotifs;
    private Volunteer volunteer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_menu, container, false);

        btnPersonalInfo = view.findViewById(R.id.btnOpcionMisDatos);
        btnSecurity = view.findViewById(R.id.btnOpcionSeguridad);
        btnHelp = view.findViewById(R.id.btnOpcionAyuda);
        btnLogout = view.findViewById(R.id.btnCerrarSesion);
        userEmail = view.findViewById(R.id.tvEmailUsuario);
        userName = view.findViewById(R.id.tvNombreUsuario);
        btnBack = view.findViewById(R.id.btnVolver);
        switchNotifs = view.findViewById(R.id.switchNotificaciones);
        
        volunteer = GlobalSession.getVolunteer();
        if (volunteer != null) {
            userName.setText(volunteer.getFullName());
            userEmail.setText(volunteer.getEmail());
        }

        switchNotifs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Notificaciones Activadas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Notificaciones Desactivadas", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Fragment fragment;
            if (GlobalSession.isOrganization()){
                fragment = new OrganizationMyProjectsFragment();
            } else {
                fragment = new VolunteerMyProjectsFragment();
            }

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(this::performLogout);

        btnPersonalInfo.setOnClickListener(v -> {
            if (GlobalSession.isOrganization()){
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileOrganizationDataFragment())
                        .commit();
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileVolunteerDataFragment())
                        .commit();
            }
        });

        btnSecurity.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileSecurityFragment())
                    .commit();
        });
        
        btnHelp.setOnClickListener(v -> {
             getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileHelpFragment())
                    .commit();
        });

        return view;
    }

    private void performLogout(View v) {
        GlobalSession.logout();
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        startActivity(intent);
    }
}
