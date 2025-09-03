package com.example.cicor.Controllers;

import com.example.cicor.database.ArticleDAO;
import com.example.cicor.database.CategoryDAO;
import com.example.cicor.models.Article;
import com.example.cicor.models.Cardboard;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class CardboardDetailsController {

    @FXML
    private TextField cartonNumber, category, manufactureDate, capacity;
    @FXML
    private TableView<Article> articlesTable;
    @FXML
    private TableColumn<Article, String> macColumn;
    @FXML
    private TableColumn<Article, String> remarkColumn;
    @FXML
    private TableColumn<Article, String> actionColumn;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private ProgressBar capacityProgress;
    @FXML
    private Label countLabel;

    private final ObservableList<Article> macList = FXCollections.observableArrayList();
    private final ArticleDAO articleDAO = new ArticleDAO();
    private long lastScanTime = 0;
    private final TextField hiddenMacInput = new TextField();
    private Cardboard currentCardboard;

    public void setCardboard(Cardboard cardboard) {
        this.currentCardboard = cardboard;
        if (cardboard != null) {
            cartonNumber.setText(cardboard.getCartonNumber());
            category.setText(cardboard.getCategoryName());
            manufactureDate.setText(cardboard.getManufactureDate());
            capacity.setText(String.valueOf(cardboard.getQuantity()));
            updateCapacityDisplay();
            loadArticlesForCurrentCardboard();
        }
    }

    private void loadArticlesForCurrentCardboard() {
        if (currentCardboard != null) {
            macList.clear();
            macList.addAll(articleDAO.getAllArticlesOfCardboard(currentCardboard.getCartonNumber()));
            updateCapacityDisplay();
        }
    }

    private void updateCapacityDisplay() {
        if (currentCardboard != null) {
            int currentCount = macList.size();
            int totalCapacity = currentCardboard.getQuantity();

            countLabel.setText(currentCount + "/" + totalCapacity);

            double progress = (double) currentCount / totalCapacity;
            capacityProgress.setProgress(progress);

            if (progress >= 1.0) {
                capacityProgress.setStyle("-fx-accent: #E14434;");
                countLabel.setStyle("-fx-text-fill: #E14434; -fx-font-weight: bold;");
            } else {
                capacityProgress.setStyle("-fx-accent: #2580c5;");
                countLabel.setStyle("-fx-text-fill: #2580c5; -fx-font-weight: bold;");
            }
        }
    }

    public void initialize() {
        // Configuration des colonnes du tableau
        macColumn.setCellValueFactory(cellData -> cellData.getValue().MacAddressProperty());

        // Colonne Remark avec bouton
        remarkColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Remark");
            {
                btn.setStyle("-fx-background-color: #2580c5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                btn.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    showRemarkDialog(article);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Colonne Action avec bouton Remove
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Remove");
            {
                btn.setStyle("-fx-background-color: #E14434; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                btn.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    if (article != null) {
                        macList.remove(article);
                        articleDAO.deleteArticle(article.getMacAddress());
                        updateCapacityDisplay();
                        articlesTable.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        articlesTable.setItems(macList);

        // Highlight des nouvelles lignes
        articlesTable.setRowFactory(tv -> new TableRow<Article>() {
            @Override
            protected void updateItem(Article item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.isNew()) {
                        setStyle("-fx-background-color: #D4EDDA;");
                        new Thread(() -> {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            Platform.runLater(() -> {
                                setStyle("");
                                item.setNew(false);
                            });
                        }).start();
                    }
                }
            }
        });

        // Configuration du champ caché pour le scanner
        mainAnchorPane.getChildren().add(hiddenMacInput);
        hiddenMacInput.setOpacity(0);
        hiddenMacInput.setManaged(false);
        hiddenMacInput.setFocusTraversable(true);
        hiddenMacInput.setOnKeyPressed(this::handleMacScannerInput);
        Platform.runLater(() -> hiddenMacInput.requestFocus());

        // Gestion du focus
        mainAnchorPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.focusOwnerProperty().addListener((o, oldNode, newNode) -> {
                    if (newNode != hiddenMacInput) {
                        Platform.runLater(() -> hiddenMacInput.requestFocus());
                    }
                });
            }
        });
    }

    private void showRemarkDialog(Article article) {
        TextInputDialog dialog = new TextInputDialog(article.getRemark());
        dialog.setTitle("Article Remark");
        dialog.getDialogPane().setPrefSize(600, 100);
        dialog.setGraphic(null);
        dialog.setHeaderText(null);
        dialog.setContentText("MAC Address  " + article.getMacAddress()+" \nPlease enter a remark: ");

        // Style de la boîte de dialogue
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f5f5f5;");
        dialogPane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            button.setStyle("-fx-background-color: #2580c5; -fx-text-fill: white; -fx-font-weight: bold;");
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(remark -> {
            article.setRemark(remark);
            articleDAO.AddORUpdateRemark(article.getMacAddress(), remark);
            articlesTable.refresh();
        });
    }

    private void handleMacScannerInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String scannedMac = hiddenMacInput.getText().trim().toUpperCase();
            hiddenMacInput.clear();

            // Anti-spam
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastScanTime < 500) {
                System.out.println("Scan trop rapide, ignoré.");
                return;
            }
            lastScanTime = currentTime;

            // Vérification doublon
            if (macList.stream().anyMatch(a -> a.getMacAddress().equalsIgnoreCase(scannedMac))) {
                playSound("error.mp3");
                showAlert(Alert.AlertType.WARNING, "Duplicate MAC", "This MAC address already exists in this carton!");
                return;
            }

            // Vérification capacité
            if (macList.size() >= Integer.parseInt(capacity.getText())) {
                playSound("error.mp3");
                showAlert(Alert.AlertType.ERROR, "Capacity Exceeded", "Cannot add more items than carton capacity!");
                return;
            }

            // Validation MAC
            if (!isValidMacAddress(scannedMac)) {
                return;
            }

            // Ajout de l'article
            int categoryId = new CategoryDAO().getCategoryIdByName(category.getText());
            Article newArticle = new Article(categoryId, cartonNumber.getText(), scannedMac);
            newArticle.setNew(true);

            articleDAO.insertArticle(newArticle);
            macList.add(newArticle);
            updateCapacityDisplay();
            playSound("success.mp3");
            newArticle.showSuccess("Article added successfully!");
        }
    }

    // Méthodes utilitaires
    private boolean isValidMacAddress(String mac) {
        if (mac == null || mac.length() != 12) {
            showError("MAC Address Must Be 12 Characters Long!");
            return false;
        }
        if (!mac.matches("[0-9A-Fa-f]{12}")) {
            showError("MAC Address Must Be Hexadecimal Characters!");
            return false;
        }
        if (mac.equalsIgnoreCase("000000000000")) {
            showError("MAC Address Cannot Be All Zeros!");
            return false;
        }
        if (mac.equalsIgnoreCase("FFFFFFFFFFFF")) {
            showError("MAC Address Cannot Be All F's!");
            return false;
        }
        if (articleDAO.getAllMacAddress().contains(mac)) {
            showError("MAC Address Already Exists!");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        playSound("error.mp3");
        showAlert(Alert.AlertType.ERROR, "INVALID MAC ADDRESS", message);
    }

    private void playSound(String soundFile) {
        try {
            URL resource = getClass().getResource("/com/example/cicor/sounds/" + soundFile);
            if (resource != null) {
                Media sound = new Media(resource.toString());
                new MediaPlayer(sound).play();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // Navigation
    public void handleBack(ActionEvent actionEvent) throws IOException {
        openWindow("UserDashboard.fxml", "USER DASHBOARD");
    }

    public void handleNewCarton(ActionEvent actionEvent) throws IOException {
        openNewWindow("addCardboard.fxml", "Add New Cardboard");
    }

    public void logOut(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log out?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                openWindow("login_page.fxml", "Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openWindow(String fxml, String title) throws IOException {
        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/" + fxml));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
    }

    private void openNewWindow(String fxml, String title) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cicor/views/" + fxml));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
    }
}