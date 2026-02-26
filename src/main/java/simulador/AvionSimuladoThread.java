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

    @Override
    public void run() {
        try (Socket socket = new Socket(ipServidor, puerto);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            
            System.out.println("Vuelo " + vuelo.getIdVuelo() + " despegando. Conectado al Radar.");
            
            // Coordenadas base
            double lat = -12.0000; 
            double lon = -77.0000;
            double altitud = 30000.0;

            // Simularemos 10 transmisiones por avión
            for (int i = 0; i < 10; i++) {
                lat += (Math.random() * 0.1); // Para que el avión se mueva
                lon += (Math.random() * 0.1);
                
                Coordenada coord = new Coordenada(vuelo.getIdVuelo(), lat, lon, altitud);
                oos.writeObject(coord);               
                // Pausa de 3 segundos
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