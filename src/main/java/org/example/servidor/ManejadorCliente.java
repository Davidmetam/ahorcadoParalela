package org.example.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private JuegoAhorcado juego;
    private List<PrintWriter> clientes;
    private PrintWriter out;
    private int idCliente;

    public ManejadorCliente(Socket socket, JuegoAhorcado juego, List<PrintWriter> clientes, int idCliente) {
        this.socket = socket;
        this.juego = juego;
        this.clientes = clientes;
        this.idCliente = idCliente;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("ID:" + idCliente); // ✅ Esto debe ser LO PRIMERO
            clientes.add(out);              // ✅ Luego se añade el cliente

            // Luego envías el estado inicial del juego
            out.println(juego.getEstadoPalabra());
            out.println(juego.getErroresRestantes());

            String entrada;
            while ((entrada = in.readLine()) != null) {
                if (!ServidorAhorcado.esSuTurno(out)) {
                    out.println("NO_ES_TU_TURNO");
                    continue;
                }

                char letra = entrada.toUpperCase().charAt(0);
                synchronized (juego) {
                    boolean acierto = juego.intentar(letra);
                    synchronized (clientes) {
                        for (PrintWriter cliente : clientes) {
                            cliente.println(acierto ? "CORRECTO" : "INCORRECTO");
                            cliente.println(juego.getEstadoPalabra());
                            cliente.println(juego.getErroresRestantes());
                        }
                    }
                    if (juego.estaGanado()) {
                        synchronized (clientes) {
                            for (PrintWriter cliente : clientes) {
                                cliente.println("GANASTE");
                            }
                        }
                        break;
                    }
                    synchronized (clientes) {
                        if (juego.estaPerdido()) {
                            for (PrintWriter cliente : clientes) {
                                cliente.println("PERDISTE");
                                cliente.println("LA PALABRA ERA: " + juego.getPalabra());
                            }
                            break;
                        }
                    }

                    ServidorAhorcado.avanzarTurno();
                }
            }

        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        }
    }

}
