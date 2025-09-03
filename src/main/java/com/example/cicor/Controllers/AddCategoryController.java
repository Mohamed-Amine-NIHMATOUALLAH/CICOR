package com.example.cicor.Controllers;

import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Category;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategoryController {
    @FXML
    private TextField Category_Name;
    @FXML
    private TextField Hardware_Version;
    @FXML
    private TextField Software_Version;


    @FXML
    private void handleAddCategory() {
        try {
            Category category = new Category();
            category.setName(Category_Name.getText());
            category.setHardwareVersion(Hardware_Version.getText());
            category.setSoftwareVersion(Software_Version.getText());


            CategoryDAO categoryDAO = new CategoryDAO();
            if (categoryDAO.addCategory(category)) {
                closeWindow();
            }
        } catch (Exception e) {

        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) Category_Name.getScene().getWindow();
        stage.close();
    }
}