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

import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorVoluntariado extends RecyclerView.Adapter<AdaptadorVoluntariado.GridHolder> {

    private List<Voluntariado> voluntariados;

    public AdaptadorVoluntariado(List<Voluntariado> voluntariados) {
        this.voluntariados  = voluntariados;
    }
    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_voluntariado_item, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assingData(voluntariados.get(position));
    }


    @Override
    public int getItemCount() {
        return voluntariados.size();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView titulo;

        TextView zona;
        TextView fecha;

        ImageView info;


        public GridHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTituloVoluntariado);
            zona = itemView.findViewById(R.id.tvZonaVoluntariado);
            fecha = itemView.findViewById(R.id.tvFechaVoluntariado);
            info = itemView.findViewById(R.id.btnInfoVoluntariado);
        }
        public void assingData(Voluntariado voluntariado) {
            titulo.setText(voluntariado.getTitulo());
            zona.setText(voluntariado.getDireccion());
            fecha.setText(voluntariado.getFechaInicio());
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View popupView = LayoutInflater.from(context).inflate(R.layout.org_dialog_info_voluntariado,null);

                    TextView descripcion = popupView.findViewById(R.id.tvDescripcionVolVal);
                    TextView fecha = popupView.findViewById(R.id.tvFechaVal);
                    LinearLayout necesidades = popupView.findViewById(R.id.containerNecesidadesVol);
                    LinearLayout ods = popupView.findViewById(R.id.containerODS);
                    LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

                    fecha.setText(voluntariado.getFechaFin());

                    rellenarTags(context,necesidades,voluntariado.getNecesidades());
                    rellenarTags(context,ods,voluntariado.getOds());

                    // 4. Mostrar
                    builder.setView(popupView);
                    AlertDialog dialog = builder.create();
                    if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cerrar.setOnClickListener(x -> dialog.dismiss());
                    dialog.show();

                }
            });

        }

        private void rellenarTags(Context context, LinearLayout container, List<String> lista) {
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
