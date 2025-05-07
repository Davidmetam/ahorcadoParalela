package org.example.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner;

public class ServidorAhorcado {
    public static final int PUERTO = 5000;
    public static final JuegoAhorcado juegoCompartido = new JuegoAhorcado();

    public static final Map<String, PrintWriter> jugadores = new ConcurrentHashMap<>();

    public static final AtomicBoolean juegoIniciado = new AtomicBoolean(false);
    public static final AtomicInteger turnoActual = new AtomicInteger(0);

    public static final List<String> ordenJugadores = Collections.synchronizedList(new ArrayList<>());

    public static final Set<Character> letrasUsadas = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Servidor del Ahorcado iniciado en el puerto " + PUERTO);
        System.out.println("Esperando jugadores...");
        System.out.println("Escribe 'start' para iniciar el juego cuando todos los jugadores estén conectados.");
        System.out.println("Escribe 'restart' para reiniciar el juego.");

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String comando = scanner.nextLine();
                if ("start".equalsIgnoreCase(comando) && !juegoIniciado.get()) {
                    iniciarJuego();
                } else if ("restart".equalsIgnoreCase(comando)) {
                    reiniciarJuego();
                }
            }
        }).start();

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado desde: " + cliente.getInetAddress());

                new Thread(new ManejadorCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void iniciarJuego() {
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores conectados. No se puede iniciar el juego.");
            return;
        }

        juegoIniciado.set(true);
        turnoActual.set(0);
        letrasUsadas.clear();

        System.out.println("¡Juego iniciado con " + jugadores.size() + " jugadores!");
        System.out.println("La palabra a adivinar es: " + juegoCompartido.getPalabra());

        broadcast("PALABRA:" + juegoCompartido.getEstadoPalabra());
        broadcast("ERRORES:" + juegoCompartido.getErroresRestantes());

        asignarSiguienteTurno();
    }

    public static void reiniciarJuego() {
        broadcast("JUEGO_REINICIADO");

        JuegoAhorcado nuevoJuego = new JuegoAhorcado();

        juegoCompartido.setPalabra(nuevoJuego.getPalabra());
        juegoCompartido.resetear();

        System.out.println("Juego reiniciado. Nueva palabra: " + juegoCompartido.getPalabra());

        letrasUsadas.clear();

        juegoIniciado.set(true);
        turnoActual.set(0);

        broadcastEstadoJuego();

        asignarSiguienteTurno();
    }

    public static void broadcastEstadoJuego() {
        broadcast("PALABRA:" + juegoCompartido.getEstadoPalabra());
        broadcast("ERRORES:" + juegoCompartido.getErroresRestantes());
    }

    public static void asignarSiguienteTurno() {
        if (ordenJugadores.isEmpty()) return;

        int indice = turnoActual.getAndIncrement() % ordenJugadores.size();
        String jugadorTurno = ordenJugadores.get(indice);

        System.out.println("Turno asignado a: " + jugadorTurno);

        for (Map.Entry<String, PrintWriter> entry : jugadores.entrySet()) {
            String nombre = entry.getKey();
            PrintWriter out = entry.getValue();

            if (nombre.equals(jugadorTurno)) {
                out.println("TU_TURNO");
            } else {
                out.println("TURNO_DE:" + jugadorTurno);
            }
        }
    }

    public static void broadcast(String mensaje) {
        System.out.println("Enviando a todos los clientes: " + mensaje);
        for (PrintWriter out : jugadores.values()) {
            out.println(mensaje);
        }
    }
}