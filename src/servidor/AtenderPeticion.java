package servidor;
import cliente.Pinturillo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;

public class AtenderPeticion implements Runnable{

    private Socket conexion;

    private LinkedList<Usuario> usuarios;

    // en realidad oos y ois lo podriamos quitar y usar actual.oos y actual.ois.
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Usuario actual;
    private static Timer timer;
    public AtenderPeticion(Socket conexion, LinkedList<Usuario> usuarios) throws IOException, ClassNotFoundException, InterruptedException {
        this.conexion = conexion;
        oos = new ObjectOutputStream(conexion.getOutputStream());
        ois = new ObjectInputStream(conexion.getInputStream());
        String id = conexion.getInetAddress().toString() + conexion.getPort();
        String nombre = (String) ois.readObject();
        this.usuarios = usuarios;
        Usuario usr = new Usuario(id, oos, ois, nombre);
        actual = usr;
        usuarios.add(usr);
        if(usuarios.size() == 1){
            Thread.sleep(3000);
            timer = new Timer();
            timer.schedule(new Cronometro(usuarios), Calendar.getInstance().getTime(), 10_000);
        }else{
            // si no es el primero en unirse, entonces se ha metido en una ronda que está en curso.
            // así que esta fuera de la ronda = true, y palabra acertada = true (true ya que si te unes en medio de la ronda
            // el resultado de este usuario no hay que tenerlo en cuenta en la ronda actual)
            actual.fuera = true;
            actual.acertado = true;
        }

    }

    @Override
    public void run() {
        while(true){
            try {
                Object o = ois.readObject();
                if (o instanceof String && !actual.fuera){
                    switch ((String)o){
                        case "Borrar" -> borrar();
                        case "Salir" -> eliminarUsuario();
                        default -> enviarMensaje((String)o);
                    }
                }else if(o instanceof Object[]){
                    enviarPintado((Object[]) o);
                }
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
                System.out.println("Adios!");
                return;
            }
        }
    }

    public void borrar(){
        for(Usuario us : usuarios){
            if(!us.equals(actual)){
                us.borrar();
            }
        }
    }

    private void resetRonda(){
        timer.cancel();timer.purge();
        timer = new Timer();
        timer.schedule(new Cronometro(), Calendar.getInstance().getTime(), 10_000);
    }

    public void eliminarUsuario(){
        usuarios.remove(actual);
        if(todosAcertados()) resetRonda();
        Pinturillo.Cerrar(actual);
        Pinturillo.Cerrar(conexion);
    }

    private boolean todosAcertados(){
        for(Usuario u: usuarios)
            if(!u.acertado) return false;

        return true;
    }

    public void enviarMensaje(String mensaje) throws IOException, ClassNotFoundException {
        if(mensaje.equals(Cronometro.palabra)){
            mensaje = "El usuario " + actual.nombre + " ha acertado la palabra";
            actual.actualizarPalabra();
            actual.acertado = true;
            if(todosAcertados())
                resetRonda();
        }else if(!mensaje.equals("Principio"))// si el mensaje es un comentario del usuario
            mensaje = actual.nombre + ": " + mensaje;
        for (Usuario usr: usuarios)// mejor sin hilos, mandar un mensaje es muy rapido
            usr.enviar(mensaje);
    }
    public void enviarPintado(Object[] puntos) throws IOException, ClassNotFoundException {
        for(Usuario usr: usuarios){
            if(!usr.equals(actual)){// no volvemos a reenviar al que lo pinta
                Thread th = new Thread(new Runnable() {
                    public void run() { usr.pintar(puntos); }
                });
                th.start();
            }
        }
    }
}
