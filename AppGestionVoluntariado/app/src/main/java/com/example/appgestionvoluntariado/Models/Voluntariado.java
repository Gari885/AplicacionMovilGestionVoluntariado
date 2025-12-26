package com.example.appgestionvoluntariado.Models;

import java.util.ArrayList;

public class Voluntariado {

    private String titulo;

    private String zona;

    private String fecha;

    private String descripcion;

    private ArrayList<String> necesidades;



    private ArrayList<String> ods;

    public Voluntariado(){}

    public Voluntariado(String titulo, String zona, String fecha){
        this.titulo = titulo;
        this.zona = zona;
        this.fecha = fecha;
    }

    public Voluntariado(String titulo, String zona, String fecha, String descripcion, ArrayList<String> necesidades, ArrayList<String> ods){
        this.titulo = titulo;
        this.zona = zona;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.necesidades = necesidades;
        this.ods = ods;
    }

    public String getTitulo(){return this.titulo;}
    public String getZona(){return this.zona;}
    public String getDescripcion(){return this.descripcion;}
    public String getFecha(){return this.fecha;}
    public ArrayList<String> getNecesidades(){return this.necesidades;}
    public ArrayList<String> getOds(){return this.ods;}




}
