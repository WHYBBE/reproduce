module com.example.demo2048 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demo2048 to javafx.fxml;
    exports com.example.demo2048;
}