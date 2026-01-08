package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.Adapters.AdaptadorVoluntariado;
import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Fragments.RegistroYFormularios.VoluntariadoCrearFragment;
import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ActivitiesAPIService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgVoluntariadosFragment extends Fragment {

    private Button anadirVol;

    private RecyclerView recyclerView;

    private AdaptadorVoluntariado adaptadorVoluntariado;

    private List<Voluntariado>  voluntariados;


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
        recyclerView = view.findViewById(R.id.rvVoluntariados);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        voluntariados = DatosGlobales.getInstance().voluntariados;

        sacarVoluntariados();



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

    private void sacarVoluntariados() {
        /*if (adaptadorVoluntariado == null) {
            adaptadorVoluntariado = new AdaptadorVoluntariado(voluntariados);
            recyclerView.setAdapter(adaptadorVoluntariado);
        }else {
            adaptadorVoluntariado.notifyDataSetChanged();
        }

         */
    }
}