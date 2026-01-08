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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Importante para colores seguros
import androidx.recyclerview.widget.RecyclerView;


import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.ModoVista;
import com.example.appgestionvoluntariado.R;

import java.util.List;

public class AdaptadorVoluntariado extends RecyclerView.Adapter<AdaptadorVoluntariado.GridHolder> {

    private List<Voluntariado> voluntariados;
    private Context context;
    private ModoVista modoActual;
    private OnItemAction listener;

    // 1. INTERFAZ PARA COMUNICAR CLICKS AL FRAGMENT
    public interface OnItemAction {
        void onAccionPrincipal(Voluntariado item);
        void onAccionSecundaria(Voluntariado item);
    }

    // 2. CONSTRUCTOR ACTUALIZADO (Recibe Contexto, Modo y Listener)
    public AdaptadorVoluntariado(Context context, List<Voluntariado> voluntariados, ModoVista modo, OnItemAction listener) {
        this.context = context;
        this.voluntariados = voluntariados;
        this.modoActual = modo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asegúrate de que 'org_voluntariado_item' tiene los botones nuevos (btnAccionPrincipal, etc.)
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_voluntariado_item, parent, false);
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
        TextView titulo, zona, fecha;
        // Cambiamos ImageView por Button (o MaterialButton) para el Info
        Button info;
        Button btnPrincipal, btnSecundario;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTituloVoluntariado);
            zona = itemView.findViewById(R.id.tvZonaVoluntariado);
            fecha = itemView.findViewById(R.id.tvFechaVoluntariado);

            // IDs NUEVOS DEL XML MODERNO
            info = itemView.findViewById(R.id.btnInfoVoluntariado);
            btnPrincipal = itemView.findViewById(R.id.btnAccionPrincipal);
            btnSecundario = itemView.findViewById(R.id.btnAccionSecundaria);
        }

        public void assingData(Voluntariado voluntariado) {

            titulo.setText(voluntariado.getTitulo());
            zona.setText("📍 " + voluntariado.getDireccion()); // Añadimos emoji por código o en XML
            fecha.setText("📅 " + voluntariado.getFechaInicio());

            // Reseteamos visibilidad
            btnPrincipal.setVisibility(View.GONE);
            btnSecundario.setVisibility(View.GONE);

            // --- LÓGICA DE BOTONES SEGÚN EL ROL ---
            switch (modoActual) {
                // MODO ADMIN: Ver solicitudes
                case ADMINISTRADOR:
                    configurarBoton(btnPrincipal, "Aceptar", android.R.color.holo_green_dark);
                    configurarBoton(btnSecundario, "Rechazar", android.R.color.holo_red_dark); // false = estilo borde (opcional)
                    break;

                // MODO ORG: Gestionar mis voluntariados
                case ORGANIZACION:
                    configurarBoton(btnPrincipal, "Borrar", android.R.color.holo_red_dark);
                    break;

                case VOLUNTARIO_DISPONIBLES:
                    // Aquí SOLO mostramos ofertas nuevas -> Botón VERDE
                    configurarBoton(btnPrincipal, "Apuntarse", android.R.color.holo_green_dark);
                    break;

                case VOLUNTARIO_MIS_VOLUNTARIADOS:
                    // Aquí SOLO mostramos lo que ya tengo -> Botón ROJO
                    configurarBoton(btnPrincipal, "Desapuntarse", android.R.color.holo_red_dark);
                    break;
            }

            // Listeners
            btnPrincipal.setOnClickListener(v -> listener.onAccionPrincipal(voluntariado));
            btnSecundario.setOnClickListener(v -> listener.onAccionSecundaria(voluntariado));

            // Listener Info (Abre tu popup original)
            info.setOnClickListener(v -> mostrarPopupInfo(voluntariado));
        }

        // Método auxiliar para configurar botones rápidamente
        private void configurarBoton(Button btn, String texto, int colorResId) {
            btn.setVisibility(View.VISIBLE);
            btn.setText(texto);

            // NOTA: Si usas MaterialButton en el XML, puedes cambiar el estilo programáticamente
            // o simplemente cambiar el color de fondo/texto aquí.
            // Para simplificar, cambiamos solo el color de fondo:
            btn.setBackgroundTintList(ContextCompat.getColorStateList(context, colorResId));
            btn.setTextColor(Color.WHITE);
        }

        // He sacado tu lógica del popup a un método separado para limpiar el código
        private void mostrarPopupInfo(Voluntariado voluntariado) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View popupView = LayoutInflater.from(context).inflate(R.layout.org_dialog_info_voluntariado, null);

            TextView descripcion = popupView.findViewById(R.id.tvDescripcionVolVal);
            TextView tvFecha = popupView.findViewById(R.id.tvFechaVal);
            LinearLayout necesidades = popupView.findViewById(R.id.containerNecesidadesVol);
            LinearLayout ods = popupView.findViewById(R.id.containerODS);
            LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

            // Asignar datos al popup
            descripcion.setText(voluntariado.getDescripcion()); // Asumiendo que tienes getDescripcion
            tvFecha.setText(voluntariado.getFechaFin());

            rellenarTags(context, necesidades, voluntariado.getNecesidades());
            rellenarTags(context, ods, voluntariado.getOds());

            builder.setView(popupView);
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            cerrar.setOnClickListener(x -> dialog.dismiss());
            dialog.show();
        }

        private void rellenarTags(Context context, LinearLayout container, List<String> lista) {
            container.removeAllViews();

            if (lista == null) return;
            for (String texto : lista) {
                TextView tag = new TextView(context);
                tag.setText(texto);
                tag.setTextSize(12);
                tag.setTextColor(Color.parseColor("#1A3B85"));
                tag.setBackgroundResource(R.drawable.tag_bg_blue);
                tag.setPadding(25, 10, 25, 10);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 16, 0);
                tag.setLayoutParams(params);

                container.addView(tag);
            }
        }
    }
}