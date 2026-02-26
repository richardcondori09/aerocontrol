package telemetria;

import models.Coordenada;
import utils.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CajaNegraManager {

	// Guarda los datos recibidos por Socket
	public static void guardarCoordenada(Coordenada coord) {
		String nombreArchivo = "cajanegra_vuelo_" + coord.getIdVuelo() + ".dat";
		// Definimos la ruta
		String rutaCompleta = FileManager.getRutaCajaNegra(nombreArchivo);

		try (FileOutputStream fos = new FileOutputStream(rutaCompleta, true);
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
		//Definimos la ruta
		String rutaCompleta = FileManager.getRutaCajaNegra(nombreArchivo);
		
		File file = new File(rutaCompleta);
		if (!file.exists())
			return null;

		Coordenada ultima = null;
		try (FileInputStream fis = new FileInputStream(file); 
			DataInputStream dis = new DataInputStream(fis)) {

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
	
	// Lee el archivo y trae todas las coordenadas
    public static List<Coordenada> recuperarHistorialCompleto(int idVuelo) {
        List<Coordenada> historial = new ArrayList<>();
        String nombreArchivo = "cajanegra_vuelo_" + idVuelo + ".dat";
        String rutaCompleta = FileManager.getRutaCajaNegra(nombreArchivo);
        
        File file = new File(rutaCompleta);
        if (!file.exists()) return historial; // Retorna lista vacía si no hay archivo

        try (FileInputStream fis = new FileInputStream(file); 
             DataInputStream dis = new DataInputStream(fis)) {

            while (true) {
                try {
                    int id = dis.readInt();
                    double lat = dis.readDouble();
                    double lon = dis.readDouble();
                    double alt = dis.readDouble();
                    historial.add(new Coordenada(id, lat, lon, alt));
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return historial; 
    }
}