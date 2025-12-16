package com.example.appgestionvoluntariado.Fragments.Ajustes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.appgestionvoluntariado.SesionGlobal;
import com.example.appgestionvoluntariado.R;

public class PerfilDatosVoluntarioFragment extends Fragment {

    private EditText nombre,email,zona;

    private ImageButton editar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_datos_voluntario, container, false);
        nombre = view.findViewById(R.id.etNombre);
        email = view.findViewById(R.id.etEmail);
        editar = view.findViewById(R.id.btnActivarEdicion);



        nombre.setText(SesionGlobal.getNombre());
        email.setText(SesionGlobal.getEmail());


        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desbloquearCampos();
            }
        });

        return view;
    }

    private void desbloquearCampos() {
        nombre.setEnabled(true);
        email.setEnabled(true);
    }
}