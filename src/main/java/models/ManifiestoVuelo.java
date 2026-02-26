package models;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlRootElement(name = "ManifiestoVuelo")
public class ManifiestoVuelo {
	private int idVuelo;
	private List<Pasajero> pasajeros;

	public ManifiestoVuelo() {
	}

	@XmlElement
	public int getIdVuelo() {
		return idVuelo;
	}

	public void setIdVuelo(int idVuelo) {
		this.idVuelo = idVuelo;
	}

	@XmlElement(name = "Pasajero")
	public List<Pasajero> getPasajeros() {
		return pasajeros;
	}

	public void setPasajeros(List<Pasajero> pasajeros) {
		this.pasajeros = pasajeros;
	}
}