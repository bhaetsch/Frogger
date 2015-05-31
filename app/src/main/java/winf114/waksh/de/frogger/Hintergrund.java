package winf114.waksh.de.frogger;

import android.graphics.Canvas;
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
    private float lanehoehe;
    private float screenbreite;

    // die Farben der Bereiche
    private Farbe farbe = new Farbe();
    private Paint zielBereichStift = new Paint();
    private Paint wasserBereichStift = new Paint();
    private Paint pausenBereichStift = new Paint();
    private Paint strassenBereichStift = new Paint();
    private Paint startBereichStift = new Paint();
    private Paint strassenMarkierungStift = new Paint();


    public Hintergrund(int breite, int hoehe) {
        this.lanehoehe = hoehe;
        this.screenbreite = breite;

        // definiert die Größe der Bereiche
        // Rect(left,top,right,bottom)
        zielBereich = new Rect(0, 0, breite, hoehe);
        wasserBereich = new Rect(0, hoehe + 1, breite, hoehe * 6);
        pausenBereich = new Rect(0, hoehe * 6 + 1, breite, hoehe * 7);
        strassenBereich = new Rect(0, hoehe * 7 + 1, breite, hoehe * 12);
        startBereich = new Rect(0, hoehe * 12 + 1, breite, hoehe * 13);
        /*  erste Lane startet oben links (0,0)
            nächste Lane immer einen Pixel unter der Überen deshalb "top" + 1 !
            eine Lane startet immer nach X Lanes, deshalb Faktor X bei "top"
            eine Lane endet immer nach X + 1(oder 5) Lanes, deshalb Faktor X bei "bottom"
         */

        // definiert die Farben der Bereiche
        zielBereichStift.setColor(Farbe.zielBereich);
        wasserBereichStift.setColor(Farbe.wasserBereich);
        pausenBereichStift.setColor(Farbe.zielBereich);
        strassenBereichStift.setColor(Farbe.strassenBereich);
        startBereichStift.setColor(Farbe.zielBereich);
        strassenMarkierungStift.setColor(Farbe.strassenMarkierung);
    }

    public void draw(Canvas canvas) {
        // zeichnet die Bereiche
        // drawRect(Rect,Paint)
        canvas.drawRect(zielBereich, zielBereichStift);
        canvas.drawRect(wasserBereich, wasserBereichStift);
        canvas.drawRect(pausenBereich, pausenBereichStift);
        canvas.drawRect(strassenBereich, strassenBereichStift);
        canvas.drawRect(startBereich, startBereichStift);
        canvas.drawLine(0, lanehoehe * 8, screenbreite, lanehoehe * 8, strassenMarkierungStift);
        canvas.drawLine(0, lanehoehe * 9, screenbreite, lanehoehe * 9, strassenMarkierungStift);
        canvas.drawLine(0, lanehoehe * 10, screenbreite, lanehoehe * 10, strassenMarkierungStift);
        canvas.drawLine(0, lanehoehe * 11, screenbreite, lanehoehe * 11, strassenMarkierungStift);
    }
}
