package com.example.appgestionvoluntariado.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for managing and displaying matches (Inscripciones).
 * Logic in English, UI text in Spanish [cite: 2026-01-16, 2026-01-09].
 */
public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchHolder> {

    private List<Match> matches;
    private final OnMatchActionListener actionListener;

    public interface OnMatchActionListener {
        void onAccept(Match match);
        void onReject(Match match);
        void onFinish(Match match);
        void onCancel(Match match);
    }

    public MatchesAdapter(List<Match> matches, OnMatchActionListener listener) {
        this.matches = new ArrayList<>(matches);
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public MatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Decide layout based on the first item's status [cite: 2026-01-16]
        int layout = matches.get(0).getStatus().equalsIgnoreCase("pendiente")
                ? R.layout.item_match_pending
                : R.layout.item_match_accepted;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MatchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchHolder holder, int position) {
        holder.bind(matches.get(position), actionListener);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public void updateList(List<Match> newList) {
        this.matches.clear();
        this.matches.addAll(newList);
        notifyDataSetChanged();
    }

    public class MatchHolder extends RecyclerView.ViewHolder {
        TextView tvVolName, tvVolEmail, tvActTitle, tvOrgEmail, tvStatus;
        AppCompatButton btnAction1, btnAction2;

        public MatchHolder(@NonNull View itemView) {
            super(itemView);
            tvVolName = itemView.findViewById(R.id.tvVolunteerName);
            tvVolEmail = itemView.findViewById(R.id.tvVolunteerEmail);
            tvActTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvOrgEmail = itemView.findViewById(R.id.tvOrganizationEmail); // Updated ID [cite: 2026-01-09]
            tvStatus = itemView.findViewById(R.id.tvMatchStatus);

            // Dynamic binding for buttons in layouts [cite: 2026-01-16]
            btnAction1 = itemView.findViewById(R.id.btnAcceptMatch);
            if (btnAction1 == null) btnAction1 = itemView.findViewById(R.id.btnFinishMatch);

            btnAction2 = itemView.findViewById(R.id.btnRejectMatch);
            if (btnAction2 == null) btnAction2 = itemView.findViewById(R.id.btnCancelMatch);
        }

        public void bind(Match match, OnMatchActionListener listener) {
            // Data binding using updated Match model [from updated response turn]
            tvVolName.setText(match.getVolunteerName());
            tvVolEmail.setText(match.getVolunteerEmail());
            tvActTitle.setText(match.getActivityName());

            // Replaced missing zone with organization email as per new schema [from updated response turn]
            if (tvOrgEmail != null) {
                tvOrgEmail.setText("Org: " + match.getOrganizationEmail());
            }


            // Button logic for PENDIENTE vs OTHER states [cite: 2026-01-16]
            if (match.getStatus().equalsIgnoreCase("pendiente")) {
                if (btnAction1 != null) btnAction1.setOnClickListener(v -> listener.onAccept(match));
                if (btnAction2 != null) btnAction2.setOnClickListener(v -> listener.onReject(match));
            } else {
                if (btnAction1 != null) btnAction1.setOnClickListener(v -> listener.onFinish(match));
                if (btnAction2 != null) btnAction2.setOnClickListener(v -> listener.onCancel(match));
            }
        }
    }
}