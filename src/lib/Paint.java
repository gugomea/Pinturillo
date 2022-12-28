package lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;

public class Paint extends JComponent {

    private Image pintar;

    private Graphics2D graphicPintar;

    private Point actual, anterior;

    int grosor = 5;

    public Paint(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                anterior = e.getPoint();
                graphicPintar.fillOval(anterior.x, anterior.y, grosor, grosor);
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                actual = e.getPoint();
                int x = actual.x , y = actual.y;
                int ax = anterior.x, ay = anterior.y;
                int diffx = x - ax, diffy = y - ay;
                //longitud del eje en la que la distancia es maxima(para poder dibujar el mayor número de puntos)
                int longitud = Math.max(Math.abs(diffx), Math.abs(diffy));
                double pendiente = (double) diffy / diffx;
                if(Math.abs(pendiente) <= 1){
                    for(int i = 1; i <= longitud; i++){
                        int k = diffx >= 0 ? i : - i;
                        double j = diffx >= 0 ? pendiente: -pendiente;
                        graphicPintar.fillOval(ax + k, (int)Math.round(ay + (i * j)), grosor, grosor);
                    }
                }else{
                    for(int i = 1; i <= longitud; i++){
                        int k = diffy >= 0 ? i: -i;
                        double j = diffy >= 0 ? 1 / pendiente: -1/pendiente;
                        graphicPintar.fillOval((int)Math.round(ax + (i * j)), ay + k, grosor, grosor);
                    }
                }
                graphicPintar.fillOval(x , y, grosor, grosor);

                repaint();// actualiza lo que hemos pintado
                anterior = actual;
            }
        });
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() < 0){
                    grosor = Math.min(grosor + 2, 80);
                }else{
                    grosor = Math.max(2, grosor - 2);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(this.pintar == null){
            //Para calcular el tamaño maximo.
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension dim = tk.getScreenSize();
            int w = (int) dim.getWidth();
            int h = (int) dim.getHeight();

            this.pintar = createImage(w, h);
            this.graphicPintar = (Graphics2D) this.pintar.getGraphics();
            this.graphicPintar.setPaint(Color.BLACK);
        }
        g.drawImage(this.pintar, 0, 0, null);
    }
}
