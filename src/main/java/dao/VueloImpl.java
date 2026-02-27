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
        // Añadimos Statement.RETURN_GENERATED_KEYS para obtener el ID del vuelo recién creado
		try (Connection cn = ConectarBD.getConexion(); 
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, vuelo.getIdAvion());
			ps.setString(2, vuelo.getOrigen());
			ps.setString(3, vuelo.getDestino());
			ps.setString(4, vuelo.getFechaSalida());
			ps.setString(5, vuelo.getEstado() != null ? vuelo.getEstado() : "PROGRAMADO");

			int filas = ps.executeUpdate();
            if (filas > 0) {
                // Si el vuelo se guardó, guardamos a su tripulación en la tabla intermedia
                try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        int idVueloGen = rsKeys.getInt(1);
                        insertarTripulacion(cn, idVueloGen, vuelo.getIdPiloto(), vuelo.getIdCopiloto(), vuelo.getIdAzafata());
                    }
                }
                return true;
            }
            return false;
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
			
            int filas = ps.executeUpdate();
            if (filas > 0) {
                // Al actualizar, borramos la tripulación vieja y metemos la nueva
                String sqlDel = "DELETE FROM vuelo_tripulacion WHERE id_vuelo = ?";
                try (PreparedStatement psDel = cn.prepareStatement(sqlDel)) {
                    psDel.setInt(1, vuelo.getIdVuelo());
                    psDel.executeUpdate();
                }
                insertarTripulacion(cn, vuelo.getIdVuelo(), vuelo.getIdPiloto(), vuelo.getIdCopiloto(), vuelo.getIdAzafata());
            }
			return filas > 0;
		} catch (SQLException e) {
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
					
					cargarTripulacion(cn, v);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return v;
	}
	
	private void insertarTripulacion(Connection cn, int idVuelo, int idPiloto, int idCopiloto, int idAzafata) throws SQLException {
        String sql = "INSERT INTO vuelo_tripulacion (id_vuelo, id_tripulante) VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            if (idPiloto > 0) { ps.setInt(1, idVuelo); ps.setInt(2, idPiloto); ps.addBatch(); }
            if (idCopiloto > 0) { ps.setInt(1, idVuelo); ps.setInt(2, idCopiloto); ps.addBatch(); }
            if (idAzafata > 0) { ps.setInt(1, idVuelo); ps.setInt(2, idAzafata); ps.addBatch(); }
            ps.executeBatch();
        }
    }
    
    private void cargarTripulacion(Connection cn, Vuelo v) throws SQLException {
        String sql = "SELECT t.id_tripulante, t.rol FROM vuelo_tripulacion vt " +
                     "INNER JOIN tripulacion t ON vt.id_tripulante = t.id_tripulante WHERE vt.id_vuelo = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, v.getIdVuelo());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idTrip = rs.getInt("id_tripulante");
                    String rol = rs.getString("rol");
                    if ("PILOTO".equals(rol)) v.setIdPiloto(idTrip);
                    else if ("COPILOTO".equals(rol)) v.setIdCopiloto(idTrip);
                    else if ("AZAFATA".equals(rol)) v.setIdAzafata(idTrip);
                }
            }
        }
    }
}