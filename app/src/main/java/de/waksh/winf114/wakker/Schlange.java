package de.waksh.winf114.wakker;

/**
 * Created by Matzef on 06.06.2015.
 */
public class Schlange extends Hindernis {
    Baum baum;
    boolean aufBaum;

    public Schlange(int x, int y, int geschwindigkeit, Baum baum, int farbe) {
        super(x, y, FP.schlangenBreite, FP.schlangenHoehe, geschwindigkeit, farbe);
        this.baum = baum;
        if (baum == null) {
            aufBaum = false;
        } else {
            aufBaum = true;
            this.setX(baum.getX());
            setZeichenBereich();
        }
    }

    void richtungWechseln() {
        setGeschwindigkeit(-getGeschwindigkeit());
    }

    public void bewegungAufBaum() {
        if (baum != null) {
            if (getZeichenBereich().centerX() == baum.getZeichenBereich().left || getZeichenBereich().centerX() == baum.getZeichenBereich().right) {
                richtungWechseln();
            }
            setZeichenBereich();
        }
    }


    public void move() {
        //TODO funktioniert nur wegen Parameter Glück

        if (baum != null) {
            //this.setX(this.getX() + this.getGeschwindigkeit() + baum.getGeschwindigkeit());
            if (getZeichenBereich().centerX() >= baum.getZeichenBereich().right) {
                richtungWechseln();
                // this.setX(this.getX() - this.getGeschwindigkeit());

            }
            if (getZeichenBereich().centerX() <= baum.getZeichenBereich().left) {
                richtungWechseln();
                this.setX(this.getX() + this.getGeschwindigkeit());
            }
            setZeichenBereich();
            super.move();
        } else {
            super.move();
        }
    }
}
