import modelo.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    public void crearTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (titulo, descripcion, solicitante_id, tecnico_id, estado_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ticket.getTitulo());
            stmt.setString(2, ticket.getDescripcion());
            stmt.setInt(3, ticket.getSolicitante().getId());
            stmt.setInt(4, ticket.getAsignado().getId());
            stmt.setInt(5, obtenerEstadoId(ticket.getEstado().getNombre()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                }
            }
        }
    }

    private int obtenerEstadoId(String nombreEstado) throws SQLException {
        String sql = "SELECT id FROM estados_ticket WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreEstado);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Estado no encontrado: " + nombreEstado);
        }
    }

    public List<Ticket> obtenerTodos() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, e.nombre as estado_nombre FROM tickets t JOIN estados_ticket e ON t.estado_id = e.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario solicitante = UsuarioDAO.buscarPorId(rs.getInt("solicitante_id"));
                Usuario tecnico = UsuarioDAO.buscarPorId(rs.getInt("tecnico_id"));

                Ticket ticket = new Ticket(
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        solicitante,
                        (Tecnico) tecnico
                );
                ticket.setId(rs.getInt("id"));
                ticket.getEstado().cambiarEstado(rs.getString("estado_nombre"));

                tickets.add(ticket);
            }
        }
        return tickets;
    }
}