package de.waksh.winf114.wakker2;

/**
 * Created by bhaetsch on 25.05.2015.
 * Oberklasse für alle Hindernisse
 */
class Hindernis extends Spielobjekt {

    private int geschwindigkeit;    //negativ == links; positiv == rechts, 0 == statisch

    public Hindernis(int x, int y, int breite, int hoehe, int geschwindigkeit, int farbe) {
        super(x, y, breite, hoehe, farbe);
        this.geschwindigkeit = geschwindigkeit;
    }

    public void move() {
        this.setX(this.getX() + this.geschwindigkeit);
        this.setZeichenBereich();
    }

    public void erscheintWieder() {
        if (this.geschwindigkeit > 0) {
            this.setX(FP.spielFlaeche.left - this.getBreite());
        } else {
            this.setX(FP.spielFlaeche.right);
        }
        this.setZeichenBereich();
    }

    public int getGeschwindigkeit() {
        return geschwindigkeit;
    }

    void setGeschwindigkeit(int geschwindigkeit){
        this.geschwindigkeit = geschwindigkeit;
    }
}