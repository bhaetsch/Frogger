package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Matzef on 28.05.2015.
 */
public class Hintergrund {

    // die einzelnen Bereiche des Spielhintergrunds
    private Rect zielBereich;
    private Rect wasserBereich;
    private Rect pausenBereich;
    private Rect strassenBereich;
    private Rect startBereich;

    // die Farben der Bereiche
    private Paint zielBereichFarbe = new Paint();
    private Paint wasserBereichFarbe = new Paint();
    private Paint pausenBereichFarbe = new Paint();
    private Paint strassenBereichFarbe = new Paint();
    private Paint startBereichFarbe = new Paint();



    public Hintergrund(int breite, int hoehe) {

        // definiert die Größe der Bereiche
        // Rect(left,top,right,bottom)
        zielBereich = new Rect(0,0,breite,hoehe);
        wasserBereich = new Rect(0,hoehe + 1,breite,hoehe * 6);
        pausenBereich = new Rect(0,hoehe * 6 + 1,breite,hoehe * 7);
        strassenBereich = new Rect(0,hoehe * 7 + 1,breite,hoehe * 12);
        startBereich = new Rect(0,hoehe * 12 + 1,breite,hoehe * 13);
        /*
            erste Lane startet oben links (0,0)
            nächste Lane immer einen Pixel unter der Überen deshalb "top" + 1 !
            eine Lane startet immer nach X Lanes, deshalb Faktor X bei "top"
            eine Lane endet immer nach X + 1(oder 5) Lanes, deshalb Faktor X bei "bottom"
         */

        // definiert die Farben der Bereiche
        zielBereichFarbe.setColor(Color.parseColor("#2f360b"));
        wasserBereichFarbe.setColor(Color.parseColor("#152a5b"));
        pausenBereichFarbe.setColor(Color.parseColor("#2f360b"));
        strassenBereichFarbe.setColor(Color.parseColor("#313131"));
        startBereichFarbe.setColor(Color.parseColor("#2f360b"));
    }

    public void draw(Canvas canvas) {
        // zeichnet die Bereiche
        // drawRect(Rect,Paint)
        canvas.drawRect(zielBereich, zielBereichFarbe);
        canvas.drawRect(wasserBereich, wasserBereichFarbe);
        canvas.drawRect(pausenBereich, pausenBereichFarbe);
        canvas.drawRect(strassenBereich, strassenBereichFarbe);
        canvas.drawRect(startBereich, startBereichFarbe);
    }
}
