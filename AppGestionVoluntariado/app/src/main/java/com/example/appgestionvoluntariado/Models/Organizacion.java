package com.example.appgestionvoluntariado.Models;

import java.util.ArrayList;

public class Organizacion {

    private String nombre;

    private String email;

    private String descripcion;

    private String horarioHabitual;

    private ArrayList<String> necesidades;

    public Organizacion(){}

    public Organizacion(String nombre, String email){
        this.nombre = nombre;
        this.email = email;
    }

    public Organizacion(String nombre, String email,String horarioHabitual, String descripcion, ArrayList<String> necesidades){
        this.nombre = nombre;
        this.email = email;
        this.descripcion = descripcion;
        this.horarioHabitual = horarioHabitual;
        this.necesidades = necesidades;
    }

    public String getNombre(){return this.nombre;}
    public String getHorarioHabitual(){return this.horarioHabitual;}
    public String getEmail(){return this.email;}
    public String getDescripcion(){return this.descripcion;}
    public ArrayList<String> getNecesidades(){return this.necesidades;}
}
