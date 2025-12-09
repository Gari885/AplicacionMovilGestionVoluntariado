package com.example.appgestionvoluntariado.Models;

import java.util.ArrayList;

public class Match {
    private Voluntario vol;

    private Voluntariado actividad;


    public Match(){

    }

    public Match(Voluntario vol, Voluntariado act){
        this.vol = vol;
        this.actividad = act;
    }

    public Voluntario getVoluntario(){
        return this.vol;
    }

    public Voluntariado getActividad(){
        return this.actividad;
    }
}
