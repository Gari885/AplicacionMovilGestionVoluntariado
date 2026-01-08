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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.GridHolder> {

    private List<Organization> organizations;

    public OrganizationAdapter(List<Organization> organizations) {
        this.organizations = new ArrayList<>(organizations);
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization_card, parent, false);
        return new GridHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        holder.assignData(organizations.get(position));
    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public void updateData(List<Organization> filteredOrganizations) {
        this.organizations.clear();
        this.organizations.addAll(filteredOrganizations);
        notifyDataSetChanged();
    }

    public class GridHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        ImageView info;

        AppCompatButton btnAccept, btnReject;
        ImageView btnBaja;

        LinearLayout pendingLayout;

        public GridHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvOrganizationName);
            email = itemView.findViewById(R.id.tvOrganizationEmail);
            info = itemView.findViewById(R.id.btnOrganizationInfo);
            btnAccept = itemView.findViewById(R.id.btnAcceptOrganization);
            btnReject = itemView.findViewById(R.id.btnRejectOrganization);
            pendingLayout = itemView.findViewById(R.id.layoutPending);
            btnBaja = itemView.findViewById(R.id.btnDeleteOrganization);
        }

        public void assignData(Organization org) {
            String status = org.getStatus();

            if ("pendiente".equals(status)) {
                pendingLayout.setVisibility(View.VISIBLE);
                btnBaja.setVisibility(View.GONE);

                btnAccept.setOnClickListener(v -> changeOrganizationStatus(org, "aprobado", itemView.getContext()));
                btnReject.setOnClickListener(v -> changeOrganizationStatus(org, "rechazado", itemView.getContext()));

            } else {
                pendingLayout.setVisibility(View.GONE);
                btnBaja.setVisibility(View.VISIBLE);

                btnBaja.setOnClickListener(v -> showConfirmDeleteDialog(v.getContext(), org));
            }

            name.setText(org.getName());
            email.setText(org.getEmail());

            info.setOnClickListener(v -> {
                Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_organization_details, null);

                TextView description = popupView.findViewById(R.id.tvDescripcionVal);
                TextView regularHours = popupView.findViewById(R.id.tvHorarioVal);
                LinearLayout needsContainer = popupView.findViewById(R.id.containerNecesidades);
                LinearLayout closeBtn = popupView.findViewById(R.id.btnCerrarPopup);

                description.setText(org.getDescription());
                // Uncomment if you have these fields in the new Model
                // regularHours.setText(org.getSchedule());
                // populateTags(context, needsContainer, org.getNeeds());

                builder.setView(popupView);
                AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                closeBtn.setOnClickListener(x -> dialog.dismiss());
                dialog.show();
            });
        }

        private void showConfirmDeleteDialog(Context context, Organization org) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("¿Dar de baja?");
            builder.setMessage("Estás a punto de dar de baja a " + org.getName() + ".\n\nEsta organización perderá el acceso y pasará a estado 'Rechazado'. ¿Estás seguro?");

            builder.setPositiveButton("Sí, dar de baja", (dialogInterface, i) -> {
                changeOrganizationStatus(org, "rechazado", context);
            });

            builder.setNegativeButton("Cancelar", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        }

        private void changeOrganizationStatus(Organization org, String newStatus, Context context) {
            OrganizationAPIService apiService = APIClient.getOrganizationAPIService();
            StatusRequest body = new StatusRequest(newStatus);
            Call<Organization> call = apiService.updateStatus(org.getCif(), body);

            call.enqueue(new Callback<Organization>() {
                @Override
                public void onResponse(Call<Organization> call, Response<Organization> response) {
                    if (response.isSuccessful()){
                        org.setStatus(newStatus);
                        organizations.remove(org);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Organización " + newStatus, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Organization> call, Throwable t) {
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Helper method for tags if needed later
         private void populateTags(Context context, LinearLayout container, List<String> list) {
            container.removeAllViews();
            if (list == null) return;
            for (String text : list){
                TextView tag = new TextView(context);
                tag.setText(text);
                tag.setTextSize(12);
                tag.setTextColor(Color.parseColor("#1A3B85"));
                tag.setBackgroundResource(R.drawable.tag_bg_blue);
                tag.setPadding(25, 10, 25, 10);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 16, 0);
                tag.setLayoutParams(params);
                container.addView(tag);
            }
        }
    }
}
