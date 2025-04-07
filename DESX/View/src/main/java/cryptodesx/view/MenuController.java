package cryptodesx.view;

import cryptoDESX.BinaryDao;
import cryptoDESX.DES;
import cryptoDESX.DESX;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MenuController {
    private DES des = new DES();
    private DESX desx = new DESX();

    @FXML
    private TextField DESKey;

    @FXML
    private TextField DESXKey1;

    @FXML
    private TextField DESXKey2;

    @FXML
    private TextArea encodeTextArea;

    @FXML
    private TextArea decodeTextArea;

    @FXML
    public void initialize() {
        textFieldLimitation(DESKey, 8);
        textFieldLimitation(DESXKey1, 8);
        textFieldLimitation(DESXKey2, 8);
    }

    @FXML
    private void DESKeySetter() {
        String key = DESKey.getText();
        if (key.length() <= 8) {
            des.setMainKey(key);
            showAlert("Klucz poprawny", "Wprowadzono klucz główny DES.");
        } else {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
        System.out.println("Klucz główny DES: " + des.arrayToDecimal(des.getMainKey(), "%8s") + "|koniec klucza");
        System.out.println(Arrays.toString(des.getMainKey()) + "|koniec klucza");
    }

    @FXML
    private void DESXKeySetter1() {
        String key = DESXKey1.getText();
        if (key.length() <= 8) {
            desx.setInitialKey(key);
            showAlert("Klucz poprawny", "Wprowadzono pierwszy klucz do DESX.");
        } else {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
        System.out.println("Pierwszy klucz DESX: " + desx.arrayToDecimal(desx.getInitialKey(), "%8s") + "|koniec klucza");
        System.out.println(Arrays.toString(desx.getInitialKey()) + "|koniec klucza");
    }

    @FXML
    private void DESXKeySetter2() {
        String key = DESXKey2.getText();
        if (key.length() <= 8) {
            desx.setFinalKey(key);
            showAlert("Klucz poprawny", "Wprowadzono drugi klucz do DESX.");
        } else {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
        System.out.println("Drugi klucz DESX: " + desx.arrayToDecimal(desx.getFinalKey(), "%8s") + "|koniec klucza");
        System.out.println(Arrays.toString(desx.getFinalKey()) + "|koniec klucza");
    }

    @FXML
    private void binaryLoader() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz dowolny plik binarny do zaszyfrowania: ");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] textBinary = dao.read(file.getAbsolutePath());
            if (textBinary != null) {
                String text = new String(textBinary, StandardCharsets.UTF_8);
                encodeTextArea.setText(text);
                showAlert("Wczytano plik binarny", "Poprawnie wczytano dane z pliku: " + file.getName());
            } else {
                showAlert("Bląd odczytu danych", "Nie udało się odczytać danych z pliku tekstowego: " + file.getName());
            }
        }
    }

    @FXML
    private void encodeData() {

    }

    @FXML
    private void decodeData() {

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void textFieldLimitation(TextField tf, int maxLength) {
        tf.textProperty().addListener((input, oldValue, newValue) -> {
                    if (newValue.length() > maxLength) {
                        tf.setText(newValue.substring(0, maxLength));
                    }
                }
        );
    }
}