package dao;

import config.ConectarBD;
import models.Vuelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VueloImpl implements IVuelo {

	@Override
	public List<Vuelo> listarTodos() {
		List<Vuelo> lista = new ArrayList<>();
		// Filtramos los cancelados de la lista general
		String sql = "SELECT * FROM vuelos WHERE estado != 'CANCELADO'";

		try (Connection cn = ConectarBD.getConexion();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Vuelo v = new Vuelo();
				v.setIdVuelo(rs.getInt("id_vuelo"));
				v.setIdAvion(rs.getInt("id_avion"));
				v.setOrigen(rs.getString("origen"));
				v.setDestino(rs.getString("destino"));
				v.setFechaSalida(rs.getString("fecha_salida"));
				v.setEstado(rs.getString("estado"));
				lista.add(v);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean registrar(Vuelo vuelo) {
		String sql = "INSERT INTO vuelos (id_avion, origen, destino, fecha_salida, estado) VALUES (?, ?, ?, ?, ?)";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setInt(1, vuelo.getIdAvion());
			ps.setString(2, vuelo.getOrigen());
			ps.setString(3, vuelo.getDestino());
			ps.setString(4, vuelo.getFechaSalida());
			ps.setString(5, vuelo.getEstado() != null ? vuelo.getEstado() : "PROGRAMADO");

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean actualizarEstado(int idVuelo, String nuevoEstado) {
		String sql = "UPDATE vuelos SET estado = ? WHERE id_vuelo = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, nuevoEstado);
			ps.setInt(2, idVuelo);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean actualizar(Vuelo vuelo) {
		String sql = "UPDATE vuelos SET id_avion = ?, origen = ?, destino = ?, fecha_salida = ?, estado = ? WHERE id_vuelo = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, vuelo.getIdAvion());
			ps.setString(2, vuelo.getOrigen());
			ps.setString(3, vuelo.getDestino());
			ps.setString(4, vuelo.getFechaSalida());
			ps.setString(5, vuelo.getEstado());
			ps.setInt(6, vuelo.getIdVuelo());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean eliminar(int id) {
		// Eliminación lógica
		String sql = "UPDATE vuelos SET estado = 'CANCELADO' WHERE id_vuelo = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Vuelo obtenerPorId(int id) {
		Vuelo v = null;
		String sql = "SELECT * FROM vuelos WHERE id_vuelo = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					v = new Vuelo();
					v.setIdVuelo(rs.getInt("id_vuelo"));
					v.setIdAvion(rs.getInt("id_avion"));
					v.setOrigen(rs.getString("origen"));
					v.setDestino(rs.getString("destino"));
					v.setFechaSalida(rs.getString("fecha_salida"));
					v.setEstado(rs.getString("estado"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return v;
	}
}