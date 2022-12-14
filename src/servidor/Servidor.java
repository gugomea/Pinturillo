package servidor;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        try(ServerSocket server = new ServerSocket(55_555)){
            LinkedList<Usuario> usuarios = new LinkedList<>();
            while(true){
                try{
                    Socket conexion = server.accept();
                    AtenderPeticion peticion = new AtenderPeticion(conexion, usuarios);
                    pool.execute(peticion);
                }catch (IOException | ClassNotFoundException | InterruptedException e){
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            pool.shutdown();
        }
    }
}
