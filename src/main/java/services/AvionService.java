package services;

import dao.IAvion;
import models.Avion;

import java.util.List;

import dao.AvionImpl;

public class AvionService {
	private IAvion avionDao = new AvionImpl();

	public List<Avion> obtenerAviones() {
		return avionDao.listarTodos();
	}

	public Avion obtenerAvionPorId(int id) {
		return avionDao.obtenerPorId(id);
	}

	public boolean guardarAvion(Avion avion) {
		return avionDao.registrar(avion);
	}

	public boolean actualizarAvion(Avion avion) {
		return avionDao.actualizar(avion);
	}

	public boolean eliminarAvion(int id) {
		return avionDao.eliminar(id);
	}
}
