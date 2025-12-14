package com.example.appgestionvoluntariado.Fragments.VistaOrganizacion;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appgestionvoluntariado.R;


public class OrganizacionesFragment extends Fragment {

    private Button crearOrganizacion;

    private TextView tabPendientes, tabAceptados;
    private RecyclerView recyclerView;




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
        recyclerView = view.findViewById(R.id.rvOrganizaciones);
        tabPendientes = view.findViewById(R.id.tabOrgPendientes);
        tabAceptados = view.findViewById(R.id.tabOrgAceptados);

        tabPendientes.setOnClickListener(v -> mostrarPendientes());
        tabAceptados.setOnClickListener(v -> mostrarAceptados());

        mostrarPendientes();

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

    private void mostrarAceptados() {
        tabAceptados.setBackgroundResource(R.drawable.background_tab_selected);
        tabAceptados.setTextColor(Color.WHITE);

        tabPendientes.setBackgroundResource(R.drawable.background_tab_unselected);
        tabPendientes.setTextColor(Color.parseColor("#1A3B85"));

    }


    private void mostrarPendientes() {
        tabAceptados.setBackgroundResource(R.drawable.background_tab_unselected);
        tabAceptados.setTextColor(Color.parseColor("#1A3B85"));

        tabPendientes.setBackgroundResource(R.drawable.background_tab_selected);
        tabPendientes.setTextColor(Color.WHITE);


    }


}