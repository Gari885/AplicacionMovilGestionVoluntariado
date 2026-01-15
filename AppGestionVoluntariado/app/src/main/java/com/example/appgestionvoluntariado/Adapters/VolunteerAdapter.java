package com.example.appgestionvoluntariado.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {

    private List<Volunteer> volunteerList;
    private final OnVolunteerActionListener listener;

    // Interfaz para que el Fragment gestione la lógica y los tokens
    public interface OnVolunteerActionListener {
        void onAccept(Volunteer volunteer);
        void onReject(Volunteer volunteer);
        void onDelete(Volunteer volunteer);
        void onDetails(Volunteer volunteer);
    }

    public VolunteerAdapter(List<Volunteer> volunteerList, OnVolunteerActionListener listener) {
        this.volunteerList = new ArrayList<>(volunteerList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el item unificado con el botón DETALLES y botones de 110dp
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_volunteer_card, parent, false);
        return new VolunteerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position) {
        holder.bind(volunteerList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public void updateList(List<Volunteer> newList) {
        this.volunteerList.clear();
        this.volunteerList.addAll(newList);
        notifyDataSetChanged();
    }

    public class VolunteerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIdentifier, tvEmail;
        MaterialButton btnInfo;
        AppCompatButton btnAccept, btnReject;
        ImageView btnDelete;
        LinearLayout layoutActions;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referencias a los IDs del XML unificado
            tvName = itemView.findViewById(R.id.tvName);
            tvIdentifier = itemView.findViewById(R.id.tvIdentifier);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnDelete = itemView.findViewById(R.id.btnDeleteVolunteer);
        }

        public void bind(Volunteer volunteer, OnVolunteerActionListener listener) {
            tvName.setText(volunteer.getFullName());
            tvIdentifier.setText("DNI: " + volunteer.getDni()); // Identificador para voluntarios
            tvEmail.setText(volunteer.getEmail());

            // Lógica visual basada en el estado del objeto
            if ("pendiente".equalsIgnoreCase(volunteer.getStatus())) {
                layoutActions.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);

                btnAccept.setOnClickListener(v -> listener.onAccept(volunteer));
                btnReject.setOnClickListener(v -> listener.onReject(volunteer));
            } else {
                layoutActions.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);

                btnDelete.setOnClickListener(v -> listener.onDelete(volunteer));
            }

            // Botón DETALLES unificado (Estilo texto + icono)
            btnInfo.setOnClickListener(v -> listener.onDetails(volunteer));
        }
    }
}