package com.example.appgestionvoluntariado.Models.UI;

public class SpinnerVolunteerItem {
    private String name;
    private String dni;

    public SpinnerVolunteerItem(String name, String dni){
        this.name = name;
        this.dni = dni;
    }
    public String getDni(){
        return this.dni;
    }
    @Override
    public String toString(){
        return this.name;
    }
}
