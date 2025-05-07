package org.example.cliente;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ControladorCliente {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ControladorCliente(String ip, PanelAhorcado panel, JLabel palabraLabel, JLabel estadoLabel, TecladoPanel teclado) {
        try {
            socket = new Socket(ip, 1234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Obtener estado inicial
            palabraLabel.setText("Palabra: " + in.readLine());
            int errores = Integer.parseInt(in.readLine());
            panel.setErrores(5 - errores);

            // Teclado
            teclado.setListener(e -> {
                JButton btn = (JButton) e.getSource();
                char letra = btn.getText().charAt(0);
                out.println(letra);
                teclado.desactivarLetra(letra);

                try {
                    String resultado = in.readLine();
                    String nuevaPalabra = in.readLine();
                    int restantes = Integer.parseInt(in.readLine());

                    palabraLabel.setText("Palabra: " + nuevaPalabra);
                    panel.setErrores(5 - restantes);

                    if ("GANASTE".equalsIgnoreCase(resultado)) {
                        estadoLabel.setText("¡Ganaste!");
                        desactivarTeclado(teclado);
                    } else if ("PERDISTE".equalsIgnoreCase(resultado)) {
                        estadoLabel.setText("¡Perdiste! " + in.readLine());
                        desactivarTeclado(teclado);
                    } else {
                        estadoLabel.setText(resultado.equals("CORRECTO") ? "Letra correcta" : "Letra incorrecta");
                    }
                } catch (IOException ex) {
                    estadoLabel.setText("Error de comunicación");
                }
            });

        } catch (IOException e) {
            palabraLabel.setText("No se pudo conectar");
        }
    }

    private void desactivarTeclado(TecladoPanel teclado) {
        teclado.setListener(e -> {}); // Elimina acciones
    }
}
