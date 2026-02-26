package telemetria;

import models.Coordenada;
import java.io.*;
import java.net.Socket;

public class VueloThread extends Thread {
	private Socket socket;

	// Inyeccion de dependencia
	public VueloThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

			while (true) {
				// Leemos el objeto entrante
				Coordenada coord = (Coordenada) ois.readObject();
				System.out.println("[Vuelo " + coord.getIdVuelo() + "] -> Lat: " + coord.getLatitud() + ", Lon: "
						+ coord.getLongitud());

				// Usamos el Manager para guardar la coordenada
				CajaNegraManager.guardarCoordenada(coord);
			}
		} catch (EOFException e) {
			System.out.println("Avión desconectado. Transmisión finalizada.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}