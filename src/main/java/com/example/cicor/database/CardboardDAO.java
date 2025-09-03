package com.example.cicor.database;

import com.example.cicor.models.Cardboard;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardboardDAO {
    private final DatabaseManager dbManager;

    public CardboardDAO() {
        this.dbManager = new DatabaseManager();
    }

    // ✅ Add new Cardboard
    public boolean addCardboard(Cardboard cardboard) {
        String checkSql = "SELECT COUNT(*) FROM Cardboard WHERE carton_number = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, cardboard.getCartonNumber());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "This carton number already exists!");
                return false;
            }

            // 🔎 get category_id from Category table
            int categoryId = getCategoryIdByName(conn, cardboard.getCategoryName());
            if (categoryId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Category not found!");
                return false;
            }

            // ➕ Insert carton
            String sqlInsert = "INSERT INTO Cardboard(carton_number, category_id, manufacture_date, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                insertStmt.setString(1, cardboard.getCartonNumber());
                insertStmt.setInt(2, categoryId);
                insertStmt.setString(3, cardboard.getManufactureDate()); // ✅ manufactureDate is auto-generated
                insertStmt.setInt(4, cardboard.getQuantity());
                insertStmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cardboard added successfully!");
                return true;
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            return false;
        }
    }

    public List<Cardboard> getAllCardboards() {
        List<Cardboard> cardboards = new ArrayList<>();
        String sql = """
            SELECT c.carton_number, cat.name AS category_name, 
                   c.manufacture_date, c.quantity
            FROM Cardboard c
            JOIN categories cat ON c.category_id = cat.id
            """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cardboard cardboard = new Cardboard(
                        rs.getString("carton_number"),
                        rs.getString("category_name"),
                        rs.getInt("quantity"));
                cardboards.add(cardboard);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
        return cardboards;
    }


    // 🔎 Helper: get category_id from category name
    private int getCategoryIdByName(Connection conn, String categoryName) throws SQLException {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // not found
    }

    // ⚠️ Show alert
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public boolean updateCardboard(Cardboard cardboard, String oldCartonNumber) {
        String checkSql = "SELECT COUNT(*) FROM Cardboard WHERE carton_number = ? AND carton_number <> ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // ✅ نتأكد أن الرقم ما مستعملش عند شي كارتون آخر
            checkStmt.setString(1, cardboard.getCartonNumber());
            checkStmt.setString(2, oldCartonNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "This carton number already exists!");
                return false;
            }

            // 🔎 get category_id from Category table
            int categoryId = getCategoryIdByName(conn, cardboard.getCategoryName());
            if (categoryId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Category not found!");
                return false;
            }

            String sqlUpdate = """
                UPDATE Cardboard 
                SET carton_number = ?, category_id = ?, manufacture_date = ?, quantity = ? 
                WHERE carton_number = ?
                """;

            try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                updateStmt.setString(1, cardboard.getCartonNumber());
                updateStmt.setInt(2, categoryId);
                updateStmt.setString(3, cardboard.getManufactureDate());
                updateStmt.setInt(4, cardboard.getQuantity());
                updateStmt.setString(5, oldCartonNumber); // ✅ استعمل القديم هنا

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cardboard updated successfully!");
                    return true;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Cardboard!");
                    return false;
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            return false;
        }
    }


}
