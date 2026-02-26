package utils;

import java.io.File;

public class FileManager {
    // Apunta a la carpeta del usuario
	
	// Cambiar ruta para probar
	private static final String BASE_DIR = "C:\\Users\\RICHARD\\Documents\\workspace ECLIPSE JEE\\AerocontrolWebApp\\AeroDatos";
	
	// Ruta para prueba en instituto
	// private static final String BASE_DIR = "D:\\AeroDatos";
	
    public static final String CAJA_NEGRA_DIR = BASE_DIR + File.separator + "BlackBox";
    public static final String TICKETS_DIR = BASE_DIR + File.separator + "Tickets";

    // Crear las carpetas si no existen
    static {
        new File(CAJA_NEGRA_DIR).mkdirs();
        new File(TICKETS_DIR).mkdirs();
    }

    public static String getRutaCajaNegra(String nombreArchivo) {
        return CAJA_NEGRA_DIR + File.separator + nombreArchivo;
    }

    public static String getRutaTicket(String nombreArchivo) {
        return TICKETS_DIR + File.separator + nombreArchivo;
    }
    
    public static String getDirectorioBase() {
    	return BASE_DIR;
    }
}