package org.example.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServidorAhorcado {
    public static final int PUERTO = 1234;
    public static final JuegoAhorcado juegoCompartido = new JuegoAhorcado();
    public static final List<PrintWriter> clientesConectados = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Servidor del Ahorcado iniciado en el puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                new Thread(new ManejadorCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String mensaje) {
        synchronized (clientesConectados) {
            for (PrintWriter out : clientesConectados) {
                out.println(mensaje);
            }
        }
    }
}
