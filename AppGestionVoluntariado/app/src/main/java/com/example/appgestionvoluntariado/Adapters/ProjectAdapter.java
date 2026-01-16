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

import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.ViewMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectHolder> {

    private List<Project> projects;
    private final OnProjectActionListener listener;
    private final ViewMode mode;

    public interface OnProjectActionListener {
        void onAccept(Project project);
        void onReject(Project project);
        void onDelete(Project project);
        void onApply(Project project); // Nueva acción para voluntarios
    }

    public ProjectAdapter(List<Project> projects, OnProjectActionListener listener, ViewMode mode) {
        this.projects = new ArrayList<>(projects);
        this.listener = listener;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_card, parent, false);
        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder holder, int position) {
        holder.bind(projects.get(position), listener, mode);
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
        TextView title, zone, date;
        Button btnInfo;
        AppCompatButton btnPrimary, btnSecondary;

        public ProjectHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvProjectTitle);
            zone = itemView.findViewById(R.id.tvProjectZone);
            date = itemView.findViewById(R.id.tvProjectDate);
            btnInfo = itemView.findViewById(R.id.btnProjectInfo);
            btnPrimary = itemView.findViewById(R.id.btnPrimaryAction);
            btnSecondary = itemView.findViewById(R.id.btnSecondaryAction);
        }

        public void bind(Project project, OnProjectActionListener listener, ViewMode mode) {
            Context context = itemView.getContext();
            title.setText(project.getTitle());
            zone.setText("📍 " + (project.getAddress() != null ? project.getAddress() : "No especificada"));
            date.setText("📅 " + project.getStartDate());

            // Reset de visibilidad para evitar errores al reciclar vistas
            btnPrimary.setVisibility(View.GONE);
            btnSecondary.setVisibility(View.GONE);

            switch(mode) {
                case ADMINISTRATOR:
                    btnPrimary.setVisibility(View.VISIBLE);
                    btnSecondary.setVisibility(View.VISIBLE);
                    configureButton(btnPrimary, "ACEPTAR", Color.parseColor("#2E7D32"));
                    configureButton(btnSecondary, "RECHAZAR", Color.parseColor("#D32F2F"));
                    btnPrimary.setOnClickListener(v -> listener.onAccept(project));
                    btnSecondary.setOnClickListener(v -> listener.onReject(project));
                    break;

                case ORGANIZATION:
                    btnPrimary.setVisibility(View.VISIBLE);
                    // Usamos ContextCompat para obtener el color real del recurso
                    configureButton(btnPrimary, "DAR DE BAJA", ContextCompat.getColor(context, R.color.white));
                    btnPrimary.setOnClickListener(v -> listener.onDelete(project));
                    break; // Agregado break

                case VOLUNTEER_AVAILABLE:
                    btnPrimary.setVisibility(View.VISIBLE);
                    configureButton(btnPrimary, "APUNTARSE", ContextCompat.getColor(context, R.color.cuatrovientos_blue));
                    btnPrimary.setOnClickListener(v -> listener.onApply(project));
                    break;
                case VOLUNTEER_MY_PROJECTS:
                    btnPrimary.setVisibility(View.VISIBLE);
                    configureButton(btnPrimary, "DESAPUNTARSE", ContextCompat.getColor(context, R.color.cuatrovientos_blue));
                    btnPrimary.setOnClickListener(v -> listener.onDelete(project));
                    break;
            }
            btnInfo.setOnClickListener(v -> showDetailsBottomSheet(project));
        }

        private void configureButton(AppCompatButton btn, String text, int colorInt) {
            btn.setText(text);
            btn.setBackgroundTintList(ColorStateList.valueOf(colorInt));
            btn.setTextColor(Color.WHITE);
        }

        private void showDetailsBottomSheet(Project project) {
            BottomSheetDialog dialog = new BottomSheetDialog(itemView.getContext());
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_organization_project_details, null);
            dialog.setContentView(view);

            View parent = (View) view.getParent();
            parent.setBackgroundResource(android.R.color.transparent);

            TextView tvDesc = view.findViewById(R.id.tvDescripcionVolVal);
            TextView tvFechaFin = view.findViewById(R.id.tvFechaVal);
            ChipGroup chipGroupNeeds = view.findViewById(R.id.chipGroupNecesidades);
            View btnClose = view.findViewById(R.id.btnCerrarPopup);

            validateAndSet(tvDesc, project.getDescription());
            tvFechaFin.setText(project.getEndDate() != null ? project.getEndDate() : "Sin fecha de fin");

            if (project.getRequiredSkills() != null) {
                chipGroupNeeds.removeAllViews();
                for (String skill : project.getRequiredSkills()) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(skill);
                    chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light);
                    chipGroupNeeds.addView(chip);
                }
            }

            btnClose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        private void validateAndSet(TextView tv, String data) {
            if (data == null || data.trim().isEmpty()) {
                tv.setText("Información no proporcionada");
                tv.setTextColor(Color.GRAY);
                tv.setTypeface(null, android.graphics.Typeface.ITALIC);
            } else {
                tv.setText(data);
                tv.setTextColor(Color.BLACK);
                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }
}