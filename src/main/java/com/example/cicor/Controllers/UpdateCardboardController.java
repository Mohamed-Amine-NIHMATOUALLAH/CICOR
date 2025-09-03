package com.example.cicor.Controllers;

import com.example.cicor.database.CardboardDAO;
import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Cardboard;
import javafx.event.ActionEvent;
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

public class UpdateCardboardController {
    @FXML
    private TextField cartonNumberField;
    @FXML
    private ComboBox<String> categoryComboBox; // ✅ String بدل Category
    @FXML
    private TextField manufactureDateField;
    @FXML
    private TextField quantityField;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CardboardDAO cardboardDAO = new CardboardDAO();
    private Cardboard cardboard; // current carton
    private String oldCartonNumber; // نخزن الرقم القديم باش نستعملو فـ update

    @FXML
    private void initialize() {
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

    public void setCardboard(Cardboard cardboard) {
        this.cardboard = cardboard;
        this.oldCartonNumber = cardboard.getCartonNumber();

        cartonNumberField.setText(cardboard.getCartonNumber());
        manufactureDateField.setText(cardboard.getManufactureDate());
        quantityField.setText(String.valueOf(cardboard.getQuantity()));

        // ✅ populate categories as String
        categoryComboBox.getItems().setAll(categoryDAO.getAllCategoryNames());
        categoryComboBox.setValue(cardboard.getCategoryName());
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
    public void handleUpdate(ActionEvent e) {
        if (cartonNumberField.getText().isEmpty() ||
                manufactureDateField.getText().isEmpty() ||
                quantityField.getText().isEmpty() ||
                categoryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields!");
            return;
        }

        try {
            cardboard.setCartonNumber(cartonNumberField.getText().trim());
            cardboard.setCategoryName(categoryComboBox.getValue());
            cardboard.setManufactureDate(manufactureDateField.getText().trim());
            cardboard.setQuantity(Integer.parseInt(quantityField.getText().trim()));

            if (cardboardDAO.updateCardboard(cardboard, oldCartonNumber)) {
                closeWindow();
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a number!");
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cartonNumberField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}


