package services;

import dao.ITripulante;
import dao.TripulanteImpl;
import models.Tripulante;
import java.util.List;

public class TripulanteService {
	private ITripulante tripulanteDao = new TripulanteImpl();

	public List<Tripulante> obtenerTripulacion() {
		return tripulanteDao.listarTodos();
	}

	public Tripulante obtenerTripulantePorId(int id) {
		return tripulanteDao.obtenerPorId(id);
	}

	public boolean guardarTripulante(Tripulante t) {
		return tripulanteDao.registrar(t);
	}

	public boolean actualizarTripulante(Tripulante t) {
		return tripulanteDao.actualizar(t);
	}

	public boolean eliminarTripulante(int id) {
		return tripulanteDao.eliminar(id);
	}
}