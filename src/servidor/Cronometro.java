package servidor;

import cliente.Pinturillo;

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

    private static LinkedList<Usuario> usuarios;
    private static int actual = 0;

    private static String[] palabras;
    public static String palabra = "Ejemplo que no se debería ver nunca";
    private static final Random r = new Random();
    public Cronometro(LinkedList<Usuario> usrs){
        BufferedReader br = null;
        try{
            URL url = new URL("https://www.ejemplos.co/sustantivos-concretos/");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String l, doc = "";
            while((l = br.readLine()) != null)
                doc += l;

            int i = doc.indexOf("<tr>");
            int j = doc.lastIndexOf("</tr>") + "</tr>".length();
            palabras = doc.substring(i, j).replaceAll("</?tr>", "").replaceAll("</?td>", " ").trim().split("  ");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Pinturillo.Cerrar(br);
        }
        usuarios = usrs;
    }
    public Cronometro(){
        // constructor que utilizamos para resetear el timer, por eso los atributos son estáticos
        // lo hago así porque esta es la única forma que he encontrado para reiniciar un timer( hacer un cancel()
        // y volver a hacer un new())
    }
    @Override
    public void run() {
        try{
            System.out.println("HOLA DESDE CRONOMETRO");
            usuarios.get(actual).noEsAnfitrion();
            actual = (actual + 1) % usuarios.size();
            usuarios.get(actual).esAnfitrion();
            int i = r.nextInt(palabras.length);
            for(Usuario usur: usuarios){
                usur.fuera = false;
                usur.acertado = false;
                usur.borrar();
                usur.enviar("Es el Turno de " + usuarios.get(actual).nombre);
                palabra = palabras[i];
                usur.enviar("La palabra es " + palabra);
                usur.enviarPalabra(palabra);
            }
            usuarios.get(actual).acertado = true;
        }catch (IndexOutOfBoundsException | ConcurrentModificationException e){
            // en caso de que haya algún fallo(se elimine un usuario, lo cual hace que el tamaño sea menor, y después
            // vayamos a ese usuario, el usuario ya no será valido, o el índice puede ser incorrecto
            // así que "actualizamos" el índice y volvemos  a llamar a la función.

            // Tal y como pone en la teoría Vector es thread safe pero preferí usar List porque es mejor para lo que
            // quiero usar y el único problema (habrá más pero no los he visto) que puede surgir es este y tiene un
            // sencillo arreglo.
            e.printStackTrace();
            actual = actual % usuarios.size();
            run();
        }
    }
}
