package services;

import dao.IVuelo;
import dao.VueloImpl;
import models.Vuelo;
import java.util.List;

public class VueloService {
	private IVuelo vueloDao = new VueloImpl();

	public List<Vuelo> obtenerVuelos() {
		return vueloDao.listarTodos();
	}

	public Vuelo obtenerVueloPorId(int id) {
		return vueloDao.obtenerPorId(id);
	}

	public boolean programarVuelo(Vuelo vuelo) {
		return vueloDao.registrar(vuelo);
	}

	public boolean actualizarVuelo(Vuelo vuelo) {
		return vueloDao.actualizar(vuelo);
	}

	public boolean eliminarVuelo(int id) {
		return vueloDao.eliminar(id);
	}

	public boolean despegarVuelo(int id) {
		return vueloDao.actualizarEstado(id, "EN_VUELO");
	}
}