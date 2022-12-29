package lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Objects;

public class Paint extends JComponent {

    private Image pintar;

    private Graphics2D graphicPintar;

    private Point actual, anterior;

    private int grosor = 5;

    private LinkedList<Object[]> puntos = new LinkedList<>();

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Paint(ObjectOutputStream os, ObjectInputStream is){
        this.oos = os;
        this.ois = is;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                anterior = e.getPoint();
                graphicPintar.fillOval(anterior.x, anterior.y, grosor, grosor);
                puntos.add(new Object[]{anterior, graphicPintar.getColor(), grosor});
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
                        Point p = new Point(ax + k, (int)Math.round(ay + (i * j)));
                        graphicPintar.fillOval(p.x, p.y, grosor, grosor);
                        puntos.add(new Object[]{p, graphicPintar.getColor(), grosor});
                    }
                }else{
                    for(int i = 1; i <= longitud; i++){
                        int k = diffy >= 0 ? i: -i;
                        double j = diffy >= 0 ? 1 / pendiente: -1/pendiente;
                        Point p = new Point((int)Math.round(ax + (i * j)), ay + k);
                        graphicPintar.fillOval(p.x, p.y, grosor, grosor);
                        puntos.add(new Object[]{p, graphicPintar.getColor(), grosor});
                    }
                }
                graphicPintar.fillOval(x , y, grosor, grosor);
                puntos.add(new Object[]{actual, graphicPintar.getColor(), grosor});
                repaint();// actualiza lo que hemos pintado
                anterior = actual;
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                LinkedList<Object[]> ll = new LinkedList<>(puntos);
                try { oos.writeObject("Puntos");oos.writeObject(ll); oos.flush();} catch (IOException ex) { throw new RuntimeException(ex); }
                puntos.clear();
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

    public void pintar(LinkedList<Object[]> l){
        Color ant = graphicPintar.getColor();
        for(Object[] o: l){
            Point p = (Point)o[0];
            Color c = (Color)o[1];
            int g = (int)o[2];
            graphicPintar.setColor(Color.RED);
            graphicPintar.fillOval(p.x, p.y, g, g);
        }
        graphicPintar.setColor(ant);
        repaint();
    }
    public void setcolor(Color c){
        graphicPintar.setColor(c);
    }

    public void borrar(){
        Color actual = this.graphicPintar.getColor();
        this.graphicPintar.setColor(this.graphicPintar.getBackground());
        this.graphicPintar.fillRect(0, 0, getWidth(), getHeight());
        this.graphicPintar.setColor(actual);
        repaint();
    }
}
