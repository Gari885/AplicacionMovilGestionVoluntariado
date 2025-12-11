package com.example.appgestionvoluntariado.Models;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Voluntario {
    private String nombre;
    private String email;
    private String disponibilidad;
    private ArrayList<String> habilidades;
    private ArrayList<String> intereses;


    public Voluntario(){

    }

    public  Voluntario(String nombre, String email){
        this.nombre = nombre;
        this.email = email;
    }
    public Voluntario(String nombre, String email, String disponibilidad, ArrayList<String> habilidades, ArrayList<String> intereses){
        this.nombre = nombre;
        this.email = email;
        this.disponibilidad = disponibilidad;
        this.habilidades = habilidades;
        this.intereses = intereses;
    }


    public String getNombre(){
        return this.nombre;
    }

    public String getEmail(){
        return this.email;
    }

    public String getDisponibilidad(){
        return  disponibilidad;
    }

    public ArrayList<String> getHabilidades(){
        return this.habilidades;
    }
    public ArrayList<String> getIntereses(){
        return this.intereses;
    }
}
