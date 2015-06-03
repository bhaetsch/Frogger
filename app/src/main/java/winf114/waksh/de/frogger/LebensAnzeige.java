package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;

/**
 * Created by Matzef on 31.05.2015.
 */
class LebensAnzeige {

    private final ArrayList<Rect> leben;
    int lebenAnzahl;
    private final Paint zeichenStift;

    public LebensAnzeige(int x, int y, int breite, int hoehe, int farbe) {
        leben = new ArrayList<>();
        lebenAnzahl = 5;

        //Position und Größe sind abhängig vom Spielfeld //TODO schicker machen
        int abstand = breite * 2;
        Rect leben1 = new Rect(x, y, x + breite, y + hoehe);
        Rect leben2 = new Rect(x + abstand, y, x + abstand + breite, y + hoehe);
        Rect leben3 = new Rect(x + abstand * 2, y, x + abstand * 2 + breite, y + hoehe);
        Rect leben4 = new Rect(x + abstand * 3, y, x + abstand * 3 + breite, y + hoehe);
        Rect leben5 = new Rect(x + abstand * 4, y, x + abstand * 4 + breite, y + hoehe);

        leben.add(leben1);
        leben.add(leben2);
        leben.add(leben3);
        leben.add(leben4);
        leben.add(leben5);

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