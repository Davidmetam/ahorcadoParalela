package org.example.cliente;

import javax.swing.*;
import java.awt.*;

public class ClienteAhorcado {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Ahorcado Cliente");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(500, 600);
            ventana.setResizable(false);
            ventana.setLayout(new BorderLayout());

            // Paneles
            PanelAhorcado panelDibujo = new PanelAhorcado();
            JLabel palabraLabel = new JLabel("Palabra: ");
            JLabel estadoLabel = new JLabel("Conectando...");
            TecladoPanel teclado = new TecladoPanel();

            // Layout
            JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
            panelSuperior.add(palabraLabel);
            panelSuperior.add(estadoLabel);

            ventana.add(panelSuperior, BorderLayout.NORTH);
            ventana.add(panelDibujo, BorderLayout.CENTER);
            ventana.add(teclado, BorderLayout.SOUTH);

            ventana.setVisible(true);

            // Iniciar controlador
            String ipServidor = JOptionPane.showInputDialog("IP del servidor:");
            if (ipServidor != null && !ipServidor.isEmpty()) {
                new Thread(() -> {
                    new ControladorCliente(ipServidor, panelDibujo, palabraLabel, estadoLabel, teclado);
                }).start();
            }
        });
    }
}
