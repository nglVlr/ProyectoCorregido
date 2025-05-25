package modelo;
import java.io.Serializable;

public class EstadoTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;

    public EstadoTicket(String nombre) {
        if (nombre == null || nombre.isEmpty())
            throw new IllegalArgumentException("Estado no puede estar vacío");
        this.nombre = nombre;
    }

    public void cambiarEstado(String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank())
            throw new IllegalArgumentException("Nuevo estado inválido");
        this.nombre = nuevoEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public StringProperty nombreProperty() {
        return new SimpleStringProperty(nombre);
    }
}




