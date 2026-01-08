package com.example.appgestionvoluntariado.Fragments.VistaVoluntario;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.AdaptadorVoluntariado;
import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.Models.VoluntarioInscribirseRequest;
import com.example.appgestionvoluntariado.ModoVista;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ActivitiesAPIService;
import com.example.appgestionvoluntariado.Services.MatchesAPIService;
import com.example.appgestionvoluntariado.SesionGlobal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoluntariadosVolFragment extends Fragment {

    // --- VARIABLES DE DATOS ---
    private List<Voluntariado> voluntariadosFiltrados = new ArrayList<>();
    private ActivitiesAPIService activitiesAPIService;

    // --- VARIABLES DE UI ---
    private RecyclerView recyclerView;
    private AdaptadorVoluntariado adaptadorVoluntariado;
    private LinearLayout layoutCarga;
    private TextView textoCarga;

    // --- VARIABLES DE ANIMACIÓN Y CONTROL ---
    private Handler handlerAnimacion;
    private Runnable runnableAnimacion;
    private final String TEXTO_CARGA = "Cargando ofertas";
    private int puntosCount = 0;
    private int contadorLLamadas = 0;
    private final int TOTAL_LLAMADAS = 2; // Actividades + Matches

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voluntariados_vol, container, false);

        inicializarVistas(view);

        // Reiniciamos estado
        contadorLLamadas = 0;
        iniciarAnimacion();

        // Ejecutamos la carga inicial
        llamadaApi();

        return view;
    }

    private void inicializarVistas(View view) {
        layoutCarga = view.findViewById(R.id.layoutCarga);
        textoCarga = view.findViewById(R.id.textoCarga); // Asegúrate que el ID en XML sea textoCarga o tvTextoCarga

        recyclerView = view.findViewById(R.id.rvVoluntariados);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializamos servicios y handlers
        activitiesAPIService = APIClient.getActivitiesAPIService();
        handlerAnimacion = new Handler(Looper.getMainLooper());
    }

    // ----------------------------------------------------------------
    // --- BLOQUE 1: CARGA DE DATOS (ACTIVIDADES + MATCHES) ---
    // ----------------------------------------------------------------

    private void llamadaApi() {
        // 1. Pedir Actividades
        activitiesAPIService.getActivities().enqueue(new Callback<List<Voluntariado>>() {
            @Override
            public void onResponse(Call<List<Voluntariado>> call, Response<List<Voluntariado>> response) {
                if (response.isSuccessful() && response.body() != null){
                    DatosGlobales.getInstance().voluntariados = response.body();
                }
                verificarSiLasLLamadasHanTerminado();
            }

            @Override
            public void onFailure(Call<List<Voluntariado>> call, Throwable t) {
                verificarSiLasLLamadasHanTerminado();
            }
        });

        // 2. Pedir Matches (Inscripciones)
        MatchesAPIService matchesAPIService = APIClient.getMatchesAPIService();
        matchesAPIService.getMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null){
                    DatosGlobales.getInstance().matches = response.body();
                }
                verificarSiLasLLamadasHanTerminado();
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                verificarSiLasLLamadasHanTerminado();
            }
        });
    }

    private void verificarSiLasLLamadasHanTerminado(){
        contadorLLamadas++;
        if (contadorLLamadas >= TOTAL_LLAMADAS) {
            detenerAnimacion();
            layoutCarga.setVisibility(View.GONE);
            prepararListaVoluntariados();
        }
    }

    // ----------------------------------------------------------------
    // --- BLOQUE 2: LÓGICA DE FILTRADO ---
    // ----------------------------------------------------------------

    private void prepararListaVoluntariados() {
        List<Voluntariado> todos = DatosGlobales.getInstance().voluntariados;
        List<Match> matches = DatosGlobales.getInstance().matches;

        // Obtenemos DNI del usuario actual de forma segura
        String miDni = "";
        if (SesionGlobal.getVoluntario() != null) {
            miDni = SesionGlobal.getVoluntario().getDni();
        }

        if (voluntariadosFiltrados == null) voluntariadosFiltrados = new ArrayList<>();
        else voluntariadosFiltrados.clear();

        if (todos != null && matches != null){
            // 1. Identificar en cuáles estoy apuntado
            HashSet<Integer> idsDondeEstoyApuntado = new HashSet<>();
            for (Match m : matches) {
                if (m.getDniVoluntario().equals(miDni)) {
                    idsDondeEstoyApuntado.add(m.getCodActividad());
                }
            }

            // 2. Mostrar SOLO las que NO estoy apuntado (Disponibles)
            for (Voluntariado vol : todos) {
                if (!idsDondeEstoyApuntado.contains(vol.getId())) {
                    voluntariadosFiltrados.add(vol);
                }
            }
        }

        // Usamos el modo DISPONIBLES para que el botón sea "Apuntarse" (Verde)
        mostrarVoluntariados(ModoVista.VOLUNTARIO_DISPONIBLES);
    }

    private void mostrarVoluntariados(ModoVista modoVista) {
        if (adaptadorVoluntariado == null) {
            adaptadorVoluntariado = new AdaptadorVoluntariado(getContext(), voluntariadosFiltrados, modoVista, new AdaptadorVoluntariado.OnItemAction() {
                @Override
                public void onAccionPrincipal(Voluntariado item) {
                    realizarApunte(item);
                }

                @Override
                public void onAccionSecundaria(Voluntariado item) {
                    // Lógica para "+ Info" si fuera necesaria
                }
            });
            recyclerView.setAdapter(adaptadorVoluntariado);
        } else {
            adaptadorVoluntariado.notifyDataSetChanged();
        }
    }

    // ----------------------------------------------------------------
    // --- BLOQUE 3: ACCIONES DEL USUARIO (APUNTARSE) ---
    // ----------------------------------------------------------------

    private void realizarApunte(Voluntariado item) {
        // Feedback visual
        layoutCarga.setVisibility(View.VISIBLE);
        textoCarga.setText("Apuntando a oferta...");

        String miDni = SesionGlobal.getVoluntario().getDni();

        // Creamos la petición (Asegúrate de haber corregido el @SerializedName en este modelo)
        VoluntarioInscribirseRequest request = new VoluntarioInscribirseRequest(miDni);

        activitiesAPIService.inscribirVoluntarioActividad(item.getId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                layoutCarga.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Inscripción realizada!", Toast.LENGTH_SHORT).show();

                    // Actualizamos la lista local eliminando el item
                    voluntariadosFiltrados.remove(item);
                    adaptadorVoluntariado.notifyDataSetChanged();

                    // (Opcional) Aquí podrías añadir el nuevo Match a DatosGlobales
                    // para que aparezca en la pestaña "Mis Voluntariados" sin recargar.
                } else {
                    Toast.makeText(getContext(), "Error al inscribirse (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                layoutCarga.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // --- BLOQUE 4: ANIMACIÓN Y LIMPIEZA ---
    // ----------------------------------------------------------------

    private void iniciarAnimacion() {
        layoutCarga.setVisibility(View.VISIBLE);
        puntosCount = 0;

        runnableAnimacion = new Runnable() {
            @Override
            public void run() {
                StringBuilder puntos = new StringBuilder();
                for (int i = 0; i < puntosCount; i++) puntos.append(".");

                textoCarga.setText(TEXTO_CARGA + puntos.toString());

                puntosCount++;
                if (puntosCount > 3) puntosCount = 0;

                handlerAnimacion.postDelayed(this, 500);
            }
        };
        handlerAnimacion.post(runnableAnimacion);
    }

    private void detenerAnimacion() {
        if (runnableAnimacion != null && handlerAnimacion != null) {
            handlerAnimacion.removeCallbacks(runnableAnimacion);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detenerAnimacion(); // Evitamos memory leaks o crashes al salir
    }
}