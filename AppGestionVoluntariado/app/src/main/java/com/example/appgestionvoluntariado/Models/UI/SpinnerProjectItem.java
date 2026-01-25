package com.example.appgestionvoluntariado.Models.UI;

public class SpinnerProjectItem {
    private String name;
    private int id;

    public SpinnerProjectItem(String name, int id){
        this.name = name;
        this.id = id;
    }

    public int getId(){
        return id;
    }
    @Override
    public String toString(){
        return this.name;
    }
}
