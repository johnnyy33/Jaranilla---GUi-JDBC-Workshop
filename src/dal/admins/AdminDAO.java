package dal.admins;

import db.Database;

import java.sql.*;

public class AdminDAO {

    public AdminDAO() {
        createTable();
        insertDefaultAdminIfNoneExists();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS admins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL)";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating admins table: " + e.getMessage());
        }
    }

    private void insertDefaultAdminIfNoneExists() {
        String checkSql = "SELECT COUNT(*) FROM admins";
        String insertSql = "INSERT INTO admins (username, password) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "1234");
                    insertStmt.executeUpdate();
                    System.out.println("Default admin (admin / admin123) created.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting default admin: " + e.getMessage());
        }
    }

    public boolean checkIfAdminExists(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ptstmt = conn.prepareStatement(sql)) {
            ptstmt.setString(1, username);
            ptstmt.setString(2, password);

            try (ResultSet rs = ptstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin credentials: " + e.getMessage());
        }
        return false;
    }
}
