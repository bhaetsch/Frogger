package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;

/**
 * Created by Matzef on 31.05.2015.
 */
public class LebensAnzeige {

    private ArrayList<Rect> leben;
    private Rect Leben1;
    private Rect Leben2;
    private Rect Leben3;
    private Rect Leben4;
    private Rect Leben5;
    protected int lebenAnzahl;
    private Paint zeichenStift;

    public LebensAnzeige(int x, int y, int breite, int hoehe, int farbe) {
        leben = new ArrayList<>();
        lebenAnzahl = 5;

        //Position und Größe sind abhängig vom Spielfeld //TODO schicker machen
        int abstand = breite * 2;
        Leben1 = new Rect(x, y, x + breite, y + hoehe);
        Leben2 = new Rect(x + abstand, y, x + abstand + breite, y + hoehe);
        Leben3 = new Rect(x + abstand * 2, y, x + abstand * 2 + breite, y + hoehe);
        Leben4 = new Rect(x + abstand * 3, y, x + abstand * 3 + breite, y + hoehe);
        Leben5 = new Rect(x + abstand * 4, y, x + abstand * 4 + breite, y + hoehe);

        leben.add(Leben1);
        leben.add(Leben2);
        leben.add(Leben3);
        leben.add(Leben4);
        leben.add(Leben5);

        this.zeichenStift = new Paint();
        this.zeichenStift.setColor(farbe);
    }

    public void draw(Canvas canvas) {
        // zeigt Vierecke nur in Anzahl der lebenAnzahl an
        for (int i = 1; i <= lebenAnzahl; i++) {
            canvas.drawRect(leben.get(i - 1), zeichenStift);
        }
    }
}