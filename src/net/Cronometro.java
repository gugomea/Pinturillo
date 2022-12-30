package net;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

public class Cronometro extends TimerTask {

    private LinkedList<Usuario> usuarios;
    int actual = 0;

    private String[] palabras = new String[]{"MANZANA", "MESA", "OCÉANO", "SILLA", "ORDENADOR", "PERA", "LEÓN", "NIÑO", "CASA", "COCHE", "TECLADO", "LIBRO", "LÁMPARA"};
    public static String palabra = "Ejemplo(No mostrar)";
    public Cronometro(LinkedList<Usuario> usrs){
        this.usuarios = usrs;
    }
    private final Random r = new Random();
    @Override
    public void run() {
        try{
            System.out.println("HOLA DESDE CRONOMETRO");
            usuarios.get(actual).noEsAnfitrion();
            actual = (actual + 1) % usuarios.size();
            usuarios.get(actual).esAnfitrion();
            int i = r.nextInt(palabras.length);
            for(Usuario usur: usuarios){
                usur.enviar("Es el Turno de " + usuarios.get(actual).nombre);
                palabra = palabras[i];
                usur.enviar("La palabra es " + palabra);
                usur.enviarPalabra(palabra);
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            actual = actual % usuarios.size();
            run();
        }
    }
}
