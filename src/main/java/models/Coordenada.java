package models;

import java.io.Serializable;

public class Coordenada implements Serializable {
	private static final long serialVersionUID = 1L;

	private int idVuelo;
	private double latitud;
	private double longitud;
	private double altitud;

	public Coordenada(int idVuelo, double latitud, double longitud, double altitud) {
		this.idVuelo = idVuelo;
		this.latitud = latitud;
		this.longitud = longitud;
		this.altitud = altitud;
	}

	public int getIdVuelo() {
		return idVuelo;
	}

	public double getLatitud() {
		return latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public double getAltitud() {
		return altitud;
	}
}