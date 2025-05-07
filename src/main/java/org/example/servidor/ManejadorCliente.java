package org.example.servidor;

import java.io.*;
import java.net.Socket;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private String nombreJugador;
    private PrintWriter out;
    private BufferedReader in;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Recibir nombre del jugador
            nombreJugador = in.readLine();

            if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
                nombreJugador = "Jugador" + socket.getInetAddress().getHostAddress();
            }

            System.out.println("Jugador conectado: " + nombreJugador);

            // Añadir jugador al mapa de jugadores
            ServidorAhorcado.jugadores.put(nombreJugador, out);
            ServidorAhorcado.ordenJugadores.add(nombreJugador);

            // Si el juego ya está iniciado, enviar estado actual
            if (ServidorAhorcado.juegoIniciado.get()) {
                out.println("PALABRA:" + ServidorAhorcado.juegoCompartido.getEstadoPalabra());
                out.println("ERRORES:" + ServidorAhorcado.juegoCompartido.getErroresRestantes());

                // Sincronizar letras usadas
                for (char letra : ServidorAhorcado.letrasUsadas) {
                    out.println("LETRA_USADA:" + letra);
                }

                // Verificar si es su turno actual
                int currentIndex = ServidorAhorcado.turnoActual.get() % ServidorAhorcado.ordenJugadores.size();
                if (currentIndex > 0) {
                    String jugadorActual = ServidorAhorcado.ordenJugadores.get(currentIndex - 1);

                    if (nombreJugador.equals(jugadorActual)) {
                        out.println("TU_TURNO");
                    } else {
                        out.println("TURNO_DE:" + jugadorActual);
                    }
                }
            } else {
                out.println("ESPERAR_INICIO");
            }

            // Procesar entradas del jugador
            String entrada;
            while ((entrada = in.readLine()) != null) {
                // Solo procesar entradas si el juego está iniciado y es el turno del jugador
                if (ServidorAhorcado.juegoIniciado.get()) {
                    int currentIndex = (ServidorAhorcado.turnoActual.get() - 1) % ServidorAhorcado.ordenJugadores.size();
                    String jugadorTurno = ServidorAhorcado.ordenJugadores.get(currentIndex);

                    if (nombreJugador.equals(jugadorTurno)) {
                        char letra = entrada.toUpperCase().charAt(0);

                        // Añadir la letra al conjunto de letras usadas y notificar a todos
                        ServidorAhorcado.letrasUsadas.add(letra);
                        ServidorAhorcado.broadcast("LETRA_USADA:" + letra);

                        boolean acierto = ServidorAhorcado.juegoCompartido.intentar(letra);

                        // Notificar a todos el resultado de la jugada
                        ServidorAhorcado.broadcast("ESTADO:" + (acierto ? "CORRECTO" : "INCORRECTO"));
                        ServidorAhorcado.broadcast("PALABRA:" + ServidorAhorcado.juegoCompartido.getEstadoPalabra());
                        ServidorAhorcado.broadcast("ERRORES:" + ServidorAhorcado.juegoCompartido.getErroresRestantes());

                        // Verificar fin del juego
                        if (ServidorAhorcado.juegoCompartido.estaGanado()) {
                            ServidorAhorcado.broadcast("GANASTE");
                            ServidorAhorcado.broadcast("LA PALABRA ERA: " + ServidorAhorcado.juegoCompartido.getPalabra());
                            ServidorAhorcado.juegoIniciado.set(false);
                            System.out.println("Juego terminado: ¡Los jugadores han ganado!");
                        } else if (ServidorAhorcado.juegoCompartido.estaPerdido()) {
                            ServidorAhorcado.broadcast("PERDISTE");
                            ServidorAhorcado.broadcast("LA PALABRA ERA: " + ServidorAhorcado.juegoCompartido.getPalabra());
                            ServidorAhorcado.juegoIniciado.set(false);
                            System.out.println("Juego terminado: ¡Los jugadores han perdido!");
                        } else {
                            // Si el juego continúa, asignar el siguiente turno
                            ServidorAhorcado.asignarSiguienteTurno();
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error con cliente " + nombreJugador + ": " + e.getMessage());
        } finally {
            try {
                // Eliminar al jugador al desconectarse
                if (nombreJugador != null) {
                    ServidorAhorcado.jugadores.remove(nombreJugador);
                    ServidorAhorcado.ordenJugadores.remove(nombreJugador);
                    System.out.println("Jugador desconectado: " + nombreJugador);

                    // Si era su turno, pasar al siguiente
                    if (ServidorAhorcado.juegoIniciado.get() && !ServidorAhorcado.ordenJugadores.isEmpty()) {
                        ServidorAhorcado.asignarSiguienteTurno();
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}