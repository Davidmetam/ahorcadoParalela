package org.example.servidor;

import java.io.*;
import java.net.Socket;

public class ManejadorCliente implements Runnable {
    private Socket socket;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Añadir cliente a la lista compartida
            ServidorAhorcado.clientesConectados.add(out);

            // Enviar estado inicial
            out.println(ServidorAhorcado.juegoCompartido.getEstadoPalabra());
            out.println(ServidorAhorcado.juegoCompartido.getErroresRestantes());

            String entrada;
            while ((entrada = in.readLine()) != null) {
                char letra = entrada.toUpperCase().charAt(0);
                boolean acierto = ServidorAhorcado.juegoCompartido.intentar(letra);

                // Enviar actualización a todos
                ServidorAhorcado.broadcast(acierto ? "CORRECTO" : "INCORRECTO");
                ServidorAhorcado.broadcast(ServidorAhorcado.juegoCompartido.getEstadoPalabra());
                ServidorAhorcado.broadcast(ServidorAhorcado.juegoCompartido.getErroresRestantes() + "");

                if (ServidorAhorcado.juegoCompartido.estaGanado()) {
                    ServidorAhorcado.broadcast("GANASTE");
                    break;
                }

                if (ServidorAhorcado.juegoCompartido.estaPerdido()) {
                    ServidorAhorcado.broadcast("PERDISTE");
                    ServidorAhorcado.broadcast("LA PALABRA ERA: " + ServidorAhorcado.juegoCompartido.getPalabra());
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        }
    }
}
