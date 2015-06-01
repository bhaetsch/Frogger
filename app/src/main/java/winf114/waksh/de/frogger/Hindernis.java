package winf114.waksh.de.frogger;

/**
 * Created by bhaetsch on 25.05.2015.
 */
class Hindernis extends Spielobjekt {

    private final int geschwindigkeit;    //negativ==links; positiv==rechts, 0==statisch
    private final GameActivity gameActivity;


    public Hindernis(int x, int y, int breite, int hoehe, int geschwindigkeit, int farbe, GameActivity gameActivity) {
        super(x, y, breite, hoehe, farbe);
        this.gameActivity = gameActivity;
        this.geschwindigkeit = geschwindigkeit;
    }

    public void move() {
        this.setX(this.getX() + this.geschwindigkeit);
        this.setZeichenBereich();
    }

    public void erscheintWieder() {
        //TODO erweiterte Spielfläche
        if (this.geschwindigkeit > 0) {
            this.setX(gameActivity.spielFlaeche.left - this.getBreite());
        } else {
            this.setX(gameActivity.spielFlaeche.right);
        }
        this.setZeichenBereich();
    }

    public int getGeschwindigkeit() {
        return geschwindigkeit;
    }
}