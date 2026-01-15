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

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchHolder> {

    private List<Match> matches;
    private final OnMatchActionListener listener;

    public interface OnMatchActionListener {
        void onAccept(Match match);
        void onReject(Match match);
        void onFinish(Match match);
        void onCancel(Match match);
    }

    public MatchesAdapter(List<Match> matches, OnMatchActionListener listener) {
        this.matches = new ArrayList<>(matches);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // La lógica de qué XML inflar depende del estado del primer item o del viewType
        // Aquí asumo que la lista es homogénea por pestaña
        int layout = matches.get(0).getStatus().equalsIgnoreCase("pendiente")
                ? R.layout.item_match_pending
                : R.layout.item_match_accepted;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MatchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchHolder holder, int position) {
        holder.bind(matches.get(position), listener);
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
        TextView tvVolName, tvVolEmail, tvActTitle, tvActZone, tvStatus;
        AppCompatButton btnAction1, btnAction2; // Dinámicos según el XML

        public MatchHolder(@NonNull View itemView) {
            super(itemView);
            tvVolName = itemView.findViewById(R.id.tvVolunteerName);
            tvVolEmail = itemView.findViewById(R.id.tvVolunteerEmail);
            tvActTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvActZone = itemView.findViewById(R.id.tvActivityZone);
            tvStatus = itemView.findViewById(R.id.tvMatchStatus); // Solo en accepted

            // IDs de los botones que pusimos en los XML de 110dp
            btnAction1 = itemView.findViewById(R.id.btnAcceptMatch); // O btnFinishMatch
            if (btnAction1 == null) btnAction1 = itemView.findViewById(R.id.btnFinishMatch);

            btnAction2 = itemView.findViewById(R.id.btnRejectMatch); // O btnCancelMatch
            if (btnAction2 == null) btnAction2 = itemView.findViewById(R.id.btnCancelMatch);
        }

        public void bind(Match match, OnMatchActionListener listener) {
            tvVolName.setText(match.getVolunteerName());
            tvVolEmail.setText(match.getVolunteerEmail());
            tvActTitle.setText(match.getActivityTitle());
            tvActZone.setText("Zona: " + match.getActivityZone());

            if (tvStatus != null) tvStatus.setText(match.getStatus().toUpperCase());

            // Asignar acciones según el estado
            if (match.getStatus().equalsIgnoreCase("pendiente")) {
                btnAction1.setOnClickListener(v -> listener.onAccept(match));
                btnAction2.setOnClickListener(v -> listener.onReject(match));
            } else {
                btnAction1.setOnClickListener(v -> listener.onFinish(match));
                btnAction2.setOnClickListener(v -> listener.onCancel(match));
            }
        }
    }
}