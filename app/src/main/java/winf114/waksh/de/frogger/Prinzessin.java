package winf114.waksh.de.frogger;

import android.graphics.Rect;

/**
 * Created by Matzef on 03.06.2015.
 */
public class Prinzessin extends Spielobjekt {

    private int farbe;

    public Prinzessin(int farbe){
        super(0, 0, 0, 0, farbe);
        this.farbe = farbe;
    }

    void versetzen(Rect position){
        getZeichenBereich().set(position); //setzt den toten Frosch an die alte Frosch Position
    }

    void aufStart(){
        Rect startPosiPrinzessin = new Rect(FP.spielFlaeche.centerX(), FP.lanePixelHoehe * 4 + FP.lanePadding, FP.spielFlaeche.centerX() + FP.objektPixelBreite, FP.lanePixelHoehe * 4 + FP.lanePadding + FP.objektPixelHoehe);
        this.erscheint(startPosiPrinzessin);

    }

    void erscheint(Rect position){
        getZeichenStift().setColor(farbe); //zeichnet die im Konstruktor übergebene Farbe
        getZeichenBereich().set(position); //setzt den toten Frosch an die alte Frosch Position
    }

    void verschwindet(){
        getZeichenStift().setColor(Farbe.transparent);
    }

    void move(){
        //Bewegt sich nicht
    }


}
