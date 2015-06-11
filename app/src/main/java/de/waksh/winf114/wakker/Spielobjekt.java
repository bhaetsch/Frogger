package de.waksh.winf114.wakker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by bhaetsch on 25.05.2015.
 */
abstract class Spielobjekt {

    private int x;                         // horizontale Position der linken oberen Ecke
    private int y;                         // vertikale Position der linken oberen Ecke
    private int breite;                    // in Pixeln
    private int hoehe;                     // in Pixeln
    private final Rect zeichenBereich;     // Viereck für die Anzeige
    private final Paint zeichenStift;      // Stift, der das Viereck zeichnet

    public Spielobjekt(int x, int y, int breite, int hoehe, int farbe) {
        this.x = x;
        this.y = y;
        this.breite = breite;
        this.hoehe = hoehe;
        this.zeichenBereich = new Rect(0, 0, 0, 0);
        this.zeichenStift = new Paint();
        this.zeichenStift.setColor(farbe);
        this.setZeichenBereich();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBreite() {
        return breite;
    }

    public void setZeichenBereich() {
        zeichenBereich.set(x, y, x + breite, y + hoehe);
    }

    public Rect getZeichenBereich() {
        return zeichenBereich;
    }

    public Paint getZeichenStift() {
        return zeichenStift;
    }

    public boolean kollidiertMit(Rect r) {
        return this.zeichenBereich.intersects(this.zeichenBereich, r);
    }

    abstract void move();

    public void draw(Canvas canvas) {
        canvas.drawRect(zeichenBereich, zeichenStift);
    }
}
