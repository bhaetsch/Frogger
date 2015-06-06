package winf114.waksh.de.frogger;

/**
 * Created by Matzef on 30.05.2015.
 */
class Ziel extends Spielobjekt {

    //Ziele werden nicht geleert wenn man verliert

    private boolean besetzt;

    public Ziel(int x, int breite, int hoehe, int farbe) {
        super(x, 0, breite, hoehe, farbe);
        besetzt = false;
    }

    public void setBesetzt(boolean besetzt) {
        if (besetzt) {
            this.getZeichenStift().setColor(Farbe.zielBesetzt);
            this.setZeichenBereich();
            this.besetzt = true;
        } else if (!besetzt) {
            this.getZeichenStift().setColor(Farbe.zielLeer);
            this.setZeichenBereich();
            this.besetzt = false;
        }
    }

    public boolean isBesetzt() {
        return besetzt;
    }

    public void move() {
        // Ziele bewegen sich nicht
    }
}

