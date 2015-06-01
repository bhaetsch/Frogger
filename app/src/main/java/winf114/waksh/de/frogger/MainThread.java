package winf114.waksh.de.frogger;

import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Canvas;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class MainThread extends Thread {

    private volatile boolean running = false;

    private SurfaceHolder surfaceHolder;
    private GameActivity gameActivity;
    private Canvas canvas;

    protected long gameCycleTimeBeginn;
    protected long gameCycleTime;
    protected long gameCycleTimeMax;
    protected long gameCycleTimeAvg;
    protected String gameCycleTimeAvgStr;
    protected long gameCycleTimeSum;
    protected int gameCycles;

    int zieleErreicht;


    public MainThread(SurfaceHolder surfaceHolder, GameActivity gameActivity) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameActivity = gameActivity;
        zieleErreicht = 0;
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

                    // gamecycle messung
                    gameCycleTimeBeginn = System.currentTimeMillis();

                    // 20 cycles wird der tote frosch angezeigt
                    if (gameCycles % 20 == 0) {
                        gameActivity.frosch.istTot = false;
                    }

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

                    // alles bewegen
                    for (Spielobjekt s : gameActivity.spielobjekte) {
                        s.move();
                    }

                    // Kol Spielobjekt mit Rand
                    for (Spielobjekt s : gameActivity.spielobjekte) {
                        if (s instanceof Hindernis) {
                            if (!s.kollidiertMit(gameActivity.erweiterteSpielFlaeche)) {
                                ((Hindernis) s).erscheintWieder();
                            }
                        }
                    }

                    // TODO stirbt bei kontakt mit ziel
                    //Kol Frosch mit Baum wasser
                    if (gameActivity.frosch.imWasser) {
                        gameActivity.frosch.hitTree = false;
                        gameActivity.frosch.setGeschwindigkeitHorizontal(gameActivity.froschGeschwX);
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

                    // Kol Frosch mit Auto !wasser
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

                    // Kol Frosch mit Rand
                    if (!gameActivity.frosch.kollidiertMit(gameActivity.spielFlaeche)) {
                        gameActivity.frosch.sterben();
                    }

                    gameCycles++;
                    if (gameCycleTime > gameCycleTimeMax) {
                        gameCycleTimeMax = gameCycleTime;
                    }
                    gameCycleTime = System.currentTimeMillis() - gameCycleTimeBeginn;
                    gameCycleTimeSum = gameCycleTimeSum + gameCycleTime;
                    if (gameCycles == 20) {
                        gameCycleTimeAvg = gameCycleTimeSum / gameCycles;
                        gameCycleTimeAvgStr = " | " + gameCycleTimeAvg;
                        gameCycles = 0;
                        gameCycleTimeSum = 0;
                    }

                    // draws the canvas
                    this.gameActivity.onDraw(canvas);
                }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } // end finally
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
