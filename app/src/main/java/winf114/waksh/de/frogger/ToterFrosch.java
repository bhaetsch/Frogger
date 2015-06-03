package winf114.waksh.de.frogger;

import android.graphics.Rect;

/**
 * Created by Matzef on 01.06.2015.
 */

class ToterFrosch extends Spielobjekt {

    private int farbe;

    public ToterFrosch(int farbe){
        super(0, 0, 0, 0, farbe);
        this.farbe = farbe;
    }

    void versetzen(Rect position){
        getZeichenStift().setColor(farbe); //zeichnet die im Konstruktor übergebene Farbe
        getZeichenBereich().set(position); //setzt den toten Frosch an die alte Frosch Position
    }

    void verstecken(){
        getZeichenStift().setColor(Farbe.transparent);
    }

    void move(){
       //Bewegt sich nicht
    }
}
