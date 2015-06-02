package winf114.waksh.de.frogger;

import android.graphics.Rect;

/**
 * Created by Matzef on 02.06.2015.
 * FP stellt alle "Frogger Parameter" zur Verfügung, paket-privat
 * da die Felder fast nur als Parameter verwendet werden ist der Klassenname so kurz
 */

final class FP {

    private static final int LANE_HOEHE_PROZENT = 5;        //Höhe einer "Lane" im Spiel in % des Screens
    private static final int OBJEKT_HOEHE_PROZENT = 80;     //Höhe des Objekts in % der Lane Hoehe

    static int lanePixelHoehe;                              //Höhe einer "Lane" im Spiel in Pixeln
    static Rect spielFlaeche;                               //Bewegungsbereich des Frosches
    static Rect erweiterteSpielFlaeche;                     //erweiterter Bewegungsbereich für die Hindernisse
    static int lanePadding;                                 //zentriert die Objekte in den Lanes
    static int objektPixelHoehe;                            //Höhe der Objekt (eg.Frosch) im Spiel in Pixeln
    static int objektPixelBreite;                           //Basis-Breite der Spielobjekte in Pixeln
    static int froschGeschwX;                               //standard Geschwindigkeit Frosch
    static int froschGeschwY;
    static int startPositionX;                              //Startposition Frosch
    static int startPositionY;
    static int smallTextSize;                               //Textgrößen
    static int largeTextSize;

    static void setParaM(int spielFeldbreite, int spielFeldHoehe){

        spielFlaeche = new Rect(0, 0, spielFeldbreite, spielFeldHoehe * LANE_HOEHE_PROZENT / 100 * 13);          //Bewegungsbereich des Frosches

        lanePixelHoehe = spielFeldHoehe * LANE_HOEHE_PROZENT / 100;                                             //Höhe einer Lane
        objektPixelHoehe = lanePixelHoehe * OBJEKT_HOEHE_PROZENT / 100;                                         //Höhe aller Objekte
        objektPixelBreite = spielFeldbreite / 15;                                                               //Basis-Breite für die Objekte

        lanePadding = (lanePixelHoehe - objektPixelHoehe) / 2;                                                  //zentriert die Obj in den Lanes

        froschGeschwX = objektPixelBreite;                                                                      //Frosch standard Geschwindigkeit
        froschGeschwY = lanePixelHoehe;

        startPositionX = spielFeldbreite / 2 - (objektPixelBreite / 2);                                         //Startposition des Frosches
        startPositionY = lanePixelHoehe * 12 + lanePadding;

        smallTextSize = lanePixelHoehe / 3;                                                                     //Textgrößen
        largeTextSize = objektPixelHoehe;

        //Hindernisse bewegen sich ausserhalb des sichtbaren Bereichs in der erweiterten Spielfläche weiter
        erweiterteSpielFlaeche = new Rect(spielFlaeche.left - objektPixelBreite * 8, spielFlaeche.top, spielFlaeche.right + objektPixelBreite * 8, spielFlaeche.bottom);
    }

}
