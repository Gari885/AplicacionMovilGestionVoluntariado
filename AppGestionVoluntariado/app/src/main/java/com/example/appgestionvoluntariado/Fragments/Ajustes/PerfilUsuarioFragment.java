package com.example.appgestionvoluntariado.Fragments.Ajustes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appgestionvoluntariado.Activities.MainActivity;
import com.example.appgestionvoluntariado.SesionGlobal;
import com.example.appgestionvoluntariado.Fragments.VistaOrganizacion.OrgMisVoluntariadosFragment;
import com.example.appgestionvoluntariado.Fragments.VistaVoluntario.VoluntariadosVolFragment;
import com.example.appgestionvoluntariado.R;


public class PerfilUsuarioFragment extends Fragment {

    private LinearLayout infPersonal,contraseña,ayuda;
    private Button btncerrarSesion, volver;

    private TextView usuarioNombre,usuarioEmail;

    private SwitchCompat switchNotis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        infPersonal = view.findViewById(R.id.btnOpcionMisDatos);
        contraseña = view.findViewById(R.id.btnOpcionSeguridad);
        ayuda = view.findViewById(R.id.btnOpcionAyuda);
        btncerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        usuarioEmail = view.findViewById(R.id.tvEmailUsuario);
        usuarioNombre = view.findViewById(R.id.tvNombreUsuario);
        volver = view.findViewById(R.id.btnVolver);
        switchNotis = view.findViewById(R.id.switchNotificaciones);

        switchNotis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Notificaciones Activadas", Toast.LENGTH_SHORT).show();
                    //Implementar logica con firebase
                } else {
                    Toast.makeText(getContext(), "Notificaciones Desactivadas", Toast.LENGTH_SHORT).show();
                }
            }
        });


        usuarioNombre.setText(SesionGlobal.getNombre());
        usuarioEmail.setText(SesionGlobal.getEmail());

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                if (SesionGlobal.esOrganizacion()){
                    fragment = new OrgMisVoluntariadosFragment();

                }else {
                    fragment = new VoluntariadosVolFragment();
                }

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                        .commit();
            }
        });

        btncerrarSesion.setOnClickListener(v -> cerrarSesion(v));
        //Preguntar a ia que cuyons es addToBackStack omegalulMecorroEnLaTazaOFuera??????
        infPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SesionGlobal.esOrganizacion()){
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PerfilDatosOrganizacionFragment())
                            .commit();
                }else {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PerfilDatosVoluntarioFragment())
                            .commit();
                }

            }
        });

        contraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilSeguridadFragment())
                        .commit();
            }
        });

        return view;
    }

    private void cerrarSesion(View v) {
        SesionGlobal.destruirSesion();
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        startActivity(intent);
    }
}