package dao;

import models.Tripulante;
import java.util.List;

public interface ITripulante {
	List<Tripulante> listarTodos();
	boolean registrar(Tripulante tripulante);
	boolean actualizar(Tripulante tripulante);
	boolean eliminar(int id);
	Tripulante obtenerPorId(int id);
}