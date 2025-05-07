package org.example.cliente;

import javax.swing.*;

import java.io.*;
import java.net.Socket;


public class ControladorCliente {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int miID;

    public ControladorCliente(String ip, PanelAhorcado panel, JLabel palabraLabel, JLabel estadoLabel, TecladoPanel teclado) {
        try {
            socket = new Socket(ip, 5000); // Primero conectamos
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Luego preparamos la lectura
            out = new PrintWriter(socket.getOutputStream(), true);

            String idLinea = in.readLine();
            System.out.println("Recibido del servidor: " + idLinea); // <-- Añade esto

            String[] partes = idLinea.split(":");
            if (partes.length == 2) {
                miID = Integer.parseInt(partes[1]);
            } else {
                throw new IOException("Formato de ID inválido: " + idLinea);
            }

            // Recibe estado inicial del juego
            palabraLabel.setText("Palabra: " + in.readLine());
            int errores = Integer.parseInt(in.readLine());
            panel.setErrores(5 - errores);

            teclado.setListener(e -> {
                JButton btn = (JButton) e.getSource();
                char letra = btn.getText().charAt(0);
                out.println(letra);
                teclado.desactivarLetra(letra);
            });

        } catch (IOException e) {
            palabraLabel.setText("No se pudo conectar: " + e.getMessage());
            return;
        }

        // Hilo para escuchar actualizaciones del servidor
        new Thread(() -> {
            try {
                String linea;
                while ((linea = in.readLine()) != null) {
                    if (linea.startsWith("TURNO:")) {
                        int turnoActual = Integer.parseInt(linea.split(":")[1]);
                        boolean esMiTurno = (turnoActual == miID);

                        SwingUtilities.invokeLater(() -> {
                            if (esMiTurno) {
                                estadoLabel.setText("¡Tu turno!");
                                teclado.activarTecladoDisponible();
                            } else {
                                estadoLabel.setText("Turno del jugador " + turnoActual);
                                desactivarTeclado(teclado);
                            }
                        });
                        continue;
                    }

                    switch (linea) {
                        case "CORRECTO":
                        case "INCORRECTO":
                            String nuevaPalabra = in.readLine();
                            int restantes = Integer.parseInt(in.readLine());

                            palabraLabel.setText("Palabra: " + nuevaPalabra);
                            panel.setErrores(5 - restantes);
                            estadoLabel.setText(linea.equals("CORRECTO") ? "Letra correcta" : "Letra incorrecta");
                            break;

                        case "GANASTE":
                            estadoLabel.setText("¡Ganaste!");
                            desactivarTeclado(teclado);
                            break;

                        case "PERDISTE":
                            String palabra = in.readLine();
                            estadoLabel.setText("¡Perdiste! " + palabra);
                            desactivarTeclado(teclado);
                            break;

                        case "NO_ES_TU_TURNO":
                            estadoLabel.setText("Espera tu turno...");
                            break;

                        default:
                            System.out.println("Mensaje desconocido: " + linea);
                            break;
                    }
                }
            } catch (IOException e) {
                estadoLabel.setText("Conexión perdida");
            }
        }).start();
    }

    private void desactivarTeclado(TecladoPanel teclado) {
        teclado.setListener(e -> {
        });
    }


}
