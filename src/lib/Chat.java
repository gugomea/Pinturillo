package lib;

import javax.swing.*;
import java.awt.*;

public class Chat extends JComponent {

    private JScrollPane chat;
    private DefaultListModel<String> mensajeChat;
    private JButton bEnviar;
    private JButton bBorrar;
    private JButton bPaleta;
    private JTextField txtEnviar;

    public Chat(){
        this.chat = new JScrollPane();
        JList<String> msj = new JList<>();
        mensajeChat = new DefaultListModel<>();
        msj.setModel(mensajeChat);
        chat.setViewportView(msj);

        this.bEnviar = new JButton("Enviar");
        this.bBorrar = new JButton("Borrar");
        this.bPaleta = new JButton("Cambiar Color");
        this.txtEnviar= new JTextField();
        JPanel abajo = new JPanel();
        abajo.setLayout(new GridLayout(4, 1));
        abajo.add(txtEnviar);
        abajo.add(bEnviar);
        abajo.add(bPaleta);
        abajo.add(bBorrar);
        this.setLayout(new BorderLayout());
        this.add(chat, BorderLayout.CENTER);
        this.add(abajo, BorderLayout.SOUTH);
    }
}
