package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appgestionvoluntariado.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class OrgChangePasswordFragment extends Fragment {

    private TextInputEditText etCurrent, etNew, etConfirm;
    private TextInputLayout tilCurrent, tilNew, tilConfirm;
    private MaterialButton btnUpdate, btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_change_password, container, false);

        initViews(view);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
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
        btnBack = v.findViewById(R.id.btnBackPass);
    }

    private void validateAndUpdate() {
        String current = etCurrent.getText().toString();
        String newPass = etNew.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (TextUtils.isEmpty(current)) { tilCurrent.setError("Campo obligatorio"); return; }
        if (newPass.length() < 6) { tilNew.setError("Mínimo 6 caracteres"); return; }
        if (!newPass.equals(confirm)) { tilConfirm.setError("Las contraseñas no coinciden"); return; }

        updatePasswordInFirebase(current, newPass);
    }

    private void updatePasswordInFirebase(String currentPass, String newPass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) return;

        // Re-autenticación necesaria por seguridad en Firebase
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                tilCurrent.setError("Contraseña actual incorrecta");
            }
        });
    }
}