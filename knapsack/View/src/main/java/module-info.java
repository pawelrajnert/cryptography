module org.example.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires Model;

    opens org.example.view to javafx.fxml;
    exports org.example.view;
}