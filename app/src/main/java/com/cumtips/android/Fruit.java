package com.cumtips.android;

public class Fruit {
    private String name;

    private int itemId;

    public Fruit(String name, int itemId){
        this.name = name;
        this.itemId = itemId;
    }

    public String getName(){
        return name;
    }

    public int getItemId(){
        return itemId;
    }
}
