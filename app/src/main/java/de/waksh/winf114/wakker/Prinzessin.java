package de.waksh.winf114.wakker;

import java.util.Random;

/**
 * Created by Matzef on 03.06.2015.
 */
public class Prinzessin extends Hindernis {
    private long start;
    boolean iscarried;
    boolean aktiv;
    Baum baum;

    public Prinzessin(Baum baum, int farbe){
        super(  FP.erweiterteSpielFlaeche.right - FP.objektPixelBreite,
                FP.lanePixelHoehe * 4 + FP.lanePadding,
                FP.objektPixelBreite,
                FP.objektPixelHoehe,
                0, farbe);
        iscarried = false;
        aktiv = false;
        this.baum = baum;
    }

    void erscheintDiePrinzessin(){
        if (!iscarried){
            if (System.currentTimeMillis() > start + 1000 && !baum.kollidiertMit(FP.spielFlaeche)){
                Random r = new Random();
                int random = r.nextInt(100) + 1;
                if (random < SpielWerte.PRINZESSIN_ERSCHEINT_CHANCE){
                    erscheint();
                }
                start = System.currentTimeMillis();
            }
        }
    }

    void erscheint(){
        aktiv = true;
        getZeichenStift().setColor(Farbe.prinzessin);
    }

    void verschwindet(){
        aktiv = false;
        getZeichenStift().setColor(Farbe.transparent);
    }

    public void move(){
        if (aktiv && !iscarried){
            this.setX(baum.getX() + FP.objektPixelBreite);
            setZeichenBereich();
        }
    }
}
