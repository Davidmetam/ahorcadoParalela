package org.example.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServidorAhorcado {
    public static final int PUERTO = 5000;
    private static JuegoAhorcado juegoCompartido = new JuegoAhorcado();
    private static final List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());
    private static int turnoActual = 0;

    public static void main(String[] args) {
        System.out.println("Servidor del Ahorcado iniciado en el puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                new Thread(new ManejadorCliente(cliente, juegoCompartido, clientes)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método sincronizado para controlar el turno
    public static synchronized boolean esSuTurno(PrintWriter out) {
        return clientes.indexOf(out) == turnoActual;
    }

    public static synchronized void avanzarTurno() {
        if (!clientes.isEmpty()) {
            turnoActual = (turnoActual + 1) % clientes.size();
        }
    }
}
