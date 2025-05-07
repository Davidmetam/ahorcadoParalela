import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorAhorcado {
    public static final int PUERTO = 1234;

    public static void main(String[] args) {
        System.out.println("Servidor del Ahorcado iniciado en el puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                // Se atiende al cliente en un hilo separado
                new Thread(new ManejadorCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
