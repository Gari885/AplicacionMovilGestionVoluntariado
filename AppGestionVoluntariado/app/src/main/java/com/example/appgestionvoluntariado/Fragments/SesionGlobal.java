package com.example.appgestionvoluntariado.Fragments;

import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Voluntario;

public class SesionGlobal {



    private enum tipoUsuario {
        VOLUNTARIO,
        ORGANIZACION
    }

    private static tipoUsuario tipoActual = tipoUsuario.VOLUNTARIO;
    private static String nombre;
    private static String email;

    private static String contraseña;


    public static void iniciarSesionVol(){
        tipoActual = tipoUsuario.VOLUNTARIO;
        nombre = "Juan Perez";
        email = "juanperez@gmail.com";
    }

    public static void iniciarSesionOrg(){
        tipoActual = tipoUsuario.ORGANIZACION;
        nombre = "ONG Ayuda Global";
        email = "contacto@ong.org";
    }

    public static boolean esOrganizacion(){
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

    public static String getNombre() { return nombre; }
    public static String getEmail() { return email; }
    public static String getContraseña() { return contraseña; }


}
