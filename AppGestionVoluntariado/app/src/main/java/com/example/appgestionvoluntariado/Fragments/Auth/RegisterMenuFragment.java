package com.example.appgestionvoluntariado.Fragments.Auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.appgestionvoluntariado.R;

public class RegisterMenuFragment extends Fragment {

    private Button btnOrganization;
    private Button btnVolunteer;
    private Button btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_register_menu, container, false);

        btnOrganization = view.findViewById(R.id.btnOrganization);
        btnOrganization.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new OrganizationRegisterFragment()) // New Fragment
                    .addToBackStack(null)
                    .commit();
        });

        btnVolunteer = view.findViewById(R.id.btnVolunteer);
        btnVolunteer.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new VolunteerRegisterFragment()) // New Fragment
                    .addToBackStack(null)
                    .commit();
        });

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
