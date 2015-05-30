package winf114.waksh.de.frogger;

import android.graphics.Canvas;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class Hindernis extends Spielobjekt {

    private int geschwindigkeit;    //negativ==links; positiv==rechts, 0==statisch

    public Hindernis(int x, int y, int breite, int hoehe, int geschwindigkeit, int farbe) {
        super(x, y, breite, hoehe, farbe);
        this.geschwindigkeit = geschwindigkeit;
        setZeichenBereich();
    }

    public void move() {
        this.setX(this.getX() + this.geschwindigkeit);
        setZeichenBereich();
    }

    public void erscheintWieder(){
        this.setX(0-this.getBreite());
        this.setZeichenBereich();
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(zeichenBereich, zeichenStift);
    }
}