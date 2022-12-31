package lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Chat extends JComponent {

    private JScrollPane chat;
    private DefaultListModel<String> mensajeChat;
    private JButton bEnviar;
    private JButton bBorrar;
    private JButton bPaleta;
    private JTextField txtEnviar;

    public ObjectOutputStream oos;
    private Paint p;
    private boolean[] esAnfitrion;
    public Chat(ObjectOutputStream os, Paint paint, boolean[] eA){
        this.esAnfitrion = eA;
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
                mandar();
            }
        });
        bBorrar.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(esAnfitrion[0]){
                    paint.borrar();
                    try {
                        oos.writeObject("Borrar");
                        oos.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        txtEnviar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    mandar();
                }
            }
        });
    }
    private void mandar(){
        try {
            if(!esAnfitrion[0]){
                oos.writeObject(txtEnviar.getText());
                oos.flush();
                txtEnviar.setText("");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public synchronized void escribir(String mensaje){
        try { Thread.sleep(10); } catch (InterruptedException e) { throw new RuntimeException(e); }
        this.mensajeChat.addElement(mensaje);
    }
}
