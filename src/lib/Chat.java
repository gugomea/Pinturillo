package lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Chat extends JComponent {

    private JScrollPane chat;
    private DefaultListModel<String> mensajeChat;
    private JButton bEnviar;
    private JButton bBorrar;
    private JButton bPaleta;
    private JTextField txtEnviar;

    private ObjectOutputStream oos;
    private Paint p;
    public Chat(ObjectOutputStream os, Paint paint){
        this.oos = os;
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
        bPaleta.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paint.setcolor(JColorChooser.showDialog(null, "Elije Color", Color.BLACK));
            }
        });
        bEnviar.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    oos.writeObject("Mensaje");
                    oos.writeObject(txtEnviar.getText());
                    oos.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        bBorrar.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paint.borrar();
            }
        });
    }

    public void escribir(String mensaje){
        this.mensajeChat.addElement(mensaje);
    }
}
