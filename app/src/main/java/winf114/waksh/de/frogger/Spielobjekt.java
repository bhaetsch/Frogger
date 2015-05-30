package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public abstract class Spielobjekt {

    private int x;                  //horizontale Position der linken oberen Ecke
    private int y;                  //vertikale Position der linken oberen Ecke
    private int breite;             //in Pixeln
    private int hoehe;              //in Pixeln
    Paint zeichenStift;
    Rect zeichenBereich;


    public Spielobjekt(int x, int y, int breite, int hoehe, int farbe) {
        this.x = x;
        this.y = y;
        this.breite = breite;
        this.hoehe = hoehe;

        zeichenBereich = new Rect(0,0,0,0);
        zeichenStift = new Paint();
        zeichenStift.setColor(farbe);
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

    public boolean kollidiertMit(Rect r){
        if(this.zeichenBereich.intersect(r)){
            return true;
        }
        return false;
    }

    abstract void move();
    abstract void draw(Canvas canvas);
}
