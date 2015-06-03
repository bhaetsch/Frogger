package winf114.waksh.de.frogger;

import java.util.Date;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class Frosch extends Spielobjekt {

    Highscore highscore;

    private final int geschwindigkeitVertikal;
    int geschwindigkeitHorizontal;
    private boolean moved;
    private richtung r;
    private final GameActivity gameActivity;
    boolean imWasser;
    boolean hitTree;
    boolean imZiel;
    long todesZeitpunkt;
    boolean kuerzlichVerendet;
    private boolean aufStartPosition;
    boolean traegtPrinzessin;

    public Frosch(int x, int y, int breite, int hoehe, int geschwindigkeitVertikal, int geschwindigkeitHorizontal, int farbe, GameActivity gameActivity) {
        super(x, y, breite, hoehe, farbe);

        this.gameActivity = gameActivity;

        this.highscore = new Highscore(gameActivity);

        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
        this.geschwindigkeitVertikal = geschwindigkeitVertikal;
        moved = false;
        hitTree = false;
        imWasser = false;
        imZiel = false;
        kuerzlichVerendet = false;
        aufStartPosition = true;
        traegtPrinzessin = false;

    }

    public void move() {
        if (hitTree) {
            this.setX(this.getX() + geschwindigkeitHorizontal);
            setZeichenBereich();
        }
        if (moved) {
            geschwindigkeitHorizontal = FP.froschGeschwX;
            move(r);
        }
    }

    private void move(richtung r) {
        if(!kuerzlichVerendet) {
            switch (r) {
                case vor:
                    this.setY(this.getY() - geschwindigkeitVertikal);
                    if (this.getY() < FP.lanePixelHoehe * 6) {
                        imWasser = true;
                    }
                    aufStartPosition = false;
                    SpielWerte.changePunkte(10);
                    break;
                case zurueck:
                    if (!aufStartPosition) {
                        this.setY(this.getY() + geschwindigkeitVertikal);
                        if (this.getY() > FP.lanePixelHoehe * 6) {
                            imWasser = false;
                            hitTree = false;
                        }
                        aufStartPosition = false;
                    }
                    break;
                case links:
                    this.setX(this.getX() - geschwindigkeitHorizontal);
                    aufStartPosition = false;
                    break;
                case rechts:
                    this.setX(this.getX() + geschwindigkeitHorizontal);
                    aufStartPosition = false;
                    break;
            }
            setZeichenBereich();
        }
        moved = false;
    }

    public void erreichtZiel() {
        imZiel = true;
        SpielWerte.changePunkte(100);
        resetFrosch();
    }

    public void stirbt() {
        traegtPrinzessin = false;
        todesZeitpunkt = System.currentTimeMillis();
        gameActivity.prinzessin.aufStart();
        kuerzlichVerendet = true;
        gameActivity.lebensAnzeige.lebenVerlieren();
        if(gameActivity.lebensAnzeige.keineLebenMehr()){
            resetZiele();
            highscore.compareScore(new HighscoreEintrag(SpielWerte.getPunkte(), new Date().getTime()));
            SpielWerte.resetPunkte();
        }
        gameActivity.toterFrosch.versetzen(getZeichenBereich());
        resetFrosch();

    }

    private void resetFrosch() {
        SpielWerte.startLevel();
        gameActivity.zeitAnzeige.resetZeitanzeige();
        geschwindigkeitHorizontal = FP.froschGeschwX;
        hitTree = false;
        imWasser = false;
        aufStartPosition = true;
        setX(FP.startPositionX);
        setY(FP.startPositionY);
        setZeichenBereich();
    }

    public void setGeschwindigkeitHorizontal(int geschwindigkeitHorizontal) {
        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
    }

    public void setMoved() {
        moved = true;
    }

    public void setRichtung(richtung r) {
        this.r = r;
    }

    public void resetZiele(){

        for (Spielobjekt s : gameActivity.spielobjekte){
            if (s instanceof Ziel){
                ((Ziel) s).setBesetzt(false);
            }
        }
    }
}