package modelo;

public class Administrador extends Usuario {
    public Administrador(String nombre, String correo, Roles rol) {
        super(nombre, correo, rol);
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Administrador: " + getNombre() + ", Correo: " + getCorreo() + ", Rol: " + getRol());
    }

    public void modificarSistema() {
        System.out.println("Sistema modificado.");
    }

    public void gestionarRoles(Roles rol) {
        System.out.println("Gestionando rol: " + rol);
    }

    public void gestionarSistema() {
        System.out.println("Gestion del sistema realizada.");
    }

    public void eliminarUsuario(Usuario usuario) {
        System.out.println("Usuario " + usuario.getNombre() + " eliminado.");
    }
}
