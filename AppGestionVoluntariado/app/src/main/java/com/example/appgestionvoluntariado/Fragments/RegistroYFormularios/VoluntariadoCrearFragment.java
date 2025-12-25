package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appgestionvoluntariado.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class VoluntariadoCrearFragment extends Fragment {

    private Button volver, crearVoluntariado;

    private TextInputEditText nombreInput, sectorInput, zonaInput, descripcionInput;

    private TextInputLayout nombreLayout, sectorLayout, zonaLayout, descripcionLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crear_voluntariado, container, false);

        crearVoluntariado = view.findViewById(R.id.btnRegistrarVoluntariado);
        nombreInput = view.findViewById(R.id.etNombre);
        nombreLayout = view.findViewById(R.id.tilNombre);
        zonaInput = view.findViewById(R.id.etZona);
        zonaLayout = view.findViewById(R.id.tilZona);
        sectorInput = view.findViewById(R.id.etSector);
        sectorLayout = view.findViewById(R.id.tilSector);
        descripcionInput = view.findViewById(R.id.etDescripcion);
        descripcionLayout = view.findViewById(R.id.tilDescripcion);

        crearVoluntariado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (esFormularioValido()){

                }
            }
        });


        return view;
    }


    private boolean esFormularioValido() {
        boolean esValido = true;

        String nombre = this.nombreInput.getText().toString().trim();
        if (nombre.isEmpty()) {
            nombreLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!nombre.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            nombreLayout.setError("El nombre solo puede contener letras");
            esValido = false;
        } else {
            nombreLayout.setError(null); // Borra el error si ya lo arregló
        }

        if (this.descripcionInput.getText().toString().trim().isEmpty()) {
            descripcionLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else {
            descripcionLayout.setError(null);
        }

        String sector = this.sectorInput.getText().toString().trim();
        if (sector.isEmpty()) {
            sectorLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!sector.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            sectorLayout.setError("Solo letras permitidas");
            esValido = false;
        } else {
            sectorLayout.setError(null);
        }

        if (this.zonaInput.getText().toString().trim().isEmpty()) {
            zonaLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else {
            zonaLayout.setError(null);
        }

        //TO DO:
        //AÑADIR VALIDACIONES QUE FALTAN, NECESITAMOS CONECTAR CON API PARA SACAR DATOS

        return esValido;
    }
}