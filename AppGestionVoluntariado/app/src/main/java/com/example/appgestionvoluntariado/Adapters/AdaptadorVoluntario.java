package com.example.appgestionvoluntariado.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.EstadoRequest;
import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.VolunteerAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaptadorVoluntario extends RecyclerView.Adapter<AdaptadorVoluntario.GridHolder> {

    private List<Voluntario> voluntarios;


    public AdaptadorVoluntario(List<Voluntario> voluntarios) {
        this.voluntarios  = new ArrayList<>(voluntarios);

    }
    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_voluntario_item, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assingData(voluntarios.get(position));
    }

    @Override
    public int getItemCount() {
        return voluntarios.size();
    }

    public void actualizarDatos(List<Voluntario> voluntariosFiltrados) {

        this.voluntarios.clear();

        this.voluntarios.addAll(voluntariosFiltrados);

        notifyDataSetChanged();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView email;

        ImageView info,darBaja;

        LinearLayout pendientesLayout;

        Button aceptar,rechazar;





        public GridHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombre);
            email = itemView.findViewById(R.id.tvCorreo);
            info = itemView.findViewById(R.id.btnInfo);
            darBaja = itemView.findViewById(R.id.btnDarBaja);
            pendientesLayout = itemView.findViewById(R.id.layoutPendientes);
            aceptar = itemView.findViewById(R.id.btnAceptar);
            rechazar = itemView.findViewById(R.id.btnRechazar);

        }
        public void assingData(Voluntario vol) {
            nombre.setText(vol.getNombre());
            email.setText(vol.getEmail());

            if (vol.getEstadoVoluntario().equals("RECHAZADO")) {
                pendientesLayout.setVisibility(View.VISIBLE);
                darBaja.setVisibility(View.GONE);

                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cambiarEstadoVoluntario(vol,"ACEPTADO",itemView.getContext());

                    }
                });

                rechazar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cambiarEstadoVoluntario(vol,"RECHAZADO",itemView.getContext());
                    }
                });
            }else {
                pendientesLayout.setVisibility(View.GONE);
                darBaja.setVisibility(View.VISIBLE);
            }

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_info_voluntario,null);

                    TextView disponibilidad = popupView.findViewById(R.id.lblDisponibilidad);
                    LinearLayout habilidades = popupView.findViewById(R.id.containerHabilidades);
                    LinearLayout intereses = popupView.findViewById(R.id.containerIntereses);
                    LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

                    /*disponibilidad.setText(voluntario.getDisponibilidad());

                    rellenarTags(context,habilidades,voluntario.getHabilidades());
                    rellenarTags(context,intereses,voluntario.getIntereses());


                     */
                    // 4. Mostrar
                    builder.setView(popupView);
                    AlertDialog dialog = builder.create();
                    if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cerrar.setOnClickListener(x -> dialog.dismiss());
                    dialog.show();

                }
            });

        }

        private void cambiarEstadoVoluntario(Voluntario vol, String nuevoEstado, Context context) {
            VolunteerAPIService apiService = APIClient.getVolunteerAPIService();
            EstadoRequest body = new EstadoRequest(nuevoEstado);

            Call<Voluntario> call = apiService.actualizarEstado(vol.getDni(),body);
            call.enqueue(new Callback<Voluntario>() {
                @Override
                public void onResponse(Call<Voluntario> call, Response<Voluntario> response) {
                        if (response.isSuccessful()){
                            vol.setEstado(nuevoEstado);
                            voluntarios.remove(vol);
                            notifyDataSetChanged();

                            Toast.makeText(context, "Voluntario " + nuevoEstado, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }


                @Override
                public void onFailure(Call<Voluntario> call, Throwable t) {
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void rellenarTags(Context context, LinearLayout container, ArrayList<String> lista) {
            container.removeAllViews();

            if (lista == null) return;
            for (String texto : lista){
                TextView tag = new TextView(context);
                tag.setText(texto);
                tag.setTextSize(12);
                tag.setTextColor(Color.parseColor("#1A3B85")); // Azul Texto
                tag.setBackgroundResource(R.drawable.tag_bg_blue); // Fondo Azul Claro
                tag.setPadding(25, 10, 25, 10);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 16, 0); // Margen derecho
                tag.setLayoutParams(params);

                container.addView(tag);
            }
        }

    }

}
