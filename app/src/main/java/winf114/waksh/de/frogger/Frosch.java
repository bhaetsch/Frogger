package winf114.waksh.de.frogger;

import com.google.android.gms.games.Games;

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
    boolean aufBaum;
    boolean hatBlume;
    private boolean aufStartPosition;

    public Frosch(int x, int y, int breite, int hoehe, int geschwindigkeitVertikal, int geschwindigkeitHorizontal, int farbe, boolean usePlayServices, GameActivity gameActivity) {
        super(x, y, breite, hoehe, farbe);
        this.gameActivity = gameActivity;
        if (!usePlayServices) {
            this.highscore = new Highscore(gameActivity);
        }
        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
        this.geschwindigkeitVertikal = geschwindigkeitVertikal;
        moved = false;
        aufBaum = false;
        imWasser = false;
        hatBlume = false;
        aufStartPosition = true;
    }

    public void move() {
        if (aufBaum) {
            this.setX(this.getX() + geschwindigkeitHorizontal);
            setZeichenBereich();
        }
        if (moved) {
            geschwindigkeitHorizontal = FP.froschGeschwX;
            move(r);
        }
    }

    private void move(richtung r) {
        switch (r) {
            case vor:
                this.setY(this.getY() - geschwindigkeitVertikal);
                if (this.getY() < FP.lanePixelHoehe * 6) {
                    imWasser = true;
                }
                aufStartPosition = false;
                SpielWerte.addScore(10 * SpielWerte.getLevelMultiplikator());
                break;
            case zurueck:
                if (!aufStartPosition) {
                    this.setY(this.getY() + geschwindigkeitVertikal);
                    if (this.getY() > FP.lanePixelHoehe * 6) {
                        imWasser = false;
                        aufBaum = false;
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
        moved = false;
    }

    public void erreichtZiel() {
        // SpielWerte.addScore(SpielWerte.getZeitImLevelVerbracht()/1000);
        if (gameActivity.prinzessin.iscarried) {
            SpielWerte.addScore(200 * SpielWerte.getLevelMultiplikator());
        }
        if (hatBlume) {
            SpielWerte.addScore(200 * SpielWerte.getLevelMultiplikator());
            SpielWerte.setTextAnzeige("Blume gepflückt");
            hatBlume = false;
            gameActivity.blume.verschwindet();
        } else {
            SpielWerte.addScore(100 * SpielWerte.getLevelMultiplikator());
        }
        resetFrosch();
    }

    public void stirbt() {
        gameActivity.toterFrosch.anzeigen(getZeichenBereich());
        gameActivity.lebensAnzeige.lebenVerlieren();
        if (gameActivity.lebensAnzeige.keineLebenMehr()) {
            resetZiele();
            if (gameActivity.usePlayServices && gameActivity.mGoogleApiClient != null && gameActivity.mGoogleApiClient.isConnected()) {
                Games.Leaderboards.submitScore(gameActivity.mGoogleApiClient, "CgkI2-engsYVEAIQAQ", SpielWerte.getPunkte());
            } else {
                if (highscore == null) {
                    this.highscore = new Highscore(gameActivity);
                }
                highscore.startCompareScore(new HighscoreEintrag(SpielWerte.getPunkte(), new Date().getTime()));
            }
            SpielWerte.resetScore();
        }
        resetFrosch();
    }

    private void resetFrosch() {
        SpielWerte.startLevel();
        gameActivity.zeitAnzeige.resetZeitanzeige();
        geschwindigkeitHorizontal = FP.froschGeschwX;
        releasePrinzess();
        aufBaum = false;
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

    public void resetZiele() {
        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Ziel) {
                ((Ziel) s).setBesetzt(false);
            }
        }
    }

    void releasePrinzess() {
        gameActivity.prinzessin.iscarried = false;
        gameActivity.prinzessin.verschwindet();
        getZeichenStift().setColor(Farbe.frosch);
    }

    void pickupPrincess() {
        gameActivity.prinzessin.iscarried = true;
        SpielWerte.setTextAnzeige("Prinzessin eingesammelt");
        getZeichenStift().setColor(Farbe.prinzessin);
        gameActivity.prinzessin.verschwindet();
    }
}