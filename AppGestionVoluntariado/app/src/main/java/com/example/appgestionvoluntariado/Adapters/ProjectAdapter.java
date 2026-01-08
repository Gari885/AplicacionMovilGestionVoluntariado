package com.example.appgestionvoluntariado.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.ViewMode;
import com.example.appgestionvoluntariado.R;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.GridHolder> {

    private List<Project> projects;
    private Context context;
    private ViewMode currentMode;
    private OnItemAction listener;

    public interface OnItemAction {
        void onPrimaryAction(Project item);
        void onSecondaryAction(Project item);
    }

    public ProjectAdapter(Context context, List<Project> projects, ViewMode mode, OnItemAction listener) {
        this.context = context;
        this.projects = projects;
        this.currentMode = mode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_card, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assignData(projects.get(position));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView title, zone, date;
        Button info;
        Button btnPrimary, btnSecondary;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvProjectTitle);
            zone = itemView.findViewById(R.id.tvProjectZone);
            date = itemView.findViewById(R.id.tvProjectDate);

            info = itemView.findViewById(R.id.btnProjectInfo);
            btnPrimary = itemView.findViewById(R.id.btnPrimaryAction);
            btnSecondary = itemView.findViewById(R.id.btnSecondaryAction);
        }

        public void assignData(Project project) {
            title.setText(project.getTitle());
            zone.setText("📍 " + project.getAddress());
            date.setText("📅 " + project.getStartDate());

            btnPrimary.setVisibility(View.GONE);
            btnSecondary.setVisibility(View.GONE);

            if (currentMode != null) {
                switch (currentMode) {
                    case ADMINISTRATOR:
                        configureButton(btnPrimary, "Aceptar", android.R.color.holo_green_dark);
                        configureButton(btnSecondary, "Rechazar", android.R.color.holo_red_dark);
                        break;
                    case ORGANIZATION:
                        configureButton(btnPrimary, "Borrar", android.R.color.holo_red_dark);
                        break;
                    case VOLUNTEER_AVAILABLE:
                        configureButton(btnPrimary, "Apuntarse", android.R.color.holo_green_dark);
                        break;
                    case VOLUNTEER_MY_PROJECTS:
                        configureButton(btnPrimary, "Desapuntarse", android.R.color.holo_red_dark);
                        break;
                }
            }

            btnPrimary.setOnClickListener(v -> listener.onPrimaryAction(project));
            btnSecondary.setOnClickListener(v -> listener.onSecondaryAction(project));
            info.setOnClickListener(v -> showInfoPopup(project));
        }

        private void configureButton(Button btn, String text, int colorResId) {
            btn.setVisibility(View.VISIBLE);
            btn.setText(text);
            btn.setBackgroundTintList(ContextCompat.getColorStateList(context, colorResId));
            btn.setTextColor(Color.WHITE);
        }

        private void showInfoPopup(Project project) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_organization_project_details, null);

            TextView description = popupView.findViewById(R.id.tvDescripcionVolVal);
            TextView tvDate = popupView.findViewById(R.id.tvFechaVal);
            LinearLayout needsContainer = popupView.findViewById(R.id.containerNecesidadesVol);
            LinearLayout odsContainer = popupView.findViewById(R.id.containerODS);
            LinearLayout closeBtn = popupView.findViewById(R.id.btnCerrarPopup);

            description.setText(project.getDescription());
            tvDate.setText(project.getEndDate());

            populateTags(context, needsContainer, project.getRequiredSkills());
            populateTags(context, odsContainer, project.getOds());

            builder.setView(popupView);
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            closeBtn.setOnClickListener(x -> dialog.dismiss());
            dialog.show();
        }

        private void populateTags(Context context, LinearLayout container, List<String> list) {
            container.removeAllViews();
            if (list == null) return;
            for (String text : list) {
                TextView tag = new TextView(context);
                tag.setText(text);
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
