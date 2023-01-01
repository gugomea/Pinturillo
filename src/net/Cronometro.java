package net;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

public class Cronometro extends TimerTask {

    private LinkedList<Usuario> usuarios;
    int actual = 0;

    private String[] palabras;
    public static String palabra = "Ejemplo(No mostrar)";
    public Cronometro(LinkedList<Usuario> usrs){
        try{
            URL url = new URL("https://www.ejemplos.co/sustantivos-concretos/");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String l, doc = "";
            while((l = br.readLine()) != null)
                doc += l;

            int i = doc.indexOf("<tr>");
            int j = doc.lastIndexOf("</tr>") + "</tr>".length();
            this.palabras = doc.substring(i, j).replaceAll("</?tr>", "").replaceAll("</?td>", " ").trim().split("  ");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                usur.borrar();
                usur.enviar("Es el Turno de " + usuarios.get(actual).nombre);
                palabra = palabras[i];
                usur.enviar("La palabra es " + palabra);
                usur.enviarPalabra(palabra);
            }
        }catch (IndexOutOfBoundsException | ConcurrentModificationException e){
            e.printStackTrace();
            actual = actual % usuarios.size();
            run();
        }
    }
}
