package telemetria;

import java.net.ServerSocket;
import java.net.Socket;

public class RadarServer {
    public static void main(String[] args) {
        int puerto = 9090;
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Radar Server iniciado. Escuchando aviones en el puerto " + puerto + "...");
            
            while (true) {
                // El servidor se queda bloqueado aquí hasta que un avión se conecta
                Socket socketAvion = serverSocket.accept();
                System.out.println("¡Nuevo avión detectado en el radar! IP: " + socketAvion.getInetAddress());
                
                // Asignamos un Hilo independiente para no bloquear el servidor
                VueloThread hiloVuelo = new VueloThread(socketAvion);
                hiloVuelo.start();
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor Radar: " + e.getMessage());
        }
    }
}