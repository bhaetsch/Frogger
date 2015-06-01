package winf114.waksh.de.frogger;

import android.graphics.Rect;

/**
 * Created by Matzef on 01.06.2015.
 */
public class ToterFrosch extends Spielobjekt {

    private int farbe;

    public ToterFrosch(int x, int y, int breite, int hoehe, int farbe) {
        super(x, y, breite, hoehe, farbe);
        // dieser Konstruktor wird nicht direkt aufgerufen
    }

    public ToterFrosch(int farbe){
        super(0, 0, 0, 0, farbe);
        this.farbe = farbe;
    }

    public void anzeigen(Rect todesOrt){
        getZeichenStift().setColor(farbe); //zeichnet die im Konstruktor übergebene Farbe
        getZeichenBereich().set(todesOrt); //setzt den toten Frosch an die alte Frosch Position
    }

    public void verstecken(){
        getZeichenStift().setColor(Farbe.transparent);
    }

    public void move(){
       //Bewegt sich nicht
    }
}
