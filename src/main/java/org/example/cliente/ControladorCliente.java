package org.example.cliente;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ControladorCliente {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nombreUsuario;
    private boolean miTurno = false;

    public ControladorCliente(String ip, String nombre, PanelAhorcado panel, JLabel palabraLabel,
                              JLabel estadoLabel, JLabel turnoLabel, TecladoPanel teclado) {
        try {
            this.nombreUsuario = nombre;
            socket = new Socket(ip, 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Enviar nombre al servidor
            out.println(nombreUsuario);
            estadoLabel.setText("Conectado como: " + nombreUsuario);

            // Inicialmente desactivar el teclado hasta recibir turno
            desactivarTeclado(teclado);

            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                try {
                    while (true) {
                        String mensaje = in.readLine();
                        if (mensaje == null) break;

                        SwingUtilities.invokeLater(() -> {
                            try {
                                procesarMensaje(mensaje, panel, palabraLabel, estadoLabel, turnoLabel, teclado);
                            } catch (Exception e) {
                                estadoLabel.setText("Error procesando mensaje: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() ->
                            estadoLabel.setText("Conexión perdida con el servidor"));
                }
            }).start();

            // Configurar acción de teclado
            teclado.setListener(e -> {
                if (miTurno) {
                    JButton btn = (JButton) e.getSource();
                    char letra = btn.getText().charAt(0);
                    out.println(letra);
                    // La desactivación de letras ahora viene del servidor para todos los clientes
                    miTurno = false;
                    turnoLabel.setText("Esperando tu turno...");
                }
            });

        } catch (IOException e) {
            palabraLabel.setText("No se pudo conectar al servidor");
            estadoLabel.setText("Error: " + e.getMessage());
        }
    }

    private void procesarMensaje(String mensaje, PanelAhorcado panel, JLabel palabraLabel,
                                 JLabel estadoLabel, JLabel turnoLabel, TecladoPanel teclado) {
        if (mensaje == null) return; // Protección contra NPE

        switch (mensaje) {
            case "ESPERAR_INICIO":
                estadoLabel.setText("Esperando que el servidor inicie el juego...");
                break;

            case "TU_TURNO":
                miTurno = true;
                turnoLabel.setText("¡ES TU TURNO!");
                estadoLabel.setText("Selecciona una letra");
                break;

            case "GANASTE":
                turnoLabel.setText("¡El juego ha terminado!");
                estadoLabel.setText("¡Han ganado! La palabra ha sido adivinada");
                desactivarTeclado(teclado);
                break;

            case "PERDISTE":
                turnoLabel.setText("¡El juego ha terminado!");
                estadoLabel.setText("¡Han perdido! Se agotaron los intentos");
                desactivarTeclado(teclado);

                try {
                    String palabraCompleta = in.readLine();
                    if (palabraCompleta != null && palabraCompleta.startsWith("LA PALABRA ERA:")) {
                        palabraLabel.setText(palabraCompleta);
                    }
                } catch (IOException e) {
                    estadoLabel.setText("Error recibiendo palabra completa");
                }
                break;

            default:
                if (mensaje.startsWith("TURNO_DE:")) {
                    String jugador = mensaje.substring(9);
                    turnoLabel.setText("Turno de: " + jugador);
                    estadoLabel.setText("Esperando que " + jugador + " seleccione una letra");
                    desactivarTeclado(teclado);
                    miTurno = false;
                } else if (mensaje.startsWith("PALABRA:")) {
                    String palabra = mensaje.substring(8);
                    palabraLabel.setText("Palabra: " + palabra);
                } else if (mensaje.startsWith("ERRORES:")) {
                    try {
                        int restantes = Integer.parseInt(mensaje.substring(8));
                        panel.setErrores(5 - restantes);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear errores: " + mensaje);
                        // Si hay error, asumimos que se perdió
                        panel.setErrores(5);
                    }
                } else if (mensaje.startsWith("ESTADO:")) {
                    String estado = mensaje.substring(7);
                    // Solo actualizamos el estado, no cambiamos el mensaje de turno
                } else if (mensaje.startsWith("LETRA_USADA:")) {
                    char letra = mensaje.charAt(11);
                    teclado.desactivarLetra(letra);
                }
                break;
        }
    }

    private void desactivarTeclado(TecladoPanel teclado) {
        miTurno = false;
    }
}