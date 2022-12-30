package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class Usuario {
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    public String id;

    public String nombre;
    public Usuario(String id, ObjectOutputStream os, ObjectInputStream is, String nombre){
        this.id = id;
        this.oos = os;
        this.ois = is;
        this.nombre = nombre;
    }

    public void borrar(){
        try {
            oos.writeObject("Borrar");
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enviar(String mensaje){
        try {
            oos.writeObject("Mensaje");
            oos.writeObject(mensaje);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pintar(LinkedList<Object[]> puntos){
        try {
            oos.writeObject("Puntos");
            oos.writeObject(puntos);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void esAnfitrion(){
        try {
            oos.writeObject("Anfitrion");
            oos.writeObject(true);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void noEsAnfitrion(){
        try {
            oos.writeObject("Anfitrion");
            oos.writeObject(false);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarPalabra(String palabra){
        try{
            oos.writeObject("Palabra");
            oos.writeObject(palabra);
            oos.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void actualizarPalabra(){
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
}
