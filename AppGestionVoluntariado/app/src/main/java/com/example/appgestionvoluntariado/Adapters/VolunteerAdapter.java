package com.example.appgestionvoluntariado.Adapters;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.R;

import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.GridHolder> {

    private List<Voluntario> voluntarios;


    public VolunteerAdapter(List<Voluntario> voluntarios) {
        this.voluntarios  = voluntarios;

    }
    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_org_voluntarios, parent, false);
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

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView email;


        public GridHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombre);
            email = itemView.findViewById(R.id.tvCorreo);

        }
        public void assingData(Voluntario voluntario) {
            nombre.setText(voluntario.getNombre());
            email.setText(voluntario.getEmail());
        }

    }

}
