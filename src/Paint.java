import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Paint extends JFrame {
    private Point actual;
    private Point anterior;
    // variable utilizada para saber cuando no hay que
    // pintar entre dos puntos separados
    private boolean tener_en_cuenta;

    public Paint(){
        super("Paint en local");
        this.actual = new Point(0,0);
        this.anterior = new Point(0,0);
        this.tener_en_cuenta = false;
        setVisible(true);
        setSize(600, 600);

        // Cuando pulsemos clic izquierdo no hay que corregir
        // ningún punto, ya que es el comienzo del trazo.
        // De lo contrario se unirá una línea desde el último punto
        //que se haya dibujado antes.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() ==  MouseEvent.BUTTON1)
                    tener_en_cuenta = false;
            }
        });

        //siempre que arrastremos el ratón dibujamos su movimiento
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                actual = e.getPoint();
                repaint();
            }
        });
    }

    public void paint(Graphics g){
        int diffX = actual.x - anterior.x;
        int diffY = actual.y - anterior.y;
        int absX = Math.abs(diffX);
        int absY = Math.abs(diffY);

        if((absX > 2 || absY > 2) && tener_en_cuenta){
            int longitud = Math.max(absX, absY);
            double pendiente = (double) diffY / diffX;
            if(Math.abs(pendiente) <= 1){
                for (int i = 1; i <= longitud; i++){
                    int k = (diffX >= 0) ? i : -i;
                    double j = (diffX >= 0) ? pendiente : -pendiente;
                    g.fillOval(anterior.x + k, (int)Math.round(anterior.y + (i * j)), 6, 6);
                }
            }else{
                for(int i = 1; i <= longitud; i++){
                    int k = (diffY >= 0) ? i : -i;
                    double j = (diffY >= 0) ? 1 / pendiente : -1 / pendiente;
                    g.fillOval((int)Math.round(anterior.x + (i*j)), anterior.y + k, 6, 6);
                }
            }
        }

        g.fillOval(actual.x, actual.y, 6, 6);
        if (!tener_en_cuenta)
            tener_en_cuenta = true;
        anterior.x = actual.x;
        anterior.y = actual.y;
    }
}
