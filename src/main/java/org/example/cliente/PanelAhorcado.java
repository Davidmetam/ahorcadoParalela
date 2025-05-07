package org.example.cliente;

import javax.swing.*;
import java.awt.*;

public class PanelAhorcado extends JPanel {
    private int errores = 0;

    public void setErrores(int errores) {
        this.errores = errores;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawLine(50, 250, 200, 250);
        g.drawLine(125, 250, 125, 50);
        g.drawLine(125, 50, 200, 50);
        g.drawLine(200, 50, 200, 80);

        if (errores >= 1) g.drawOval(175, 80, 50, 50);
        if (errores >= 2) g.drawLine(200, 130, 200, 180);
        if (errores >= 3) g.drawLine(200, 140, 170, 160);
        if (errores >= 4) g.drawLine(200, 140, 230, 160);
        if (errores >= 5) g.drawLine(200, 180, 170, 210);
        if (errores >= 6) g.drawLine(200, 180, 230, 210);
    }
}
