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
				t.setActivo(rs.getBoolean("activo"));
				lista.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean registrar(Tripulante tripulante) {
		
		tripulante.setLicencia(generarLicencia(tripulante.getRol()));
		
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
					t.setActivo(rs.getBoolean("activo"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return t;
	}

	private String generarLicencia(String rol) {
		String prefijo = "";
		switch (rol) {
		case "PILOTO":
			prefijo = "LIC-PIL-";
			break;
		case "COPILOTO":
			prefijo = "LIC-COP-";
			break;
		case "AZAFATA":
			prefijo = "LIC-AZA-";
			break;
		}

		// Buscamos la última licencia registrada para ese rol específico
		String sql = "SELECT licencia FROM tripulacion WHERE rol = ? ORDER BY id_tripulante DESC LIMIT 1";
		int maxNumero = 0;

		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, rol);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String ultimaLicencia = rs.getString("licencia");

					if (ultimaLicencia != null && ultimaLicencia.contains("-")) {
						String[] partes = ultimaLicencia.split("-");
						if (partes.length == 3) {
							try {
								maxNumero = Integer.parseInt(partes[2]); // Extraemos el "004" -> 4
							} catch (NumberFormatException e) {
								maxNumero = 0;
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		maxNumero++; // Le sumamos 1 al último encontrado
		return String.format("%s%03d", prefijo, maxNumero); // Lo formateamos a 3 dígitos (Ej. 005)
	}
}