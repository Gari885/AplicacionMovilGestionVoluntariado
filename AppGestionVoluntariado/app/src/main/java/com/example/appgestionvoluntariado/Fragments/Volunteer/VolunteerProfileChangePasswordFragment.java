package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.textfield.TextInputEditText;

public class VolunteerProfileChangePasswordFragment extends Fragment {

    private TextInputEditText etCurrent, etNew, etConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_change_passwords, container, false);

        etCurrent = view.findViewById(R.id.etCurrentPassword);
        etNew = view.findViewById(R.id.etNewPassword);
        etConfirm = view.findViewById(R.id.etConfirmNewPassword);

        com.google.android.material.appbar.MaterialToolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btnSubmitNewPassword).setOnClickListener(v -> validateAndChange());

        return view;
    }

    private void validateAndChange() {
        String pass = etNew.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (pass.isEmpty() || !pass.equals(confirm)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamada a la API para cambiar contraseña
        Toast.makeText(getContext(), "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }
}