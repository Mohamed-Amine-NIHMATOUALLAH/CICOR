package com.example.cicor.Controllers;

import com.example.cicor.services.CABPrinterService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.*;
import java.util.Properties;

/**
 * Controller for printer setup and configuration
 */
public class PrinterConfigController {

    @FXML
    private TextField printerIPField;
    @FXML
    private TextField printerPortField;
    @FXML
    private TextField printerNameField;
    @FXML
    private ComboBox<String> connectionTypeCombo;
    @FXML
    private TextArea testResultArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button testButton;
    @FXML
    private Button cancelButton;

    private CABPrinterService printerService;

    @FXML
    public void initialize() {
        // Load current settings
        loadCurrentSettings();

        // Setup connection type list
        connectionTypeCombo.getItems().addAll(
                "Network (TCP/IP)",
                "USB (Direct)",
                "Parallel Port"
        );
        connectionTypeCombo.setValue("Network (TCP/IP)");

        // Initialize printer service
        printerService = new CABPrinterService();

        // Set tooltips
        printerIPField.setTooltip(new Tooltip("Enter the printer's IP address (e.g., 192.168.1.100)"));
        printerPortField.setTooltip(new Tooltip("Port number, usually 9100 for network printers"));
        printerNameField.setTooltip(new Tooltip("Printer name for identification"));
    }

    /**
     * Update printer service from form fields
     */
    private void updatePrinterService() {
        String ip = printerIPField.getText().trim();
        int port = Integer.parseInt(printerPortField.getText().trim());
        String name = printerNameField.getText().trim();

        printerService.setPrinterInfo(ip, port, name);
    }

