module com.example.cicor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.media;
    requires javafx.graphics;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.zaxxer.hikari;

    opens com.example.cicor.Controllers to javafx.fxml;
    opens com.example.cicor.views to javafx.fxml; // si tu charges des fxml dans ce package
    exports com.example.cicor;
    exports com.example.cicor.database;
}
