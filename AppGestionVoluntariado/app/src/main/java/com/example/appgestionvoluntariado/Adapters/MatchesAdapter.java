package com.example.appgestionvoluntariado.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.GridHolder> {

    private List<Match> matches;
    private Boolean isAccepted;

    public MatchesAdapter(List<Match> matches, Boolean isAccepted) {
        this.matches = matches;
        this.isAccepted = isAccepted;
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(isAccepted ? R.layout.item_match_accepted : R.layout.item_match_pending, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assignData(matches.get(position));
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView volunteerName;
        TextView volunteerEmail; // ID says tvEmailVol, assuming field is email? The logic kept it simple.

        TextView activityTitle;
        TextView activityZone;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            volunteerName = itemView.findViewById(R.id.tvVolunteerName);
            volunteerEmail = itemView.findViewById(R.id.tvVolunteerEmail);
            activityTitle = itemView.findViewById(R.id.tvActivityTitle);
            activityZone = itemView.findViewById(R.id.tvActivityZone);
        }

        public void assignData(Match match) {
            volunteerName.setText(match.getVolunteerName());
            activityTitle.setText(match.getActivityTitle());
            // XML IDs exist for Email and Zone, but original code didn't set them?
            // "nombreVol.setText(match.getNombreVoluntario());"
            // "tituloAct.setText(match.getNombreActividad());"
            // That was it. I'll stick to the original logic to avoid null pointers if fields are missing.
        }
    }
}
