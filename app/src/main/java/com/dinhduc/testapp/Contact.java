package com.dinhduc.testapp;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Nguyen Dinh Duc on 8/3/2015.
 */
public class Contact {
    private int id;
    private Bitmap icon;
    private String name;
    private ArrayList<Phone> phones;

    public Contact(String name, Bitmap icon) {
        this.name = name;
        this.icon = icon;
    }

    public Contact(int id, String name, Bitmap icon, ArrayList<Phone> phones) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.phones = phones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareIgnoreCase(Contact other) {
        return name.compareToIgnoreCase(other.getName());
    }

    public ArrayList<Phone> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<Phone> phones) {
        this.phones = phones;
    }
}
