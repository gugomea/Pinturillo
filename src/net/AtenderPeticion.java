package net;

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
        while(true){
            try {
                Object o = ois.readObject();
                if (o instanceof String){
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

    public void eliminarUsuario(){
        usuarios.remove(actual);
    }

    public void enviarMensaje(String mensaje) throws IOException, ClassNotFoundException {
        if(mensaje.equals(Cronometro.palabra)){
            mensaje = "El usuario " + actual.nombre + " ha acertado la palabra";
            actual.actualizarPalabra();
        }else mensaje = actual.nombre + ": " + mensaje;
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
