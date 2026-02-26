package models;

public class Avion {
	private int idAvion;
	private String modelo;
	private int capacidad;
	private String estado;

	public Avion() {
	}

	public Avion(String modelo, int capacidad, String estado) {
		this.modelo = modelo;
		this.capacidad = capacidad;
		this.estado = estado;
	}


	public int getIdAvion() {
		return idAvion;
	}

	public void setIdAvion(int idAvion) {
		this.idAvion = idAvion;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
