package com.example.cicor.Controllers;

import com.example.cicor.database.ArticleDAO;
import com.example.cicor.database.CardboardDAO;
import com.example.cicor.models.Cardboard;
import com.example.cicor.models.ExcelExporter;
import com.example.cicor.services.CABPrinterService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Optional;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDashboardController {

    @FXML
    private TableView<Cardboard> cartonsTable;
    @FXML
    private TableColumn<Cardboard, String> cartonNumberColumn;
    @FXML
    private TableColumn<Cardboard, String> categoryColumn;
    @FXML
    private TableColumn<Cardboard, String> dateColumn;
    @FXML
    private TableColumn<Cardboard, Integer> quantityColumn;
    @FXML
    private TableColumn<Cardboard, Integer> filledColumn;
    @FXML
    private TableColumn<Cardboard, Void> actionsColumnOpenBoxes;
    @FXML
    private TableColumn<Cardboard, Void> actionsColumnExportExcel;
    @FXML
    private TableColumn<Cardboard, Void> actionsColumnPrintTicket;

    private final ObservableList<Cardboard> cardboardslist = FXCollections.observableArrayList();
    private final CardboardDAO cardboardDAO = new CardboardDAO();

    private final CABPrinterService printerService = new CABPrinterService();

    @FXML
    public void initialize() {
        cartonNumberColumn.setCellValueFactory(cellData -> cellData.getValue().cartonNumberProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().manufactureDateProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

        filledColumn.setCellValueFactory(cellData -> {
            ArticleDAO articleDAO = new ArticleDAO();
            String cartonNumber = cellData.getValue().getCartonNumber();
            int articlesInCarton = articleDAO.getAllArticlesOfCardboard(cartonNumber).size();
            int remaining = cellData.getValue().getQuantity() - articlesInCarton;
            return new SimpleIntegerProperty(remaining).asObject();
        });

        // 🟢 "Open" button
        actionsColumnOpenBoxes.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("📦 Open");
            {
                btn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; "
                        + "-fx-font-weight: bold; -fx-background-radius: 6; "
                        + "-fx-cursor: hand; -fx-padding: 4 12;");
                btn.setOnAction(event -> {
                    Cardboard selectedCardboard = getTableView().getItems().get(getIndex());
                    if (selectedCardboard != null) {
                        try {
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/CardboardDetails.fxml"));
                            Parent root = fxmlLoader.load();
                            CardboardDetailsController controller = fxmlLoader.getController();

                            controller.setCardboard(selectedCardboard);

                            stage.setTitle("Cardboard Details");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // 🔵 "Export" button
        actionsColumnExportExcel.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("⬇ Export Excel");
            {
                btn.setStyle("-fx-background-color: #2580c5; -fx-text-fill: white; "
                        + "-fx-font-weight: bold; -fx-background-radius: 6; "
                        + "-fx-cursor: hand; -fx-padding: 4 12;");
                btn.setOnAction(event -> {
                    Cardboard selectedCardboard = getTableView().getItems().get(getIndex());
                    if (selectedCardboard != null) {
                        try {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Save Excel File");

                            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                                    "Excel Files (*.xlsx)", "*.xlsx");
                            fileChooser.getExtensionFilters().add(extFilter);

                            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                            String defaultFileName = "Carton_" + selectedCardboard.getCartonNumber() + "_" + timestamp + ".xlsx";
                            fileChooser.setInitialFileName(defaultFileName);

                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            File file = fileChooser.showSaveDialog(stage);

                            if (file != null) {
                                ExcelExporter.exportCardboardToExcel(selectedCardboard, file.getAbsolutePath());
                                showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                                        "Excel file has been successfully generated at:\n" + file.getAbsolutePath());
                            }
                        } catch (IOException e) {
                            showAlert(Alert.AlertType.ERROR, "Export Error",
                                    "An error occurred while generating the Excel file: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // 🔷 "Print" button
        actionsColumnPrintTicket.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🎟 Print Ticket");
            {
                btn.setStyle("-fx-background-color: #113F67; -fx-text-fill: white; "
                        + "-fx-font-weight: bold; -fx-background-radius: 6; "
                        + "-fx-cursor: hand; -fx-padding: 4 12; ");

                // Add printing functionality
                btn.setOnAction(event -> {
                    Cardboard selectedCardboard = getTableView().getItems().get(getIndex());
                    if (selectedCardboard != null) {
                        showPrintOptionsDialog(selectedCardboard);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        loadCartons();
    }

    private void loadCartons() {
        cardboardslist.clear();
        cardboardslist.addAll(cardboardDAO.getAllCardboards());
        cartonsTable.setItems(cardboardslist);
    }

    @FXML
    public void handleAddCarton(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/addCardboard.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Add New Cardboard");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleUpdateCarton() throws IOException {
        Cardboard selectedCardboard = cartonsTable.getSelectionModel().getSelectedItem();
        if (selectedCardboard == null) {
            showAlert(Alert.AlertType.ERROR , "Selection Error" , "Please select a carton to update");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/UpdateCardboard.fxml"));
        Parent root = fxmlLoader.load();

        UpdateCardboardController controller = fxmlLoader.getController();
        controller.setCardboard(selectedCardboard);

        Stage stage = new Stage();
        stage.setTitle("Update Cardboard");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void handleRefresh() {
        loadCartons();
    }

    @FXML
    public void log_Out(ActionEvent e) throws IOException {
        Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        closeAlert.setTitle("Log Out Confirmation");
        closeAlert.setContentText("Are you sure you want to log out?");
        Optional<ButtonType> option = closeAlert.showAndWait();
        if (option.isPresent() && option.get() == ButtonType.OK){
            open(e,"login_page.fxml","CICOR Login Page");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

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

    /**
     * Displays print options for a selected cardboard
     */
    private void showPrintOptionsDialog(Cardboard cardboard) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Print Options");
        dialog.setHeaderText("Choose the type of label to print");
        dialog.setContentText("Carton: " + cardboard.getCartonNumber() + "\n" +
                "Product: " + cardboard.getCategoryName());

        ButtonType simpleLabel = new ButtonType("Label");
        ButtonType configPrinter = new ButtonType("Printer Settings");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(simpleLabel, configPrinter, cancel);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == simpleLabel) {
                handleSimplePrint(cardboard);
            } else if (buttonType == configPrinter) {
                openPrinterConfig();
            }
        });
    }

    /**
     * Print a simple label
     */
    private void handleSimplePrint(Cardboard cardboard) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Print Confirmation");
        confirmDialog.setHeaderText("Print Simple Label");
        confirmDialog.setContentText("A label will be printed containing:\n" +
                "- Basic carton information\n" +
                "- Barcode\n" +
                "- Print date\n\n" +
                "Make sure there is paper in the printer.");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            new Thread(() -> {
                try {
                    boolean success = printerService.printCardboardLabel(cardboard);

                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Print Successful",
                                    "The simple label has been printed successfully!");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Print Failed",
                                    "An error occurred while printing. Please check printer connection.");
                        }
                    });

                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Printing Error",
                                "Unexpected error: " + e.getMessage());
                    });
                }
            }).start();
        }
    }
    /**
     * Open printer settings window
     */
    private void openPrinterConfig() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/com/example/cicor/views/PrinterConfig.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Printer Settings");
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to open printer settings window: " + e.getMessage());
        }
    }

    /**
     * Open printer settings from main menu
     */
    @FXML
    private void handlePrinterSettings(ActionEvent event) {
        openPrinterConfig();
    }

    /**
     * Test printer connection from main menu
     */
    @FXML
    private void handleTestPrinter(ActionEvent event) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Test Printer");
        confirmDialog.setHeaderText("Test Printer Connection");
        confirmDialog.setContentText("A test label will be sent to the printer.\n" +
                "Make sure there is paper in the printer.\n\n" +
                "Do you want to continue?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            new Thread(() -> {
                try {
                    boolean success = printerService.testConnection();

                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Printer Test",
                                    "✅ Printer test was successful!\nCheck the printed label.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Printer Test",
                                    "❌ Printer test failed!\nCheck connection and settings.");
                        }
                    });

                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Test Error",
                                "Unexpected error: " + e.getMessage());
                    });
                }
            }).start();
        }
    }
}
