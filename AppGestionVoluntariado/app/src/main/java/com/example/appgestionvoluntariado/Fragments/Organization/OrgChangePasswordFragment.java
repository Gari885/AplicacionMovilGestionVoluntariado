package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.appgestionvoluntariado.Models.Request.PasswordRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrgChangePasswordFragment extends Fragment {

    private TextInputEditText etCurrent, etNew, etConfirm;
    private TextInputLayout tilCurrent, tilNew, tilConfirm;
    private MaterialButton btnUpdate;
    private View loadingOverlay;
    private ImageView logoSpinner;
    private Animation rotateAnimation;

    private MaterialToolbar back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_change_password, container, false);

        initViews(view);

        back = view.findViewById(R.id.topAppBarOrg);
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
        back = v.findViewById(R.id.topAppBarOrg);
        
        loadingOverlay = v.findViewById(R.id.loadingOverlay);
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
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
        toggleLoading(true);
        
        PasswordRequest request = new PasswordRequest(currentPass, newPass);
        
        APIClient.getAuthAPIService().changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                toggleLoading(false);
                
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Éxito", "Contraseña actualizada correctamente", false);
                    getParentFragmentManager().popBackStack();
                } else {
                    String errorMessage = "";
                    
                    // Try to parse backend error message
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject jsonError = new JSONObject(errorBody);
                            if (jsonError.has("error")) {
                                errorMessage = jsonError.getString("error");
                            } else if (jsonError.has("message")) {
                                errorMessage = jsonError.getString("message");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    // If no message from backend, provide specific message based on status code
                    if (errorMessage.isEmpty()) {
                        switch (response.code()) {
                            case 400:
                                errorMessage = "Datos inválidos. Verifica que la nueva contraseña cumpla los requisitos";
                                break;
                            case 401:
                                errorMessage = "Contraseña actual incorrecta";
                                tilCurrent.setError("Contraseña incorrecta");
                                break;
                            case 403:
                                errorMessage = "No tienes permisos para realizar esta acción";
                                break;
                            case 404:
                                errorMessage = "Servicio no disponible. Contacta con soporte técnico";
                                break;
                            case 500:
                                errorMessage = "Error del servidor. Inténtalo de nuevo más tarde";
                                break;
                            default:
                                errorMessage = "No se pudo actualizar la contraseña (Error " + response.code() + ")";
                        }
                    } else if (response.code() == 401) {
                        tilCurrent.setError("Contraseña incorrecta");
                    }
                    
                    StatusHelper.showStatus(getContext(), "Error", errorMessage, true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                toggleLoading(false);
                StatusHelper.showStatus(getContext(), "Error de conexión", "Sin conexión con el servidor", true);
            }
        });
    }
    
    private void toggleLoading(boolean isLoading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading && logoSpinner != null && rotateAnimation != null) {
                logoSpinner.startAnimation(rotateAnimation);
            }
        }
        if (btnUpdate != null) {
            btnUpdate.setEnabled(!isLoading);
        }
    }
}