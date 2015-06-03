package winf114.waksh.de.frogger;

import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Canvas;

import java.util.Date;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class MainThread extends Thread {

    private volatile boolean running = false;
    private final SurfaceHolder surfaceHolder;
    private final GameActivity gameActivity;
    private Canvas canvas;

    final ZeitMessung gameCycleMessung;
    private int zieleErreicht;

    Highscore highscore;

    public MainThread(SurfaceHolder surfaceHolder, GameActivity gameActivity) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameActivity = gameActivity;
        zieleErreicht = 0;
        gameCycleMessung = new ZeitMessung();
        this.highscore = new Highscore(gameActivity);
    }

    @Override
    public void run() {
        Log.d("MainThread", "running");
        while (running) {
            canvas = null;
            // try locking the canvas for exclusive pixel editing on the surface
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {

                    gameCycleMessung.start();
                    SpielWerte.updateLevelZeit(System.currentTimeMillis());
                    gameActivity.zeitAnzeige.tick();
                    levelZuendeCheck();
                    todesAnzeigeCheck();
                    prinzessinAnzeigeCheck();
                    prinzessinCheck();
                    zieleErreichtCheck();
                    alleObjekteBewegen();
                    kolHindernisMitRand();
                    kolFroschMitBaumOderZiel();
                    kolFroschMitAuto();
                    kolFroschMitRand();
                    gameCycleMessung.end();

                    this.gameActivity.onDraw(canvas); // alles zeichnen
                }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void levelZuendeCheck(){

        if (SpielWerte.levelZuende()){
            gameActivity.frosch.stirbt();
        }
    }

    private void todesAnzeigeCheck(){
        //versteckt den toten Frosch und deaktiviert kurz Froschbewegung

        if (System.currentTimeMillis() > gameActivity.frosch.todesZeitpunkt + 1000) {
            gameActivity.toterFrosch.verstecken();
            gameActivity.frosch.kuerzlichVerendet = false;
        }
    }

    private void prinzessinAnzeigeCheck(){

    }

    private void prinzessinCheck(){

        if (gameActivity.frosch.kollidiertMit(gameActivity.prinzessin.getZeichenBereich())) {
            gameActivity.frosch.traegtPrinzessin = true;
        }

        if (gameActivity.frosch.traegtPrinzessin) {
            gameActivity.prinzessin.versetzen(gameActivity.frosch.getZeichenBereich());
        }
    }

    private void zieleErreichtCheck(){
        // wenn 5 ziele gefüllt sind wird das spiel zurück gesetzt

        if (zieleErreicht == 5) {
            SpielWerte.changePunkte(500);
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Ziel) {
                    ((Ziel) s).setBesetzt(false);
                    zieleErreicht = 0;
                }
            }
        }
    }

    private void alleObjekteBewegen(){
        for (Spielobjekt s : gameActivity.spielobjekte) {
            s.move();
        }
    }

    private void kolFroschMitRand(){
        // Kollision Frosch mit Rand ?

        if (!gameActivity.frosch.kollidiertMit(FP.spielFlaeche)) {
            SpielWerte.setTextAnzeige("Nicht abhauen!");
            gameActivity.frosch.stirbt();
        }
    }

    private void kolHindernisMitRand(){
        // Kol Spielobjekt mit Rand

        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Hindernis) {
                if (!s.kollidiertMit(FP.erweiterteSpielFlaeche)) {
                    ((Hindernis) s).erscheintWieder();
                }
            }
        }
    }

    private void kolFroschMitAuto(){
        // Kollision Frosch mit Auto ?

        if (!gameActivity.frosch.imWasser) {
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Hindernis) {
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                        SpielWerte.setTextAnzeige("Matsch!");
                        gameActivity.frosch.stirbt();
                    }
                }
            }
        }
    }

    private void kolFroschMitBaumOderZiel(){
        //Kol Frosch mit Baum wasser

        if (gameActivity.frosch.imWasser) {
            gameActivity.frosch.hitTree = false;
            gameActivity.frosch.setGeschwindigkeitHorizontal(FP.froschGeschwX);
            for (Spielobjekt s : gameActivity.spielobjekte) {
                // mit Ziel
                if (s instanceof Ziel) {
                    // unbesetzt
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && !((Ziel) s).isBesetzt()) {
                        zieleErreicht++;
                        ((Ziel) s).setBesetzt(true);
                        gameActivity.frosch.erreichtZiel();
                    }
                    // besetzt
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && ((Ziel) s).isBesetzt()) {
                        gameActivity.frosch.stirbt();
                    }
                }
                if (s instanceof Hindernis) {
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                        gameActivity.frosch.hitTree = true;
                        gameActivity.frosch.setGeschwindigkeitHorizontal(((Hindernis) s).getGeschwindigkeit());
                    }
                }
            }
            if (!gameActivity.frosch.hitTree && !gameActivity.frosch.imZiel) {
                gameActivity.frosch.stirbt();
            }
        }
        gameActivity.frosch.imZiel = false;
    }

}


