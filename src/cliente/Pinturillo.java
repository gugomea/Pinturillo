package cliente;

import componentes.Chat;
import componentes.Paint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Pinturillo extends JFrame {
    private String nombre;
    //Arriba
    private JLabel lblPalabra;
    private JLabel txtPalabra;

    //izquierda
    private Chat chatterino;

    //derecha
    private Paint zonaDibujo;

    private ObjectOutputStream oos;

    private ObjectInputStream ois;

    // es boolean[] para compartir la misma variable con el chat y la zona de dibujo
    private boolean[] esAnfitrion = new boolean[1];

    private Socket conexion;

    private String palabra;
    private void inicializar(){
        try {
            conexion = new Socket("localhost", 55_555);
            oos = new ObjectOutputStream(conexion.getOutputStream());
            ois = new ObjectInputStream(conexion.getInputStream());
            oos.writeObject(nombre);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.zonaDibujo = new Paint(oos, ois, esAnfitrion);

        this.chatterino = new Chat(oos, zonaDibujo, esAnfitrion);
        this.lblPalabra = new JLabel("Palabra: ");
        this.lblPalabra.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        this.txtPalabra = new JLabel("__ __ __ __ __ __ __");
        this.txtPalabra.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        this.txtPalabra.setMinimumSize(new Dimension(1000, 10));
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
                } catch (IOException ex) {
                    ex.printStackTrace();
                }finally {
                    Cerrar(oos);
                    Cerrar(ois);
                    Cerrar(conexion);
                }
            }
        });

        Thread escuchador = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                try {
                    Object o = ois.readObject();
                        if(o instanceof Boolean) // actualizar si somos anfitriones o no
                            esAnfitrion[0] = (boolean) o;
                        else if(o instanceof Object[]) // pintar el Object[]{Punto, Color, Grosor}
                            zonaDibujo.pintar((Object[]) o);
                        else if(o instanceof String){
                            String msj = (String)o;
                            switch (msj){
                                case "Palabra" -> {//Nos tienen que enviar la palabra a adivinar
                                    txtPalabra.setForeground(Color.BLACK);
                                    palabra = (String) ois.readObject();
                                    String barras = "";
                                    for(int i = 0; i < palabra.length(); i++) barras += " __ ";
                                    txtPalabra.setText(barras);
                                    if(esAnfitrion[0]) txtPalabra.setText(palabra);
                                    else txtPalabra.setForeground(Color.RED);
                                }
                                case "Actualizar" -> {//Hemos acertado la palabra
                                    txtPalabra.setText(palabra);
                                    txtPalabra.setForeground(Color.GREEN);
                                }
                                case "Borrar" -> zonaDibujo.borrar();
                                case "Principio" -> {//Es el comienzo de un trazo, así que hay que actualizar el punto "anterior"
                                    if(!esAnfitrion[0]) zonaDibujo.actualizar();
                                }
                                default -> chatterino.escribir(msj);//no tiene palabras clave, así que es un mensaje
                            }
                        }
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                    return;
                }}
            }
        });
        escuchador.start();
        setVisible(true);
    }

    public static void Cerrar(Closeable c){
        if(c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // solo he utilizado ObjectInput/OutputStream ya que si apunto con dos Readers al mismo stream
        // el segundo reader lee la cabecera del primero, y saltan excepciones. Tenemos la suerte de que todo
        // en java es un objeto y con un simple instance of se puede saber el tipo del objeto enviado
        Random r = new Random();
        int i = r.nextInt(100);
        Pinturillo p = new Pinturillo("titulo", "Usuario " + i);
    }
}
