package modelo;

import java.util.List;

public class Tecnico extends Usuario {
    public Tecnico(String nombre, String correo, Roles rol) {
        super(nombre, correo, rol);
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Tecnico: " + getNombre() + ", Correo: " + getCorreo() + ", Rol: " + getRol());
    }

    public void atenderTicket(Ticket ticket) {
        System.out.println("Atendiendo ticket c:" + ticket.getId());
    }

    public void modificarTicket(Ticket ticket, String nuevaDescripcion) {
        ticket.setDescripcion(nuevaDescripcion);
    }
}
