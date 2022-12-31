package net;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(55_555)){
            ExecutorService pool = Executors.newCachedThreadPool();
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
        }
    }
}
