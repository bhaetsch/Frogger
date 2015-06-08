package winf114.waksh.de.frogger;

/**
 * Created by Matzef on 03.06.2015.
 */
final class SpielWerte {

    static final int LEVEL_ZEIT_SEK = 120;
    static final int LEBEN = 7;
    static final int PRINZESSIN_ERSCHEINT_CHANCE = 10;

    private static int punkte;
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
        if (getZeitImLevelVerbracht() > SpielWerte.LEVEL_ZEIT_SEK) {
            return true;
        }
        return false;
    }

    static String levelZeit() {
        return "Zeit " + (LEVEL_ZEIT_SEK - getZeitImLevelVerbracht());
    }

    static void addScore(int differenz) {
        punkte += differenz;
    }

    static void resetScore() {
        punkte = 0;
        level = 0;
    }

    static String punkte() {
        return "Punkte: " + punkte;
    }

    static String level() {
        return "Level: " + level;
    }

    static void levelUp() {
        level++;
    }

    static int getLevelMultiplikator() {
        return 1 + level / 10;
    }

    static int getPunkte() {
        return punkte;
    }

    static void setTextAnzeige(String text) {
        SpielWerte.text = text;
    }

    static String textAnzeige() {
        return text;
    }

}

