module knapsackProject.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires knapsackModel;

    opens knapsackProject.view to javafx.fxml;
    exports knapsackProject.view;
}