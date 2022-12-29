package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class Usuario {
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    public String id;

    public Usuario(String id, ObjectOutputStream os, ObjectInputStream is){
        this.id = id;
        this.oos = os;
        this.ois = is;
    }

    public void enviar(String mensaje){
        try {
            oos.writeObject("Mensaje");
            oos.writeObject(mensaje);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void pintar(LinkedList<Object[]> puntos){
        try {
            oos.writeObject("Puntos");
            oos.writeObject(puntos);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
