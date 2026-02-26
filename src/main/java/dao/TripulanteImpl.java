package dao;

import config.ConectarBD;
import models.Tripulante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripulanteImpl implements ITripulante {

	@Override
	public List<Tripulante> listarTodos() {
		List<Tripulante> lista = new ArrayList<>();
		// Traemos los activos
		String sql = "SELECT * FROM tripulacion WHERE activo = 1";

		try (Connection cn = ConectarBD.getConexion();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Tripulante t = new Tripulante();
				t.setIdTripulante(rs.getInt("id_tripulante"));
				t.setNombre(rs.getString("nombre"));
				t.setRol(rs.getString("rol"));
				t.setLicencia(rs.getString("licencia"));
				lista.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean registrar(Tripulante tripulante) {
		String sql = "INSERT INTO tripulacion (nombre, rol, licencia) VALUES (?, ?, ?)";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, tripulante.getNombre());
			ps.setString(2, tripulante.getRol());
			ps.setString(3, tripulante.getLicencia());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error (¿Licencia duplicada?): " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean actualizar(Tripulante tripulante) {
		String sql = "UPDATE tripulacion SET nombre = ?, rol = ?, licencia = ? WHERE id_tripulante = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, tripulante.getNombre());
			ps.setString(2, tripulante.getRol());
			ps.setString(3, tripulante.getLicencia());
			ps.setInt(4, tripulante.getIdTripulante());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean eliminar(int id) {
		// Eliminación lógica
		String sql = "UPDATE tripulacion SET activo = 0 WHERE id_tripulante = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public Tripulante obtenerPorId(int id) {
		Tripulante t = null;
		String sql = "SELECT * FROM tripulacion WHERE id_tripulante = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					t = new Tripulante();
					t.setIdTripulante(rs.getInt("id_tripulante"));
					t.setNombre(rs.getString("nombre"));
					t.setRol(rs.getString("rol"));
					t.setLicencia(rs.getString("licencia"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return t;
	}
}