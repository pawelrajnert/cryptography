package knapsackProject.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import knapsackProject.KeysGenerator;

import java.util.List;


public class MainController {
    List<Integer> privKeyHolder;

    @FXML
    private TextField pubKey;

    @FXML
    private TextField privKey;

    @FXML
    private TextField nBox;

    @FXML
    private TextField mBox;

    @FXML
    private void initialize() {
        privKey.setEditable(false);
        pubKey.setEditable(false);
        nBox.setEditable(false);
        mBox.setEditable(false);
    }

    @FXML
    private void generatePrivKey() {
        if (privKeyHolder != null) {
            privKeyHolder.clear();
        }

        privKeyHolder = KeysGenerator.generatePrivateKey();
        String key = privKeyHolder.toString();

        try {
            privKey.setText(key);
            showAlert("Operacja wykonana poprawnie!", "Wygenerowano klucz prywatny.");
        } catch (NullPointerException e) {
            showAlert("Uzyskano błąd!", "Błąd przy generowaniu klucza prywatnego.");
        }
    }

    @FXML
    private void generateM() {
        try {
            int mValue = KeysGenerator.generateM(privKeyHolder);
            String mHolder = Integer.toString(mValue);
            mBox.setText(mHolder);
        } catch (NullPointerException e) {
            showAlert("Uzyskano błąd!", "Błąd przy generowaniu wartości m.");
        }
    }

    @FXML
    private void generateN() {
        int mValue = Integer.parseInt(mBox.getText());
        try {
            int nValue = KeysGenerator.generateN(mValue);
            String nHolder = Integer.toString(nValue);
            nBox.setText(nHolder);
        } catch (NullPointerException e) {
            showAlert("Uzyskano błąd!", "Błąd przy generowaniu wartosci n.");
        }
    }

    @FXML
    private void generatePubKey() {
        generateM();
        generateN();
        int m = Integer.parseInt(mBox.getText());
        int n = Integer.parseInt(nBox.getText());
        String key = KeysGenerator.generatePublicKey(m, n, privKeyHolder).toString();

        try {
            pubKey.setText(key);
            showAlert("Operacja wykonana poprawnie!", "Wygenerowano klucz publiczny.");
        } catch (NullPointerException e) {
            showAlert("Uzyskano błąd!", "Błąd przy generowaniu klucza publicznego.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}