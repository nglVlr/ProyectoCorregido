package com.proyecto;

public class Roles {
    private String nombre;
    private int id;

    public Roles(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }


    public Roles(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol esta vacio");
        }
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol esta vacio");
        }
        this.nombre = nombre;
    }
}
