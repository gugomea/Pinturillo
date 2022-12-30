import lib.Chat;
import lib.Paint;
import net.Cronometro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Pinturillo extends JFrame {
    private String nombre;
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

    private String palabra;
    private void inicializar(){
        try {
            conexion = new Socket("localhost", 55_555);
            oos = new ObjectOutputStream(conexion.getOutputStream());
            ois = new ObjectInputStream(conexion.getInputStream());
            oos.writeObject(nombre);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.zonaDibujo = new Paint(oos, ois, esAnfitrion);

        this.chatterino = new Chat(oos, zonaDibujo, esAnfitrion);
        this.lblPalabra = new JLabel("Palabra: ");
        this.lblPalabra.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        this.txtPalabra = new JTextField("__ __ __ __ __ __ __");
        this.txtPalabra.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        Thread escuchador = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                            case "Actualizar" ->
                                txtPalabra.setText(palabra);
                            case "Palabra" -> {
                                palabra = (String) ois.readObject();
                                if(esAnfitrion[0])
                                    txtPalabra.setText(palabra);
                                else
                                    txtPalabra.setText("______________");
                            }
                            case "Borrar" ->
                                zonaDibujo.borrar();
                            case "Anfitrion" ->
                                esAnfitrion[0] = (boolean) ois.readObject();
                        }
                    }
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        });
        escuchador.start();
    }
    public Pinturillo(String titulo, String nombre){
        super(titulo);
        this.nombre = nombre;
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
        LinkedList<String> l = new LinkedList<>();
        l.add("Guillermo");
        l.add("Jorge");
        l.add("Margarita");
        l.add("Nico");
        l.add("Iker");
        l.add("Lucia");
        l.add("Oscar");
        l.add("Uno");
        l.add("Otro");
        l.add("NombredeOtro");
        Random r = new Random();
        int i = r.nextInt(l.size());
        Pinturillo p = new Pinturillo("titulo", l.get(i));
    }
}
