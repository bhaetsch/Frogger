package winf114.waksh.de.frogger;

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
        while (running) {
            canvas = null;
            try {
                /* Oberfläche für das Zeichnen blockieren */
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    /* Spielroutine */
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

                    /* alles zeichnen */
                    this.gameActivity.onDraw(canvas);
                }
            } finally {
                /* Bei einer Exception bleibt die Oberfläche nicht in einem inkonsistenten Zustand */
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /* Ist die Zeit abgelaufen? */
    private void levelZuendeCheck() {
        if (SpielWerte.levelZuende()) {
            SpielWerte.setTextAnzeige("Zu lahm!");
            gameActivity.frosch.stirbt();
        }
    }

    /* Kollidiert der Frosch mit der Blume? */
    private void kolFroschMitBlume() {
        if (gameActivity.frosch.kollidiertMit(gameActivity.blume.getZeichenBereich()) && gameActivity.blume.aktiv) {
            gameActivity.frosch.hatBlume = true;
        }
    }

    /* Kollidiert der Frosch mit der Prinzessin? */
    private void kolFroschMitPrinzessin() {
        if (gameActivity.frosch.kollidiertMit(gameActivity.prinzessin.getZeichenBereich()) && gameActivity.prinzessin.aktiv) {
            gameActivity.frosch.pickupPrincess();
        }
    }

    /* Wenn alle 5 Ziele erreicht wurden, wird das Spiel zurückgesetzt */
    private void zieleErreichtCheck() {
        if (zieleErreicht == 5) {
            SpielWerte.addScore(500);
            SpielWerte.levelUp();
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Ziel) {
                    ((Ziel) s).setBesetzt(false);
                    zieleErreicht = 0;
                }
                if (s instanceof Hindernis) {
                    ((Hindernis) s).setGeschwindigkeit((int) (((Hindernis) s).getGeschwindigkeit() * SpielWerte.getGeschwindigkeitsMultiplikator()));
                }
            }
        }
    }

    /* Bewegt alle Spielobjekte */
    private void alleObjekteBewegen() {
        for (Spielobjekt s : gameActivity.spielobjekte) {
            s.move();
        }
        gameActivity.prinzessin.move();
    }

    /* Kollidiert der Frosch mit dem Rand des Spielfelds? */
    private void kolFroschMitRand() {
        if (!gameActivity.frosch.kollidiertMit(FP.spielFlaeche)) {
            SpielWerte.setTextAnzeige("Nicht abhauen!");
            gameActivity.frosch.stirbt();
        }
    }

    /* Kollidiert die Schlange mit dem Rand des Baums? */
    private void kolSchlangeMitRand() {
        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Schlange) {
                if (!((Schlange) s).aufBaum) {
                    if (!s.kollidiertMit(FP.schlangenFlaeche)) {
                        ((Schlange) s).richtungWechseln();
                    }
                }
            }
        }
    }

    /* Kollidiert das Hindernis mit dem Rand des Spielfelds? */
    private void kolHindernisMitRand() {
        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Hindernis) {
                if (!s.kollidiertMit(FP.erweiterteSpielFlaeche)) {
                    ((Hindernis) s).erscheintWieder();
                }
            }
        }
    }

    /* Kollidiert der Frosch mit einer Schlange? */
    private void kolFroschMitSchlange() {
        for (Spielobjekt s : gameActivity.spielobjekte) {
            if (s instanceof Schlange) {
                if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich())) {
                    SpielWerte.setTextAnzeige("SSSsssZZzzz");
                    gameActivity.frosch.stirbt();
                }
            }
        }
    }

    /* Kollidiert der Frosch mit dem Kopf des Krokodils? */
    private void kolFroschMitKrokodilKopf() {
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

    /* Kollidiert der Frosch mit einem Auto? */
    private void kolFroschMitAuto() {
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

    /* Kollidiert der Frosch mit einem Ziel? */
    private void kolFroschMitZiel() {
        if (gameActivity.frosch.imWasser) {
            for (Spielobjekt s : gameActivity.spielobjekte) {
                if (s instanceof Ziel) {
                    /* Ziel nicht besetzt */
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && !((Ziel) s).isBesetzt()) {
                        zieleErreicht++;
                        ((Ziel) s).setBesetzt(true);
                        SpielWerte.setTextAnzeige("Ziel erreicht");
                        gameActivity.frosch.erreichtZiel();
                    }
                    /* Ziel besetzt */
                    if (gameActivity.frosch.kollidiertMit(s.getZeichenBereich()) && ((Ziel) s).isBesetzt()) {
                        SpielWerte.setTextAnzeige("Ziel war besetzt");
                        gameActivity.frosch.stirbt();
                    }
                }
            }
        }
    }

    /* Kollidiert der Frosch mit einem Baum? */
    private void kolFroschMitBaum() {
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


