package knapsackProject.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import knapsackProject.BinaryDao;
import knapsackProject.KeysGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainController {
    List<Integer> privKeyHolder;
    List<Integer> pubKeyHolder;

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
    private void binarySaver() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz gdzie chcesz zapisać klucz prywatny:");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (privKeyHolder == null) {
                showAlert("Błąd", "Brak odszyfrowanych danych do zapisania.");
                return;
            }
            String sb = privKeyHolder + System.lineSeparator() + mBox.getText() + System.lineSeparator() + nBox.getText();

            BinaryDao<byte[]> dao = new BinaryDao<>();
            dao.write(file.getAbsolutePath(), sb.getBytes());
            showAlert("Zapisano klucz prywatny", "Poprawnie zapisano dane do pliku: " + file.getName());
        }
    }

    @FXML
    private void binaryLoader() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik zawierający klucz prywatny: ");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            clearAll();
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] data = dao.read(file.getAbsolutePath());
            if (data != null) {
                try {
                    String fileData = new String(data);
                    String[] lines = fileData.split("\n");

                    if (lines.length != 3) {
                        showAlert("Błąd!", "Niepoprawny plik do odczytu klucza!");
                        return;
                    }

                    String privKeyLine = lines[0].trim();
                    privKey.setText(privKeyLine); // wczytujemy klucz do pola tekstowego
                    privKeyHolder = new ArrayList<>(); // a nastepnie tez do zmiennej
                    String keyNumbers = privKeyLine.replace("[", "").replace("]", "");
                    for (String num : keyNumbers.split(",")) {
                        privKeyHolder.add(Integer.parseInt(num.trim()));
                    }
                    mBox.setText(lines[1].trim()); // w 2 linii pliku znajduje sie wartosc m
                    nBox.setText(lines[2].trim()); // a w 3 linii pliku znajduje sie wartosc n

                } catch (Exception e) {
                    showAlert("Wystąpił nieoczekiwany błąd:", e.getMessage());
                    return;
                }
            } else {
                showAlert("Błąd odczytu danych", "Nie udało się odczytać klucza prywatnego z pliku: " + file.getName());
                return;
            }
            try {
                int m = Integer.parseInt(mBox.getText());
                int n = Integer.parseInt(nBox.getText());
                KeysGenerator.verifyLoadedData(privKeyHolder, m, n);
            }
            catch (IllegalArgumentException e) {
                clearAll();
                showAlert("Błąd odczytu danych!", e.getMessage());
                return;
            }
            showAlert("Odczytano klucz prywatny", "Poprawnie odczytano klucz prywatny wraz z wartościami m i n.");
        }
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
            generateM();
            generateN();
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
        if (privKeyHolder == null) {
            showAlert("Uzyskano błąd!", "Klucz prywatny jest pusty!");
            return;
        }

        if (pubKeyHolder != null) {
            pubKeyHolder.clear();
        }

        try {
            if (nBox.getText().isEmpty() && mBox.getText().isEmpty()) {
                generateM();
                generateN();
            }
        } catch (Exception e) {
            showAlert("Uzyskano błąd!", "Błąd przy odczycie wartości m i n.");
        }
        int m = Integer.parseInt(mBox.getText());
        int n = Integer.parseInt(nBox.getText());
        pubKeyHolder = KeysGenerator.generatePublicKey(m, n, privKeyHolder);
        String key = pubKeyHolder.toString();

        try {
            pubKey.setText(key);
            showAlert("Operacja wykonana poprawnie!", "Wygenerowano klucz publiczny.");
        } catch (NullPointerException e) {
            showAlert("Uzyskano błąd!", "Błąd przy generowaniu klucza publicznego.");
        }
    }

    private void clearAll() {
        if (privKeyHolder != null && privKey != null) {
            privKeyHolder.clear();
            privKey.setText("");
        }
        if (pubKeyHolder != null && pubKey != null) {
            pubKeyHolder.clear();
            pubKey.setText("");
        }
        if (nBox != null) nBox.setText("");
        if (mBox != null) mBox.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}