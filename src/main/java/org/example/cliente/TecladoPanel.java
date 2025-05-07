import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TecladoPanel extends JPanel {
    private JButton[] botones = new JButton[26];

    public TecladoPanel() {
        setLayout(new GridLayout(3, 9));
        for (char c = 'A'; c <= 'Z'; c++) {
            JButton btn = new JButton(String.valueOf(c));
            botones[c - 'A'] = btn;
            add(btn);
        }
    }

    public void setListener(ActionListener listener) {
        for (JButton b : botones) {
            b.addActionListener(listener);
        }
    }

    public void desactivarLetra(char letra) {
        letra = Character.toUpperCase(letra);
        botones[letra - 'A'].setEnabled(false);
    }
}
