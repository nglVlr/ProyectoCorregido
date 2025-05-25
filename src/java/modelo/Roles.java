package modelo;

public class Roles {
    private String nombre;

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
