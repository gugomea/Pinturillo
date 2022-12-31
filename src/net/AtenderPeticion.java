package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class AtenderPeticion implements Runnable{

    private Socket conexion;

    private LinkedList<Usuario> usuarios;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Usuario actual;
    public AtenderPeticion(Socket conexion, LinkedList<Usuario> usuarios) throws IOException, ClassNotFoundException, InterruptedException {
        this.conexion = conexion;
        oos = new ObjectOutputStream(conexion.getOutputStream());
        ois = new ObjectInputStream(conexion.getInputStream());
        String id = conexion.getInetAddress().toString() + conexion.getPort();
        String nombre = (String) ois.readObject();
        this.usuarios = usuarios;
        Usuario usr = new Usuario(id, oos, ois, nombre);
//        if(usuarios.size() == 0)
//            usr.esAnfitrion();
        actual = usr;
        usuarios.add(usr);
        if(usuarios.size() == 1){
            Thread.sleep(3000);
            Timer timer = new Timer();
            timer.schedule(new Cronometro(usuarios), Calendar.getInstance().getTime(), 10_000);
        }

    }

    @Override
    public void run() {
        String mensaje;
        while(true){
            try {
//                mensaje = (String)ois.readObject();
//                switch (mensaje){
//                    case "Mensaje" -> enviarMensaje();
//                    case "Puntos" -> enviarPintado();
//                    case "Salir" -> eliminarUsuario();
//                    case "Borrar" -> borrar();
//                 }
                Object o = ois.readObject();
                if (o instanceof String){
                    if(o.equals("Borrar")) borrar();
                    else if(o.equals("Salir")) eliminarUsuario();
                    else enviarMensaje((String)o);
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

    public void eliminarUsuario(){
        usuarios.remove(actual);
    }

    public void enviarMensaje(String mensaje) throws IOException, ClassNotFoundException {
        if(mensaje.equals(Cronometro.palabra)){
            mensaje = "El usuario " + actual.nombre + " ha acertado la palabra";
            actual.actualizarPalabra();
        }
        for (Usuario usr: usuarios){// mejor sin hilos, mandar un mensaje es muy rapido
//            Thread th = new Thread(new Runnable() {
//                public void run() {
                    usr.enviar(mensaje);
//                }
//            });
//            th.start();
        }
    }
    public void enviarPintado(Object[] puntos) throws IOException, ClassNotFoundException {
        for(Usuario usr: usuarios){
            if(!usr.equals(actual)){
                Thread th = new Thread(new Runnable() {
                    public void run() {
                    System.out.println("p");
                        usr.pintar(puntos);
                    System.out.println("fr");
                    }
                });
                th.start();
            }
        }
    }
}
