package com.typetrack;

import com.typetrack.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckAdmin {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username, password_hash, role FROM users WHERE username = 'admin'")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("DB Admin: " + rs.getString("username"));
                System.out.println("DB Hash: " + rs.getString("password_hash"));
                System.out.println("DB Role: " + rs.getString("role"));
            } else {
                System.out.println("Admin not found in DB.");
            }
            
            System.out.println("Generated Hash for Admin@123: " + com.typetrack.util.PasswordHasher.hash("Admin@123"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
