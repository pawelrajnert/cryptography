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
import java.util.Base64;

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
            desx.setMainKey(key);
            showAlert("Klucz poprawny", "Wprowadzono klucz główny DES.");
        } else {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
        System.out.println("Klucz główny DES: " + desx.arrayToDecimal(desx.getMainKey(), "%8s") + "|koniec klucza");
        System.out.println(Arrays.toString(desx.getMainKey()) + "|koniec klucza");
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
        if (encodeTextArea.getText().equals("")) {
            showAlert("Błąd", "Pole tekstowe jest puste, nie można zakodować danych.");
        }

        String input = encodeTextArea.getText().trim();
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        des.isMessageCorrect(input);
        int blockCount = (data.length + 7) / 8;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < blockCount; i++) {
            byte[] block = Arrays.copyOf(Arrays.copyOfRange(data, i * 8, (i + 1) * 8), 8); // przetwazamy blok po bloku
            byte[] encrypted = desx.encryptMessage(block, false); // szyfrujemy kazdy blok
            result.append(Base64.getEncoder().encodeToString(encrypted)); // przetwarzamy do base64 zeby bylo to czytelne do odczytu
            if (i < blockCount - 1) { // jesli nie jest to ostatni blok to robimy spacje po bloku zeby ulatwic potem odkodowanie
                result.append(" ");
            }
        }
        encodeTextArea.setText(result.toString());
    }

    @FXML
    private void decodeData() {
        if (encodeTextArea.getText().equals("")) {
            showAlert("Błąd", "Pole tekstowe jest puste, nie można odkodować danych.");
        }

        String input = encodeTextArea.getText().trim();

        StringBuilder output = new StringBuilder();
        for (String block : input.split(" ")) { // mamy pomocnicze spacje dodane przy szyfrowaniu, dlatego tak szukamy bloków
            byte[] encrypted = Base64.getDecoder().decode(block); // wracamy do postaci binarnej (z postaci czytelnej dla nas)
            byte[] decrypted = desx.encryptMessage(encrypted, true); // deszyfrujemy dany blok
            output.append(new String(decrypted, StandardCharsets.UTF_8));
        }
        decodeTextArea.setText(output.toString());
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