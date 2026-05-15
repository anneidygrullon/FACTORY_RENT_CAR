package com.example.factory_rent_car.Database.DAO;

import com.example.factory_rent_car.Database.Conexion;
import com.example.factory_rent_car.Modelo.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final Conexion conexion = Conexion.getInstance();

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT pk_id_cliente, nombre, edad, fecha_nacimiento, correo_electronico, " +
                "telefono, identificacion, licencia, nacionalidad, num_pasaporte " +
                "FROM TBL_CLIENTE ORDER BY pk_id_cliente";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(fila(rs));
            }
        }
        return lista;
    }

    public Cliente porId(int id) throws SQLException {
        String sql = "SELECT * FROM TBL_CLIENTE WHERE pk_id_cliente = ?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? fila(rs) : null;
            }
        }
    }

    public int siguienteId() throws SQLException {
        String sql = "SELECT ISNULL(MAX(pk_id_cliente), 0) + 1 AS next_id FROM TBL_CLIENTE";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("next_id") : 1;
        }
    }

    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO TBL_CLIENTE (pk_id_cliente, nombre, edad, fecha_nacimiento, " +
                "correo_electronico, telefono, identificacion, licencia, nacionalidad, num_pasaporte) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNombre());
            ps.setInt(3, c.getEdad());
            ps.setDate(4, c.getFechaNacimiento() != null ? Date.valueOf(c.getFechaNacimiento()) : null);
            ps.setString(5, c.getCorreoElectronico());
            ps.setString(6, c.getTelefono());
            ps.setString(7, c.getIdentificacion());
            ps.setString(8, c.getLicencia());
            ps.setString(9, c.getNacionalidad());
            ps.setString(10, c.getNumPasaporte());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE TBL_CLIENTE SET nombre=?, edad=?, fecha_nacimiento=?, " +
                "correo_electronico=?, telefono=?, identificacion=?, licencia=?, " +
                "nacionalidad=?, num_pasaporte=? WHERE pk_id_cliente=?";
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getEdad());
            ps.setDate(3, c.getFechaNacimiento() != null ? Date.valueOf(c.getFechaNacimiento()) : null);
            ps.setString(4, c.getCorreoElectronico());
            ps.setString(5, c.getTelefono());
            ps.setString(6, c.getIdentificacion());
            ps.setString(7, c.getLicencia());
            ps.setString(8, c.getNacionalidad());
            ps.setString(9, c.getNumPasaporte());
            ps.setInt(10, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection con = conexion.establecerConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TBL_CLIENTE WHERE pk_id_cliente = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Cliente fila(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("pk_id_cliente"),
                rs.getString("nombre"),
                rs.getInt("edad"),
                rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null,
                rs.getString("correo_electronico"),
                rs.getString("telefono"),
                rs.getString("identificacion"),
                rs.getString("licencia"),
                rs.getString("nacionalidad"),
                rs.getString("num_pasaporte")
        );
    }
}
