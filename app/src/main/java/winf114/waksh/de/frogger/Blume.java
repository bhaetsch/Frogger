package winf114.waksh.de.frogger;

import java.util.Random;

/**
 * Created by Matzef on 06.06.2015.
 */
public class Blume extends Hindernis {

    private long start;
    boolean aktiv;
    Ziel ziel01;
    Ziel ziel02;
    Ziel ziel03;
    Ziel ziel04;
    Ziel ziel05;

    public Blume(Ziel ziel01, Ziel ziel02, Ziel ziel03, Ziel ziel04, Ziel ziel05) {
        super(0, 0, FP.objektPixelBreite, FP.objektPixelHoehe, 0, Farbe.transparent);
        this.ziel01 = ziel01;
        this.ziel02 = ziel02;
        this.ziel03 = ziel03;
        this.ziel04 = ziel04;
        this.ziel05 = ziel05;
        aktiv = false;
    }

    /* Lässt die Blume zufällig erscheinen */
    void erscheintDieBlume() {
        if (!aktiv) {
            if (System.currentTimeMillis() > start + 1000) {
                Random r = new Random();
                int random = r.nextInt(100) + 1;
                if (random < 11) {
                    int random2 = r.nextInt(5) + 1;
                    switch (random2) {
                        case 1:
                            if (!ziel01.isBesetzt()) {
                                setX(ziel01.getX());
                                setZeichenBereich();
                                erscheint();
                            }
                            break;
                        case 2:
                            if (!ziel02.isBesetzt()) {
                                setX(ziel02.getX());
                                setZeichenBereich();
                                erscheint();
                            }
                            break;
                        case 3:
                            if (!ziel03.isBesetzt()) {
                                setX(ziel03.getX());
                                setZeichenBereich();
                                erscheint();
                            }
                            break;
                        case 4:
                            if (!ziel04.isBesetzt()) {
                                setX(ziel04.getX());
                                setZeichenBereich();
                                erscheint();
                            }
                            break;
                        case 5:
                            if (!ziel05.isBesetzt()) {
                                setX(ziel05.getX());
                                setZeichenBereich();
                                erscheint();
                            }
                            break;
                    }
                }
                start = System.currentTimeMillis();
            }
        }
    }

    void erscheint() {
        aktiv = true;
        getZeichenStift().setColor(Farbe.blume);
    }

    void verschwindet() {
        aktiv = false;
        getZeichenStift().setColor(Farbe.transparent);
    }

    public void move() {
        /* Die Blume bewegt sich nicht */
    }
}
