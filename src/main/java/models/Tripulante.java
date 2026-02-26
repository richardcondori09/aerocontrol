package models;

public class Tripulante {
	private int idTripulante;
	private String nombre;
	private String rol; // 'PILOTO', 'COPILOTO', 'AZAFATA'
	private String licencia;
	private boolean activo;

	public Tripulante() {
	}

	public int getIdTripulante() {
		return idTripulante;
	}

	public void setIdTripulante(int idTripulante) {
		this.idTripulante = idTripulante;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getLicencia() {
		return licencia;
	}

	public void setLicencia(String licencia) {
		this.licencia = licencia;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
}