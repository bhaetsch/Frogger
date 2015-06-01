package winf114.waksh.de.frogger;

import android.graphics.Color;

/**
 * Created by Matzef on 31.05.2015.
 */
final class Farbe {

    static int zielBereich;
    static int wasserBereich;
    static int pausenBereich;
    static int strassenBereich;
    static int startBereich;
    static int strassenMarkierung;
    static int frosch;
    static int deadFrosch;
    static int auto;
    static int baum;
    static int zielLeer;
    static int zielBesetzt;
    static int text;
    static int transparent;


    public Farbe() {
        zielBereich = Color.parseColor("#3b522e");
        wasserBereich = Color.parseColor("#2e4975");
        pausenBereich = Color.parseColor("#3b522e");
        strassenBereich = Color.parseColor("#7b7b7b");
        startBereich = Color.parseColor("#3b522e");
        strassenMarkierung = Color.parseColor("#ffffff");
        frosch = Color.parseColor("#c4e86a");
        deadFrosch = Color.parseColor("#cfd1a7");
        zielLeer = Color.parseColor("#000000");
        zielBesetzt = Color.parseColor("#c4e86a");
        auto = Color.parseColor("#9b4523");
        baum = Color.parseColor("#79491d");
        text = Color.parseColor("#ffffff");
        transparent = Color.parseColor("#00000000");
    }
}
