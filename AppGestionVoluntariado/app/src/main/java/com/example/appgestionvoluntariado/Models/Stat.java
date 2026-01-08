package com.example.appgestionvoluntariado.Models;

public class Stat {
    private String title;
    private int count;
    private String description;
    private int iconResId;

    public Stat(){
    }
    
    public Stat(String title, int count, String description, int iconResId){
        this.title = title;
        this.count = count;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getTitle(){
        return this.title;
    }
    public int getCount(){
        return count;
    }

    public String getDescription(){
        return description;
    }

    public int getIconResId(){
        return iconResId;
    }
}
