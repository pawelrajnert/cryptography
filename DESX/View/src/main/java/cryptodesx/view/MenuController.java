package cryptodesx.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController {
    @FXML
    private Label welcomeText;
    // branch created
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}