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

import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrgHolder> {

    private List<Organization> organizations;
    private final OnOrgActionListener listener;

    // Interfaz para que el Fragment gestione la API y los tokens
    public interface OnOrgActionListener {
        void onAccept(Organization org);
        void onReject(Organization org);
        void onDelete(Organization org);
        void onDetails(Organization org);
    }

    public OrganizationAdapter(List<Organization> organizations, OnOrgActionListener listener) {
        this.organizations = new ArrayList<>(organizations);
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el item unificado con los botones de 110dp
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization_card, parent, false);
        return new OrgHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgHolder holder, int position) {
        holder.bind(organizations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public void updateList(List<Organization> newList) {
        this.organizations.clear();
        this.organizations.addAll(newList);
        notifyDataSetChanged();
    }

    public class OrgHolder extends RecyclerView.ViewHolder {
        TextView name, cif, email;
        MaterialButton btnInfo;
        AppCompatButton btnAccept, btnReject;
        ImageView btnBaja;
        LinearLayout pendingLayout;

        public OrgHolder(@NonNull View itemView) {
            super(itemView);
            // IDs del XML final
            name = itemView.findViewById(R.id.tvOrganizationName);
            cif = itemView.findViewById(R.id.tvOrganizationCif);
            email = itemView.findViewById(R.id.tvOrganizationEmail);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            pendingLayout = itemView.findViewById(R.id.layoutActions);
            btnBaja = itemView.findViewById(R.id.btnDeleteOrganization);
        }

        public void bind(Organization org, OnOrgActionListener listener) {
            name.setText(org.getName());
            cif.setText("CIF: " + org.getVat());
            email.setText(org.getEmail());

            // Lógica visual basada en el estado del objeto
            if ("pendiente".equalsIgnoreCase(org.getStatus())) {
                pendingLayout.setVisibility(View.VISIBLE);
                btnBaja.setVisibility(View.GONE);

                // El adaptador solo avisa al fragmento
                btnAccept.setOnClickListener(v -> listener.onAccept(org));
                btnReject.setOnClickListener(v -> listener.onReject(org));
            } else {
                pendingLayout.setVisibility(View.GONE);
                btnBaja.setVisibility(View.VISIBLE);

                btnBaja.setOnClickListener(v -> listener.onDelete(org));
            }

            // Estilo DETALLES unificado
            btnInfo.setOnClickListener(v -> listener.onDetails(org));
        }
    }
}