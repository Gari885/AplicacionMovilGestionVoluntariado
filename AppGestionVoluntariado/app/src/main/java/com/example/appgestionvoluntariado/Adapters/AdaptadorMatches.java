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

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorMatches extends RecyclerView.Adapter<AdaptadorMatches.GridHolder> {

    private List<Match> matches;
    private Boolean aceptado;

    public AdaptadorMatches(List<Match> matches, Boolean aceptado) {
        this.matches  = matches;
        this.aceptado = aceptado;
    }
    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(aceptado ? R.layout.org_matches_aceptados_item : R.layout.org_matches_pendientes_item, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assingData(matches.get(position));

    }


    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView nombreVol;
        TextView emailVol;

        TextView tituloAct;
        TextView zonaAct;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            nombreVol = itemView.findViewById(R.id.tvNombreVol);
            emailVol = itemView.findViewById(R.id.tvEmailVol);
            tituloAct = itemView.findViewById(R.id.tvTituloActividad);
            zonaAct = itemView.findViewById(R.id.tvZonaActividad);
        }
        public void assingData(Match match) {

            nombreVol.setText(match.getVoluntario().getNombre());
            emailVol.setText(match.getVoluntario().getEmail());
            tituloAct.setText(match.getActividad().getTitulo());
            zonaAct.setText(match.getActividad().getDireccion());

        }

    }

}
