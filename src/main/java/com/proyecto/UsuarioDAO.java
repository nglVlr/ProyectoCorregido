package com.proyecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.*, r.nombre as rol_nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id WHERE u.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return crearUsuarioDesdeResultSet(rs);
            }
            return null;
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre as rol_nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(crearUsuarioDesdeResultSet(rs));
            }
        }
        return usuarios;
    }

    public List<Tecnico> listarTecnicos() throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre as rol_nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id WHERE r.nombre = 'Técnico'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Roles rol = new Roles(rs.getString("rol_nombre"));
                Tecnico tecnico = new Tecnico(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rol
                );
                tecnico.setId(rs.getInt("id"));
                tecnicos.add(tecnico);
            }
        }
        return tecnicos;
    }

    public void crearUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, correo, rol_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setInt(3, obtenerRolId(usuario.getRol().getNombre()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
            }
        }
    }

    public void eliminarUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Usuario crearUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        Roles rol = new Roles(rs.getString("rol_nombre"));
        Usuario usuario;

        switch(rol.getNombre()) {
            case "Administrador":
                usuario = new Administrador(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rol
                );
                break;
            case "Técnico":
                usuario = new Tecnico(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rol
                );
                break;
            default:
                usuario = new Usuario(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rol
                ) {
                    @Override
                    public void mostrarInfo() {
                        System.out.println("Usuario: " + getNombre());
                    }
                };
        }

        usuario.setId(rs.getInt("id"));
        return usuario;
    }

    private int obtenerRolId(String nombreRol) throws SQLException {
        String sql = "SELECT id FROM roles WHERE nombre = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombreRol);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Rol no encontrado: " + nombreRol);
        }
    }
}