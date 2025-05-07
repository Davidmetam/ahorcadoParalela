import java.util.*;

public class JuegoAhorcado {
    private static final String[] PALABRAS = {
            "JAVA", "SOCKET", "PARALELO", "MULTIHILO", "SERVIDOR", "CLIENTE", "RED", "COMUNICACION"
    };

    private final String palabra;
    private final Set<Character> letrasAdivinadas;
    private int errores;

    public JuegoAhorcado() {
        this.palabra = PALABRAS[new Random().nextInt(PALABRAS.length)];
        this.letrasAdivinadas = new HashSet<>();
        this.errores = 0;
    }

    public boolean intentar(char letra) {
        letra = Character.toUpperCase(letra);

        if (!palabra.contains(String.valueOf(letra))) {
            errores++;
            return false;
        }

        letrasAdivinadas.add(letra);
        return true;
    }

    public String getEstadoPalabra() {
        StringBuilder estado = new StringBuilder();
        for (char c : palabra.toCharArray()) {
            if (letrasAdivinadas.contains(c)) {
                estado.append(c).append(" ");
            } else {
                estado.append("_ ");
            }
        }
        return estado.toString().trim();
    }

    public int getErroresRestantes() {
        return 5 - errores;
    }

    public boolean estaGanado() {
        for (char c : palabra.toCharArray()) {
            if (!letrasAdivinadas.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean estaPerdido() {
        return errores >= 5;
    }

    public String getPalabra() {
        return palabra;
    }
}
