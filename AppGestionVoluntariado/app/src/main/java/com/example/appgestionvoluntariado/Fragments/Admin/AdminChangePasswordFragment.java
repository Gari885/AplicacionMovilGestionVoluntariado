package com.example.appgestionvoluntariado.Fragments.Admin;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChangePasswordFragment extends Fragment {

    private TextInputEditText etCurrent, etNew, etConfirm;
    private TextInputLayout tilCurrent, tilNew, tilConfirm;
    private MaterialButton btnUpdate, btnBack;

    private MaterialToolbar back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_change_password, container, false);

        initViews(view);

        back.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnUpdate.setOnClickListener(v -> validateAndUpdate());

        return view;
    }

    private void initViews(View v) {
        etCurrent = v.findViewById(R.id.etCurrentPass);
        etNew = v.findViewById(R.id.etNewPass);
        etConfirm = v.findViewById(R.id.etConfirmPass);
        tilCurrent = v.findViewById(R.id.tilCurrentPass);
        tilNew = v.findViewById(R.id.tilNewPass);
        tilConfirm = v.findViewById(R.id.tilConfirmPass);
        btnUpdate = v.findViewById(R.id.btnUpdatePassword);
        back = v.findViewById(R.id.topAppBarAdmin);
    }

    private void validateAndUpdate() {
        String current = etCurrent.getText().toString();
        String newPass = etNew.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (TextUtils.isEmpty(current)) { tilCurrent.setError("Campo obligatorio"); return; }
        if (newPass.length() < 6) { tilNew.setError("Mínimo 6 caracteres"); return; }
        if (!newPass.equals(confirm)) { tilConfirm.setError("Las contraseñas no coinciden"); return; }

        updatePasswordInBackend(current, newPass);
    }

    private void updatePasswordInBackend(String currentPass, String newPass) {
        btnUpdate.setEnabled(false);
        btnUpdate.setText("Actualizando...");
        
        PasswordRequest request = new PasswordRequest(currentPass, newPass);
        
        APIClient.getAuthAPIService().changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                btnUpdate.setEnabled(true);
                btnUpdate.setText("ACTUALIZAR CONTRASEÑA");
                
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Éxito", "Contraseña actualizada correctamente", false);
                    getParentFragmentManager().popBackStack();
                } else {
                    if (response.code() == 401) {
                        tilCurrent.setError("Contraseña actual incorrecta");
                    } else {
                        StatusHelper.showStatus(getContext(), "Error", "No se pudo actualizar la contraseña", true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnUpdate.setEnabled(true);
                btnUpdate.setText("ACTUALIZAR CONTRASEÑA");
                StatusHelper.showStatus(getContext(), "Error de conexión", "Sin conexión con el servidor", true);
            }
        });
    }
}