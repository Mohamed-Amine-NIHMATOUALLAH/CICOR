package com.example.cicor.models;

import com.example.cicor.database.ArticleDAO;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.List;

public class Article {
    private final IntegerProperty CategoryId;
    private final StringProperty cartonNumber;
    private final StringProperty MacAddress;
    private final StringProperty Remark;
    private final BooleanProperty isNew;
    private final StringProperty hardwareVersion = new SimpleStringProperty();
    private final StringProperty softwareVersion = new SimpleStringProperty();

    private final ArticleDAO ArticleDAO = new ArticleDAO();

    public Article(){
        this.CategoryId= new SimpleIntegerProperty();
        this.cartonNumber=new SimpleStringProperty();
        this.MacAddress=new SimpleStringProperty();
        this.Remark = new SimpleStringProperty();
        this.isNew = new SimpleBooleanProperty(true);
    }

    public Article(int CategoryId , String cartonNumber , String MacAddress){
        this();
        setCategoryId(CategoryId);
        setcartonNumber(cartonNumber);
        setMacAddress(MacAddress);}

    public IntegerProperty CategoryIdProperty(){return CategoryId;}
    public StringProperty cartonNumberProperty(){return cartonNumber;}
    public StringProperty MacAddressProperty(){return MacAddress;}
    public StringProperty RemarkProperty(){return Remark;}
    public BooleanProperty isNewProperty(){return isNew;}
    public StringProperty hardwareVersionProperty() { return hardwareVersion; }
    public StringProperty softwareVersionProperty() { return softwareVersion; }


    public int getCategoryId(){ return CategoryId.get();}
    public void setCategoryId(int id){ this.CategoryId.set(id);}

    public String getcartonNumber(){ return cartonNumber.get();}
    public void setcartonNumber(String cartonNumber){ this.cartonNumber.set(cartonNumber);}

    public String getMacAddress(){return MacAddress.get();}
    public void setMacAddress(String MacAddress){
        if (isValidMacAddressWithoutDatabaseVerification(MacAddress)){
            this.MacAddress.set(MacAddress); }}

    public String getRemark(){ return Remark.get();}
    public void setRemark(String Remark){ this.Remark.set(Remark); }

    public boolean isNew() { return isNew.get(); }
    public void setNew(boolean isNew) { this.isNew.set(isNew); }

    public String getHardwareVersion() { return hardwareVersion.get(); }
    public void setHardwareVersion(String value) { hardwareVersion.set(value); }

    public String getSoftwareVersion() { return softwareVersion.get(); }
    public void setSoftwareVersion(String value) { softwareVersion.set(value); }



    public boolean isValidMacAddressWithoutDatabaseVerification(String mac){
        List<String> ExistingMacs = ArticleDAO.getAllMacAddress();
        if (mac == null || mac.length() != 12) {
            showAlert(Alert.AlertType.ERROR,"INVALID MAC ADDRESS","MAC Address Must Be 12 Characters Long !!");
            playSound("error.mp3");
            return false; }
        // 2. Vérifier que tous les caractères sont hexadécimaux (0-9, A-F)
        if (!mac.matches("[0-9A-Fa-f]{12}")) {
            playSound("error.mp3");
            showAlert(Alert.AlertType.ERROR,"INVALID MAC ADDRESS","MAC Address Must Be Hexadecimal Characters !!");
            return false; }
        // 3. Vérifier que ce n’est pas tout en "0"
        if (mac.equalsIgnoreCase("000000000000")) {
            playSound("error.mp3");
            showAlert(Alert.AlertType.ERROR,"INVALID MAC ADDRESS","MAC Address Cannot Be All Zeros !!");
            return false; }
        // 4. Vérifier que ce n’est pas tout en "F" (sauf broadcast si tu veux autoriser)
        if (mac.equalsIgnoreCase("FFFFFFFFFFFF")) {
            playSound("error.mp3");
            showAlert(Alert.AlertType.ERROR,"INVALID MAC ADDRESS","MAC Address Cannot Be All F's !!");
            return false; }
        return true;
    }


    public void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void playSound(String soundFile) {
        try {
            // تغيير المسار ليتناسب مع هيكل مجلد resources
            URL resource = getClass().getResource("/com/example/cicor/sounds/" + soundFile);
            if (resource == null) {
                System.err.println("Sound file not found: " + soundFile);
                return;
            }
            Media sound = new Media(resource.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }


}
