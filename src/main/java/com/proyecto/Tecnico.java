package com.proyecto;

import java.util.List;

public class Tecnico extends Usuario {
    private String departamento;

    public Tecnico(String nombre, String correo, Roles rol) {
        super(nombre, correo, rol);
    }

    public Tecnico(String nombre, String correo, Roles rol, String departamento) {
        super(nombre, correo, rol);
        this.departamento = departamento;
    }
    @Override
    public void mostrarInfo() {
        System.out.println("Tecnico: " + getNombre() + ", Correo: " + getCorreo() + ", Rol: " + getRol());
    }

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    public void atenderTicket(Ticket ticket) {
        System.out.println("Atendiendo ticket c:" + ticket.getId());
    }

    public void modificarTicket(Ticket ticket, String nuevaDescripcion) {
        ticket.setDescripcion(nuevaDescripcion);
    }
}
