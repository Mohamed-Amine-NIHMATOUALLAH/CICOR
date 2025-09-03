package com.example.cicor.Controllers;

import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoriesController implements Initializable {

    // --- UI Components from FXML ---
    @FXML
    private TableView<Category> categoriesTable;
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> hardwareColumn;
    @FXML
    private TableColumn<Category, String> softwareColumn;


    // --- DAO and Data ---
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ObservableList<Category> categorieList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ✅ Link each table column to the correct property
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        hardwareColumn.setCellValueFactory(cellData -> cellData.getValue().hardwareVersionProperty());
        softwareColumn.setCellValueFactory(cellData -> cellData.getValue().softwareVersionProperty());


        // ✅ Load categories from DB when screen is initialized
        loadCategories();
    }

    // --- Navigation and Actions ---
    public void openCategory(ActionEvent e) throws IOException {
        open(e, "Categories.fxml", "Categories List");
    }

    public void openRapports(ActionEvent e) {
        // TODO: Add logic to open Reports page
    }

    public void log_out(ActionEvent e) throws IOException {
        Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        closeAlert.setTitle("Log Out Confirmation");
        closeAlert.setContentText("Are you sure you want to log out?");
        Optional<ButtonType> option = closeAlert.showAndWait();
        if (option.isPresent() && option.get() == ButtonType.OK){
        open(e, "login_page.fxml", "CICOR login page");}
    }

    // ✅ Add category
    public void handleAddCategory(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/addCategory.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Add New Category");
        stage.setScene(scene);
        stage.show();
    }

    // ✅ Update category (open update form)
    public void handleUpdateCategory(ActionEvent e) throws IOException {
        Category selected = categoriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/UpdateCategory.fxml"));
            Parent root = fxmlLoader.load();

            // Pass selected category to update controller
            UpdateCategoryController controller = fxmlLoader.getController();
            controller.setCategory(selected);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Category");
            stage.show();
        } else {
            showAlert(Alert.AlertType.WARNING, "Update Error" , "Please select a category to update!" );
        }
    }

    // ✅ Refresh table
    public void handlerefresh(ActionEvent e) {
        loadCategories();
    }

    // --- Data Loading ---
    private void loadCategories() {
        categorieList.clear();
        categorieList.addAll(categoryDAO.getAllCategories());
        categoriesTable.setItems(categorieList);

    }

    // --- Utility function for scene switching ---
    private void open(ActionEvent e, String fxmlFile, String title) throws IOException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/" + fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}
