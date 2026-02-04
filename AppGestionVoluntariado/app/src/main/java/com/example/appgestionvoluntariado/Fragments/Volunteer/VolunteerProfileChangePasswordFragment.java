package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Request.PasswordRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerProfileChangePasswordFragment extends Fragment {

    private TextInputEditText etCurrent, etNew, etConfirm;
    private TextInputLayout tilCurrent, tilNew, tilConfirm;
    private MaterialButton btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_change_passwords, container, false);

        etCurrent = view.findViewById(R.id.etCurrentPassword);
        etNew = view.findViewById(R.id.etNewPassword);
        etConfirm = view.findViewById(R.id.etConfirmNewPassword);
        
        tilCurrent = view.findViewById(R.id.tilCurrentPassword);
        tilNew = view.findViewById(R.id.tilNewPassword);
        tilConfirm = view.findViewById(R.id.tilConfirmNewPassword);
        
        btnSubmit = view.findViewById(R.id.btnSubmitNewPassword);

        com.google.android.material.appbar.MaterialToolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnSubmit.setOnClickListener(v -> validateAndChange());

        return view;
    }

    private void validateAndChange() {
        String current = etCurrent.getText().toString();
        String newPass = etNew.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (TextUtils.isEmpty(current)) { 
            if (tilCurrent != null) tilCurrent.setError("Campo obligatorio"); 
            return; 
        }
        if (newPass.length() < 6) { 
            if (tilNew != null) tilNew.setError("Mínimo 6 caracteres"); 
            return; 
        }
        if (!newPass.equals(confirm)) { 
            if (tilConfirm != null) tilConfirm.setError("Las contraseñas no coinciden"); 
            return; 
        }

        updatePasswordInBackend(current, newPass);
    }

    private void updatePasswordInBackend(String currentPass, String newPass) {
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Actualizando...");
        
        PasswordRequest request = new PasswordRequest(currentPass, newPass);
        
        APIClient.getAuthAPIService().changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("CAMBIAR CONTRASEÑA");
                
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Éxito", "Contraseña actualizada correctamente", false);
                    getParentFragmentManager().popBackStack();
                } else {
                    if (response.code() == 401) {
                        if (tilCurrent != null) tilCurrent.setError("Contraseña actual incorrecta");
                    } else {
                        StatusHelper.showStatus(getContext(), "Error", "No se pudo actualizar la contraseña", true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("CAMBIAR CONTRASEÑA");
                StatusHelper.showStatus(getContext(), "Error de conexión", "Sin conexión con el servidor", true);
            }
        });
    }
}