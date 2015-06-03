package winf114.waksh.de.frogger;

/**
 * Created by Matzef on 03.06.2015.
 */
final class SpielWerte {

    private static final int LEVEL_ZEIT_SEK = 45;
    static final int LEBEN = 7;

    private static int punkte;
    private static long levelStartZeitpunkt;
    private static long zeitImLevelVerbracht;
    private static String text;

    public SpielWerte(){
        punkte = 0;
        text = "";
        levelStartZeitpunkt = 0;
        zeitImLevelVerbracht = 0;
    }

    static void startLevel(){
        levelStartZeitpunkt = System.currentTimeMillis();
    }

    static void updateLevelZeit(long zeit){
        zeitImLevelVerbracht = zeit - levelStartZeitpunkt;
    }

    static int getZeitImLevelVerbracht(){
        return (int)zeitImLevelVerbracht;
    }

    static boolean levelZuende(){
        if (zeitImLevelVerbracht > (SpielWerte.LEVEL_ZEIT_SEK * 1000)){
            return true;
        }
       return false;
    }

    static String levelZeit(){
        return "Zeit: " + (LEVEL_ZEIT_SEK - (zeitImLevelVerbracht / 1000));
    }

    static void changePunkte(int differenz){
        punkte += differenz;
    }

    static void resetPunkte(){
        punkte = 0;
    }

    static String punkte(){
        return "Punkte: " + punkte;
    }

    static void setTextAnzeige(String text){
        SpielWerte.text = text;
    }

    static String textAnzeige(){
        return text;
    }

}
