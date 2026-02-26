package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConectarBD;
import models.Avion;

public class AvionImpl implements IAvion {

	@Override
	public List<Avion> listarTodos() {
		List<Avion> lista = new ArrayList<>();
		// Filtramos los aviones dados de baja
		String sql = "SELECT * FROM aviones WHERE estado != 'BAJA'";

		try (Connection cn = ConectarBD.getConexion();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Avion a = new Avion();
				a.setIdAvion(rs.getInt("id_avion"));
				a.setModelo(rs.getString("modelo"));
				a.setCapacidad(rs.getInt("capacidad"));
				a.setEstado(rs.getString("estado"));
				lista.add(a);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean registrar(Avion avion) {
		String sql = "INSERT INTO aviones (modelo, capacidad, estado) VALUES (?, ?, ?)";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, avion.getModelo());
			ps.setInt(2, avion.getCapacidad());
			ps.setString(3, avion.getEstado());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean actualizar(Avion avion) {
		String sql = "UPDATE aviones SET modelo = ?, capacidad = ?, estado = ? WHERE id_avion = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, avion.getModelo());
			ps.setInt(2, avion.getCapacidad());
			ps.setString(3, avion.getEstado());
			ps.setInt(4, avion.getIdAvion());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean eliminar(int id) {
		// Eliminación lógica
		String sql = "UPDATE aviones SET estado = 'BAJA' WHERE id_avion = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public Avion obtenerPorId(int id) {
		Avion a = null;
		String sql = "SELECT * FROM aviones WHERE id_avion = ?";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					a = new Avion();
					a.setIdAvion(rs.getInt("id_avion"));
					a.setModelo(rs.getString("modelo"));
					a.setCapacidad(rs.getInt("capacidad"));
					a.setEstado(rs.getString("estado"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return a;
	}

}
