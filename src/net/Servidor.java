package net;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    public static void main(String[] args) {
//        try(ServerSocket ss = new ServerSocket(55_555)){
//            while(true){
//                Socket conexion = ss.accept();
//                ObjectOutputStream oos = new ObjectOutputStream(conexion.getOutputStream());
//                ObjectInputStream ois = new ObjectInputStream(conexion.getInputStream());
//                while(true){
//                    String tipo = (String) ois.readObject();
//                    System.out.println(tipo);
//                    if(tipo.equals("Mensaje")){
//                        oos.writeObject("Mensaje");
//                        oos.writeObject("GUILLERMO: ECHO");
//                        System.out.println("El mensaje es :  " + (String)ois.readObject());
//                        oos.flush();
//                    }else{
//                        oos.writeObject("Puntos");
//                        LinkedList<Object[]> o = (LinkedList<Object[]>) ois.readObject();
//                        System.out.println(o);
//                        oos.writeObject(o);
//                        oos.flush();
//                    }
//                }
//            }
//        }catch (IOException | ClassNotFoundException e){
//            e.printStackTrace();
//        }
        try(ServerSocket server = new ServerSocket(55_555)){
            ExecutorService pool = Executors.newCachedThreadPool();
            LinkedList<Usuario> usuarios = new LinkedList<>();
            while(true){
                try{
                    Socket conexion = server.accept();
                    AtenderPeticion peticion = new AtenderPeticion(conexion, usuarios);
                    pool.execute(peticion);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
