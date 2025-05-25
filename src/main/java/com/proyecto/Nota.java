package com.proyecto;
import java.time.LocalDate;

public class Nota {
    private String descripcion;
    private int id;
    private LocalDate fecha;

    // Constructor principal
    public Nota(String descripcion) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("La nota no puede estar vacía");
        }
        this.descripcion = descripcion;
        this.fecha = LocalDate.now(); // Asigna la fecha actual automáticamente
    }

    // Constructor alternativo si se necesita especificar fecha manualmente
    public Nota(String descripcion, LocalDate fecha) {
        this(descripcion); // Llama al constructor principal para vali
        this.fecha = fecha;
    }

    // Getters y Setters
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("La nota no puede estar vacía");
        }
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Nota [id=" + id + ", fecha=" + fecha + ", descripcion=" + descripcion + "]";
    }
}