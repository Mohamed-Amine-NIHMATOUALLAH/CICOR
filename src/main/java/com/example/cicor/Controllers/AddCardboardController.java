package com.example.cicor.Controllers;

import com.example.cicor.database.CardboardDAO;
import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Cardboard;
import com.example.cicor.models.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class AddCardboardController {
    @FXML
    private TextField cartonNumberField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextField manufactureDateField;
    @FXML
    private TextField quantityField;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CardboardDAO cardboardDAO = new CardboardDAO();

    @FXML
    private void initialize() {
        ObservableList<Category> categories =
                FXCollections.observableArrayList(categoryDAO.getAllCategories());
        categoryComboBox.setItems(categories);

        // ✅ display category name
        categoryComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }
            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        // Add listener to cartonNumberField to auto-populate manufactureDateField
        cartonNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() >= 8) {
                String datePart = newValue.substring(0, 8);
                String weekNumber = convertDateToWeekNumber(datePart);
                manufactureDateField.setText(weekNumber);
            } else {
                manufactureDateField.setText("");
            }
        });
    }

    private String convertDateToWeekNumber(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int year = date.getYear() % 100; // Get last two digits of the year
            int week = date.get(weekFields.weekOfWeekBasedYear());
            return String.format("%02d%02d", year, week);
        } catch (DateTimeParseException e) {
            return ""; // Return empty string if date format is invalid
        }
    }

    @FXML
    public void handleSave() {
        try {
            // ✅ validation
            String cartonNumber = cartonNumberField.getText().trim();
            if (cartonNumber.isEmpty() ||
                    manufactureDateField.getText().isEmpty() ||
                    quantityField.getText().isEmpty() ||
                    categoryComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields!");
                return;
            }

            // ✅ check format
            if (!cartonNumber.matches("\\d{12}")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Format",
                        "Carton Number must be 12 digits (YYYYMMDDNNNN)!");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText().trim());

            Cardboard cardboard = new Cardboard();
            cardboard.setCartonNumber(cartonNumber);
            cardboard.setCategoryName(categoryComboBox.getValue().getName());
            cardboard.setManufactureDate(manufactureDateField.getText().trim());
            cardboard.setQuantity(qty);

            if (cardboardDAO.addCardboard(cardboard)) {
                closeWindow();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a number!");
        }
    }

    @FXML
    public void handleCancel() {
        clearForm();
        closeWindow();
    }

    private void clearForm() {
        cartonNumberField.clear();
        manufactureDateField.clear();
        quantityField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void closeWindow() {
        Stage stage = (Stage) cartonNumberField.getScene().getWindow();
        stage.close();
    }
}


