package modelo;

public class Nota {
    private String descripcion;

    public Nota(String descripcion) {
        if (descripcion == null || descripcion.isEmpty()) {
            throw new IllegalArgumentException("La nota no puede estar vacía");
        }
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

