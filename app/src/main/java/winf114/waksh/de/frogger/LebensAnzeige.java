package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;

/**
 * Created by Matzef on 31.05.2015.
 */
class LebensAnzeige {

    private  ArrayList<Rect> leben;
    private final Paint zeichenStift;
    private Rect[] lebenRect;

    public LebensAnzeige() {
        this.zeichenStift = new Paint();
        this.zeichenStift.setColor(Farbe.frosch);
        leben = new ArrayList<>();
        lebenRect = new Rect[SpielWerte.LEBEN];
        resetLebensAnzeige();
    }

    void lebenVerlieren(){
        leben.remove(leben.size() - 1);
    }

    boolean keineLebenMehr(){
        if (leben.size() == 0) {
            resetLebensAnzeige();
            return true;
        }
        return false;
    }

    void resetLebensAnzeige(){
        for(int i = 0; i < SpielWerte.LEBEN; i++){
            leben.add(lebenRect[i] = new Rect(
                    FP.lebensAnzeigeX + (FP.lebensAnzeigeAbstand * i),
                    FP.lebensAnzeigeY,
                    FP.lebensAnzeigeX + FP.lebensAnzeigeBreite + (FP.lebensAnzeigeAbstand * i),
                    FP.lebensAnzeigeY + FP.lebensAnzeigeHöhe));
        }
    }

    void draw(Canvas canvas) {
        // zeigt Vierecke nur in Anzahl der lebenAnzahl an
        for (Rect r : leben) {
            canvas.drawRect(r, zeichenStift);
        }
    }
}