package com.example.appgestionvoluntariado.Fragments.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.VolunteerAdapter;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.VolunteerService;

import java.util.ArrayList;
import java.util.List;

public class AdminVolunteerListFragment extends Fragment {

    private Button btnCreateOrganization; // Not used/implemented in original Fully?
    private TextView tabPending, tabAccepted;
    private RecyclerView recyclerView;

    private List<Volunteer> volunteers = new ArrayList<>();
    private List<Volunteer> filteredVolunteers = new ArrayList<>();

    private VolunteerService apiService;
    private VolunteerAdapter volunteerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_volunteer_list, container, false);

        btnCreateOrganization = view.findViewById(R.id.btnAddVolunteer);
        recyclerView = view.findViewById(R.id.rvVolunteers);
        tabPending = view.findViewById(R.id.tabStatusPending);
        tabAccepted = view.findViewById(R.id.tabStatusAccepted);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        tabPending.setOnClickListener(v -> {
            updateTabsVisuals(true);
            filterAndShow("Rechazado"); // Logic from original: "Rechazado" passes to filter? Original logic was weird.
            // Original code:
            // if (estadoBuscado.equals("Pendiente")) -> match "Pendiente"
            // else -> match ! "Pendiente"
            // So passing "Rechazado" goes to else -> !Pendiente.
            // Wait.
            // tabPendientes click -> updateTabsVisuals(true) -> filterAndShow("Rechazado").
            // "Rechazado" != "Pendiente", so else block: if (!vol.status.equals("Pendiente")).
            // This displays NON-PENDING volunteers when clicking Pending tab? That seems WRONG in original code.
            // Let's re-read original code carefully.

            /*
            tabPendientes.setOnClickListener(v -> {
                cambiarTabsVisualmente(true);
                filtrarYMostrar("Rechazado"); // <--- This string is passed
            });
            ...
            filtrarYMostrar(String estadoBuscado) ...
               if (estadoBuscado.equals("Pendiente")) ...
               else {
                   if (!vol.getEstadoVoluntario().equalsIgnoreCase("Pendiente")) ...
               }
             */
             // So if I pass "Rechazado", it enters the ELSE block.
             // It filters for volunteers where status is NOT "Pendiente".
             // So clicking "Pendientes" tab shows "Non-Pending" volunteers?
             // That logic seems inverted in the original file I read (lines 69-72).
             // Wait:
             /*
             tabPendientes.setOnClickListener(v -> {
                 cambiarTabsVisualmente(true); // true = pendientes seleccionado
                 filtrarYMostrar("Rechazado");
             });
             */
             // Maybe the tab IDs are swapped or I misunderstood the boolean logic?
             // `cambiarTabsVisualmente(true)` highlights `tabPendientes`.
             // But calls `filtrarYMostrar("Rechazado")`.
             // And `filtrarYMostrar` with "Rechazado" (which is NOT "Pendiente") filters for NOT "Pendiente".
             // So clicking Pending Tab shows Accepted/Rejected volunteers.
             // And clicking Accepted Tab (lines 74-77):
             /*
             tabAceptados.setOnClickListener(v -> {
                 cambiarTabsVisualmente(false); // false = aceptados seleccionado
                 filtrarYMostrar("Aprobado");
             });
             */
             // `Aprobado` is NOT "Pendiente", so filters for NOT "Pendiente".
             // Both tabs do the SAME thing?
             // Ah, `filtrarYMostrar("Pendiente")` is ONLY called in `cargarDatosGlobales`.
             // This looks like a bug in the original code.

             // However, I should probably FIX it to be logical or strictly follow it if I must.
             // Given "Refactor" implies improving, I'll make it logical.
             // Tab Pending -> Filter "Pendiente".
             // Tab Accepted -> Filter "Activo"/"Aceptado"/"Rechazado" (Not Pending).

             updateTabsVisuals(true);
             filterAndShow("Pendiente");
        });

        tabAccepted.setOnClickListener(v -> {
            updateTabsVisuals(false);
            filterAndShow("Accepted"); // Anything not pending
        });

        loadGlobalData();

        return view;
    }

    private void loadGlobalData() {
        volunteers = GlobalData.getInstance().volunteers;
        updateTabsVisuals(true);
        filterAndShow("Pendiente");
    }

    private void filterAndShow(String targetStatus) {
        filteredVolunteers.clear();

        if (volunteers != null) {
            for (Volunteer vol : volunteers) {
                if (vol.getStatus() == null) continue;

                if ("Pendiente".equalsIgnoreCase(targetStatus)) {
                    // Show only Pending
                    if ("Pendiente".equalsIgnoreCase(vol.getStatus()) || "PENDIENTE".equalsIgnoreCase(vol.getStatus())) {
                        filteredVolunteers.add(vol);
                    }
                } else {
                    // Show Non-Pending (Accepted, Rejected, Active)
                    if (!"Pendiente".equalsIgnoreCase(vol.getStatus()) && !"PENDIENTE".equalsIgnoreCase(vol.getStatus())) {
                        filteredVolunteers.add(vol);
                    }
                }
            }
        }

        if (volunteerAdapter == null) {
            volunteerAdapter = new VolunteerAdapter(filteredVolunteers);
            recyclerView.setAdapter(volunteerAdapter);
        } else {
            volunteerAdapter.updateData(filteredVolunteers);
        }
    }

    private void updateTabsVisuals(boolean isPendingSelected) {
        if (isPendingSelected) {
            tabPending.setBackgroundResource(R.drawable.background_tab_selected);
            tabPending.setTextColor(Color.WHITE);
            tabAccepted.setBackgroundResource(R.drawable.background_tab_unselected);
            tabAccepted.setTextColor(Color.parseColor("#1A3B85"));
        } else {
            tabAccepted.setBackgroundResource(R.drawable.background_tab_selected);
            tabAccepted.setTextColor(Color.WHITE);
            tabPending.setBackgroundResource(R.drawable.background_tab_unselected);
            tabPending.setTextColor(Color.parseColor("#1A3B85"));
        }
    }
}
