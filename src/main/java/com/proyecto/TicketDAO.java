package com.proyecto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    private Connection connection;

    public TicketDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    // Método para crear un nuevo ticket
    public void crearTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (titulo, description, fecha_creacion, solicitante_id, tecnico_id, estado_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ticket.getTitulo());
            stmt.setString(2, ticket.getDescripcion());
            stmt.setDate(3, Date.valueOf(ticket.getFechaCreacion()));
            stmt.setInt(4, ticket.getSolicitante().getId());
            stmt.setInt(5, ticket.getAsignado().getId());
            stmt.setInt(5, ticket.getAsignado() != null ? ticket.getAsignado().getId() : null);
            stmt.setInt(6, ticket.getEstado().getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticket.setId(rs.getInt(1));
                    }
                }
            }
        }
    }

    // Método para obtener todos los tickets
    public List<Ticket> obtenerTodos() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.id, t.titulo, t.descripcion, t.fecha_creacion, t.fecha_cierre, " +
                "est.id as estado_id, est.nombre as estado_nombre, " +
                "u_sol.id as solicitante_id, u_sol.nombre as solicitante_nombre, u_sol.correo as solicitante_correo, " +
                "r_sol.id as solicitante_rol_id, r_sol.nombre as solicitante_rol, " +
                "u_tec.id as tecnico_id, u_tec.nombre as tecnico_nombre, u_tec.correo as tecnico_correo, " +
                "r_tec.id as tecnico_rol_id, r_tec.nombre as tecnico_rol, " +
                "d.id as departamento_id, d.nombre as departamento_nombre " +
                "FROM tickets t " +
                "JOIN estados_ticket est ON t.estado_id = est.id " +
                "JOIN usuarios u_sol ON t.solicitante_id = u_sol.id " +
                "JOIN roles r_sol ON u_sol.rol_id = r_sol.id " +
                "JOIN tecnicos tec ON t.tecnico_id = tec.usuario_id " +
                "JOIN usuarios u_tec ON tec.usuario_id = u_tec.id " +
                "JOIN roles r_tec ON u_tec.rol_id = r_tec.id " +
                "JOIN departamentos d ON tec.departamento_id = d.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Crear solicitante
                Roles rolSolicitante = new Roles(
                        rs.getInt("solicitante_rol_id"),
                        rs.getString("solicitante_rol")
                );
                Usuario solicitante = new Usuario(
                        rs.getString("solicitante_nombre"),
                        rs.getString("solicitante_correo"),
                        rolSolicitante
                );
                solicitante.setId(rs.getInt("solicitante_id"));

                // Crear tecnico con su departamento
                Roles rolTecnico = new Roles(
                        rs.getInt("tecnico_rol_id"),
                        rs.getString("tecnico_rol")
                );
                Tecnico tecnico = new Tecnico(
                        rs.getString("tecnico_nombre"),
                        rs.getString("tecnico_correo"),
                        rolTecnico,
                        rs.getString("departamento_nombre")
                );
                tecnico.setId(rs.getInt("tecnico_id"));

                // Crear estado del ticket
                EstadoTicket estado = new EstadoTicket(
                        rs.getInt("estado_id"),
                        rs.getString("estado_nombre")
                );

                // Crear ticket
                Ticket ticket =
                        new Ticket(
                        rs.getString("titulo"),
                        rs.getString("description"),
                        solicitante,
                        tecnico
                );
                ticket.setId(rs.getInt("id"));
                ticket.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());

                if (rs.getDate("fecha_cierre") != null) {
                    ticket.setFechaFinalizacion(rs.getDate("fecha_cierre").toLocalDate());
                }

                tickets.add(ticket);
            }
        }
        return tickets;
    }

    // Método para actualizar un ticket
    public void actualizarTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET titulo = ?, description = ?, estado_id = ?, tecnico_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTitulo());
            stmt.setString(2, ticket.getDescripcion());
            stmt.setInt(3, ticket.getEstado().getId());
            stmt.setInt(4, ticket.getAsignado().getId());
            stmt.setInt(5, ticket.getId());

            stmt.executeUpdate();
        }
    }

    // metodo para eliminar un ticket
    public void eliminarTicket(int id) throws SQLException {
        String deleteNotas = "DELETE FROM notas WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteNotas)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        String deleteTicket = "DELETE FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteTicket)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // metodo para agregar una nota a un ticket
    public void agregarNota(int ticketId, Nota nota) throws SQLException {
        String sql = "INSERT INTO notas (ticket_id, description, fecha) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.setString(2, nota.getDescripcion());
            stmt.setDate(3, Date.valueOf(LocalDate.now()));

            stmt.executeUpdate();
        }
    }

    // metodo para obtener todas las notas de un ticket
    public List<Nota> obtenerNotas(int ticketId) throws SQLException {
        List<Nota> notas = new ArrayList<>();
        String sql = "SELECT id, descripcion, fecha FROM notas WHERE ticket_id = ? ORDER BY fecha DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Nota nota = new Nota(rs.getString("description"));
                nota.setId(rs.getInt("id"));
                nota.setFecha(rs.getDate("fecha").toLocalDate());
                notas.add(nota);
            }
        }
        return notas;
    }

    // Método para cerrar la conexión
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}