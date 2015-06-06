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
                    SpielWerte.updateLevelZeit(System.currentTimeMillis());
                    gameActivity.zeitAnzeige.tick();
                    levelZuendeCheck();
                    gameActivity.toterFrosch.aktualisieren();
                    gameActivity.prinzessin.erscheintDiePrinzessin();
                    gameActivity.blume.erscheintDieBlume();
                    kolFroschMitPrinzessin();
                    zieleErreichtCheck();
                    alleObjekteBewegen();
                    kolFroschMitSchlange();
                    kolFroschMitKrokodilKopf();
                    kolSchlangeMitRand();
                    kolHindernisMitRand();
                    kolFroschMitBlume();
                    kolFroschMitZiel();
                    kolFroschMitBaum();
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
            SpielWerte.setTextAnzeige("Zu lahm!");
            gameActivity.frosch.stirbt();
        }
    }

    private void kolFroschMitBlume(){
        if(gameActivity.frosch.kollidiertMit(gameActivity.blume.getZeichenBereich()) && gameActivity.blume.aktiv){
            gameActivity.frosch.hatBlume = true;
        }
    }

    private void kolFroschMitPrinzessin(){

        if (gameActivity.frosch.kollidiertMit(gameActivity.prinzessin.getZeichenBereich()) && gameActivity.prinzessin.aktiv) {
            gameActivity.frosch.pickupPrincess();
        }
    }

    private void zieleErreichtCheck(){
        // wenn 5 ziele gefüllt sind wird das spiel zurück gesetzt

        if (zieleErreicht == 5) {
            SpielWerte.addScore(500);
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
        gameActivity.prinzessin.move();
    }

    private void kolFroschMitRand(){
        if (!gameActivity.frosch.kollidiertMit(FP.spielFlaeche)) {
            SpielWerte.setTextAnzeige("Nicht abhauen!");
            gameActivity.frosch.stirbt();
        }
    }

    private void kolSchlangeMitRand() {

        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Schlange) {
                if (!((Schlange) s).aufBaum){
                    if (!s.kollidiertMit(FP.schlangenFlaeche)) {
                        ((Schlange) s).richtungWechseln();
                    }
                }
                else{
                    //((Schlange) s).bewegungAufBaum();
                }
            }
        }
    }

    private void kolHindernisMitRand(){
        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Hindernis) {
                if (!s.kollidiertMit(FP.erweiterteSpielFlaeche)) {
                    ((Hindernis) s).erscheintWieder();
                }
            }

        }
    }

    private void kolFroschMitSchlange(){
        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Schlange) {
                if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                    SpielWerte.setTextAnzeige("SSSsssZZzzz");
                    gameActivity.frosch.stirbt();
                }
            }
        }
    }

    private void kolFroschMitKrokodilKopf(){
        if (gameActivity.frosch.imWasser) {
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof KrokodilKopf) {
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                        SpielWerte.setTextAnzeige("Omnomnom");
                        gameActivity.frosch.stirbt();
                    }
                }
            }
        }
    }

    private void kolFroschMitAuto(){
        if (!gameActivity.frosch.imWasser) {
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Auto) {
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                        SpielWerte.setTextAnzeige("Matsch!");
                        gameActivity.frosch.stirbt();
                    }
                }
            }
        }
    }

    private void kolFroschMitZiel(){
        if (gameActivity.frosch.imWasser) {
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Ziel) {
                    // unbesetzt
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && !((Ziel) s).isBesetzt()) {
                        zieleErreicht++;
                        ((Ziel) s).setBesetzt(true);
                        SpielWerte.setTextAnzeige("Ziel erreicht");
                        gameActivity.frosch.erreichtZiel();
                    }
                    // besetzt
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && ((Ziel) s).isBesetzt()) {
                        SpielWerte.setTextAnzeige("Ziel war besetzt");
                        gameActivity.frosch.stirbt();
                    }
                }
            }
        }
    }

    private void kolFroschMitBaum(){
        if (gameActivity.frosch.imWasser) {
            gameActivity.frosch.aufBaum = false;
            gameActivity.frosch.setGeschwindigkeitHorizontal(FP.froschGeschwX);
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Baum) {
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                        gameActivity.frosch.aufBaum = true;
                        gameActivity.frosch.setGeschwindigkeitHorizontal(((Baum) s).getGeschwindigkeit());
                    }
                }
            }
            if (!gameActivity.frosch.aufBaum) {
                SpielWerte.setTextAnzeige("Blub");
                gameActivity.frosch.stirbt();
            }
        }
    }
}


