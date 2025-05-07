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

    // Mapa para almacenar la conexión y el nombre de cada jugador
    public static final Map<String, PrintWriter> jugadores = new ConcurrentHashMap<>();

    // Control de estado del juego
    public static final AtomicBoolean juegoIniciado = new AtomicBoolean(false);
    public static final AtomicInteger turnoActual = new AtomicInteger(0);

    // Lista para mantener el orden de los jugadores
    public static final List<String> ordenJugadores = Collections.synchronizedList(new ArrayList<>());

    // Conjunto para almacenar letras ya utilizadas
    public static final Set<Character> letrasUsadas = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Servidor del Ahorcado iniciado en el puerto " + PUERTO);
        System.out.println("Esperando jugadores...");
        System.out.println("Escribe 'start' para iniciar el juego cuando todos los jugadores estén conectados.");

        // Hilo para escuchar comandos de consola del servidor
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

        // Reiniciar estado del juego
        juegoIniciado.set(true);
        turnoActual.set(0);
        letrasUsadas.clear();

        System.out.println("¡Juego iniciado con " + jugadores.size() + " jugadores!");
        System.out.println("La palabra a adivinar es: " + juegoCompartido.getPalabra());

        // Notificar a todos que el juego ha iniciado
        broadcastEstadoJuego();

        // Dar el primer turno
        asignarSiguienteTurno();
    }

    public static void reiniciarJuego() {
        if (juegoIniciado.get()) {
            broadcast("JUEGO_REINICIADO");
        }

        // Crear un nuevo juego
        JuegoAhorcado nuevoJuego = new JuegoAhorcado();
        // Reemplazar el juego compartido con uno nuevo
        juegoCompartido.setPalabra(nuevoJuego.getPalabra());
        juegoCompartido.resetear();

        System.out.println("Juego reiniciado. Nueva palabra: " + juegoCompartido.getPalabra());

        // Reiniciar letras usadas
        letrasUsadas.clear();

        // Iniciar el juego con los jugadores conectados
        iniciarJuego();
    }

    public static void broadcastEstadoJuego() {
        for (PrintWriter out : jugadores.values()) {
            out.println("PALABRA:" + juegoCompartido.getEstadoPalabra());
            out.println("ERRORES:" + juegoCompartido.getErroresRestantes());
        }
    }

    public static void asignarSiguienteTurno() {
        if (ordenJugadores.isEmpty()) return;

        int indice = turnoActual.getAndIncrement() % ordenJugadores.size();
        String jugadorTurno = ordenJugadores.get(indice);

        System.out.println("Turno asignado a: " + jugadorTurno);

        // Notificar a todos de quién es el turno
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
        for (PrintWriter out : jugadores.values()) {
            out.println(mensaje);
        }
    }
}