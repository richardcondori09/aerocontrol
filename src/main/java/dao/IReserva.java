package dao;

import models.Reserva;
import java.util.List;

public interface IReserva {
	List<Reserva> listarTodas();
	boolean registrar(Reserva reserva) throws Exception; // Excepción para manejar el asiento duplicado
	boolean actualizar(Reserva reserva);
	boolean eliminar(int id);
	Reserva obtenerPorId(int id);
}