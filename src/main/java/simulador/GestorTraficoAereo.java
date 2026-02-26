package simulador;

import models.Vuelo;
import services.VueloService;

import java.util.List;

public class GestorTraficoAereo {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SISTEMA DE TRÁFICO AÉREO AUTOMÁTICO ===");
        
        VueloService vueloService = new VueloService();
        List<Vuelo> todosLosVuelos = vueloService.obtenerVuelos();
        
        int avionesDespegados = 0;

        for (Vuelo vuelo : todosLosVuelos) {
            // Filtramos solo los que están programados
            if ("PROGRAMADO".equals(vuelo.getEstado())) {
                
                // 1. Actualizamos la BD para pasarlo a "EN_VUELO"
                boolean actualizado = vueloService.despegarVuelo(vuelo.getIdVuelo());
                
                if (actualizado) {
                    avionesDespegados++;
                    // 2. Lanzamos el Hilo de Simulación
                    AvionSimuladoThread hiloAvion = new AvionSimuladoThread(vuelo);
                    hiloAvion.start();
                }
            }
        }
        
        if (avionesDespegados == 0) {
            System.out.println("No hay vuelos en estado 'PROGRAMADO' para hacer despegar.");
        } else {
            System.out.println("Se ha dado la orden de despegue a " + avionesDespegados + " aviones.");
            System.out.println("Monitoriza el RadarServer para ver la telemetría entrando en paralelo.");
        }
    }
}