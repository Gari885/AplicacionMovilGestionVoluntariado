package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appgestionvoluntariado.Adapters.AdaptadorOrganizador;
import com.example.appgestionvoluntariado.Adapters.AdaptadorVoluntario;
import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.VolunteerAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VoluntariosFragment extends Fragment {


    private Button crearOrganizacion;

    private TextView tabPendientes, tabAceptados;
    private RecyclerView recyclerView;

    // Inicializamos las listas aquí para evitar NullPointerException
    private List<Voluntario> voluntarios = new ArrayList<>();
    private List<Voluntario> voluntariosFiltrados = new ArrayList<>();

    private VolunteerAPIService apiService;
    private AdaptadorVoluntario adaptadorVoluntario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voluntarios, container, false);

        crearOrganizacion = view.findViewById(R.id.btnAnadirVoluntario);
        recyclerView = view.findViewById(R.id.rvVoluntarios);
        tabPendientes = view.findViewById(R.id.tabPendientes);
        tabAceptados = view.findViewById(R.id.tabAceptados);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        tabPendientes.setOnClickListener(v -> {
            cambiarTabsVisualmente(true); // true = pendientes seleccionado
            filtrarYMostrar("Rechazado");
        });

        tabAceptados.setOnClickListener(v -> {
            cambiarTabsVisualmente(false); // false = aceptados seleccionado
            filtrarYMostrar("Aprobado"); // O el estado que uses para aceptados
        });

        cargarDatosGlobales();

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

    private void cargarDatosGlobales() {
        voluntarios = DatosGlobales.getInstance().voluntarios;
        cambiarTabsVisualmente(true);
        filtrarYMostrar("Pendiente");

    }

    private void filtrarYMostrar(String estadoBuscado) {
        voluntariosFiltrados.clear();

        // 2. Filtrar
        if (voluntarios != null) {
            for (Voluntario vol : voluntarios) {
                if (vol.getEstadoVoluntario() == null) continue;

                if (estadoBuscado.equals("Pendiente")) {
                    // Muestra solo los pendientes
                    if (vol.getEstadoVoluntario().equalsIgnoreCase("Pendiente")) {
                        voluntariosFiltrados.add(vol);
                    }
                } else {
                    // Muestra todo lo que NO sea pendiente (Aceptados, rechazados, etc)
                    if (!vol.getEstadoVoluntario().equalsIgnoreCase("Pendiente")) {
                        voluntariosFiltrados.add(vol);
                    }
                }
            }
        }

        // 3. Actualizar el adaptador
        if (adaptadorVoluntario == null) {
            adaptadorVoluntario = new AdaptadorVoluntario(voluntariosFiltrados);
            recyclerView.setAdapter(adaptadorVoluntario);
        } else {
            adaptadorVoluntario.actualizarDatos(voluntariosFiltrados);
        }
    }

    private void cambiarTabsVisualmente(boolean esPendientes) {
        if (esPendientes) {
            tabPendientes.setBackgroundResource(R.drawable.background_tab_selected);
            tabPendientes.setTextColor(Color.WHITE);
            tabAceptados.setBackgroundResource(R.drawable.background_tab_unselected);
            tabAceptados.setTextColor(Color.parseColor("#1A3B85"));
        } else {
            tabAceptados.setBackgroundResource(R.drawable.background_tab_selected);
            tabAceptados.setTextColor(Color.WHITE);
            tabPendientes.setBackgroundResource(R.drawable.background_tab_unselected);
            tabPendientes.setTextColor(Color.parseColor("#1A3B85"));
        }
    }
}