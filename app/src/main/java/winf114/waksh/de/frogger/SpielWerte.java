package winf114.waksh.de.frogger;

/**
 * Created by Matzef on 03.06.2015.
 */
final class SpielWerte {

    /* Konstanten zur Steuerung des Spielablaufs */
    static final int LEVEL_ZEIT_SEK = 120;
    static final int LEBEN = 7;
    static final int PRINZESSIN_ERSCHEINT_CHANCE = 10;

    private static int punkte;
    private static int punkteAlt;
    private static int level;
    private static long levelStartZeitpunkt;
    private static long zeitImLevelVerbracht;
    private static String text;

    public SpielWerte() {
        punkte = 0;
        level = 0;
        text = "";
        levelStartZeitpunkt = 0;
        zeitImLevelVerbracht = 0;
    }

    static void startLevel() {
        levelStartZeitpunkt = System.currentTimeMillis();
    }

    static void updateLevelZeit(long zeit) {
        zeitImLevelVerbracht = zeit - levelStartZeitpunkt;
    }

    static int getZeitImLevelVerbracht() {
        return (int) (zeitImLevelVerbracht / 1000);
    }

    static boolean levelZuende() {
        return getZeitImLevelVerbracht() > SpielWerte.LEVEL_ZEIT_SEK;
    }

    static String levelZeit() {
        return Integer.toString(LEVEL_ZEIT_SEK - getZeitImLevelVerbracht());
    }

    static void addScore(int differenz) {
        punkte += differenz * getPunkteMultiplikator();
    }

    static void resetScore() {
        punkteAlt = punkte;
        punkte = 0;
        level = 0;
    }

    static String punkte() {
        return Integer.toString(punkte);
    }

    static String level() {
        return Integer.toString(level);
    }

    static void levelUp() {
        level++;
    }

    static float getPunkteMultiplikator() {
        return 1 + (float) level / 10;
    }

    static float getGeschwindigkeitsMultiplikator() {
        return 1 + (float) level / 5;
    }

    static int getPunkte() {
        return punkte;
    }

    static int getPunkteAlt() {
        return punkteAlt;
    }

    static void setTextAnzeige(String text) {
        SpielWerte.text = text;
    }

    static String textAnzeige() {
        return text;
    }

}

