package com.example.appgestionvoluntariado.Fragments.Ajustes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.appgestionvoluntariado.Models.Voluntario;
import com.example.appgestionvoluntariado.SesionGlobal;
import com.example.appgestionvoluntariado.R;


public class AyudaFormularioFragment extends Fragment {

    private Button volver,enviar;

    private TextView emailUsuario;

    private EditText mensaje;

    private Voluntario vol;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ayuda_formulario, container, false);

        volver = view.findViewById(R.id.btnVolver);
        enviar = view.findViewById(R.id.btnEnviar);
        emailUsuario = view.findViewById(R.id.tvDeUsuario);
        mensaje = view.findViewById(R.id.etMensaje);
        vol = SesionGlobal.getVoluntario();

        emailUsuario.setText(vol.getEmail());

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilAyudaFragment())
                        .commit();
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarFormulario();
            }
        });


        return view;
    }

    private void comprobarFormulario() {
        String textoEscrito = mensaje.getText().toString();
        String error = "";
        if (textoEscrito.isBlank() || textoEscrito.isEmpty()) {
           error = "No puedes dejar el mensaje vacio";
        }

        if (error == ""){
            enviarMensaje();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PerfilAyudaFragment())
                    .commit();
        }
    }

    private void enviarMensaje() {
        //Implementar logica
    }
}