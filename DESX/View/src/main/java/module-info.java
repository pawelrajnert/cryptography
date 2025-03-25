module cryptodesx.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires cryptodesx;

    opens cryptodesx.view to javafx.fxml;
    exports cryptodesx.view;
}