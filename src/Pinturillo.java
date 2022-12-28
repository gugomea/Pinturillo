import lib.Chat;
import lib.Paint;

import javax.swing.*;
import java.awt.*;

public class Pinturillo extends JFrame {
    //Arriba
    private JLabel lblPalabra;
    private JTextField txtPalabra;

    //izquierda
    private Chat chatterino;

    //derecha
    private Paint zonaDibujo;


    private void inicializar(){
        this.lblPalabra = new JLabel("Palabra: ");

        this.txtPalabra = new JTextField();

        this.zonaDibujo = new Paint();

        this.chatterino = new Chat();
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
