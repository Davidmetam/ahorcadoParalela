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

    public ManejadorCliente(Socket socket, JuegoAhorcado juego, List<PrintWriter> clientes) {
        this.socket = socket;
        this.juego = juego;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            clientes.add(out);

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

                    for (PrintWriter cliente : clientes) {
                        cliente.println(acierto ? "CORRECTO" : "INCORRECTO");
                        cliente.println(juego.getEstadoPalabra());
                        cliente.println(juego.getErroresRestantes());
                    }

                    if (juego.estaGanado()) {
                        for (PrintWriter cliente : clientes) {
                            cliente.println("GANASTE");
                        }
                        break;
                    }

                    if (juego.estaPerdido()) {
                        for (PrintWriter cliente : clientes) {
                            cliente.println("PERDISTE");
                            cliente.println("LA PALABRA ERA: " + juego.getPalabra());
                        }
                        break;
                    }

                    ServidorAhorcado.avanzarTurno();
                }
            }

        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        }
    }
}
