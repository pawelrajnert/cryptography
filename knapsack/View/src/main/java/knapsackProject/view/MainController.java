package knapsackProject.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import knapsackProject.BinaryDao;
import knapsackProject.DataConverter;
import knapsackProject.KeysGenerator;
import knapsackProject.Knapsack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainController {
    Knapsack knap = new Knapsack();
    List<Integer> privKeyHolder;
    List<Integer> pubKeyHolder;
    private byte[] decodedTextHolder;

    @FXML
    private TextField pubKey;

    @FXML
    private TextField privKey;

    @FXML
    private TextField nBox;

    @FXML
    private TextField mBox;

    @FXML
    private TextArea upperText;

    @FXML
    private TextArea lowerText;

    @FXML
    private void initialize() {
        privKey.setEditable(false);
        pubKey.setEditable(false);
        nBox.setEditable(false);
        mBox.setEditable(false);
        lowerText.setEditable(false);
    }

    @FXML
    private void binaryKeySaver() { // funkcja do zapisu klucza prywatnego + wartości n i m
        if (privKeyHolder == null || mBox == null || nBox == null) {
            showAlert("Błąd", "Brak wystarczających danych klucza prywatnego do zapisania.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz gdzie chcesz zapisać klucz prywatny:");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            String sb = privKeyHolder + System.lineSeparator() + mBox.getText() + System.lineSeparator() + nBox.getText();
            BinaryDao<byte[]> dao = new BinaryDao<>();
            dao.write(file.getAbsolutePath(), sb.getBytes());
            showAlert("Zapisano klucz prywatny", "Poprawnie zapisano dane do pliku: " + file.getName());
        }
    }

    @FXML
    private void binaryKeyLoader() { // funkcja do odczytu klucza prywatnego wraz z wartościami n i m (3 linie w pliku)
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
                } catch (NumberFormatException e) {
                    clearAll();
                    showAlert("Wystąpił błąd:", "Upewnij się, że klucz to lista Integerów!");
                    return;
                } catch (Exception e) {
                    clearAll();
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
                KeysGenerator.verifyData(privKeyHolder, m, n);
            } catch (NumberFormatException e) {
                clearAll();
                showAlert("Błąd danych!", "Niepoprawny format liczbowy w pliku w polu m lub n.");
                return;
            } catch (IllegalArgumentException e) {
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
        if (privKeyHolder == null || privKeyHolder.size() != 8) {
            showAlert("Uzyskano błąd!", "Klucz prywatny jest pusty!");
            return;
        }
        if (pubKeyHolder != null) {
            pubKeyHolder.clear();
        }
        int m, n;
        try {
            m = Integer.parseInt(mBox.getText());
            n = Integer.parseInt(nBox.getText());
        } catch (NumberFormatException e) {
            showAlert("Uzyskano błąd!", "Błąd przy odczycie wartości m i n.");
            return;
        }
        pubKeyHolder = KeysGenerator.generatePublicKey(m, n, privKeyHolder);
        String key = pubKeyHolder.toString();
        try {
            pubKey.setText(key);
            showAlert("Operacja wykonana poprawnie!", "Wygenerowano klucz publiczny.");
        } catch (NullPointerException e) {
            pubKey.setText("");
            pubKeyHolder = null;
            showAlert("Uzyskano błąd!", "Błąd przy generowaniu klucza publicznego.");
        }
    }

    @FXML
    public void encodedTextSaver() { // zapis zakodowanego pliku
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz gdzie chcesz zapisać odszyfrowany plik:");
        File file = fileChooser.showSaveDialog(new Stage());
        lowerText.getText();
        if (file != null) {
            if (lowerText.getText().isEmpty()) {
                showAlert("Błąd", "Brak zaszyfrowanych danych do zapisania.");
                return;
            }
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] data = lowerText.getText().getBytes();
            dao.write(file.getAbsolutePath(), data);
            showAlert("Zapisano zaszyfrowany plik binarny", "Poprawnie zapisano dane do pliku: " + file.getName());
        }

    }

    @FXML
    public void encodedTextLoader() { // odczyt zakodowanego pliku
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zakodowany plik binarny do odczytu: ");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] data = dao.read(file.getAbsolutePath());
            if (data != null) {
                String encodedText = new String(data);
                lowerText.setText(encodedText);
                showAlert("Wczytano zakodowany plik binarny", "Poprawnie wczytano dane z pliku: " + file.getName());
            } else {
                showAlert("Błąd odczytu danych", "Nie udało się odczytać danych z zakodowanego pliku binarnego: " + file.getName());
            }
        }
    }

    @FXML
    public void decodedTextSaver() { // zapis pliku odkodowanego
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz gdzie chcesz zapisać odszyfrowany plik/obecnie wpisany tekst:");
        File file = fileChooser.showSaveDialog(new Stage());
        upperText.getText();
        if (file != null) {
            if (upperText.getText().isEmpty()) {
                showAlert("Błąd", "Brak odszyfrowanych danych do zapisania.");
                return;
            }
            byte[] data = upperText.getText().getBytes();
            if (decodedTextHolder != null) {
                data = decodedTextHolder;
            }
            BinaryDao<byte[]> dao = new BinaryDao<>();
            dao.write(file.getAbsolutePath(), data);
            showAlert("Zapisano odszyfrowany plik binarny/obecnie wpisany tekst", "Poprawnie zapisano dane do pliku: " + file.getName());
        }
    }

    @FXML
    public void decodedTextLoader() { // odczyt pliku odkodowanego
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zakodowany plik binarny do odczytu: ");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] data = dao.read(file.getAbsolutePath());
            if (data != null) {
                setDataHolder(data);
                showAlert("Wczytano odkodowany plik binarny", "Poprawnie wczytano dane z pliku: " + file.getName());
            } else {
                clearDataHolder();
                showAlert("Błąd odczytu danych", "Nie udało się odczytać danych z odkodowanego pliku binarnego: " + file.getName());
            }

        }
    }

    @FXML
    public void decodeAll() { // odkodowanie danych z dolnego pola (zawierającego zakodowany tekst w postaci tablicy intów [ALE TRZYMAMY TO JAKO TEKST BASE64 W POLU])
        if (privKey.getText().isEmpty()) {
            showAlert("Błąd!", "Brak klucza prywatnego.");
            return;
        }
        try {
            String decodedText = lowerText.getText();
            List<Integer> toDecode = DataConverter.decodeCipherText(decodedText);
            configureKnapsack();
            byte[] decrypted = knap.decrypt(toDecode);

            setDataHolder(decrypted);
            showAlert("Odkodowano wiadomość", "Udało się odkodować wiadomość.");
        } catch (IllegalArgumentException e) {
            clearDataHolder();
            showAlert("Błąd odkodowania!", "Upewnij się, że w polu znajduje się poprawny tekst base64!");
        } catch (Exception e) {
            clearDataHolder();
            showAlert("Nieoczekiwany błąd!", "Nie udało się odkodować wiadomości.");
        }
    }

    @FXML
    public void encodeAll() { // kodowanie danych z górnego pola i zamieszczenie ich w dolnym polu (jest to tablica intów ale w pole wpisujemy to jako BASE64)
        byte[] toEncode = upperText.getText().getBytes();;
        if (decodedTextHolder != null) {
            toEncode = decodedTextHolder;
        }

        if (pubKey.getText().isEmpty()) {
            showAlert("Błąd!", "Brak klucza publicznego.");
            return;
        }
        try {
            configureKnapsack();
            List<Integer> cipherText = knap.encrypt(toEncode);
            String base64CipherText = DataConverter.showCipherText(cipherText);
            lowerText.setText(base64CipherText);
            upperText.clear();
            showAlert("Zakodowano wiadomość", "Udało się zakodować wiadomość.");
        } catch (Exception e) {
            showAlert("Błąd!", "Nie udało się zakodować wiadomości.");
            lowerText.setText("");
        }
    }

    private void configureKnapsack() {
        knap.setPublicKey(pubKeyHolder);
        knap.setPrivateKey(privKeyHolder);
        int m = Integer.parseInt(mBox.getText().trim());
        knap.setM(m);
        int n = Integer.parseInt(nBox.getText().trim());
        knap.setN(n);
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

    private void clearDataHolder() {
        decodedTextHolder = null;
        upperText.setText("");
    }

    private void setDataHolder(byte[] data) {
        decodedTextHolder = data;
        upperText.setText(new String(data));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}