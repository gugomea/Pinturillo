import lib.Chat;
import lib.Paint;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class Pinturillo extends JFrame {
    //Arriba
    private JLabel lblPalabra;
    private JTextField txtPalabra;

    //izquierda
    private Chat chatterino;

    //derecha
    private Paint zonaDibujo;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private void inicializar(){
        CountDownLatch count = new CountDownLatch(1);
        Thread escuchador = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket conexion = new Socket("localhost", 55_555);
                    oos = new ObjectOutputStream(conexion.getOutputStream());
                    ois = new ObjectInputStream(conexion.getInputStream());
                    count.countDown(); // para poder asegurarnos de que this.oos y this.ois se inicializan.
                    while(true){
                        String tipo = (String)ois.readObject();
                        if(tipo.equals("Mensaje")){
                            String mensaje = (String)ois.readObject();
                            chatterino.escribir(mensaje);
                        }else if (tipo.equals("Puntos")){
                            LinkedList<Object[]> nuevoDibujo = (LinkedList<Object[]>) ois.readObject();
                            zonaDibujo.pintar(nuevoDibujo);
                        }
                    }
                } catch (IOException | ClassNotFoundException e){
                    throw new RuntimeException(e);
                }
            }
        });
        escuchador.start();
        try { count.await(); } catch (InterruptedException e) { throw new RuntimeException(e); }
        this.lblPalabra = new JLabel("Palabra: ");

        this.txtPalabra = new JTextField();

        this.zonaDibujo = new Paint(oos, ois);

        this.chatterino = new Chat(oos, zonaDibujo);
    }
    public Pinturillo(String titulo){
        super(titulo);
        inicializar();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Dividimos la pantalla en el chat(y sus botones) a la izda y para pintar a la derecha.
        JSplitPane split = new JSplitPane();
        this.add(split, BorderLayout.CENTER);
        split.setLeftComponent(chatterino);
        split.setRightComponent(zonaDibujo);

        setSize(1000, 500);


        setVisible(true);
    }

    public static void main(String[] args) {
        Pinturillo p = new Pinturillo("titulo");
    }
}
