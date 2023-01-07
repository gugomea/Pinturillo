package servidor;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Usuario implements Closeable {
    // los metodos son synchronized porque como el envio de informacion es paralelo, si un usuario empieza a enviar
    // un objeto y sin haber acabado otro hilo empieza a enviarlo, las cabeceras se mezclan y la apicación se para.
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    public String id;

    public String nombre;

    public boolean fuera = false;
    public boolean acertado = false;
    public Usuario(String id, ObjectOutputStream os, ObjectInputStream is, String nombre){
        this.id = id;
        this.oos = os;
        this.ois = is;
        this.nombre = nombre;
    }
    public synchronized void borrar(){
        try {
            oos.writeObject("Borrar");
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void enviar(String mensaje){
        try {
            oos.writeObject(mensaje);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void pintar(Object[] puntos){
        try {
            oos.writeObject(puntos);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void esAnfitrion(){
        try {
            oos.writeObject(true);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void noEsAnfitrion(){
        try {
            oos.writeObject(false);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void enviarPalabra(String palabra){
        try{
            oos.writeObject("Palabra");
            oos.writeObject(palabra);
            oos.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public synchronized void actualizarPalabra(){
        try{
            oos.writeObject("Actualizar");
            oos.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        Usuario usr = obj instanceof Usuario ? (Usuario) obj : null;
        return usr != null && this.id.equals(usr.id);
    }

    @Override
    public void close() throws IOException {
        try{
            oos.close();
        }finally {
            ois.close();
        }
    }
}
