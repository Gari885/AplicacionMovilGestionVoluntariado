package com.example.appgestionvoluntariado.Fragments.Ajustes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.appgestionvoluntariado.SesionGlobal;
import com.example.appgestionvoluntariado.R;


public class PerfilSeguridadFragment extends Fragment {

    private Button actualizarContra, volver;

    private EditText contraActual, contraNueva,contraNuevaRepetido;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_seguridad, container, false);

        actualizarContra = view.findViewById(R.id.btnCambiarPass);
        contraActual = view.findViewById(R.id.etPassActual);
        contraNueva = view.findViewById(R.id.etPassNueva);
        contraNuevaRepetido = view.findViewById(R.id.etPassConfirmar);
        volver = view.findViewById(R.id.btnVolver);

        actualizarContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCampos();
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilUsuarioFragment())
                        .commit();
            }
        });

        return view;
    }

    private void verificarCampos() {
        String error = "";
        String contraActual, nueva, verificacion;
        contraActual = this.contraActual.getText().toString();
        if (contraActual.isEmpty() ||contraActual.isBlank()){
            error = "No puedes dejar ningun campo vacio";
        }
        nueva = contraNueva.getText().toString();
        if (nueva.isEmpty() ||nueva.isBlank()){
            error = "No puedes dejar ningun campo vacio";
        }
        verificacion = contraNuevaRepetido.getText().toString();
        if (verificacion.isEmpty() ||nueva.isBlank()){
            error = "No puedes dejar ningun campo vacio";
        }
        if (error.equals("")){
            if (!contraActual.equals(SesionGlobal.getContraseña())){
                error = "Has introducido la contraseña acutal incorrecta";
            }

            if (comprobarContra(nueva)){
                if (!nueva.equals(verificacion)){
                    error = "La contraseña nueva y de verificacion tienen que ser iguales";
                }
            }else {
                error = "La contraseña nueva tiene que tener una minuscula, mayuscula,un numero y un caracter especial, y minimo 10 caracteres";
            }

        }

        if (error.equals("")){
            SesionGlobal.setContrasena(nueva);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PerfilUsuarioFragment())
                    .commit();
        }

    }

    private boolean comprobarContra(String contra) {
        Boolean minuscula = false;
        Boolean mayuscula = false;
        Boolean caractererEspecial = false;
        Boolean numero = false;
        Boolean longMinima = false;
        int longitud = 0;

        for (char c : contra.toCharArray()) {
            if (Character.isLowerCase(c)) {
                minuscula = true;
            } else if (Character.isUpperCase(c)) {
                mayuscula = true;
            } else if (Character.isDigit(c)) {
                numero = true;
            } else {
                caractererEspecial = true;
            }
            longitud++;

        }
        if (longitud >= 10) longMinima = true;

        return minuscula && mayuscula && caractererEspecial && numero && longMinima;
    }
}