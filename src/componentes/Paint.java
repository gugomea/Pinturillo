package componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Paint extends JComponent {

    private Image pintar;

    private Graphics2D graphicPintar;

    private Point actual = null, anterior = null;

    private int grosor = 5;

    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    private boolean[] esAnitrion;

    private int maxWidth, maxHeight;
    public Paint(ObjectOutputStream os, ObjectInputStream is, boolean[] eA){
        this.esAnitrion = eA;
        this.oos = os;
        this.ois = is;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(esAnitrion[0]){
                    anterior = e.getPoint();
                    graphicPintar.fillOval(anterior.x, anterior.y, grosor, grosor);
                    // mandamos que comienza el trazo, ya que de otra forma this.anterior seria el último punto
                    // dibujado en el anterior trazo, y dibujaría una línea recta entre estos, en vez de no hacer nada.
                    try { oos.writeObject("Principio");oos.flush();} catch (IOException ex) { ex.printStackTrace(); }
                    try{ oos.writeObject(new Object[]{anterior, graphicPintar.getColor(), grosor}); oos.flush(); }catch (IOException ee){ee.printStackTrace();}
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(esAnitrion[0]){
                    // para pintar puntos individuales y no trazos
                    pintar(e.getPoint(), true);
                }
            }
        });
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //cambiar el grosor con la rueda del ratón
                if(e.getWheelRotation() < 0){
                    grosor = Math.min(grosor + 2, 80);
                }else{
                    grosor = Math.max(2, grosor - 2);
                }
            }
        });
    }
    public void pintar(Point e, boolean mandar){
        // función en la que rellenamos los huecos que se pueden formar entre los puntos si el ratón
        // se mueve rápido.
        actual = e;
        if(anterior == null)
            anterior = actual;
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
                // se hace round para coger el punto mas cercano a la pendiente.
                Point p = new Point(ax + k, (int)Math.round(ay + (i * j)));
                graphicPintar.fillOval(p.x, p.y, grosor, grosor);
            }
        }else{
            for(int i = 1; i <= longitud; i++){
                int k = diffy >= 0 ? i: -i;
                double j = diffy >= 0 ? 1 / pendiente: -1/pendiente;
                Point p = new Point((int)Math.round(ax + (i * j)), ay + k);
                graphicPintar.fillOval(p.x, p.y, grosor, grosor);
            }
        }
        graphicPintar.fillOval(x , y, grosor, grosor);
        if(mandar) try{ oos.writeObject(new Object[]{actual, graphicPintar.getColor(), grosor}); oos.flush(); }catch (IOException ee){ee.printStackTrace();}
        repaint();// actualiza lo que hemos pintado
        anterior = actual;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(this.pintar == null){
            //Para calcular el tamaño maximo.
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension dim = tk.getScreenSize();
            maxWidth = (int) dim.getWidth();
            maxHeight = (int) dim.getHeight();

            this.pintar = createImage(maxWidth,maxHeight);
            this.graphicPintar = (Graphics2D) this.pintar.getGraphics();
            this.graphicPintar.setPaint(Color.BLACK);
        }
        g.drawImage(this.pintar, 0, 0, null);
    }

    public void actualizar(){
        this.anterior = null;
    }

    public void pintar(Object[] o){
        // Sabemos que el Object[] es de la forma {Punto, Color, Grosor}
        Color ant = graphicPintar.getColor();
        Point p = (Point)o[0];
        Color c = (Color)o[1];
        int antt = grosor;
        grosor = (int)o[2];
        graphicPintar.setColor(c);
        pintar(p, false);
        grosor = antt;
        graphicPintar.setColor(ant);
        repaint();
    }
    public void setcolor(Color c){
        graphicPintar.setColor(c);
    }

    public void borrar(){
        if(this.graphicPintar != null){
            Color actual = this.graphicPintar.getColor();
            this.graphicPintar.setColor(this.graphicPintar.getBackground());
            this.graphicPintar.fillRect(0, 0, maxWidth, maxHeight);
            this.graphicPintar.setColor(actual);
            repaint();
        }
    }
}
