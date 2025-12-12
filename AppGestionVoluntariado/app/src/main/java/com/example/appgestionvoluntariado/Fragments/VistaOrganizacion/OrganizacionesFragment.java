package com.example.appgestionvoluntariado.Fragments.VistaOrganizacion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.R;


public class OrganizacionesFragment extends Fragment {

    private Button crearOrganizacion;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_organizaciones, container, false);

        crearOrganizacion = view.findViewById(R.id.btnAnadirOrg);

        /*crearOrganizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, OrgRegistroActivity.class);
                intent.putExtra("tipo", "Añadir");
                startActivity(intent);
            }
        });
        */
        return view;
    }
}