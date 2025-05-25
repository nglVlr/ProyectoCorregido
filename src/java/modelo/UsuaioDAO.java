import modelo.Usuario;
import modelo.Roles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public static Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.*, r.nombre as rol_nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id WHERE u.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Roles rol = new Roles(rs.getString("rol_nombre"));
                return new Usuario(
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
            return null;
        }
    }

    public static List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre as rol_nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Roles rol = new Roles(rs.getString("rol_nombre"));
                usuarios.add(new Usuario(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rol
                ) {
                    @Override
                    public void mostrarInfo() {
                        System.out.println("Usuario: " + getNombre());
                    }
                });
            }
        }
        return usuarios;
    }
}