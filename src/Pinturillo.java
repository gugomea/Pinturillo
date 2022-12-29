import lib.Chat;
import lib.Paint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    private boolean[] esAnfitrion = new boolean[1];

    private Socket conexion;
    private void inicializar(){
        CountDownLatch count = new CountDownLatch(1);
        Thread escuchador = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    conexion = new Socket("localhost", 55_555);
                    oos = new ObjectOutputStream(conexion.getOutputStream());
                    ois = new ObjectInputStream(conexion.getInputStream());
                    count.countDown(); // para poder asegurarnos de que this.oos y this.ois se inicializan.
                    while(true){
                        String tipo = (String)ois.readObject();
                        switch (tipo) {
                            case "Mensaje" -> {
                                String mensaje = (String) ois.readObject();
                                chatterino.escribir(mensaje);
                            }
                            case "Puntos" -> {
                                LinkedList<Object[]> nuevoDibujo = (LinkedList<Object[]>) ois.readObject();
                                zonaDibujo.pintar(nuevoDibujo);
                            }
                            case "Anfitrion" -> esAnfitrion[0] = (boolean) ois.readObject();
                        }
                    }
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        });
        escuchador.start();
        try { count.await(); } catch (InterruptedException e) { throw new RuntimeException(e); }


        this.lblPalabra = new JLabel("Palabra: ");
        this.lblPalabra.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        this.txtPalabra = new JTextField("__ __ __ __ __ __ __");
        this.txtPalabra.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        this.zonaDibujo = new Paint(oos, ois, esAnfitrion);

        this.chatterino = new Chat(oos, zonaDibujo, esAnfitrion);
    }
    public Pinturillo(String titulo){
        super(titulo);
        inicializar();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel arriba = new JPanel();
        arriba.setLayout(new FlowLayout());
        arriba.add(lblPalabra);
        arriba.add(txtPalabra);
        this.add(arriba, BorderLayout.NORTH);

        // Dividimos la pantalla en el chat(y sus botones) a la izda y para pintar a la derecha.
        JSplitPane split = new JSplitPane();
        this.add(split, BorderLayout.CENTER);
        split.setLeftComponent(chatterino);
        split.setRightComponent(zonaDibujo);

        setSize(1000, 500);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    System.out.println("ME salgo de acá");
                    oos.writeObject("Salir");
                    oos.flush();
                    conexion.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        Pinturillo p = new Pinturillo("titulo");
    }
}
