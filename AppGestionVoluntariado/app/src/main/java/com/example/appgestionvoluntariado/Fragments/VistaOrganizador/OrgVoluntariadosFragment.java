package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.Fragments.RegistroYFormularios.VoluntariadoCrearFragment;
import com.example.appgestionvoluntariado.R;

public class OrgVoluntariadosFragment extends Fragment {

    private Button anadirVol;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_voluntariados, container, false);

        anadirVol = view.findViewById(R.id.btnAnadirVoluntariado);

        anadirVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new VoluntariadoCrearFragment())
                        .commit();
            }
        });

        return view;
    }
}