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

public class AdaptadorDashboard extends RecyclerView.Adapter<AdaptadorDashboard.GridHolder> {

    private List<Stat> stats;


    public AdaptadorDashboard(List<Stat> stats) {
        this.stats  = stats;

    }
    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_dashboard_item, parent, false);
        return new GridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assingData(stats.get(position));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    public class GridHolder extends RecyclerView.ViewHolder {

        //Mirar como sacar el icono bien
        //TextView textoIcono;
        TextView textoTitulo;
        TextView numStat1;
        TextView textoStat2;
        int icon;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            //textoIcono = itemView.findViewById(R.id.iconoImg);
            textoTitulo = itemView.findViewById(R.id.titulo);
            numStat1 = itemView.findViewById(R.id.stats1);
            textoStat2 = itemView.findViewById(R.id.stats2);
        }
        public void assingData(Stat stat) {
            textoTitulo.setText(stat.getTitulo());
            String stat1 = "" + stat.getStat1() + "";
            numStat1.setText(stat1);
            textoStat2.setText(stat.getStat2());
            icon = stat.getIcon2();
            //setIcon(icon);
        }

        /*private void setIcon(String icon) {
            switch (icon) {
                case "voluntarios":
                    textoIcono.setText("👥");
                    break;
                case "organizacion":
                    textoIcono.setText("🏢");
                    break;
                case "matches":
                    textoIcono.setText("❤️");
                    break;
                case "pendientes":
                    textoIcono.setText("🕒");
                    break;
                default:
                    textoIcono.setText("?");
                    break;
            }

        }
        */
    }

}
