package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Matzef on 28.05.2015.
 */
public class GameHintergrund {

    private int spielZeilenHoehe;   //Höhe einer "Lane" im Spiel in Pixeln
    private int breite;             //Breite der Anzeigefläche in Pixeln
    private int hoehe;              //Höhe der Anzeigeflächein Pixeln

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


    public GameHintergrund(int hoehe, int breite) {
        this.hoehe = hoehe;
        this.breite = breite;
        this.spielZeilenHoehe = hoehe * 10 / 100;

        // definiert die Größe der rechteckigen Farb-Flächen für den Hintergrund
        // Rect(left,top,right,bottom)
        zielBereich = new Rect(0, 0, this.breite, spielZeilenHoehe);
        wasserBereich = new Rect(0, spielZeilenHoehe + 1, this.breite, spielZeilenHoehe * 6);
        pausenBereich = new Rect(0, spielZeilenHoehe * 6 + 1, this.breite, spielZeilenHoehe * 7);
        strassenBereich = new Rect(0, spielZeilenHoehe * 7 + 1, this.breite, spielZeilenHoehe * 12);
        startBereich = new Rect(0, spielZeilenHoehe * 12 + 1, this.breite, spielZeilenHoehe * 13);

        zielBereichFarbe.setColor(Color.parseColor("#008000"));
        wasserBereichFarbe.setColor(Color.parseColor("#0000ff"));
        pausenBereichFarbe.setColor(Color.parseColor("#008000"));
        strassenBereichFarbe.setColor(Color.parseColor("#313131"));
        startBereichFarbe.setColor(Color.parseColor("#008000"));
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(zielBereich, zielBereichFarbe);
        canvas.drawRect(wasserBereich, wasserBereichFarbe);
        canvas.drawRect(pausenBereich, pausenBereichFarbe);
        canvas.drawRect(strassenBereich, strassenBereichFarbe);
        canvas.drawRect(startBereich, startBereichFarbe);
    }
}