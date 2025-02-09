/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Bagas Aldianata
 */
package src;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.SQLException;
import java.sql.Connection;

public class Koneksi {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tubesPBO";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    
    public Connection getConnection() {
        MysqlDataSource dataSource = new MysqlDataSource();
        Connection conn = null;
        
        dataSource.setURL(DB_URL);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        
        try {
            conn = dataSource.getConnection();
            System.out.println("Koneksi sukses");
        } catch (SQLException ex) {
            System.out.println("Eksepsi akses data : " + ex.getMessage());
        }
        
        return conn;
    }
}
