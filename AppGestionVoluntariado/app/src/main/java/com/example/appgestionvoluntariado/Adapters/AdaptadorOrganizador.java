package com.example.appgestionvoluntariado.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.EstadoRequest;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaptadorOrganizador extends RecyclerView.Adapter<AdaptadorOrganizador.GridHolder> {

    private List<Organizacion> organizaciones;


    public AdaptadorOrganizador(List<Organizacion> organizaciones) {
        this.organizaciones  = new ArrayList<>(organizaciones);
    }
    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_organizaciones_item, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assingData(organizaciones.get(position));

    }

    @Override
    public int getItemCount() {
        return organizaciones.size();
    }

    public void actualizarDatos(List<Organizacion> organizacionsFiltradas) {

        this.organizaciones.clear();

        this.organizaciones.addAll(organizacionsFiltradas);

        notifyDataSetChanged();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView email;
        ImageView info;

        AppCompatButton btnAcetpar,btnRechazar;
        ImageView btnBaja;

        LinearLayout pendientes;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreOrg);
            email = itemView.findViewById(R.id.tvCorreoOrg);
            info = itemView.findViewById(R.id.btnInfoOrg);
            btnAcetpar = itemView.findViewById(R.id.btnAceptarOrg);
            btnRechazar = itemView.findViewById(R.id.btnRechazarOrg);
            pendientes = itemView.findViewById(R.id.layoutPendientes);
            btnBaja = itemView.findViewById(R.id.btnDarBaja);
        }
        public void assingData(Organizacion org) {

            String estado = org.getEstado();

            if (estado.equals("pendiente")) {
                // ESTADO PENDIENTE: Muestra Check/X, oculta Papelera
                pendientes.setVisibility(View.VISIBLE);
                btnBaja.setVisibility(View.GONE);

                btnAcetpar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cambiarEstadoOrganizacion(org,"aprobado", itemView.getContext());
                    }

                });

                btnRechazar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cambiarEstadoOrganizacion(org,"rechazado",itemView.getContext());
                    }
                });



            } else {
                pendientes.setVisibility(View.GONE);
                btnBaja.setVisibility(View.VISIBLE);

                btnBaja.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarDialogoConfirmacionBaja(v.getContext(), org);
                    }
                });
            }
            nombre.setText(org.getNombre());
            email.setText(org.getEmail());


            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_info_organizacion,null);


                    TextView descripcion = popupView.findViewById(R.id.tvDescripcionVal);
                    TextView horarioHabitual = popupView.findViewById(R.id.tvHorarioVal);
                    LinearLayout necesidades = popupView.findViewById(R.id.containerNecesidades);
                    LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

                    descripcion.setText(org.getDescripcion());


                    /*horarioHabitual.setText(organizacion.get());

                    rellenarTags(context,necesidades,organizacion.getNecesidades());

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

        private void mostrarDialogoConfirmacionBaja(Context context, Organizacion org) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("¿Dar de baja?");
            builder.setMessage("Estás a punto de dar de baja a " + org.getNombre() + ".\n\nEsta organización perderá el acceso y pasará a estado 'Rechazado'. ¿Estás seguro?");

            // Botón SÍ (Destructivo)
            builder.setPositiveButton("Sí, dar de baja", (dialogInterface, i) -> {
                // Llamamos a tu método de API enviando la acción "DarBaja"
                // Pasamos 'null' en el 3er parámetro porque aquí no hay un popup personalizado que cerrar
                cambiarEstadoOrganizacion(org, "rechazado", context);
            });

            // Botón NO (Cancelar)
            builder.setNegativeButton("Cancelar", (dialogInterface, i) -> {
                dialogInterface.dismiss(); // Simplemente cierra el aviso
            });

            // Crear y mostrar
            AlertDialog dialog = builder.create();
            dialog.show();

            // Opcional: Poner el botón de "Sí" en rojo para indicar peligro
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
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

        private void cambiarEstadoOrganizacion(Organizacion org,String nuevoEstado,Context context) {
            OrganizationAPIService apiService = APIClient.getOrganizationAPIService();
            EstadoRequest body = new EstadoRequest(nuevoEstado);
            Call<Organizacion> call  = apiService.actualizarEstado(org.getCif(), body);

            call.enqueue(new Callback<Organizacion>() {
                @Override
                public void onResponse(Call<Organizacion> call, Response<Organizacion> response) {
                    if (response.isSuccessful()){
                        org.setEstado(nuevoEstado);
                        organizaciones.remove(org);
                        notifyDataSetChanged();

                        Toast.makeText(context, "Organización " + nuevoEstado, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Organizacion> call, Throwable t) {
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

}
