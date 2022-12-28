import lib.Paint;

import javax.swing.*;
import java.awt.*;

public class Pinturillo extends JFrame {
    //Arriba
    private JLabel lblPalabra;
    private JTextField txtPalabra;

    //izquierda
    private JScrollPane chat;
    private DefaultListModel<String> mensajeChat;
    private JButton bEnviar;
    private JButton bBorrar;
    private JButton bPaleta;
    private JTextField txtEnviar;

    //derecha
    private Paint zonaDibujo;

    private void inicializar(){
        this.lblPalabra = new JLabel("Palabra: ");

        this.txtPalabra = new JTextField();

        this.chat = new JScrollPane();
        JList<String> msj = new JList<>();
        mensajeChat = new DefaultListModel<>();
        msj.setModel(mensajeChat);
        chat.setViewportView(msj);

        this.bEnviar = new JButton("Enviar");
        this.bBorrar = new JButton("Borrar");
        this.bPaleta = new JButton("Cambiar Color");
        this.txtEnviar= new JTextField();
        this.zonaDibujo = new Paint();
    }
    public Pinturillo(String titulo){
        super(titulo);
        inicializar();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        JSplitPane split = new JSplitPane();
        this.add(split, BorderLayout.CENTER);
        JPanel izquierda = new JPanel();
        JPanel abajo_izquierda = new JPanel();

        // Añadir los botones de enviar, paleta y borrar
        abajo_izquierda.setLayout(new GridLayout(4, 1));
        abajo_izquierda.add(txtEnviar);
        abajo_izquierda.add(bEnviar);
        abajo_izquierda.add(bPaleta);
        abajo_izquierda.add(bBorrar);

        izquierda.setLayout(new BorderLayout());
        izquierda.add(chat, BorderLayout.CENTER);
        izquierda.add(abajo_izquierda, BorderLayout.SOUTH);

        // Dividimos la pantalla en el chat(y sus botones) a la izda y para pintar a la derecha.
        split.setLeftComponent(izquierda);
        split.setRightComponent(zonaDibujo);

        setSize(1000, 500);
        setVisible(true);
    }

    public static void main(String[] args) {
        Pinturillo p = new Pinturillo("titulo");
    }
}
