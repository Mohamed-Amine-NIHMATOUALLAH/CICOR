package com.example.cicor.services;

import com.example.cicor.models.Article;
import com.example.cicor.models.Cardboard;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Properties;

/**
 * Service for printing labels on CAB printers.
 * Supports network connection and direct printing.
 */
public class CABPrinterService {

    // Printer settings
    private String printerIP;
    private int printerPort ;
    private String printerName ;

    // Connection variables
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public CABPrinterService() {
        loadPrinterSettings();
    }

    public CABPrinterService(String ip, int port) {
        this.printerIP = ip;
        this.printerPort = port;
    }

    /**
     * Load printer settings from properties file.
     */
    private void loadPrinterSettings() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("printer.properties");

            if (input != null) {
                props.load(input);
                this.printerIP = props.getProperty("printer.ip");
                this.printerPort = Integer.parseInt(
                        props.getProperty("printer.port"));
                this.printerName = props.getProperty("printer.name");
            }
        } catch (Exception e) {
            System.out.println("Using default printer settings.");
        }
    }

    /**
     * Connect to the printer.
     */
    public boolean connectToPrinter() {
        try {
            socket = new Socket(printerIP, printerPort);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connected to printer: " + printerIP + ":" + printerPort);
            return true;

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Connection Error",
                    "Failed to connect to printer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Disconnect from the printer.
     */
    public void disconnect() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();

            System.out.println("Disconnected from printer.");

        } catch (IOException e) {
            System.err.println("Error while disconnecting: " + e.getMessage());
        }
    }

    /**
     * Print a simple cardboard label.
     */
    public boolean printCardboardLabel(Cardboard cardboard) {
        if (!connectToPrinter()) {
            return false;
        }

        try {
            String labelCommand = generateSimpleCardboardLabel(cardboard);
            writer.print(labelCommand);
            writer.flush();

            Thread.sleep(1000); // short wait for printing

            showAlert(Alert.AlertType.INFORMATION, "Printing Successful",
                    "Cardboard label printed successfully.");

            return true;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Printing Error",
                    "Failed to print cardboard label: " + e.getMessage());
            return false;

        } finally {
            disconnect();
        }
    }

    /**
     * Generate a simple cardboard label command.
     */
    private String generateSimpleCardboardLabel(Cardboard cardboard) {
        StringBuilder sb = new StringBuilder();

        // Start label - CAB/ZPL format
        sb.append("m m\n");
        sb.append("J\n");
        sb.append("S l1;0,0,68,71,104\n");

        // Company info
        sb.append("T 5,3,0,3,1;CICOR ELECTRONICS\n");
        sb.append("T 5,8,0,1,1;Manufacturing Label\n");

        // Separator line
        sb.append("L 5,13,58,1,3\n");

        // Cardboard info
        sb.append("T 5,16,0,2,1;Réference: ").append(cardboard.getCategoryName()).append("\n");
        sb.append("T 5,21,0,2,1;Numére de série carton: \n").append(cardboard.getCartonNumber()).append("\n");
        sb.append("T 5,26,0,2,1;Date code: ").append(cardboard.getManufactureDate()).append("\n");
        sb.append("T 5,31,0,2,1;Quantité: ").append(cardboard.getQuantity()).append(" pcs\n");

        // Barcode
        sb.append("B 5,36,0,1,2,2,60,B,\"").append(cardboard.getCartonNumber()).append("\"\n");

        // Print date
        String printDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        sb.append("T 5,50,0,1,1;Printed: ").append(printDate).append("\n");

        // End label
        sb.append("A 1\n");

        return sb.toString();
    }

    /**
     * Generate a single article label command.
     */
    private String generateArticleLabel(Article article, Cardboard cardboard) {
        StringBuilder sb = new StringBuilder();

        sb.append("m m\n");
        sb.append("J\n");
        sb.append("S l1;0,0,40,30,104\n");

        // Article info
        sb.append("T 2,2,0,2,1;CICOR\n");
        sb.append("T 2,7,0,1,1;Article: ").append(cardboard.getCategoryName()).append("\n");
        sb.append("T 2,11,0,1,1;MAC: ").append(article.getMacAddress()).append("\n");
        sb.append("T 2,15,0,1,1;Carton: ").append(article.getcartonNumber()).append("\n");

        // Barcode
        sb.append("B 2,19,0,1,1,1,30,B,\"").append(article.getMacAddress()).append("\"\n");

        sb.append("A 1\n");

        return sb.toString();
    }

    /**
     * Test connection with the printer.
     */
    public boolean testConnection() {
        if (!connectToPrinter()) {
            return false;
        }

        try {
            writer.print("m m\nJ\nT 5,5,0,2,1;TEST PRINT\nA 1\n");
            writer.flush();

            Thread.sleep(1000);

            showAlert(Alert.AlertType.INFORMATION, "Print Test",
                    "Test successful - check the printer.");

            return true;

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Test Failed",
                    "Failed to perform test print: " + e.getMessage());
            return false;

        } finally {
            disconnect();
        }
    }

    /**
     * Set printer information.
     */
    public void setPrinterInfo(String ip, int port, String name) {
        this.printerIP = ip;
        this.printerPort = port;
        this.printerName = name;
    }

    /**
     * Get printer information.
     */
    public String getPrinterInfo() {
        return "Printer: " + printerName + " (" + printerIP + ":" + printerPort + ")";
    }

    /**
     * Show alert messages.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}
