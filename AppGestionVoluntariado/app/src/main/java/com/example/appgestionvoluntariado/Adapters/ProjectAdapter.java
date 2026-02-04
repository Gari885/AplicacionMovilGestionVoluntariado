package com.example.appgestionvoluntariado.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Need;
import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Skill;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.ViewMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for managing and displaying projects (Activities).
 * Variables in English, UI labels in Spanish [cite: 2026-01-16].
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectHolder> {

    private List<Project> projects;
    private final OnProjectActionListener actionListener;
    private ViewMode viewMode;
    private boolean showStatusLabel = false;



    public void updateAdapter(List<Project> list, ViewMode view) {

    }


    public interface OnProjectActionListener {
        void onAccept(Project project);
        void onReject(Project project);
        void onDelete(Project project);
        void onApply(Project project);
        void onEdit(Project project);
    }

    public ProjectAdapter(List<Project> projects, OnProjectActionListener listener, ViewMode mode) {
        this.projects = new ArrayList<>(projects);
        this.actionListener = listener;
        this.viewMode = mode;
    }

    public void setShowStatusLabel(boolean show) {
        this.showStatusLabel = show;
    }

    public void notifyAdapter(List<Project> projects) {
        this.projects.clear();
        this.projects = projects;
        notifyDataSetChanged();

    }

    public void notifyAdapterAdmin(List<Project> projects, ViewMode v) {
        this.projects.clear();
        this.projects = projects;
        viewMode = v;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_card, parent, false);
        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder holder, int position) {
        holder.bind(projects.get(position), actionListener, viewMode);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void updateList(List<Project> newList) {
        this.projects.clear();
        this.projects.addAll(newList);
        notifyDataSetChanged();
    }

    public class ProjectHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvZone, tvDate, tvOrgName, tvStatusLabel;
        Button btnInfo, btnEdit;
        AppCompatButton btnPrimary, btnSecondary;

        public ProjectHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProjectTitle);
            tvZone = itemView.findViewById(R.id.tvProjectZone);
            tvDate = itemView.findViewById(R.id.tvProjectDate);
            tvOrgName = itemView.findViewById(R.id.tvOrganizationName); // Nuevo campo del modelo
            btnInfo = itemView.findViewById(R.id.btnProjectInfo);
            btnPrimary = itemView.findViewById(R.id.btnPrimaryAction);
            btnSecondary = itemView.findViewById(R.id.btnSecondaryAction);
            tvStatusLabel = itemView.findViewById(R.id.tvStatusLabel);
            btnEdit = itemView.findViewById(R.id.btnProjectEdit);
        }

        public void bind(Project project, OnProjectActionListener listener, ViewMode mode) {
            Context context = itemView.getContext();

            // Actualizado para usar los nuevos campos del modelo [cite: 2026-01-09]
            tvTitle.setText(project.getName());
            tvZone.setText("📍 " + (project.getAddress() != null ? project.getAddress() : "No especificada"));
            tvDate.setText("📅 " + project.getStartDate());
            if (tvOrgName != null) {
                tvOrgName.setText("🏢 " + project.getOrganizationName());
            }

            if (project.getApprovalStatus() != null && project.getApprovalStatus().equalsIgnoreCase("aceptada")){
                showStatusLabel = true;
            }

            // Configurar etiqueta de estado [cite: 2026-01-18]
            if (showStatusLabel && tvStatusLabel != null) {
                tvStatusLabel.setVisibility(View.VISIBLE);
                String status = project.getStatus() != null ? project.getStatus() : "Desconocido";
                tvStatusLabel.setText(status);
                
                // Color por estado (Simple logic)
                int color = Color.parseColor("#757575"); // Default Gray
                if (status.equalsIgnoreCase("ABIERTA") || status.equalsIgnoreCase("EN CURSO")) color = Color.parseColor("#2E7D32"); // Green
                else if (status.equalsIgnoreCase("CERRADA") || status.equalsIgnoreCase("FINALIZADA")) color = Color.parseColor("#C62828"); // Red
                
                tvStatusLabel.setBackgroundTintList(ColorStateList.valueOf(color));
            } else if (tvStatusLabel != null) {
                tvStatusLabel.setVisibility(View.GONE);
            }

            btnPrimary.setVisibility(View.GONE);
            btnSecondary.setVisibility(View.GONE);

            // Lógica de botones por modo de visualización [cite: 2026-01-16]
            switch(viewMode) {
                case ADMINISTRATOR_PENDING:
                    btnPrimary.setVisibility(View.VISIBLE);
                    btnSecondary.setVisibility(View.VISIBLE);
                    btnPrimary.setText("ACEPTAR");
                    btnPrimary.setBackgroundResource(R.drawable.background_button_pill);
                    btnPrimary.setOnClickListener(v -> listener.onAccept(project));
                    btnSecondary.setOnClickListener(v -> listener.onReject(project));
                    break;

                case ADMINISTRATOR_ACCEPTED:
                    btnPrimary.setVisibility(View.VISIBLE);
                    btnPrimary.setText("DAR DE BAJA");
                    btnPrimary.setOnClickListener(v -> listener.onDelete(project));
                    break;

                case ORGANIZATION:
                    btnPrimary.setVisibility(View.VISIBLE);
                    btnPrimary.setText("DAR DE BAJA");
                    btnPrimary.setOnClickListener(v -> listener.onDelete(project));
                    
                    btnEdit.setVisibility(View.VISIBLE);
                    btnEdit.setOnClickListener(v -> listener.onEdit(project));
                    break;

                case VOLUNTEER_AVAILABLE:
                    btnPrimary.setVisibility(View.VISIBLE);
                    btnPrimary.setText("APUNTARSE");
                    btnPrimary.setOnClickListener(v -> listener.onApply(project));
                    break;

                case VOLUNTEER_MY_PROJECTS:
                    btnPrimary.setVisibility(View.VISIBLE);
                    btnPrimary.setText("DESAPUNTARSE");
                    btnPrimary.setOnClickListener(v -> listener.onDelete(project));
                    break;
            }
            btnInfo.setOnClickListener(v -> showDetailsBottomSheet(project));
        }

        private void showDetailsBottomSheet(Project project) {
            BottomSheetDialog dialog = new BottomSheetDialog(itemView.getContext());
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_organization_project_details, null);
            dialog.setContentView(view);

            View parent = (View) view.getParent();
            parent.setBackgroundResource(android.R.color.transparent);

            TextView tvDesc = view.findViewById(R.id.tvDescripcionVolVal);
            TextView tvFechaFin = view.findViewById(R.id.tvFechaVal);
            TextView tvParticipantes = view.findViewById(R.id.tvMaxParticipantes); // Campo nuevo
            ChipGroup chipGroupOds = view.findViewById(R.id.chipGroupOds);
            ChipGroup chipGroupSkills = view.findViewById(R.id.chipGroupHabilidades);
            ChipGroup chipGroupNeeds = view.findViewById(R.id.chipGroupNecesidades);
            View btnClose = view.findViewById(R.id.btnCerrarPopup);

            // Mapeo de datos detallados
            tvDesc.setText(project.getName()); // Usando nombre como descripción si no hay campo dedicado
            tvFechaFin.setText(project.getEndDate() != null ? project.getEndDate() : "Indefinida");
            if (tvParticipantes != null) {
                tvParticipantes.setText("Máx. Participantes: " + project.getMaxParticipants());
            }

            // Procesamiento de ODS [cite: 2026-01-16]
            if (project.getOdsList() != null && chipGroupOds != null) {
                chipGroupOds.removeAllViews();
                for (Ods ods : project.getOdsList()) {
                    addChipToGroup(chipGroupOds, ods.getName(), R.color.cuatrovientos_blue);
                }
            }

            // Procesamiento de Habilidades
            if (project.getSkillsList() != null && chipGroupSkills != null) {
                chipGroupSkills.removeAllViews();
                for (Skill skill : project.getSkillsList()) {
                    addChipToGroup(chipGroupSkills, skill.getName(), R.color.cuatrovientos_blue_light);
                }
            }

            // Procesamiento de Necesidades
            if (project.getNeedsList() != null && chipGroupNeeds != null) {
                chipGroupNeeds.removeAllViews();
                for (Need need : project.getNeedsList()) {
                    addChipToGroup(chipGroupNeeds, need.getName(), R.color.cuatrovientos_blue);
                }
            }

            btnClose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        private void addChipToGroup(ChipGroup group, String text, int colorRes) {
            Chip chip = new Chip(itemView.getContext());
            chip.setText(text);
            chip.setChipBackgroundColorResource(colorRes);
            chip.setTextColor(Color.WHITE);
            group.addView(chip);
        }
    }
}