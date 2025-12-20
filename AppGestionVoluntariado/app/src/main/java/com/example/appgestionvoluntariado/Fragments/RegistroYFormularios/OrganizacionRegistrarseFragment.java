package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.appgestionvoluntariado.R;

public class OrganizacionRegistrarseFragment extends Fragment {

    private Button registrado;

    private Button volver;

    private EditText nombre,email,sector, zona,descripcion;

    private String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrarse_organizacion, container, false);

        nombre = view.findViewById(R.id.etNombre);
        email = view.findViewById(R.id.etCorreo);
        sector = view.findViewById(R.id.etSector);
        zona = view.findViewById(R.id.etZona);
        descripcion = view.findViewById(R.id.etDescripcion);


        registrado = view.findViewById(R.id.btnRegistrar);

        registrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarFormulario()){
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerFragments, new LogInFragment())
                            .commit();
                }

            }
        });

        volver = view.findViewById(R.id.btnVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new MenuRegistrarseFragment())
                        .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                        .commit();

            }
        });

        return view;
    }

    private boolean verificarFormulario() {
        Boolean validado = true;
        String error = "";
        String nombreVerificar = nombre.getText().toString();
        String emailVerificar = email.getText().toString();
        String sectorVerificar = sector.getText().toString();
        String zonaVerificar = zona.getText().toString();
        String descripcionVerificar = descripcion.getText().toString();

        if (nombreVerificar.isEmpty() || emailVerificar.isEmpty() || sectorVerificar.isEmpty()
        || zonaVerificar.isEmpty() || descripcionVerificar.isEmpty()){
            nombre.setError("No puedes dejar ningun campo vacio");
            validado = false;
        }else {
            for (char c : nombreVerificar.toCharArray()){
                if ((!Character.isUpperCase(c)) || (!Character.isUpperCase(c))){
                    error = "Debes introducir un nombre valido";
                    break;
                }
            }

            if (!emailVerificar.matches(regex)){
                error = "Debes introducir un email valido";
                validado = false;

            }

            for (char c : sectorVerificar.toCharArray()){
                if ((!Character.isUpperCase(c)) || (!Character.isUpperCase(c))){
                    error = "Debes introducir un nombre valido";
                    break;
                }
            }
        }



        if (validado){
            registrarOrganizacion();
            return true;
        }else {
            return false;
        }
    }

    private void registrarOrganizacion() {
        //Logica para registrar organizacion
    }
}