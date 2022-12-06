import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        Paint paint_local = new Paint();
        paint_local.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Paint se ha cerrado");
                System.exit(0);
            }
        });
    }
}