package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appgestionvoluntariado.Adapters.AdaptadorDashboard;
import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ActivitiesAPIService;
import com.example.appgestionvoluntariado.Services.MatchesAPIService;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;
import com.example.appgestionvoluntariado.Services.VolunteerAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    //TODO cargar las apis aqui, calcualr todo, sacar estadisticas y hacer una clase auxiliar o usar la que tengo
    //para guardar las listas y luego usarlas en el fragment que toque


    private List<Stat> stats;
    private RecyclerView recyclerView;
    private AdaptadorDashboard adaptadorDashboard;

    private View layoutCarga;

    // CONTADOR PARA SINCRONIZAR
    private int llamadasCompletadas = 0;
    private final int TOTAL_LLAMADAS = 4; // Voluntariados, Org, Voluntarios

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_organizador, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewDashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        layoutCarga = view.findViewById(R.id.layoutCarga);
        layoutCarga.setVisibility(View.VISIBLE);

        stats = new ArrayList<Stat>();

        //TODO mostrar progress bar

        cargarDatosEnParalelo();


        return view;
    }

    private void cargarDatosEnParalelo() {
        llamadasCompletadas = 0;
        //Cargar actividades
        ActivitiesAPIService activitiesAPIService;
        activitiesAPIService = APIClient.getActivitiesAPIService();
        Call<List<Voluntariado>> callAct = activitiesAPIService.getActivities();
        callAct.enqueue(new Callback<List<Voluntariado>>() {
            @Override
            public void onResponse(Call<List<Voluntariado>> call, Response<List<Voluntariado>> response) {
                if (response.isSuccessful() && response.body() != null){
                    DatosGlobales.getInstance().voluntariados = response.body();
                }
                verificarSiHemosTerminado();
            }

            @Override
            public void onFailure(Call<List<Voluntariado>> call, Throwable t) {
                verificarSiHemosTerminado();

            }
        });

        //Cargar organizaciones
        OrganizationAPIService organizationAPIService;
        organizationAPIService = APIClient.getOrganizationAPIService();
        Call<List<Organizacion>> callOrg = organizationAPIService.getOrganizations();
        callOrg.enqueue(new Callback<List<Organizacion>>() {
            @Override
            public void onResponse(Call<List<Organizacion>> call, Response<List<Organizacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatosGlobales.getInstance().organizaciones = response.body();
                }
                verificarSiHemosTerminado();
            }

            @Override
            public void onFailure(Call<List<Organizacion>> call, Throwable t) {
                verificarSiHemosTerminado();

            }
        });

        //Cargar voluntarios
        VolunteerAPIService volunteerAPIService;
        volunteerAPIService = APIClient.getVolunteerAPIService();
        Call<List<Voluntario>> callVol = volunteerAPIService.getVoluntarios();
        callVol.enqueue(new Callback<List<Voluntario>>() {
            @Override
            public void onResponse(Call<List<Voluntario>> call, Response<List<Voluntario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatosGlobales.getInstance().voluntarios = response.body();
                }
                verificarSiHemosTerminado();
            }


            @Override
            public void onFailure(Call<List<Voluntario>> call, Throwable t) {
                verificarSiHemosTerminado();

            }
        });

        MatchesAPIService matchesAPIService;
        matchesAPIService = APIClient.getMAtchesAPIService();
        Call<List<Match>> callMatch = matchesAPIService.getMatches();
        callMatch.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatosGlobales.getInstance().matches = response.body();
                }
                verificarSiHemosTerminado();

            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                verificarSiHemosTerminado();

            }
        });
    }

    private void verificarSiHemosTerminado() {
        llamadasCompletadas++;
        if (llamadasCompletadas == TOTAL_LLAMADAS) {
            //Quitar barra de progreso
            calcularEstadisticas();
            layoutCarga.setVisibility(View.GONE);
        }
    }

    private void calcularEstadisticas() {
        stats.clear();
        crearTarjetaVoluntario();
        crearTarjetaOrganizaciones();
        mostrarStats();
    }

    private void mostrarStats() {
        if (adaptadorDashboard == null) {
            adaptadorDashboard = new AdaptadorDashboard(stats);
            recyclerView.setAdapter(adaptadorDashboard);
        } else {
            //Dejar esto por ahora por si añadimos algo que haga cambiar el adaptador
            adaptadorDashboard.notifyDataSetChanged();
        }

    }

    private void crearTarjetaOrganizaciones() {
        int aceptados = 0;
        int pendientes = 0;
        //Necesito actualizar la base de datos para que no crashee
        //Renzulli me cago en ti pasame ya la query buena tio
        List <Organizacion> organizaciones = DatosGlobales.getInstance().organizaciones;
        if (organizaciones != null) {
            for (Organizacion org : organizaciones) {
                String estadoOrg = org.getEstado().toString();
                if (estadoOrg.equalsIgnoreCase("Activo")) {
                    aceptados++;
                }
                if (org.getEstado().equals("Pendiente")) {
                    pendientes++;
                }
            }
        }


        String pendientesStr = (pendientes > 0) ? "+" + pendientes + " Pendientes" : "Ningun pendiente";

        stats.add(new Stat("ORGANIACIONES",aceptados,pendientesStr,R.drawable.voluntarios));
    }

    private void crearTarjetaVoluntario() {
        int aceptados = 0;
        int pendientes = 0;
        List<Voluntario> voluntarios = DatosGlobales.getInstance().voluntarios;
        if (voluntarios != null) {
            for (Voluntario vol : voluntarios) {
                String estado = vol.getEstadoVoluntario();
                if (estado.equalsIgnoreCase("Activo")) {
                    aceptados++;
                }
                if (estado.equalsIgnoreCase("Pendiente")) {
                    pendientes++;
                }
            }
        }

        String pendientesStr = (pendientes > 0) ? "+" + pendientes + " Pendientes" : "Ningun pendiente";


        stats.add(new Stat("VOLUNTARIOS",aceptados,pendientesStr,R.drawable.voluntarios));
    }
}