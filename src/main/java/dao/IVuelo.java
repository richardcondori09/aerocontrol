package dao;

import java.util.List;

import models.Vuelo;

public interface IVuelo {
    List<Vuelo> listarTodos();
    boolean registrar(Vuelo vuelo);
    boolean actualizarEstado(int idVuelo, String nuevoEstado);
    boolean actualizar(Vuelo vuelo);
    boolean eliminar(int id);
    Vuelo obtenerPorId(int id);
}
