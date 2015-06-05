package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Matzef on 03.06.2015.
 */
class ZeitAnzeige {

    //TODO wird niemals komplett aufgebraucht
    private final Paint zeichenStift;
    private Rect zeichenBereich;
    private int aktuelleBreite;
    private long timerStart;
    private int breiteProSekunde;

    public ZeitAnzeige() {
        this.zeichenStift = new Paint();
        this.zeichenStift.setColor(Farbe.auto);
        timerStart = System.currentTimeMillis();
        breiteProSekunde = FP.zeitAnzeigeBreite / SpielWerte.LEVEL_ZEIT_SEK;

        zeichenBereich =  new Rect(
                FP.zeitAnzeigeX,
                FP.zeitAnzeigeY + FP.lanePadding,
                FP.zeitAnzeigeX + FP.zeitAnzeigeBreite,
                FP.zeitAnzeigeY + FP.lanePadding + FP.zeitAnzeigeHöhe);
    }

    void tick(){
        if (System.currentTimeMillis() > timerStart + 1000){
            verringereZeitAnzeige();
            timerStart = System.currentTimeMillis();
        }
    }

    void resetZeitanzeige(){
        zeichenBereich.set(
                FP.zeitAnzeigeX,
                FP.zeitAnzeigeY + FP.lanePadding,
                FP.zeitAnzeigeX + FP.zeitAnzeigeBreite,
                FP.zeitAnzeigeY + FP.lanePadding + FP.zeitAnzeigeHöhe);
    }

    void verringereZeitAnzeige(){
            aktuelleBreite = FP.zeitAnzeigeBreite - (breiteProSekunde *(SpielWerte.getZeitImLevelVerbracht()/1000));
            zeichenBereich.set(FP.zeitAnzeigeX,
                    FP.zeitAnzeigeY + FP.lanePadding,
                    FP.zeitAnzeigeX + aktuelleBreite,
                    FP.zeitAnzeigeY + FP.lanePadding + FP.zeitAnzeigeHöhe);
    }

    void draw(Canvas canvas) {
        canvas.drawRect(zeichenBereich, zeichenStift);
    }
}
