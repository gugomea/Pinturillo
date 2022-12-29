package net;

import java.util.LinkedList;
import java.util.TimerTask;

public class Cronometro extends TimerTask {

    private LinkedList<Usuario> usuarios;
    int actual = 0;
    public Cronometro(LinkedList<Usuario> usrs){
        this.usuarios = usrs;
    }
    @Override
    public void run() {
        usuarios.get(actual).noEsAnfitrion();
        actual = (actual + 1) % usuarios.size();
        usuarios.get(actual).esAnfitrion();
    }
}
