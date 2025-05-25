package modelo;
import java.util.ArrayList;
import java.util.List;

public class Departamento {
    private String nombre;
    private List<Tecnico> tecnicos;

    public Departamento(String nombre) {
        if (nombre == null || nombre.isEmpty())
            throw new IllegalArgumentException("Nombre de departamento incorrecto");
        this.nombre = nombre;
        this.tecnicos = new ArrayList<>();
    }

    public void asignarTecnico(Tecnico tecnico) {
        tecnicos.add(tecnico);
    }

    public List<Tecnico> buscarTecnicos() {
        return tecnicos;
    }

    public String getNombre() {
        return nombre;
    }
}
