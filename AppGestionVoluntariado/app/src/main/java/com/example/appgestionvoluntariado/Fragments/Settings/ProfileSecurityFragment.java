package com.example.appgestionvoluntariado.Fragments.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.R;

public class ProfileSecurityFragment extends Fragment {

    private Button btnUpdatePassword, btnBack;
    private EditText etCurrentPassword, etNewPassword, etRepeatPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_security, container, false);

        btnUpdatePassword = view.findViewById(R.id.btnCambiarPass);
        etCurrentPassword = view.findViewById(R.id.etPassActual);
        etNewPassword = view.findViewById(R.id.etPassNueva);
        etRepeatPassword = view.findViewById(R.id.etPassConfirmar);
        btnBack = view.findViewById(R.id.btnVolver);

        btnUpdatePassword.setOnClickListener(v -> verifyFields());

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileUserFragment())
                    .commit();
        });

        return view;
    }

    private void verifyFields() {
        String error = "";
        String current = etCurrentPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();
        String repeat = etRepeatPassword.getText().toString();

        if (current.trim().isEmpty() || newPass.trim().isEmpty() || repeat.trim().isEmpty()){
            error = "No puedes dejar ningun campo vacio";
        }

        if (error.isEmpty()){
            if (checkPassword(newPass)){
                if (!newPass.equals(repeat)){
                    error = "La contraseña nueva y de verificacion tienen que ser iguales";
                }
            } else {
                error = "La contraseña nueva tiene que tener una minuscula, mayuscula,un numero y un caracter especial, y minimo 10 caracteres";
            }
        }

        if (error.isEmpty()){
            GlobalSession.setPassword(newPass);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileUserFragment())
                    .commit();
        }
    }

    private boolean checkPassword(String password) {
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasSpecial = false;
        boolean hasDigit = false;
        boolean hasLength = password.length() >= 10;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasLower && hasUpper && hasSpecial && hasDigit && hasLength;
    }
}
