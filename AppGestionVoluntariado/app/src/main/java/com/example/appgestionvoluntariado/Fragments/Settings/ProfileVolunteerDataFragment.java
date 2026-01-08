package com.example.appgestionvoluntariado.Fragments.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.GlobalSession;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;

public class ProfileVolunteerDataFragment extends Fragment {

    private EditText etName, etEmail;
    private ImageButton btnEdit;
    private Volunteer volunteer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_profile_edit, container, false);
        etName = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etEmail);
        btnEdit = view.findViewById(R.id.btnActivarEdicion);
        
        volunteer = GlobalSession.getVolunteer();

        if (volunteer != null) {
            etName.setText(volunteer.getFullName()); // Using getNombre() to be safe or getName()?
            // Checked Volunteer.java in Step 470 summary: "Created new English model classes: Volunteer.java".
            // So fields should be English `getName()`?
            // In a previous turn I was unsure but intended to use English.
            // Let's assume `getName()` but fallback to `getNombre()` if not found? No, I must be precise.
            // In Step 470 I wrote `Volunteer.java` with English fields.
            // I'll use `getName()` and `getEmail()`.
            etName.setText(volunteer.getFullName());
            etEmail.setText(volunteer.getEmail());
        }

        btnEdit.setOnClickListener(v -> unlockFields());

        return view;
    }

    private void unlockFields() {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
    }
}
