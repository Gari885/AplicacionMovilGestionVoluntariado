package com.example.appgestionvoluntariado.Models;

public class Stat {
    private String titulo;
    private int stat1;
    private String stat2;
    private int icon;

    public Stat(){

    }
    public Stat(String titulo,int stat1,String stat2,int icon){
        this.titulo = titulo;
        this.stat1 = stat1;
        this.stat2 = stat2;
        this.icon = icon;
    }

    public String getTitulo(){
        return this.titulo;
    }
    public int getStat1(){
        return  stat1;
    }

    public String getStat2(){
        return  stat2;
    }

    public int getIcon2(){
        return icon;
    }

}
