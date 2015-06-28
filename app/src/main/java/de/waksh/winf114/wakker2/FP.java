package de.waksh.winf114.wakker2;

import android.graphics.Rect;

/**
 * Created by Matzef on 02.06.2015.
 * FP stellt alle "Frogger Parameter" zur Verfügung, paket-privat
 * da die Felder fast nur als Parameter verwendet werden ist der Klassenname so kurz
 */
final class FP {
    /* Konstanten zur Berechnug der Dimensionen abhängig von der Bildschirmgröße */
    private static final int LANE_HOEHE_PROZENT = 6;
    private static final int OBJEKT_HOEHE_PROZENT = 80;
    private static final int LEBENSANZEIGE_GROESSE_PROZENT = 60;

    /* Parameter der Spielfläche */
    static Rect spielFlaeche;
    static Rect erweiterteSpielFlaeche;
    static int lanePixelHoehe;
    static int lanePadding;

    /* Dimensionen der Objekte */
    static int objektPixelHoehe;
    static int objektPixelBreite;

    /* Standard-Geschwindigkeit des Frosches */
    static int froschGeschwX;
    static int froschGeschwY;

    /* Startposition des Frosches */
    static int startPositionX;
    static int startPositionY;

    /* Textgrößen */
    static int smallTextSize;
    static int largeTextSize;

    /* Parameter Lebensanzeige */
    static int lebensAnzeigeX;
    static int lebensAnzeigeY;
    static int lebensAnzeigeHöhe;
    static int lebensAnzeigeBreite;
    static int lebensAnzeigeAbstand;

    /* Parameter Zeitanzeige */
    static int zeitAnzeigeX;
    static int zeitAnzeigeY;
    static int zeitAnzeigeHöhe;
    static int zeitAnzeigeBreite;

    /* Parameter Schlange */
    static int schlangenBreite;
    static int schlangenHoehe;
    static int schlangenPadding;
    static Rect schlangenFlaeche;

    static void erstelleSpielParameter(int spielFeldbreite, int spielFeldHoehe) {
        /* Spielfläche erstellen (Bewegungsbereich des Frosches) */
        spielFlaeche = new Rect(0, 0, spielFeldbreite, spielFeldHoehe * LANE_HOEHE_PROZENT / 100 * 13);

        /* Dimensionen der verschiedenen Objekte festlegen */
        lanePixelHoehe = spielFeldHoehe * LANE_HOEHE_PROZENT / 100;
        objektPixelHoehe = lanePixelHoehe * OBJEKT_HOEHE_PROZENT / 100;
        objektPixelBreite = spielFeldbreite / 15;

        /* Objekte in den Lanes zentrieren */
        lanePadding = (lanePixelHoehe - objektPixelHoehe) / 2;

        /* Geschwindigkeit des Frosches festlegen */
        froschGeschwX = objektPixelBreite;
        froschGeschwY = lanePixelHoehe;

        /* Startpositoin des Frosches festlegen */
        startPositionX = spielFeldbreite / 2 - (objektPixelBreite / 2);
        startPositionY = lanePixelHoehe * 12 + lanePadding;

        /* Textgrößen festlegen */
        smallTextSize = lanePixelHoehe / 3;
        largeTextSize = objektPixelHoehe;

        /* Parameter für die erweiterte Spielfläche festlegen
           (Hindernisse bewegen sich ausserhalb des sichtbaren Bereichs in der erweiterten Spielfläche weiter) */
        erweiterteSpielFlaeche = new Rect(spielFlaeche.left - objektPixelBreite * 8, spielFlaeche.top, spielFlaeche.right + objektPixelBreite * 8, spielFlaeche.bottom);

        /* Parameter für die Lebensanzeige festlegen */
        lebensAnzeigeX = spielFlaeche.centerX();
        lebensAnzeigeY = lanePixelHoehe * 14 + (FP.objektPixelBreite * LEBENSANZEIGE_GROESSE_PROZENT / 100);
        lebensAnzeigeHöhe = objektPixelBreite * LEBENSANZEIGE_GROESSE_PROZENT / 100;
        lebensAnzeigeBreite = objektPixelHoehe * LEBENSANZEIGE_GROESSE_PROZENT / 100;
        lebensAnzeigeAbstand = lebensAnzeigeBreite + (lebensAnzeigeBreite / 2);

        /* Parameter für die Zeitanzeige festlegen */
        zeitAnzeigeX = spielFlaeche.centerX();
        zeitAnzeigeY = lanePixelHoehe * 13 + (FP.objektPixelHoehe * LEBENSANZEIGE_GROESSE_PROZENT / 100);
        zeitAnzeigeBreite = (spielFlaeche.right / 2) * 80 / 100;
        zeitAnzeigeHöhe = objektPixelHoehe * LEBENSANZEIGE_GROESSE_PROZENT / 100;

        /* Parameter für die Schlange festlegen */
        schlangenBreite = objektPixelBreite * 2;
        schlangenHoehe = objektPixelHoehe * 40 / 100;
        schlangenPadding = (lanePixelHoehe - schlangenHoehe) / 2;
        schlangenFlaeche = new Rect(spielFlaeche.left - schlangenBreite, 0, spielFlaeche.right + schlangenBreite, spielFlaeche.bottom);
    }
}
