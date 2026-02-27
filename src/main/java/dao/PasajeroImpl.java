package dao;

import config.ConectarBD;
import models.Pasajero;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasajeroImpl implements IPasajero {

	@Override
	public List<Pasajero> listarTodos() {
		List<Pasajero> lista = new ArrayList<>();
		// Traemos solo los activos
		String sql = "SELECT * FROM pasajeros WHERE activo = 1";

		try (Connection cn = ConectarBD.getConexion();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Pasajero p = new Pasajero();
				p.setIdPasajero(rs.getInt("id_pasajero"));
				p.setPasaporte(rs.getString("pasaporte"));
				p.setNombre(rs.getString("nombre"));
				p.setEmail(rs.getString("email"));
				p.setActivo(rs.getBoolean("activo"));
				lista.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean registrar(Pasajero pasajero) {
		String sql = "INSERT INTO pasajeros (pasaporte, nombre, email) VALUES (?, ?, ?)";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, pasajero.getPasaporte());
			ps.setString(2, pasajero.getNombre());
			ps.setString(3, pasajero.getEmail());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al registrar pasajero (¿Pasaporte duplicado?): " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean actualizar(Pasajero pasajero) {
		String sql = "UPDATE pasajeros SET pasaporte = ?, nombre = ?, email = ? WHERE id_pasajero = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, pasajero.getPasaporte());
			ps.setString(2, pasajero.getNombre());
			ps.setString(3, pasajero.getEmail());
			ps.setInt(4, pasajero.getIdPasajero());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean eliminar(int id) {
		// Eliminación lógica
		String sql = "UPDATE pasajeros SET activo = 0 WHERE id_pasajero = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public Pasajero obtenerPorId(int id) {
		Pasajero p = null;
		String sql = "SELECT * FROM pasajeros WHERE id_pasajero = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					p = new Pasajero();
					p.setIdPasajero(rs.getInt("id_pasajero"));
					p.setPasaporte(rs.getString("pasaporte"));
					p.setNombre(rs.getString("nombre"));
					p.setEmail(rs.getString("email"));
					p.setActivo(rs.getBoolean("activo"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}

	@Override
	public List<Pasajero> listarPorVuelo(int idVuelo) {
		List<Pasajero> lista = new ArrayList<>();
		// Hacemos INNER JOIN con reservas para obtener solo los de ese vuelo
		String sql = "SELECT p.* FROM pasajeros p " +
					 "INNER JOIN reservas r ON p.id_pasajero = r.id_pasajero " +
					 "WHERE r.id_vuelo = ? AND p.activo = 1";

		try (Connection cn = ConectarBD.getConexion();
			 PreparedStatement ps = cn.prepareStatement(sql)) {
			
			ps.setInt(1, idVuelo);
			
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Pasajero p = new Pasajero();
					p.setIdPasajero(rs.getInt("id_pasajero")); 
					p.setPasaporte(rs.getString("pasaporte"));
					p.setNombre(rs.getString("nombre"));
					p.setEmail(rs.getString("email"));
					p.setActivo(rs.getBoolean("activo"));
					lista.add(p);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
}