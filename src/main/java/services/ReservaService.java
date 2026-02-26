package services;

import dao.IReserva;
import dao.ReservaImpl;
import models.Pasajero;
import models.Reserva;
import models.Vuelo;
import utils.TicketPdfManager;

import java.util.List;

public class ReservaService {
	private IReserva reservaDao = new ReservaImpl();
	private PasajeroService pasajeroService = new PasajeroService();
	private VueloService vueloService = new VueloService();

	public List<Reserva> obtenerReservas() {
		return reservaDao.listarTodas();
	}

	public Reserva obtenerReservaPorId(int id) {
		return reservaDao.obtenerPorId(id);
	}

	public boolean actualizarReserva(Reserva reserva) {
		return reservaDao.actualizar(reserva);
	}

	public boolean eliminarReserva(int id) {
		return reservaDao.eliminar(id);
	}

	public String procesarReserva(Reserva reserva) {
        try {
            boolean exito = reservaDao.registrar(reserva);
            if (exito) {
                // Si la base de datos guardó la reserva, generamos el PDF
                Pasajero p = pasajeroService.obtenerPasajeroPorId(reserva.getIdPasajero());
                Vuelo v = vueloService.obtenerVueloPorId(reserva.getIdVuelo());
                
                if (p != null && v != null) {
                    TicketPdfManager.generarTicket(reserva, p, v);
                }
                return "OK";
            }
            return "Error desconocido al registrar la reserva.";
        } catch (Exception e) {
            return e.getMessage(); 
        }
    }
}