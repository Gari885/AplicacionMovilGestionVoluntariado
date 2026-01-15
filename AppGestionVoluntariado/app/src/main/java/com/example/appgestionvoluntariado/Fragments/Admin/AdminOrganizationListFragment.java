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

import com.example.appgestionvoluntariado.Adapters.OrganizationAdapter;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.OrganizationService;

import java.util.ArrayList;
import java.util.List;

public class AdminOrganizationListFragment extends Fragment {

    private Button btnCreateOrganization;
    private TextView tabPending, tabAccepted;
    private RecyclerView recyclerView;

    private List<Organization> organizations = new ArrayList<>();
    private List<Organization> filteredOrganizations = new ArrayList<>();

    private OrganizationService apiService;
    private OrganizationAdapter organizationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_organization_list, container, false);

        btnCreateOrganization = view.findViewById(R.id.btnAddOrganization);
        recyclerView = view.findViewById(R.id.rvOrganizations);
        tabPending = view.findViewById(R.id.tabStatusPending);
        tabAccepted = view.findViewById(R.id.tabStatusAccepted);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabPending.setOnClickListener(v -> {
            updateTabsVisuals(true);
            filterAndShow("Pendiente");
        });

        tabAccepted.setOnClickListener(v -> {
            updateTabsVisuals(false);
            filterAndShow("Activo");
        });

        loadGlobalData();

        return view;
    }

    private void loadGlobalData() {
        organizations = GlobalData.getInstance().organizations;
        updateTabsVisuals(true);
        filterAndShow("Pendiente");
    }

    private void filterAndShow(String targetStatus) {
        filteredOrganizations.clear();

        if (organizations != null) {
            for (Organization org : organizations) {
                if (org.getStatus() == null) continue;

                if ("Pendiente".equalsIgnoreCase(targetStatus)) {
                    if ("Pendiente".equalsIgnoreCase(org.getStatus())) {
                        filteredOrganizations.add(org);
                    }
                } else if ("Activo".equalsIgnoreCase(targetStatus)) {
                    if ("Activo".equalsIgnoreCase(org.getStatus()) || "aprobado".equalsIgnoreCase(org.getStatus())) {
                        filteredOrganizations.add(org);
                    }
                }
            }
        }

        if (organizationAdapter == null) {
            organizationAdapter = new OrganizationAdapter(filteredOrganizations);
            recyclerView.setAdapter(organizationAdapter);
        } else {
            organizationAdapter.updateData(filteredOrganizations);
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
