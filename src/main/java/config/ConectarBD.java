package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectarBD {
    public static String url = "jdbc:mysql://localhost:3306/aerocontrol_bd";
    public static String usuario = "root";
    public static String password = "";
    
    public static Connection getConexion() {
        Connection cn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection(url, usuario, password);
            if(cn != null) {
                System.out.println("Éxito: Conexión establecida a aerocontrol_db");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
        return cn;
    }
}