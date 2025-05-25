package com.proyecto;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private String correo;
    private Roles rol;

    public Usuario(String nombre, String correo, Roles rol) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }

        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        this.correo = correo;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = rol;
    }

    @Override
    public String toString() {
        return nombre + " (" + correo + ") - " + rol.getNombre();
    }

    public void mostrarInfo() {
        System.out.println("Usuario: " + getNombre());
    }
}