package org.example.cliente;

import javax.swing.*;
import java.awt.*;

public class PanelAhorcado extends JPanel {
    private int errores = 0;

    public void setErrores(int errores) {
        this.errores = errores;
        repaint();
    }

    public PanelAhorcado() {
        setPreferredSize(new Dimension(300, 400)); // tamaño fijo
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Fondo negro
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Luna
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillOval(220, 20, 60, 60);

        // Estrellas
        g2.setColor(Color.WHITE);
        for (int i = 0; i < 30; i++) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * 150);
            g2.fillOval(x, y, 3, 3);
        }

        // Montaña / suelo
        g2.setColor(new Color(30, 30, 30));
        g2.fillPolygon(new int[]{0, 100, 200}, new int[]{400, 300, 400}, 3);
        g2.fillPolygon(new int[]{200, 300, 400}, new int[]{400, 250, 400}, 3);

        // Posición base del dibujo
        int centerX = 150;
        int headY = 100;

        // Dibujar cuerda
        g2.setColor(new Color(102, 51, 0)); // marrón
        g2.fillRect(centerX - 5, 0, 10, headY + 10); // cuerda recta
        // nudos
        g2.fillOval(centerX - 12, headY + 5, 24, 10);
        g2.fillOval(centerX - 12, headY + 12, 24, 10);
        g2.fillOval(centerX - 12, headY + 19, 24, 10);

        if (errores >= 1) {
            // Cabeza
            g2.setColor(new Color(255, 220, 180)); // color piel
            g2.fillOval(centerX - 40, headY + 20, 80, 80);

            // Contorno cabeza
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(4));
            g2.drawOval(centerX - 40, headY + 20, 80, 80);

            // Ojos
            g2.setColor(Color.WHITE);
            g2.fillOval(centerX - 20, headY + 50, 15, 15);
            g2.fillOval(centerX + 5, headY + 50, 15, 15);
            g2.setColor(new Color(30, 30, 150)); // azul
            g2.fillOval(centerX - 16, headY + 54, 8, 8);
            g2.fillOval(centerX + 9, headY + 54, 8, 8);

            // Boca
            g2.setColor(Color.BLACK);
            g2.fillOval(centerX - 5, headY + 80, 10, 5);

        }
        if (errores >= 2) {
            // Cuerpo
            g2.setColor(new Color(220, 50, 50)); // rojo
            g2.fillRect(centerX - 25, headY + 100, 50, 40);
            g2.setColor(Color.BLACK);
            g2.drawRect(centerX - 25, headY + 100, 50, 40);
        }
        if (errores >= 3) {
            // Brazo izq
            g2.setColor(new Color(150, 50, 150)); // morado
            g2.fillOval(centerX - 40, headY + 100, 20, 20);
            g2.setColor(Color.BLACK);
            g2.drawOval(centerX - 40, headY + 100, 20, 20);

        }
        if (errores >= 4) {
            // Brazo der
            g2.setColor(new Color(150, 50, 150)); // morado
            g2.fillOval(centerX + 20, headY + 100, 20, 20);
            g2.setColor(Color.BLACK);
            g2.drawOval(centerX + 20, headY + 100, 20, 20);
        }
        if (errores >= 5) {
            // Piernas
            g2.setColor(new Color(50, 50, 150)); // azul
            g2.fillRect(centerX - 20, headY + 140, 15, 30);
            g2.setColor(Color.BLACK);
            g2.drawRect(centerX - 20, headY + 140, 15, 30);
        }
        if (errores >= 6) {
            g2.setColor(new Color(50, 50, 150)); // azul
            g2.fillRect(centerX + 5, headY + 140, 15, 30);
            g2.setColor(Color.BLACK);
            g2.drawRect(centerX + 5, headY + 140, 15, 30);
        }

    }
}
