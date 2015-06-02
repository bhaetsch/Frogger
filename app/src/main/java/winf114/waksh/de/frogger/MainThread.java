package winf114.waksh.de.frogger;

import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Canvas;

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
    String levelZeit;



    public MainThread(SurfaceHolder surfaceHolder, GameActivity gameActivity) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameActivity = gameActivity;
        zieleErreicht = 0;
        gameCycleMessung = new ZeitMessung();
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
                    levelZuendeCheck();
                    todesAnzeigeCheck();
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
        levelZeit = "Time:"  + (System.currentTimeMillis() - gameActivity.frosch.getLevelStartZeitpunkt())/1000;
        if (System.currentTimeMillis() > gameActivity.frosch.getLevelStartZeitpunkt() + (45*1000)){
            gameActivity.frosch.sterben();
        }
    }

    private void todesAnzeigeCheck(){
        //versteckt den toten Frosch und deaktiviert kurz Froschbewegung

        if (System.currentTimeMillis() > gameActivity.frosch.todesZeitpunkt + 1000) {
            gameActivity.toterFrosch.verstecken();
            gameActivity.frosch.kuerzlichVerendet = false;
        }
    }

    private void zieleErreichtCheck(){
        // wenn 5 ziele gefüllt sind wird das spiel zurück gesetzt

        if (zieleErreicht == 5) {
            gameActivity.punkte += 500;
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
            gameActivity.frosch.sterben();
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
                        gameActivity.testText = "hit car";
                        gameActivity.frosch.sterben();
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
                        gameActivity.frosch.gewinnt();
                    }
                    // besetzt
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && ((Ziel) s).isBesetzt()) {
                        gameActivity.frosch.sterben();
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
                gameActivity.frosch.sterben();
            }
            gameActivity.testText = "Tree? " + gameActivity.frosch.hitTree + " - Speed: " + gameActivity.frosch.geschwindigkeitHorizontal;
        }
        gameActivity.frosch.imZiel = false;
    }

}


