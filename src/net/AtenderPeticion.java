package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class AtenderPeticion implements Runnable{

    private Socket conexion;

    private LinkedList<Usuario> usuarios;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    public AtenderPeticion(Socket conexion, LinkedList<Usuario> usuarios) throws IOException {
        this.conexion = conexion;
        oos = new ObjectOutputStream(conexion.getOutputStream());
        ois = new ObjectInputStream(conexion.getInputStream());
        String id = conexion.getInetAddress().toString() + conexion.getPort();
        this.usuarios = usuarios;
        this.usuarios.add(new Usuario(id,oos, ois));
    }

    @Override
    public void run() {
        String mensaje;
        while(true){
            try{
                mensaje = (String)ois.readObject();
                switch (mensaje){
                    case "Mensaje" -> enviarMensaje();
                    case "Puntos" -> enviarPintado();
                }
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
                System.out.println("Adios!");
                return;
            }
        }
    }

    public void enviarMensaje() throws IOException, ClassNotFoundException {
        String mensaje = (String)ois.readObject();
        for (Usuario usr: usuarios){// mejor sin hilos, mandar un mensaje es muy rapido
//            Thread th = new Thread(new Runnable() {
//                public void run() {
                    usr.enviar(mensaje);
//                }
//            });
//            th.start();
        }
    }
    public void enviarPintado() throws IOException, ClassNotFoundException {
        LinkedList<Object[]> puntos = (LinkedList<Object[]>) ois.readObject();
        for(Usuario usr: usuarios){
            Thread th = new Thread(new Runnable() {
                public void run() {
                    usr.pintar(puntos);
                }
            });
            th.start();
        }
    }
}
