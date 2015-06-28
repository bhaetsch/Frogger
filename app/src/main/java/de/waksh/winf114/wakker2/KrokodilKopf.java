package de.waksh.winf114.wakker2;

/**
 * Created by Matzef on 06.06.2015.
 */
public class KrokodilKopf extends Hindernis {
    private Baum baum;

    public KrokodilKopf(Baum baum, int farbe) {
        super(baum.getX() + baum.getBreite(), baum.getY(), FP.objektPixelBreite, FP.objektPixelHoehe, baum.getGeschwindigkeit(), farbe);
        this.baum = baum;
    }

    public void move() {
        this.setX(baum.getX() + baum.getBreite());
        setZeichenBereich();
    }
}
