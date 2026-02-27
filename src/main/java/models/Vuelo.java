package models;

public class Vuelo {
	private int idVuelo;
	private int idAvion;
	private String origen;
	private String destino;
	private String fechaSalida; // Formato esperado: "YYYY-MM-DD HH:MM:SS"
	private String estado;
	private int idPiloto;
	private int idCopiloto;
	private int idAzafata;

	public Vuelo() {
	}

	// Getters y Setters
	public int getIdVuelo() {
		return idVuelo;
	}

	public void setIdVuelo(int idVuelo) {
		this.idVuelo = idVuelo;
	}

	public int getIdAvion() {
		return idAvion;
	}

	public void setIdAvion(int idAvion) {
		this.idAvion = idAvion;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(String fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getIdPiloto() {
		return idPiloto;
	}

	public void setIdPiloto(int idPiloto) {
		this.idPiloto = idPiloto;
	}

	public int getIdCopiloto() {
		return idCopiloto;
	}

	public void setIdCopiloto(int idCopiloto) {
		this.idCopiloto = idCopiloto;
	}

	public int getIdAzafata() {
		return idAzafata;
	}

	public void setIdAzafata(int idAzafata) {
		this.idAzafata = idAzafata;
	}
	
	
}