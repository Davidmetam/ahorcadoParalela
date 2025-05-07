package org.example.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class TecladoPanel extends JPanel {
    private JButton[] botones = new JButton[26];
    private static final Color BUTTON_COLOR = new Color(65, 105, 225); // Royal blue
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 20);
    
    public TecladoPanel() {
        setLayout(new GridLayout(3, 9, 8, 8));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 240, 240));
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String letter = String.valueOf(c);
            JButton btn = createStyledButton(letter);
            botones[c - 'A'] = btn;
            add(btn);
        }
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180));
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(this.getText(), g2).getBounds();
                
                int textX = (getWidth() - textRect.width) / 2;
                int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();
                
                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
            
            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 15, 15).contains(x, y);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set consistent size for all buttons
        button.setPreferredSize(new Dimension(50, 40));
        
        return button;
    }

    public void setListener(ActionListener listener) {
        for (JButton b : botones) {
            b.addActionListener(listener);
        }
    }

    public void desactivarLetra(char letra) {
        letra = Character.toUpperCase(letra);
        JButton button = botones[letra - 'A'];
        button.setEnabled(false);
        button.setForeground(new Color(80, 80, 80));
        button.repaint();
    }
}
