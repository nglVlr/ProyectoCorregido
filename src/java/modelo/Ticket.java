package modelo;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador para la serialización
    private transient static int contador = 0; // No se serializa
    private int id;
    private LocalDate fechaCreacion;
    private String titulo;
    private String descripcion;
    private LocalDate fechaFinalizacion;
    private Usuario solicitante;
    private Tecnico asignado;
    private EstadoTicket estado;
    private List<Nota> notas;

    public Ticket(String titulo, String descripcion, Usuario solicitante, Tecnico asignado) {
        this.id = ++contador;
        this.fechaCreacion = LocalDate.now();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.asignado = asignado;
        this.estado = new EstadoTicket("Abierto");
        this.notas = new ArrayList<>();
    }

    public void agregarNota(Nota nota) {
        notas.add(nota);
    }

    public int getId() {
        return id;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static void setContador(int contador) {
        Ticket.contador = contador;
    }

    public static int getContador() {
        return contador;
    }

    // Métodos adicionales
    public void guardarEnBD() {
        try {
            new TicketDAO().crearTicket(this);
        } catch (SQLException e) {
            e.printStackTrace();
            // Maneja el error adecuadamente
        }
    }

    public static List<Ticket> cargarTodos() {
        try {
            return new TicketDAO().obtenerTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public StringProperty tituloProperty() {
        return new SimpleStringProperty(titulo);
    }

    public ObjectProperty<EstadoTicket> estadoProperty() {
        return new SimpleObjectProperty<>(estado);
    }

    public ObjectProperty<Tecnico> asignadoProperty() {
        return new SimpleObjectProperty<>(asignado);
    }
}



