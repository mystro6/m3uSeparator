import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FileChooser fileChooser = new FileChooser();
        M3USeparator m3USeparator = new M3USeparator(fileChooser.getSelectedFile());
    }
}
