package org.example.cliente;

import javax.swing.*;
import java.awt.*;

public class ClienteAhorcado {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            String nombreUsuario = JOptionPane.showInputDialog(null,
                    "Introduce tu nombre de jugador:",
                    "Juego del Ahorcado",
                    JOptionPane.QUESTION_MESSAGE);

            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                nombreUsuario = "Jugador" + (int)(Math.random() * 1000);
            }

            final String nombreFinal = nombreUsuario;

            JFrame ventana = new JFrame("Ahorcado Cliente - " + nombreFinal);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(500, 600);
            ventana.setResizable(false);
            ventana.setLayout(new BorderLayout());

            PanelAhorcado panelDibujo = new PanelAhorcado();
            JLabel palabraLabel = new JLabel("Palabra: ");
            JLabel estadoLabel = new JLabel("Conectando...");
            JLabel turnoLabel = new JLabel("Esperando turno...");
            TecladoPanel teclado = new TecladoPanel();

            JPanel panelSuperior = new JPanel(new GridLayout(3, 1));
            panelSuperior.add(palabraLabel);
            panelSuperior.add(turnoLabel);
            panelSuperior.add(estadoLabel);

            ventana.add(panelSuperior, BorderLayout.NORTH);
            ventana.add(panelDibujo, BorderLayout.CENTER);
            ventana.add(teclado, BorderLayout.SOUTH);

            ventana.setVisible(true);

            String ipServidor = "192.168.137.177";
            new Thread(() -> {
                new ControladorCliente(ipServidor, nombreFinal, panelDibujo, palabraLabel, estadoLabel, turnoLabel, teclado);
            }).start();
        });
    }
}