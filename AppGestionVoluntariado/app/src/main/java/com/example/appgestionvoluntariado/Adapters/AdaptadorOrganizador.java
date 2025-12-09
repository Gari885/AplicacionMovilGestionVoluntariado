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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorOrganizador extends RecyclerView.Adapter<AdaptadorOrganizador.GridHolder> {

    private List<Organizacion> organizaciones;

    public AdaptadorOrganizador(List<Organizacion> organizaciones) {
        this.organizaciones  = organizaciones;
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

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView nombre;

        TextView email;



        ImageView info;


        public GridHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreOrg);
            email = itemView.findViewById(R.id.tvCorreo);
            info = itemView.findViewById(R.id.btnInfoVoluntariado);
        }
        public void assingData(Organizacion organizacion) {
            nombre.setText(organizacion.getNombre());
            email.setText(organizacion.getEmail());
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

                    descripcion.setText(organizacion.getDescripcion());
                    horarioHabitual.setText(organizacion.getHorarioHabitual());

                    rellenarTags(context,necesidades,organizacion.getNecesidades());

                    // 4. Mostrar
                    builder.setView(popupView);
                    AlertDialog dialog = builder.create();
                    if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cerrar.setOnClickListener(x -> dialog.dismiss());
                    dialog.show();

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
