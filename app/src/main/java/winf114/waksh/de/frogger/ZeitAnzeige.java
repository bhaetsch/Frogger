package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Matzef on 03.06.2015.
 */
class ZeitAnzeige {

    private final Paint zeichenStift;
    private Rect zeitRect;
    private int breite;
    private int startbreite;
    private long start;

    public ZeitAnzeige() {
        this.zeichenStift = new Paint();
        this.zeichenStift.setColor(Farbe.auto);
        startbreite = 405;
        start = System.currentTimeMillis();

        zeitRect =  new Rect(
                FP.spielFlaeche.centerX(),
                FP.lanePixelHoehe * 14 + FP.lanePadding,
                FP.spielFlaeche.centerX() + 405,
                FP.lanePixelHoehe * 14 + FP.lanePadding + FP.lebensAnzeigeHöhe);
    }

    void tick(){
        if (System.currentTimeMillis() > start + 1000){
            verringereZeitAnzeige();
            start = System.currentTimeMillis();
        }
    }


    void resetZeitanzeige(){
        zeitRect.set(FP.spielFlaeche.centerX(),
                FP.lanePixelHoehe * 14 + FP.lanePadding,
                FP.spielFlaeche.centerX() + 405,
                FP.lanePixelHoehe * 14 + FP.lanePadding + FP.lebensAnzeigeHöhe);
    }

    void verringereZeitAnzeige(){
            breite = startbreite - (9*(SpielWerte.getZeitImLevelVerbracht()/1000));
            zeitRect.set(FP.spielFlaeche.centerX(),
                    FP.lanePixelHoehe * 14 + FP.lanePadding,
                    FP.spielFlaeche.centerX() + breite,
                    FP.lanePixelHoehe * 14 + FP.lanePadding + FP.lebensAnzeigeHöhe);

    }

    void draw(Canvas canvas) {
        canvas.drawRect(zeitRect, zeichenStift);
    }
}
