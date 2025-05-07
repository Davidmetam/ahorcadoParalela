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

            nombreJugador = in.readLine();

            if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
                nombreJugador = "Jugador" + socket.getInetAddress().getHostAddress();
            }

            System.out.println("Jugador conectado: " + nombreJugador);

            ServidorAhorcado.jugadores.put(nombreJugador, out);
            ServidorAhorcado.ordenJugadores.add(nombreJugador);

            if (ServidorAhorcado.juegoIniciado.get()) {
                out.println("PALABRA:" + ServidorAhorcado.juegoCompartido.getEstadoPalabra());
                out.println("ERRORES:" + ServidorAhorcado.juegoCompartido.getErroresRestantes());

                for (char letra : ServidorAhorcado.letrasUsadas) {
                    out.println("LETRA_USADA:" + letra);
                }

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

            String entrada;
            while ((entrada = in.readLine()) != null) {
                if (ServidorAhorcado.juegoIniciado.get()) {
                    int currentIndex = (ServidorAhorcado.turnoActual.get() - 1) % ServidorAhorcado.ordenJugadores.size();
                    String jugadorTurno = ServidorAhorcado.ordenJugadores.get(currentIndex);

                    if (nombreJugador.equals(jugadorTurno)) {
                        char letra = entrada.toUpperCase().charAt(0);

                        ServidorAhorcado.letrasUsadas.add(letra);
                        ServidorAhorcado.broadcast("LETRA_USADA:" + letra);

                        boolean acierto = ServidorAhorcado.juegoCompartido.intentar(letra);
                        int erroresRestantes = ServidorAhorcado.juegoCompartido.getErroresRestantes();

                        ServidorAhorcado.broadcast("ESTADO:" + (acierto ? "CORRECTO" : "INCORRECTO"));
                        ServidorAhorcado.broadcast("PALABRA:" + ServidorAhorcado.juegoCompartido.getEstadoPalabra());
                        ServidorAhorcado.broadcast("ERRORES:" + erroresRestantes);

                        System.out.println("Errores restantes: " + erroresRestantes +
                                ", Ganado: " + ServidorAhorcado.juegoCompartido.estaGanado() +
                                ", Perdido: " + ServidorAhorcado.juegoCompartido.estaPerdido());

                        if (ServidorAhorcado.juegoCompartido.estaGanado()) {
                            ServidorAhorcado.broadcast("GANASTE");
                            ServidorAhorcado.broadcast("LA PALABRA ERA: " + ServidorAhorcado.juegoCompartido.getPalabra());
                            ServidorAhorcado.juegoIniciado.set(false);
                            System.out.println("Juego terminado: ¡Los jugadores han ganado!");
                        } else if (ServidorAhorcado.juegoCompartido.estaPerdido()) {
                            // Asegurarse de que todos los clientes sepan que se han agotado los errores
                            ServidorAhorcado.broadcast("ERRORES:0"); // 0 restantes = 5 errores
                            ServidorAhorcado.broadcast("PERDISTE");
                            ServidorAhorcado.broadcast("LA PALABRA ERA: " + ServidorAhorcado.juegoCompartido.getPalabra());
                            ServidorAhorcado.juegoIniciado.set(false);
                            System.out.println("Juego terminado: ¡Los jugadores han perdido!");
                        } else {
                            ServidorAhorcado.asignarSiguienteTurno();
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error con cliente " + nombreJugador + ": " + e.getMessage());
        } finally {
            try {
                if (nombreJugador != null) {
                    ServidorAhorcado.jugadores.remove(nombreJugador);
                    ServidorAhorcado.ordenJugadores.remove(nombreJugador);
                    System.out.println("Jugador desconectado: " + nombreJugador);

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