package services;

import dao.IPasajero;
import dao.PasajeroImpl;
import models.Pasajero;
import java.util.List;

public class PasajeroService {
	private IPasajero pasajeroDao = new PasajeroImpl();

	public List<Pasajero> obtenerPasajeros() {
		return pasajeroDao.listarTodos();
	}

	public Pasajero obtenerPasajeroPorId(int id) {
		return pasajeroDao.obtenerPorId(id);
	}

	public boolean guardarPasajero(Pasajero pasajero) {
		return pasajeroDao.registrar(pasajero);
	}

	public boolean actualizarPasajero(Pasajero pasajero) {
		return pasajeroDao.actualizar(pasajero);
	}

	public boolean eliminarPasajero(int id) {
		return pasajeroDao.eliminar(id);
	}
}