package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public abstract class Spielobjekt {

    // TODO könnt Rect beerben
    // => Rect ist final und kann nicht beerbt werden!

    private int x;                  //horizontale Position der linken oberen Ecke
    private int y;                  //vertikale Position der linken oberen Ecke
    private int breite;             //in Pixeln
    private int hoehe;              //in Pixeln
    private Paint zeichenStift;
    private Rect zeichenBereich;


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

    public void setBreite(int breite) {
        this.breite = breite;
    }

    public int getHoehe() {
        return hoehe;
    }

    public void setHoehe(int hoehe) {
        this.hoehe = hoehe;
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
        if (this.zeichenBereich.intersects(this.zeichenBereich, r)) {
            return true;
        }
        return false;
    }

    abstract void move();

    public void draw(Canvas canvas) {
        canvas.drawRect(zeichenBereich, zeichenStift);
    }
}
