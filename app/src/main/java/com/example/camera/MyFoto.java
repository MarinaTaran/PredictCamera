package com.example.camera;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class MyFoto implements Serializable {
    private   String fotoId;
    private Set<Face> Facescollection=new TreeSet<>();


    public MyFoto(String fotoId) {
        this.fotoId = fotoId;

    }

    public MyFoto() {
    }

    public String getFotoId() {
        return fotoId;
    }

    public Set<Face> getFacescollection() {
        return Facescollection;
    }

    @Override
    public String toString() {
        return "MyFoto{" +
                "fotoId='" + fotoId + '\'' +
                ", Facescollection=" + Facescollection +
                '}';
    }


}
