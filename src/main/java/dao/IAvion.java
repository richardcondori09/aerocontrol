package dao;

import java.util.List;

import models.Avion;

public interface IAvion {
	List<Avion> listarTodos();
	boolean registrar(Avion avion);
	boolean actualizar(Avion avion);
	boolean eliminar(int id);
	Avion obtenerPorId(int id);
}