    /**
     * Load advanced configuration from a file
     */
    @FXML
    private void handleLoadConfig(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Load Configuration File");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Properties Files", "*.properties"));

        Stage stage = (Stage) saveButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Properties props = new Properties();
                FileInputStream input = new FileInputStream(file);
                props.load(input);
                input.close();

                printerIPField.setText(props.getProperty("printer.ip"));
                printerPortField.setText(props.getProperty("printer.port"));
                printerNameField.setText(props.getProperty("printer.name"));

                showAlert(Alert.AlertType.INFORMATION, "Configuration Loaded",
                        "Printer settings loaded successfully from the file.");

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Load Error",
                        "Failed to load configuration file: " + e.getMessage());
            }
        }
    }

    /**
     * Export configuration to an external file
     */
    @FXML
    private void handleExportConfig(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Configuration File");
        fileChooser.setInitialFileName("printer_config.properties");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Properties Files", "*.properties"));

        Stage stage = (Stage) saveButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Properties props = new Properties();
                props.setProperty("printer.ip", printerIPField.getText().trim());
                props.setProperty("printer.port", printerPortField.getText().trim());
                props.setProperty("printer.name", printerNameField.getText().trim());
                props.setProperty("connection.type", connectionTypeCombo.getValue());
                props.setProperty("exported.date", new java.util.Date().toString());
                props.setProperty("exported.by", System.getProperty("user.name"));

                FileOutputStream output = new FileOutputStream(file);
                props.store(output, "CICOR Printer Configuration Export");
                output.close();

                showAlert(Alert.AlertType.INFORMATION, "Configuration Exported",
                        "Printer settings exported successfully to:\n" + file.getAbsolutePath());

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Export Error",
                        "Failed to export configuration file: " + e.getMessage());
            }
        }
    }

    private void loadCurrentSettings() {
        try {
            Properties props = new Properties();
            File configFile = new File("printer.properties");

            if (configFile.exists()) {
                FileInputStream input = new FileInputStream(configFile);
                props.load(input);
                input.close();

                printerIPField.setText(props.getProperty("printer.ip", "192.168.1.100"));
                printerPortField.setText(props.getProperty("printer.port", "9100"));
                printerNameField.setText(props.getProperty("printer.name", "CAB Label Printer"));

                String connType = props.getProperty("connection.type", "Network (TCP/IP)");
                connectionTypeCombo.setValue(connType);
            } else {
                // Default settings
                printerIPField.setText("192.168.1.100");
                printerPortField.setText("9100");
                printerNameField.setText("CAB Label Printer");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Load Settings",
                    "No existing settings found. Default settings will be used.");
        }
    }

    /**
     * Save printer settings
     */
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            Properties props = new Properties();
            props.setProperty("printer.ip", printerIPField.getText().trim());
            props.setProperty("printer.port", printerPortField.getText().trim());
            props.setProperty("printer.name", printerNameField.getText().trim());
            props.setProperty("connection.type", connectionTypeCombo.getValue());
            props.setProperty("last.updated", new java.util.Date().toString());

            FileOutputStream output = new FileOutputStream("printer.properties");
            props.store(output, "CICOR Printer Configuration");
            output.close();

            showAlert(Alert.AlertType.INFORMATION, "Settings Saved",
                    "Printer settings saved successfully!");

            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Save Error",
                    "Failed to save printer settings: " + e.getMessage());
        }
    }

    /**
     * Test connection to the printer
     */
    @FXML
    private void handleTest(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        testResultArea.clear();
        testResultArea.appendText("Starting connection test...\n");

        try {
            String ip = printerIPField.getText().trim();
            int port = Integer.parseInt(printerPortField.getText().trim());
            String name = printerNameField.getText().trim();

            printerService.setPrinterInfo(ip, port, name);

            testResultArea.appendText("Attempting to connect to: " + ip + ":" + port + "\n");

            new Thread(() -> {
                try {
                    boolean success = printerService.testConnection();

                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            testResultArea.appendText("✅ Connection successful!\n");
                            testResultArea.appendText("Printer is ready.\n");
                            testResultArea.appendText("Printer info: " +
                                    printerService.getPrinterInfo() + "\n");
                        } else {
                            testResultArea.appendText("❌ Connection failed!\n");
                            testResultArea.appendText("Please check:\n");
                            testResultArea.appendText("- Correct IP address\n");
                            testResultArea.appendText("- Printer is connected to the network\n");
                            testResultArea.appendText("- Correct port number (usually 9100)\n");
                            testResultArea.appendText("- No firewall is blocking the connection\n");
                        }
                    });

                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        testResultArea.appendText("❌ Test error: " + e.getMessage() + "\n");
                    });
                }
            }).start();

        } catch (NumberFormatException e) {
            testResultArea.appendText("❌ Error: Port must be a valid number\n");
        } catch (Exception e) {
            testResultArea.appendText("❌ Unexpected error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Cancel and close the window
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    /**
     * Print a test label
     */
    @FXML
    private void handleTestPrint(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            String ip = printerIPField.getText().trim();
            int port = Integer.parseInt(printerPortField.getText().trim());
            String name = printerNameField.getText().trim();

            printerService.setPrinterInfo(ip, port, name);

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Test Print");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Do you want to print a test label?\n" +
                    "Make sure paper is loaded in the printer.");

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                boolean success = printerService.testConnection();
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Test Print",
                            "Test label sent to printer!");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Test Print Error",
                    "Failed to print: " + e.getMessage());
        }
    }


    /**
     * Validate form inputs
     */
    private boolean validateInputs() {
        if (printerIPField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please enter the printer IP address");
            printerIPField.requestFocus();
            return false;
        }

        if (printerPortField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please enter the port number");
            printerPortField.requestFocus();
            return false;
        }

        try {
            int port = Integer.parseInt(printerPortField.getText().trim());
            if (port < 1 || port > 65535) {
                showAlert(Alert.AlertType.ERROR, "Port Error",
                        "Port number must be between 1 and 65535");
                printerPortField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Port Error",
                    "Port number must be a valid integer");
            printerPortField.requestFocus();
            return false;
        }

        if (printerNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please enter a printer name");
            printerNameField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Close the current window
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show an alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private boolean validatePrinterSettings() {
        if (printerIPField.getText().trim().isEmpty() || printerPortField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Configuration Error", "Please configure printer settings before proceeding.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleResetSettings() {
        printerIPField.clear();
        printerPortField.clear();
        printerNameField.clear();
        connectionTypeCombo.setValue("Network (TCP/IP)");
        showAlert(Alert.AlertType.INFORMATION, "Reset", "Printer settings have been reset.");
    }

}
