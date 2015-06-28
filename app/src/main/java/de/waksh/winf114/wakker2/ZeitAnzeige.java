package de.waksh.winf114.wakker2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Matzef on 03.06.2015.
 */
class ZeitAnzeige {

    private final Paint zeichenStift;
    private Rect zeichenBereich;
    private long timerStart;
    private float breiteProSekunde;

    public ZeitAnzeige() {
        this.zeichenStift = new Paint();
        this.zeichenStift.setColor(Farbe.auto);
        timerStart = System.currentTimeMillis();
        breiteProSekunde = (float) FP.zeitAnzeigeBreite / (float) SpielWerte.LEVEL_ZEIT_SEK;

        zeichenBereich = new Rect(
                FP.zeitAnzeigeX,
                FP.zeitAnzeigeY + FP.lanePadding,
                FP.zeitAnzeigeX + FP.zeitAnzeigeBreite,
                FP.zeitAnzeigeY + FP.lanePadding + FP.zeitAnzeigeHöhe);
    }

    /* Wrapper, um die Zeitanzeige nur einmal pro Sekunde zu ändern */
    void tick() {
        if (System.currentTimeMillis() > timerStart + 1000) {
            verringereZeitAnzeige();
            timerStart = System.currentTimeMillis();
        }
    }

    /* Zurücksetzen der Zeitanzeige auf Ausgangswerte */
    void resetZeitanzeige() {
        zeichenBereich.set(
                FP.zeitAnzeigeX,
                FP.zeitAnzeigeY + FP.lanePadding,
                FP.zeitAnzeigeX + FP.zeitAnzeigeBreite,
                FP.zeitAnzeigeY + FP.lanePadding + FP.zeitAnzeigeHöhe);
    }

    /* Änderung der Zeitanzeige */
    void verringereZeitAnzeige() {
        float aktuelleBreite = FP.zeitAnzeigeBreite - (breiteProSekunde * SpielWerte.getZeitImLevelVerbracht());
        zeichenBereich.set(FP.zeitAnzeigeX,
                FP.zeitAnzeigeY + FP.lanePadding,
                FP.zeitAnzeigeX + Math.round(aktuelleBreite),
                FP.zeitAnzeigeY + FP.lanePadding + FP.zeitAnzeigeHöhe);
    }

    void draw(Canvas canvas) {
        canvas.drawRect(zeichenBereich, zeichenStift);
    }
}