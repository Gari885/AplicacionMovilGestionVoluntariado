package com.example.appgestionvoluntariado;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Voluntario;

public class SesionGlobal {



    private enum tipoUsuario {
        VOLUNTARIO,
        ORGANIZACION,
        ADMINISTRADOR
    }

    private static tipoUsuario tipoActual = tipoUsuario.VOLUNTARIO;
    private static String nombre;
    private static String email;

    private static Voluntario sesionVol;

    private static Organizacion sesionOrg;

    private static String contraseña;


    public static void logearVol(Voluntario voluntario){
        sesionVol = voluntario;
    }

    public static void logearOrg(Organizacion organizacion){
        sesionOrg = organizacion;
    }

    public static void iniciarSesionVol(Voluntario vol){
        sesionVol = vol;
        tipoActual = tipoUsuario.VOLUNTARIO;
    }

    public static void iniciarSesionOrg(Organizacion org){
        tipoActual = tipoUsuario.ORGANIZACION;
    }

    public static void invocarError(Context context, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_mensaje_error, null);

        TextView msnError = popupView.findViewById(R.id.mensajeError);
        LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

        msnError.setText(error);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        cerrar.setOnClickListener(x -> dialog.dismiss());
        dialog.show();
    }    public static boolean esOrganizacion(){
        return tipoActual == tipoUsuario.ORGANIZACION;
    }

    public static void setContrasena(String contrasena){
        contraseña = contrasena;
    }

    public static void destruirSesion() {
        tipoActual = null;
        nombre = null;
        email = null;
    }


    public static Voluntario getVoluntario() {
        return sesionVol;
    }

    public static Organizacion getOrganizacion(){
        return sesionOrg;
    }


}
