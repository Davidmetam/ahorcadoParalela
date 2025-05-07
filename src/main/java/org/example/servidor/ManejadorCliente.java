import java.io.*;
import java.net.Socket;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private JuegoAhorcado juego;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
        this.juego = new JuegoAhorcado();
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Enviar palabra inicial (enmascarada)
            out.println(juego.getEstadoPalabra());
            out.println(juego.getErroresRestantes());

            String entrada;
            while ((entrada = in.readLine()) != null) {
                char letra = entrada.toUpperCase().charAt(0);
                boolean acierto = juego.intentar(letra);

                out.println(acierto ? "CORRECTO" : "INCORRECTO");
                out.println(juego.getEstadoPalabra());
                out.println(juego.getErroresRestantes());

                if (juego.estaGanado()) {
                    out.println("GANASTE");
                    break;
                }

                if (juego.estaPerdido()) {
                    out.println("PERDISTE");
                    out.println("LA PALABRA ERA: " + juego.getPalabra());
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        }
    }
}
