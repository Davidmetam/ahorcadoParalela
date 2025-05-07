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

        // Base
        g.drawLine(50, 250, 200, 250); // base
        g.drawLine(125, 250, 125, 50); // palo vertical
        g.drawLine(125, 50, 200, 50);  // palo horizontal
        g.drawLine(200, 50, 200, 80);  // cuerda

        if (errores >= 1) g.drawOval(175, 80, 50, 50); // cabeza
        if (errores >= 2) g.drawLine(200, 130, 200, 180); // torso
        if (errores >= 3) g.drawLine(200, 140, 170, 160); // brazo izq
        if (errores >= 4) g.drawLine(200, 140, 230, 160); // brazo der
        if (errores >= 5) g.drawLine(200, 180, 170, 210); // pierna izq
        if (errores >= 6) g.drawLine(200, 180, 230, 210); // pierna der
    }
}
