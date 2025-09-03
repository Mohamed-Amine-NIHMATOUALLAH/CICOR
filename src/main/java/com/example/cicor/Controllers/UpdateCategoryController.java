package com.example.cicor.Controllers;

import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Category;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateCategoryController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField hardwareField;
    @FXML
    private TextField softwareField;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Category category;  // category selected from table
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // --- Method to receive category from CategoriesController ---
    public void setCategory(Category category) {
        this.category = category;

        // Prefill form fields with current values
        nameField.setText(category.getName());
        hardwareField.setText(category.getHardwareVersion());
        softwareField.setText(category.getSoftwareVersion());

    }

    // --- Save updated category ---
    @FXML
    private void handleSave(ActionEvent e) {
        try {
            // Update values
            category.setName(nameField.getText());
            category.setHardwareVersion(hardwareField.getText());
            category.setSoftwareVersion(softwareField.getText());


            boolean success = categoryDAO.updateCategory(category);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Category updated successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to update category.");
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Invalid input: " + ex.getMessage());
        }
    }

    // --- Cancel / Close window ---
    @FXML
    private void handleCancel(ActionEvent e) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Utility: show alert
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
