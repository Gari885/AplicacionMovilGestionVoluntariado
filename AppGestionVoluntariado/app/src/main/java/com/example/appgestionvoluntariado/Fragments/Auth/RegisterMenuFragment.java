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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_register_menu, container, false);

        btnOrganization = view.findViewById(R.id.btnOrganization);
        btnOrganization.setOnClickListener(v -> {
            OrganizationRegisterFragment fragment = new OrganizationRegisterFragment();
            fragment.setArguments(getArguments());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnVolunteer = view.findViewById(R.id.btnVolunteer);
        btnVolunteer.setOnClickListener(v -> {
            VolunteerRegisterFragment fragment = new VolunteerRegisterFragment();
            fragment.setArguments(getArguments());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        com.google.android.material.appbar.MaterialToolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }
}
