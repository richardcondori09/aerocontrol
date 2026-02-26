package telemetria;

import models.Coordenada;
import java.io.*;

public class CajaNegraManager {

	// Guarda los datos primitivos recibidos por Socket
	public static void guardarCoordenada(Coordenada coord) {
		String nombreArchivo = "cajanegra_vuelo_" + coord.getIdVuelo() + ".dat";

		// Usamos el parámetro true que permite agregar datos al final del archivo
		try (FileOutputStream fos = new FileOutputStream(nombreArchivo, true);
				DataOutputStream dos = new DataOutputStream(fos)) {

			dos.writeInt(coord.getIdVuelo());
			dos.writeDouble(coord.getLatitud());
			dos.writeDouble(coord.getLongitud());
			dos.writeDouble(coord.getAltitud());
			dos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Lee el archivo completo para encontrar la última coordenada registrada
	public static Coordenada recuperarUltimaPosicion(int idVuelo) {
		String nombreArchivo = "cajanegra_vuelo_" + idVuelo + ".dat";
		File file = new File(nombreArchivo);
		if (!file.exists())
			return null;

		Coordenada ultima = null;
		try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {

			// Leemos secuencialmente hasta que no haya más bytes disponibles
			while (dis.available() > 0) {
				int id = dis.readInt();
				double lat = dis.readDouble();
				double lon = dis.readDouble();
				double alt = dis.readDouble();
				ultima = new Coordenada(id, lat, lon, alt); // Se sobreescribe hasta quedar con la última
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ultima; // Retorna la última posición antes de que el avión se desconectara
	}
}