package dao;

import config.ConectarBD;
import models.Reserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaImpl implements IReserva {

	@Override
	public List<Reserva> listarTodas() {
		List<Reserva> lista = new ArrayList<>();
		String sql = "SELECT * FROM reservas";

		try (Connection cn = ConectarBD.getConexion();
				PreparedStatement ps = cn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Reserva r = new Reserva();
				r.setIdReserva(rs.getInt("id_reserva"));
				r.setIdVuelo(rs.getInt("id_vuelo"));
				r.setIdPasajero(rs.getInt("id_pasajero"));
				r.setAsiento(rs.getString("asiento"));
				r.setFechaReserva(rs.getString("fecha_reserva"));
				lista.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public boolean registrar(Reserva reserva) throws Exception {
		String sql = "INSERT INTO reservas (id_vuelo, id_pasajero, asiento) VALUES (?, ?, ?)";
		try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setInt(1, reserva.getIdVuelo());
			ps.setInt(2, reserva.getIdPasajero());
			ps.setString(3, reserva.getAsiento());

			return ps.executeUpdate() > 0;
		} catch (SQLIntegrityConstraintViolationException e) {
			// Error para violación del UNIQUE(id_vuelo, asiento)
			throw new Exception("El asiento " + reserva.getAsiento() + " ya está ocupado en este vuelo.");
		} catch (SQLException e) {
			throw new Exception("Error de base de datos: " + e.getMessage());
		}
	}

	@Override
    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE reservas SET id_vuelo = ?, id_pasajero = ?, asiento = ? WHERE id_reserva = ?";
        try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, reserva.getIdVuelo());
            ps.setInt(2, reserva.getIdPasajero());
            ps.setString(3, reserva.getAsiento());
            ps.setInt(4, reserva.getIdReserva());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM reservas WHERE id_reserva = ?";
        try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
    public Reserva obtenerPorId(int id) {
        Reserva r = null;
        String sql = "SELECT * FROM reservas WHERE id_reserva = ?";
        try (Connection cn = ConectarBD.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r = new Reserva();
                    r.setIdReserva(rs.getInt("id_reserva"));
                    r.setIdVuelo(rs.getInt("id_vuelo"));
                    r.setIdPasajero(rs.getInt("id_pasajero"));
                    r.setAsiento(rs.getString("asiento"));
                    r.setFechaReserva(rs.getString("fecha_reserva"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }
}