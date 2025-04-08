package cryptodesx.view;

import cryptoDESX.BinaryDao;
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
    private DESX desx = new DESX();
    private String textToSave;
    private byte[] decryptedBytes;

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
        try {
            desx.setMainKey(key);
            showAlert("Klucz poprawny", "Wprowadzono klucz główny DES.");
            System.out.println("Klucz główny DES: " + desx.arrayToDecimal(desx.getMainKey(), "%8s") + "|koniec klucza");
            System.out.println(Arrays.toString(desx.getMainKey()) + "|koniec klucza");
        } catch (NullPointerException e) {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
    }

    @FXML
    private void DESXKeySetter1() {
        String key = DESXKey1.getText();
        try {
            desx.setInitialKey(key);
            showAlert("Klucz poprawny", "Wprowadzono pierwszy klucz do DESX.");
            System.out.println("Pierwszy klucz DESX: " + desx.arrayToDecimal(desx.getInitialKey(), "%8s") + "|koniec klucza");
            System.out.println(Arrays.toString(desx.getInitialKey()) + "|koniec klucza");
        } catch (NullPointerException e) {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
    }

    @FXML
    private void DESXKeySetter2() {
        String key = DESXKey2.getText();
        try {
            desx.setFinalKey(key);
            showAlert("Klucz poprawny", "Wprowadzono drugi klucz do DESX.");
            System.out.println("Drugi klucz DESX: " + desx.arrayToDecimal(desx.getFinalKey(), "%8s") + "|koniec klucza");
            System.out.println(Arrays.toString(desx.getFinalKey()) + "|koniec klucza");
        } catch (NullPointerException e) {
            showAlert("Klucz niepoprawny", "Wprowadzono niepoprawny klucz.");
        }
    }
    /*
    Metoda do wczytywania plików. Zakłada ona, że wczytywany plik nie jest zakodowany.
    Przy wczytywaniu pliku, ustawia ona 2 ważne rzeczy — encodeTextArea oraz zmienną textToSave.
    encodeTextArea pokazuje na ekranie odczytany tekst, korzystając z kodowania UTF_8.
    Natomiast textToSave w przyszłości będzie po to, by użytkownik mógł zapisać odszyfrowane dane do pliku.
    To na tej zmiennej będą przeprowadzane kolejne operacje.
    Wyłączamy także możliwość edytowania encodeTextArea. Odblokuje się ona wraz z zaszyfrowaniem danych.
     */
    @FXML
    private void binaryLoaderDecoded() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz dowolny plik binarny do zaszyfrowania: ");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] textBinary = dao.read(file.getAbsolutePath());
            if (textBinary != null) {
                textToSave = Base64.getEncoder().encodeToString(textBinary);
                String text = new String(textBinary, StandardCharsets.UTF_8);
                encodeTextArea.setText(text);
                showAlert("Wczytano plik binarny", "Poprawnie wczytano dane z pliku: " + file.getName());
                encodeTextArea.setEditable(false);
            } else {
                showAlert("Błąd odczytu danych", "Nie udało się odczytać danych z pliku binarnego: " + file.getName());
            }
        }
    }
    /*
    Na ogół zasada działania jest podobna, ale metoda ta zakłada, że pracujemy na zakodowanym już pliku,
    zatem nie ustawia zmiennej textToSave, gdyż procedura zapisu będzie inna.
     */
    @FXML
    private void binaryLoaderEncoded() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zakodowany plik binarny do odczytu: ");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] data = dao.read(file.getAbsolutePath());
            if (data != null) {
                String encodedText = new String(data, StandardCharsets.UTF_8);
                encodeTextArea.setText(encodedText);
                showAlert("Wczytano zakodowany plik binarny", "Poprawnie wczytano dane z pliku: " + file.getName());
                encodeTextArea.setEditable(false);
            } else {
                showAlert("Błąd odczytu danych", "Nie udało się odczytać danych z zakodowanego pliku binarnego: " + file.getName());
            }
        }
    }
    /*
    Zapis zaszyfrowanych danych do pliku. decryptedBytes zostaną ustawione przez metodę deszyfrującą.
    Jeśli takowe istnieją, to je zapisujemy, w przeciwnym razie takich danych po prostu nie zapiszemy.
     */
    @FXML
    private void binarySaverDecoded() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz gdzie chcesz zapisać odszyfrowany plik:");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (decryptedBytes == null) {
                showAlert("Błąd", "Brak odszyfrowanych danych do zapisania.");
                return;
            }
            BinaryDao<byte[]> dao = new BinaryDao<>();
            dao.write(file.getAbsolutePath(), decryptedBytes);
            showAlert("Zapisano odszyfrowany plik binarny", "Poprawnie zapisano dane do pliku: " + file.getName());
        }
    }
    /*
    Zapis normalnych/odszyfrowanych danych do pliku.
    Jeśli w tekstArea nie znajdują się żadne dane, to naturalnie ich nie zapisuejmy.
    W przeciwnym razie pobieramy te dane i je zapisujemy.
     */
    @FXML
    private void binarySaverEncoded() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz gdzie chcesz zapisać zaszyfrowany plik:");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (encodeTextArea.getText() == null || encodeTextArea.getText().isEmpty()) {
                showAlert("Błąd", "Brak zaszyfrowanych danych do zapisania.");
                return;
            }
            BinaryDao<byte[]> dao = new BinaryDao<>();
            byte[] dataToSave = encodeTextArea.getText().getBytes(StandardCharsets.UTF_8);
            dao.write(file.getAbsolutePath(), dataToSave);
            showAlert("Zapisano zakodowany plik binarny", "Poprawnie zapisano dane do pliku: " + file.getName());
        }
    }
    /*
    Szyfrowanie danych. Na początek sprawdzamy, czy użytkownik wprowadził klucze.
    Potem odblokowujemy możliwość edytowania pola z danymi do zaszyfrowania.
    Oczywiście nie musiało być ono zablokowane, jest ono blokowane po odczycie danych z pliku.
    Następnie sprawdzamy, czy w ogóle mamy cokolwiek do kodowania.
    Kolejnym krokiem jest ustalenie, skąd pobieramy dane. Jeśli textToSave jest nullem/pusty,
    to oznacza to, że użytkownik wpisał dane. Wówczas konwertujemy tekst z encodeTextArea na bajty i wykorzystujemy UTF-8.
    Później przekształcamy to na format Base64, by nie występowały problemy związane ze znakami specjalnymi w algorytmie.
    Natomiast jeśli textToSave coś zawierał, to pobieramy te dane bezpośrednio, bo dane tam już były zapisane w formacie Base64.
    Finalnie te dane i tak zamieniamy na UTF-8, ponieważ tutaj problem ze znakami specjalnymi już nie będzie występował.
     */
    @FXML
    private void encodeData() {
        if (desx.getMainKey() == null || desx.getInitialKey() == null || desx.getFinalKey() == null) {
            showAlert("Błąd", "Nie ustawiono wszystkich kluczy.");
            return;
        }
        encodeTextArea.setEditable(true);
        if (encodeTextArea.getText().equals("")) {
            showAlert("Błąd", "Pole tekstowe jest puste, nie można zakodować danych.");
            return;
        }
        decodeTextArea.clear();
        if (textToSave == null || textToSave.isEmpty()) {
            byte[] originalBytes = encodeTextArea.getText().getBytes(StandardCharsets.UTF_8);
            textToSave = Base64.getEncoder().encodeToString(originalBytes);
        }
        String input = textToSave;
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        desx.isMessageCorrect(input);
        int blockCount = (data.length + 7) / 8;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < blockCount; i++) {
            byte[] block = Arrays.copyOf(Arrays.copyOfRange(data, i * 8, Math.min((i + 1) * 8, data.length)), 8); // przetwazamy blok po bloku
            // bierzemy minimum z (i+1) * 8 lub rozmiaru danych, by nie przekroczyć indeksu tablicy.
            byte[] encrypted = desx.encryptMessage(block, false); // szyfrujemy kazdy blok
            result.append(Base64.getEncoder().encodeToString(encrypted)); // przetwarzamy do base64 zeby bylo to czytelne do odczytu
            if (i < blockCount - 1) { // jesli nie jest to ostatni blok to robimy spacje po bloku zeby ulatwic potem odkodowanie
                result.append(" ");
            }
        }
        encodeTextArea.setText(result.toString());
        showAlert("Udało się", "Zakodowano dane.");
        textToSave = null;
    }
    /*
    Tak samo jak przy kodowaniu, najpierw sprawdzamy klucze, odblokowujemy textArea, sprawdzamy czy wiadomość istnieje.
    Podobnie jak w przypadku szyfrowania, będziemy ustawiać jedne dane dla użytkownika w oknie tekstowym,
    a dane zapisane w formacie base64 będą wykorzystywane do plików.
     */
    @FXML
    private void decodeData() {
        if (desx.getMainKey() == null || desx.getInitialKey() == null || desx.getFinalKey() == null) {
            showAlert("Błąd", "Nie ustawiono wszystkich kluczy.");
            return;
        }
        encodeTextArea.setEditable(true);
        if (encodeTextArea.getText().equals("")) {
            showAlert("Błąd", "Pole tekstowe jest puste, nie można odkodować danych.");
            return;
        }
        try {
            String input = encodeTextArea.getText().trim();
            StringBuilder output = new StringBuilder();
            for (String block : input.split(" ")) { // mamy pomocnicze spacje dodane przy szyfrowaniu, dlatego tak szukamy bloków
                byte[] encrypted = Base64.getDecoder().decode(block); // wracamy do postaci binarnej (z postaci czytelnej dla nas)
                byte[] decrypted = desx.encryptMessage(encrypted, true); // deszyfrujemy dany blok
                output.append(new String(decrypted, StandardCharsets.UTF_8));
            }
            String base64Result = output.toString().replaceAll("\u0000+$", ""); // usuwamy wszystkie puste znaki, jeśli takowe wystąpiły
            decryptedBytes = Base64.getDecoder().decode(base64Result);
            String text = new String(decryptedBytes, StandardCharsets.UTF_8);
            decodeTextArea.setText(text);
            showAlert("Udało się", "Odkodowano dane.");
        } catch (Exception e) {
            showAlert("Błąd odszyfrowania", "Otrzymano niepoprawny ciąg Base64: " + e.getMessage());
        }
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