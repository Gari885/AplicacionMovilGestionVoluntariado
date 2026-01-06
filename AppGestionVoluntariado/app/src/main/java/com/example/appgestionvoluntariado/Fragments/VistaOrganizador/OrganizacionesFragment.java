package com.example.appgestionvoluntariado.Fragments.VistaOrganizador;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull; // Importante para buenas prácticas
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Falta importar esto
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Para avisar al usuario si falla

import com.example.appgestionvoluntariado.Adapters.AdaptadorOrganizador;
import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;

import java.util.ArrayList; // Importante
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizacionesFragment extends Fragment {

    private Button crearOrganizacion;
    private TextView tabPendientes, tabAceptados;
    private RecyclerView recyclerView;

    // Inicializamos las listas aquí para evitar NullPointerException
    private List<Organizacion> organizaciones = new ArrayList<>();
    private List<Organizacion> organizacionsFiltradas = new ArrayList<>();

    private OrganizationAPIService apiService;
    private AdaptadorOrganizador adaptadorOrganizador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizaciones, container, false);

        // 1. Vincular Vistas
        crearOrganizacion = view.findViewById(R.id.btnAnadirOrg);
        recyclerView = view.findViewById(R.id.rvOrganizaciones);
        tabPendientes = view.findViewById(R.id.tabOrgPendientes);
        tabAceptados = view.findViewById(R.id.tabOrgAceptados);

        // 2. Configurar RecyclerView (¡OBLIGATORIO!)
        // Si no pones el LayoutManager, no se verá nada aunque tengas datos
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Listeners de las Tabs
        tabPendientes.setOnClickListener(v -> {
            cambiarTabsVisualmente(true); // true = pendientes seleccionado
            filtrarYMostrar("Pendiente");
        });

        tabAceptados.setOnClickListener(v -> {
            cambiarTabsVisualmente(false); // false = aceptados seleccionado
            filtrarYMostrar("Activo"); // O el estado que uses para aceptados
        });

        // 4. Cargar Datos
        cargarDatosGlobales();

        return view;
    }

    private void cargarDatosGlobales() {

        organizaciones = DatosGlobales.getInstance().organizaciones;
        cambiarTabsVisualmente(true);
        filtrarYMostrar("Pendiente");
    }

    // He unificado tu lógica de filtrado en un solo método más limpio
    private void filtrarYMostrar(String estadoBuscado) {
        // 1. Limpiar la lista anterior para no duplicar
        organizacionsFiltradas.clear();

        // 2. Filtrar
        if (organizaciones != null) {
            for (Organizacion org : organizaciones) {
                if (org.getEstado() == null) continue;

                if (estadoBuscado.equals("Pendiente")) {
                    if (org.getEstado().equalsIgnoreCase("Pendiente")) {
                        organizacionsFiltradas.add(org);
                    }
                } else if (estadoBuscado.equals("Activo")) {
                    if (org.getEstado().equalsIgnoreCase("Activo")) {
                        organizacionsFiltradas.add(org);
                    }
                }
            }
        }

        // 3. Actualizar el adaptador
        if (adaptadorOrganizador == null) {
            adaptadorOrganizador = new AdaptadorOrganizador(organizacionsFiltradas);
            recyclerView.setAdapter(adaptadorOrganizador);
        } else {
            adaptadorOrganizador.actualizarDatos(organizacionsFiltradas);
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