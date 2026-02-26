package dao;

import models.Pasajero;
import java.util.List;

public interface IPasajero {
	List<Pasajero> listarTodos();
	boolean registrar(Pasajero pasajero);
	boolean actualizar(Pasajero pasajero);
	boolean eliminar(int id);
	Pasajero obtenerPorId(int id);
}