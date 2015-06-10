package winf114.waksh.de.frogger;

import android.graphics.Rect;

/**
 * Created by Matzef on 01.06.2015.
 */

class ToterFrosch extends Spielobjekt {
    private long todesZeitpunkt;

    public ToterFrosch(int farbe) {
        super(0, 0, 0, 0, farbe);
    }

    void aktualisieren() {
        if (System.currentTimeMillis() > todesZeitpunkt + 1000) {
            verstecken();
        }
    }

    void anzeigen(Rect position) {
        todesZeitpunkt = System.currentTimeMillis();
        getZeichenStift().setColor(Farbe.deadFrosch); // zeichnet die im Konstruktor übergebene Farbe
        getZeichenBereich().set(position);            //setzt den toten Frosch an die alte Frosch-Position
    }

    void verstecken() {
        getZeichenStift().setColor(Farbe.transparent);
    }

    void move() {
       /* Bewegt sich nicht */
    }
}
