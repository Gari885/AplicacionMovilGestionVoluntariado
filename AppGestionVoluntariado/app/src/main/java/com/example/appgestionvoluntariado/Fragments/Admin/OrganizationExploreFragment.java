package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Fragments.Auth.ProjectCreateFragment;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.R;

import java.util.List;

public class OrganizationExploreFragment extends Fragment {

    private Button btnAddProject;
    private RecyclerView recyclerView;
    // private ProjectAdapter projectAdapter; // Commented out in original?
    private List<Project> projects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_my_projects, container, false);

        btnAddProject = view.findViewById(R.id.btnAddProject);
        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        projects = GlobalData.getInstance().projects;

        showProjects();

        btnAddProject.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProjectCreateFragment())
                    .commit();
        });

        return view;
    }

    private void showProjects() {
        // Original code was commented out?
        // "if (adaptadorVoluntariado == null)..."
        // I should probably implement it if the file implies it should be there.
        // It says "sacarVoluntariados()" but content matches "fragment_organization_explore.xml" which has a RecyclerView.
        // I should stick to the pattern but if it was commented out I'll keep it commented out or fix it?
        // If it was commented out, maybe it wasn't working.
        // But "Organization Explore" implies listing projects.
        // I'll implement it to be safe and useful.
        
        /*
        if (projects != null && !projects.isEmpty()) {
             ProjectAdapter adapter = new ProjectAdapter(getContext(), projects, ViewMode.ORGANIZATION, new ProjectAdapter.OnItemAction() {
                 @Override
                 public void onPrimaryAction(Project item) {
                     // Delete action for Organization according to Adapter
                 }
                 @Override
                 public void onSecondaryAction(Project item) {
                 }
             });
             recyclerView.setAdapter(adapter);
        }
        */
        // Since I don't know the exact logic intended (original was commented out), I'll leave it as a TODO or structure it but commented.
        // Wait, the original had: `voluntariados = DatosGlobales.getInstance().voluntariados; sacarVoluntariados();` but `sacarVoluntariados` was commented out.
        // So I'll keep it commented to avoid crashes if data isn't ready.
    }
}
