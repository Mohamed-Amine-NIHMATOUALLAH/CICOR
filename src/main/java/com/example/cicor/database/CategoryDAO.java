package com.example.cicor.database;

import com.example.cicor.models.Category;
import javafx.scene.control.Alert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final DatabaseManager dbManager;

    public CategoryDAO() {
        this.dbManager = new DatabaseManager();
    }

    // 🔹 Add new category
    public boolean addCategory(Category category) {
        String checkSql = "SELECT COUNT(*) FROM categories WHERE name = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, category.getName());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Adding ERROR", "This name already exists!!");
                return false;
            }

            String insertSql = "INSERT INTO categories(name, hardware_ver, software_ver) VALUES(?,?,?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, category.getName());
                insertStmt.setString(2, category.getHardwareVersion());
                insertStmt.setString(3, category.getSoftwareVersion());


                insertStmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Adding SUCCESS", "Category added successfully!");
                return true;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while adding category: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Get all categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("hardware_ver"),
                        rs.getString("software_ver")
                );
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération: " + e.getMessage());
        }
        return categories;
    }

    // 🔹 Get only category names
    public List<String> getAllCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        String sql = "SELECT name FROM categories";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categoryNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération des noms de catégories: " + e.getMessage());
        }
        return categoryNames;
    }

    // 🔹 Update category
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ?, hardware_ver = ?, software_ver = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getHardwareVersion());
            stmt.setString(3, category.getSoftwareVersion());
            stmt.setInt(6, category.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Update SUCCESS", "Category updated successfully!");
                return true;
            } else {
                showAlert(Alert.AlertType.ERROR, "Update ERROR", "No category found with the given ID.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur de mise à jour: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while updating category: " + e.getMessage());
            return false;
        }
    }

    public int getCategoryIdByName(String name) {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1; // Not found
            }
        } catch (SQLException e) {
            System.err.println("Erreur de récupération de l'ID: " + e.getMessage());
            return -1;
        }
    }

    // 🔹 Utility: Show alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
