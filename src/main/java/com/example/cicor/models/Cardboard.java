package com.example.cicor.models;

import com.example.cicor.database.ArticleDAO;
import javafx.beans.property.*;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.Collection;
import java.util.List;

public class Cardboard {
    private final StringProperty cartonNumber;
    private final StringProperty categoryName;
    private final StringProperty manufactureDate;
    private final IntegerProperty quantity;

    public Cardboard() {
        this.cartonNumber = new SimpleStringProperty();
        this.categoryName = new SimpleStringProperty();
        this.manufactureDate = new SimpleStringProperty();
        this.quantity = new SimpleIntegerProperty();
    }

    public Cardboard(String number, String categoryName, int qty) {
        this();
        setCartonNumber(number);
        this.categoryName.set(categoryName);
        this.quantity.set(qty);

    }

    public StringProperty cartonNumberProperty() { return cartonNumber; }
    public StringProperty categoryNameProperty() { return categoryName; }
    public StringProperty manufactureDateProperty() { return manufactureDate; }
    public IntegerProperty quantityProperty() { return quantity; }

    public String getCartonNumber() { return cartonNumber.get(); }
    public void setCartonNumber(String num) {
        String cleaned = num.trim();
        if (cleaned.matches("\\d{12}")) {
            this.cartonNumber.set(cleaned);
            this.manufactureDate.set(generateManufactureDate(cleaned));
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "ERROR CARTON NUMBER FORMAT",
                    "Carton Number must be 12 digits long (YYYYMMDDNNNN)!");
        }
    }

    public String getCategoryName() { return categoryName.get(); }
    public void setCategoryName(String name) { this.categoryName.set(name.trim()); }

    public String getManufactureDate() { return manufactureDate.get(); }
    public void setManufactureDate(String date) { this.manufactureDate.set(date); }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int qty) { this.quantity.set(qty); }



    private String generateManufactureDate(String cartonNumber) {
        try {
            String year = cartonNumber.substring(0, 4);
            String month = cartonNumber.substring(4, 6);
            String day = cartonNumber.substring(6, 8);

            LocalDate date = LocalDate.of(
                    Integer.parseInt(year),
                    Integer.parseInt(month),
                    Integer.parseInt(day)
            );

            int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            String yy = year.substring(2);
            return yy + String.format("%02d", week);
        } catch (Exception e) {
            return "";
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public List<Article> getArticles(String cartonNumber) {
        ArticleDAO ArticleDAO = new ArticleDAO();
        return ArticleDAO.getAllArticlesOfCardboard(cartonNumber);
    }
}