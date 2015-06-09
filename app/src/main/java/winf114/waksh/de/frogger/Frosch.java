package winf114.waksh.de.frogger;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

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
        if (!usePlayServices) { // Highscore-Handler nur anlegen, wenn Google Play Services nicht genutzt werden
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
                SpielWerte.addScore(10);
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
        if (gameActivity.prinzessin.iscarried) {
            SpielWerte.addScore(200);
        }
        if (hatBlume) {
            SpielWerte.addScore(200);
            SpielWerte.setTextAnzeige("Blume gepflückt");
            hatBlume = false;
            gameActivity.blume.verschwindet();
        } else {
            SpielWerte.addScore(100);
        }
        resetFrosch();
    }

    public void stirbt() {
        gameActivity.toterFrosch.anzeigen(getZeichenBereich());
        gameActivity.lebensAnzeige.lebenVerlieren();
        if (gameActivity.lebensAnzeige.keineLebenMehr()) {
            resetZiele();
            /* Prüfen, ob Google Play Services oder lokaler Highscore genutzt wird */
            if (gameActivity.usePlayServices && gameActivity.mGoogleApiClient != null && gameActivity.mGoogleApiClient.isConnected()) {
                /* Highscore von Google Play Services laden */
                Games.Leaderboards.loadTopScores(gameActivity.mGoogleApiClient, FP.HIGHSCORE_ID, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC, 1, true).setResultCallback(new ResultCallback<Leaderboards.LoadScoresResult>() {
                    @Override
                    public void onResult(final Leaderboards.LoadScoresResult scoreResult) {
                        if (scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScores() != null) {
                            /* Prüfen, ob aktueller Score > Highscore bei Google Play Services */
                            if (scoreResult.getScores().get(0).getRawScore() < SpielWerte.getPunkte()) {
                                /* Toast auf dem UI-Thread starten, um den User über neuen Highscore zu informieren */
                                gameActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Context context = gameActivity.getApplicationContext();
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context, "Neuer Highscore!", duration);
                                        toast.setGravity(Gravity.TOP, 0, 0);
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }
                });
                /* aktuellen Score an Google Play Services übertragen */
                Games.Leaderboards.submitScore(gameActivity.mGoogleApiClient, FP.HIGHSCORE_ID, SpielWerte.getPunkte());
            } else {
                /* Highscore-Handler erstellen, falls noch nicht erstellt */
                if (highscore == null) {
                    this.highscore = new Highscore(gameActivity);
                }
                /* Prüfen, ob aktueller Score > lokaler Highscore */
                if (highscore.getHighscore().get(0).getScore() < SpielWerte.getPunkte()) {
                    /* Toast auf dem UI-Thread starten, um den User über neuen Highscore zu informieren */
                    gameActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Context context = gameActivity.getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, "Neuer Highscore!", duration);
                            toast.setGravity(Gravity.TOP, 0, 0);
                            toast.show();
                        }
                    });
                }
                /* Schreiben des aktuellen Scores in den lokalen Speicher initiieren */
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