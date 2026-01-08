package com.example.appgestionvoluntariado.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.R;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.GridHolder> {

    private List<Stat> stats;

    public DashboardAdapter(List<Stat> stats) {
        this.stats = stats;
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization_dashboard_card, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assignData(stats.get(position));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    public class GridHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView numStat1;
        TextView textStat2;
        int icon;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.titulo);
            numStat1 = itemView.findViewById(R.id.stats1);
            textStat2 = itemView.findViewById(R.id.stats2);
        }

        public void assignData(Stat stat) {
            textTitle.setText(stat.getTitle());
            numStat1.setText(String.valueOf(stat.getCount()));
            textStat2.setText(stat.getDescription());
            icon = stat.getIconResId();
        }
    }
}
