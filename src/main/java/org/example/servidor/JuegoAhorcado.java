package org.example.servidor;

import java.util.*;

public class JuegoAhorcado {
    private static final String[] PALABRAS = {
            "MICROSOFT", "AVESTRUZ", "PARALELISMO", "MULTIHILO", "SERVIDORES", "XENOFOBIA", "PARALELOGRAMO", "COMUNICACIONES"
    };

    private String palabra;
    private Set<Character> letrasAdivinadas;
    private int errores;
    private static final int MAX_ERRORES = 5;

    public JuegoAhorcado() {
        this.palabra = PALABRAS[new Random().nextInt(PALABRAS.length)];
        this.letrasAdivinadas = new HashSet<>();
        this.errores = 0;
    }

    public synchronized boolean intentar(char letra) {
        letra = Character.toUpperCase(letra);

        if (letrasAdivinadas.contains(letra)) {
            return true; // ya fue adivinada
        }

        if (!palabra.contains(String.valueOf(letra))) {
            errores++;
            System.out.println("Letra '" + letra + "' incorrecta. Errores: " + errores);
            return false;
        }

        letrasAdivinadas.add(letra);
        System.out.println("Letra '" + letra + "' correcta. Letras adivinadas: " + letrasAdivinadas);
        return true;
    }

    public synchronized String getEstadoPalabra() {
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

    public synchronized int getErroresRestantes() {
        return MAX_ERRORES - errores;
    }

    public synchronized boolean estaGanado() {
        for (char c : palabra.toCharArray()) {
            if (!letrasAdivinadas.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean estaPerdido() {
        return errores >= MAX_ERRORES;
    }

    public synchronized String getPalabra() {
        return palabra;
    }

    public synchronized void setPalabra(String nuevaPalabra) {
        this.palabra = nuevaPalabra;
    }

    public synchronized void resetear() {
        this.letrasAdivinadas.clear();
        this.errores = 0;
    }

    // Método auxiliar para depuración
    public synchronized int getErrores() {
        return errores;
    }
}