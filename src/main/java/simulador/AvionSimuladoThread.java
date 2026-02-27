package simulador;

import models.Coordenada;
import models.Vuelo;
import services.VueloService;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class AvionSimuladoThread extends Thread {
    private Vuelo vuelo;
    private String ipServidor = "localhost";
    private int puerto = 9090;

    public AvionSimuladoThread(Vuelo vuelo) {
        this.vuelo = vuelo;
    }
    
    private double[] obtenerCoordenadasCiudad(String ciudad) {
        switch (ciudad.toUpperCase()) {
            case "LIMA": return new double[]{-12.0464, -77.0428};
            case "BOGOTA": return new double[]{4.7110, -74.0721};
            case "SANTIAGO": return new double[]{-33.4489, -70.6693};
            case "RIO": return new double[]{-21.9068, -43.1729};
            case "CUSCO": return new double[]{-13.5319, -71.9675};
            case "QUITO": return new double[]{-0.1807, -78.4678};
            case "BUENOS AIRES": return new double[]{-34.6037, -58.3816};
            case "LA PAZ": return new double[]{-16.4897, -68.1193};
            case "BRASILIA": return new double[]{-15.8267, -47.9218};
            case "SAO PAULO": return new double[]{-23.5505, -46.6333};
            case "MONTEVIDEO": return new double[]{-35.9011, -56.1645};
            case "BARRANQUILLA": return new double[]{10.9685, -74.7813};
            case "CARACAS": return new double[]{10.4806, -66.9036};
            case "ASUNCION": return new double[]{-25.2637, -57.5759};
            default: return new double[]{-12.0464, -77.0428}; // Por defecto Lima
        }
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(ipServidor, puerto);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            
        	System.out.println("Vuelo " + vuelo.getIdVuelo() + " (" + vuelo.getOrigen() + " -> " + vuelo.getDestino() + ") despegando.");
            
        	double[] coordsOrigen = obtenerCoordenadasCiudad(vuelo.getOrigen());
            double[] coordsDestino = obtenerCoordenadasCiudad(vuelo.getDestino());

            double latActual = coordsOrigen[0];
            double lonActual = coordsOrigen[1];

            // Simularemos 15 transmisiones por avión
            int transmisiones = 15;
            double pasoLat = (coordsDestino[0] - coordsOrigen[0]) / transmisiones;
            double pasoLon = (coordsDestino[1] - coordsOrigen[1]) / transmisiones;
            
            for (int i = 0; i < transmisiones; i++) {
            	latActual += pasoLat;
                lonActual += pasoLon;
                
                // Variamos la altitud (Sube hasta la mitad del viaje, luego desciende)
                double altitud = (i < 8) ? 15000 + (i * 2000) : 30000 - ((i - 7) * 3000);
                
                Coordenada coord = new Coordenada(vuelo.getIdVuelo(), latActual, lonActual, altitud);
                oos.writeObject(coord); 
                Thread.sleep(3000);
            }
            
            System.out.println("Vuelo " + vuelo.getIdVuelo() + " ha aterrizado. Transmisión finalizada.");
            
            //Cambiar estado a ATERRIZADO
            new VueloService().aterrizarVuelo(vuelo.getIdVuelo());
            System.out.println("Base de datos actualizada: Vuelo " + vuelo.getIdVuelo() + " -> ATERRIZADO");
            
        } catch (Exception e) {
            System.err.println("Vuelo " + vuelo.getIdVuelo() + " perdió señal: " + e.getMessage());
        }
    }
}