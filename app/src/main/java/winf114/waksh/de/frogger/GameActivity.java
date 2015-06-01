package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.SurfaceView;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.graphics.Paint;
import java.util.ArrayList;

public class GameActivity extends Activity implements SurfaceHolder.Callback {

    //TODO memo to self: renderpausen: logcat:GC_FOR_ALLOC freed 7576K, 32% free 17140K/25000K, paused 81ms, total 90ms

    private final int LANE_HOEHE_PROZENT = 5;       //Höhe einer "Lane" im Spiel in % des Screens
    private final int OBJEKT_HOEHE_PROZENT = 80;    //Höhe des Objekts in % der Lane Hoehe

    //Spielfeld Variablen
    protected int lanePixelHoehe;                    //Höhe einer "Lane" im Spiel in Pixeln
    protected Rect spielFlaeche;                    //Bewegungsbereich des Frosches
    protected Rect erweiterteSpielFlaeche;          //erweiterter Bewegungsbereich für die Hindernisse
    private int lanePadding;                        //zentriert die Objekte in den Lanes
    private int objektPixelHoehe;                   //Höhe der Objekt (eg.Frosch) im Spiel in Pixeln
    private int objektPixelBreite;                  //Basis-Breite der Spielobjekte in Pixeln
    protected int froschGeschwX;                    //standard Geschwindigkeit Frosch
    protected int froschGeschwY;
    protected int startPositionX;                   //Startposition Frosch
    protected int startPositionY;
    private int smallTextSize;                      //Textgrößen
    private int largeTextSize;

    // private Farbe farbe;                            //stellt eigene Farben als Felder bereit

    protected int punkte;                           //die Punkte des aktuellen durchlaufs

    //die Spielobjekte und ihre Liste
    protected ArrayList<Spielobjekt> spielobjekte;
    protected LebensAnzeige lebensAnzeige;
    protected ToterFrosch toterFrosch;
    protected Frosch frosch;
    private Hindernis auto01;
    private Hindernis auto02;
    private Hindernis auto03;
    private Hindernis auto04;
    private Hindernis auto05;
    private Hindernis auto06;
    private Hindernis auto07;
    private Hindernis baum01;
    private Hindernis baum02;
    private Hindernis baum03;
    private Hindernis baum04;
    private Hindernis baum05;
    private Hindernis baum06;
    private Hindernis baum07;
    private Hindernis baum08;
    private Hindernis baum09;
    private Hindernis baum10;
    private Hindernis baum11;
    private Hindernis baum12;
    private Hindernis baum13;
    private Ziel ziel01;
    private Ziel ziel02;
    private Ziel ziel03;
    private Ziel ziel04;
    private Ziel ziel05;

    private ZeitMessung renderCycleMessung;

    //Textausgabe, Stift und Hintergrund
    private Paint textStift;
    protected String testText;
    private Hintergrund hintergrund;

    //SurfaceView und MainThread
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MainThread mainThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // wird automatisch beim erzeugen der Activity aufgerufen

        Log.d("GameActivity", "onCreate");
        super.onCreate(savedInstanceState);  //TODO kann man hiermit das Game pausieren und wieder laden!?

        //surfaceView als Spieloberfläche, surfaceHolder ist ein abstraktes interface für die Oberflächennutzung
        setContentView(R.layout.activity_game);
        aktiviereImmersiveFullscreen();
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        renderCycleMessung = new ZeitMessung();

        // start mit 0 Punkten
        punkte = 0;

        //die Knöpfe müssen nach dem Thread erstellt werden!
        mainThread = new MainThread(surfaceHolder, this);
        programmiereKnöpfe();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //wird automatisch aufgerufen sobald das SurfaceView erstellt wird

        Log.d("GameActivity", "surfaceCreated");
        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //wird automatisch aufgerufen sobald das SurfaceView auf Größe gezogen wird

        Log.d("GameActivity", "surfaceChanged");
        erstelleSpielParameter(width, height);
        hintergrund = new Hintergrund(width, lanePixelHoehe);

        //Stift und Farbe und für die Textanzeigen
        testText = height + ":" + width + ":" + objektPixelBreite + ":" + froschGeschwX;
        textStift = new Paint();
        textStift.setColor(Farbe.text);

        toterFrosch = new ToterFrosch(Farbe.deadFrosch);
        lebensAnzeige = new LebensAnzeige(startPositionX + (objektPixelBreite / 2), lanePixelHoehe * 13 + (objektPixelBreite * 60 / 100), objektPixelBreite * 60 / 100, objektPixelHoehe * 60 / 100, Farbe.frosch);

        //<editor-fold erstellt alle Hindernisse,die Ziele und die Objektliste>
        spielobjekte = new ArrayList<>();

        // LANE 2
        int hindernisBreite = objektPixelBreite * 3;
        int hindernisGeschw = 4;
        int lanePositionY = lanePixelHoehe + lanePadding;
        spielobjekte.add(baum01 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 7, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));
        spielobjekte.add(baum08 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 14, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));
        spielobjekte.add(baum09 = new Hindernis(erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, objektPixelHoehe, 4, Farbe.baum, this));

        //LANE 3
        hindernisBreite = objektPixelBreite * 6;
        hindernisGeschw = -2;
        lanePositionY = lanePixelHoehe * 2 + lanePadding;
        spielobjekte.add(baum02 = new Hindernis(erweiterteSpielFlaeche.right + objektPixelBreite * 3, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));

        //LANE 4
        hindernisBreite = objektPixelBreite * 4;
        hindernisGeschw = 3;
        lanePositionY = lanePixelHoehe * 3 + lanePadding;
        spielobjekte.add(baum03 = new Hindernis(erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, objektPixelHoehe, 3, Farbe.baum, this));
        spielobjekte.add(baum12 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 8, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));
        spielobjekte.add(baum13 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 16, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));

        //LANE 5
        hindernisBreite = objektPixelBreite * 5;
        hindernisGeschw = -2;
        lanePositionY = lanePixelHoehe * 4 + lanePadding;
        spielobjekte.add(baum04 = new Hindernis(erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));
        spielobjekte.add(baum10 = new Hindernis(erweiterteSpielFlaeche.right + objektPixelBreite * 10, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));

        //LANE 6
        hindernisBreite = objektPixelBreite * 3;
        hindernisGeschw = 2;
        lanePositionY = lanePixelHoehe * 5 + lanePadding;
        spielobjekte.add(baum05 = new Hindernis(erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));
        spielobjekte.add(baum06 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 7, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));
        spielobjekte.add(baum07 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 14, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.baum, this));

        //LANE 8
        hindernisBreite = objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = lanePixelHoehe * 7 + lanePadding;
        spielobjekte.add(auto01 = new Hindernis(erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));

        //LANE 9
        hindernisBreite = objektPixelBreite * 4;
        hindernisGeschw = -2;
        lanePositionY = lanePixelHoehe * 8 + lanePadding;
        spielobjekte.add(auto02 = new Hindernis(erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));

        //LANE 10
        hindernisBreite = objektPixelBreite * 3;
        hindernisGeschw = 3;
        lanePositionY = lanePixelHoehe * 9 + lanePadding;
        spielobjekte.add(auto03 = new Hindernis(erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));

        //LANE 11
        hindernisBreite = objektPixelBreite * 2;
        hindernisGeschw = -5;
        lanePositionY = lanePixelHoehe * 10 + lanePadding;
        spielobjekte.add(auto04 = new Hindernis(erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));
        spielobjekte.add(auto07 = new Hindernis(erweiterteSpielFlaeche.right + objektPixelBreite * 6, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));

        //LANE 12
        hindernisBreite = objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = lanePixelHoehe * 11 + lanePadding;
        spielobjekte.add(auto05 = new Hindernis(erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));
        spielobjekte.add(auto06 = new Hindernis(erweiterteSpielFlaeche.left + objektPixelBreite * 5, lanePositionY, hindernisBreite, objektPixelHoehe, hindernisGeschw, Farbe.auto, this));


        //LANE 1 Ziele
        spielobjekte.add(ziel01 = new Ziel(startPositionX, 0, objektPixelBreite, lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel02 = new Ziel(startPositionX + (3 * objektPixelBreite), 0, objektPixelBreite, lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel03 = new Ziel(startPositionX - (3 * objektPixelBreite), 0, objektPixelBreite, lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel04 = new Ziel(startPositionX + (6 * objektPixelBreite), 0, objektPixelBreite, lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel05 = new Ziel(startPositionX - (6 * objektPixelBreite), 0, objektPixelBreite, lanePixelHoehe, Farbe.zielLeer));
        //</editor-fold>

        //Frosch
        spielobjekte.add(frosch = new Frosch(startPositionX, startPositionY, objektPixelBreite, objektPixelHoehe, froschGeschwY, froschGeschwX, Farbe.frosch, this));
        frosch.levelStartZeitpunkt = System.currentTimeMillis();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //wird automatisch aufgerufen bevor das SurfaceView zerstört wird

        Log.d("GameActivity", "surfaceDestroyed");
        boolean retry = true;
        while (retry) {
            try {
                mainThread.join(); //blockt die GameActivity bis der MainThread tot ist
                retry = false;
            } catch (InterruptedException e) {
                // solange versuchen bis der Thread tot ist
            }
        }
    }

    private void erstelleSpielParameter(int width, int height) {
        // berechnet Objektgrößen und Startpositionen in Abhängigkeit von der Spielfeldgröße

        lanePixelHoehe = height * LANE_HOEHE_PROZENT / 100;                             //Höhe einer Lane
        objektPixelHoehe = lanePixelHoehe * OBJEKT_HOEHE_PROZENT / 100;                 //Höhe aller Objekte
        lanePadding = (lanePixelHoehe - objektPixelHoehe) / 2;                          //zentriert die Obj in den Lanes
        spielFlaeche = new Rect(0, 0, width, height * LANE_HOEHE_PROZENT / 100 * 13);          //Bewegungsbereich des Frosches
        objektPixelBreite = width / 15;                                                 //Basis-Breite für die Objekte
        froschGeschwX = objektPixelBreite;                                              //Frosch standard Geschwindigkeit
        froschGeschwY = lanePixelHoehe;
        startPositionX = width / 2 - (objektPixelBreite / 2);                           //Startposition des Frosches
        startPositionY = lanePixelHoehe * 12 + lanePadding;
        smallTextSize = lanePixelHoehe / 3;                                             //Textgrößen
        largeTextSize = objektPixelHoehe;

        //Hindernisse bewegen sich ausserhalb des sichtbaren Bereichs in der erweiterten Spielfläche weiter
        erweiterteSpielFlaeche = new Rect(spielFlaeche.left - objektPixelBreite * 8, spielFlaeche.top, spielFlaeche.right + objektPixelBreite * 8, spielFlaeche.bottom);
    }

    @Override
    public void onPause() {
        //erklärt duch activity_lifecycle.png

        Log.d("GameActivity", "onPause");
        super.onPause();
        mainThread.setRunning(false);
        this.finish();
    }

    private void aktiviereImmersiveFullscreen() {
        //versteckt ober Statusleiste und untere virtuelle Knöpfe

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void programmiereKnöpfe(){
        //programmiert die Knöpfe, die den Frosch steuern

        Button linksButton = (Button) findViewById(R.id.links);
        linksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                frosch.setMoved();
                frosch.setRichtung(richtung.links);
            }
        });

        Button rechtsButton = (Button) findViewById(R.id.rechts);
        rechtsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                frosch.setMoved();
                frosch.setRichtung(richtung.rechts);
            }
        });

        Button untenButton = (Button) findViewById(R.id.unten);
        untenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                frosch.setMoved();
                frosch.setRichtung(richtung.zurueck);
            }
        });

        Button obenButton = (Button) findViewById(R.id.oben);
        obenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                punkte += 10;
                frosch.setMoved();
                frosch.setRichtung(richtung.vor);
            }
        });
    }

    protected void onDraw(Canvas canvas) { //RenderRoutine wird vom MainThread aufgerufen

        renderCycleMessung.start();
        renderGame(canvas);
        renderCycleMessung.end();

    }

    private void renderGame(Canvas canvas) {

        //schwarz und dann Hintergrund u. Lebensanzeige
        canvas.drawColor(Color.BLACK);
        hintergrund.draw(canvas);
        lebensAnzeige.draw(canvas);

        //alle Spielobjekte malen
        for (Spielobjekt s : spielobjekte) {
            s.draw(canvas);
        }

        //Toter Frosch wird nur zeitweise angezeigt
        toterFrosch.draw(canvas);

        //Punkteanzeige und 4 Textfelder(positioniert) als Kontrollanzeige
        textStift.setTextSize(smallTextSize);
        canvas.drawText("GCmax|avg: " + mainThread.gameCycleMessung + " (ms)", 10, lanePixelHoehe * 15, textStift);
        canvas.drawText("RCmax|avg: " + renderCycleMessung + " (ms)", 10, lanePixelHoehe * 15 - (lanePixelHoehe / 2), textStift);
        canvas.drawText(mainThread.levelZeit, startPositionX + (objektPixelBreite / 2), lanePixelHoehe * 15 - (lanePixelHoehe / 2), textStift);
        canvas.drawText(testText, startPositionX + (objektPixelBreite / 2), lanePixelHoehe * 15, textStift);
        textStift.setTextSize(largeTextSize);
        canvas.drawText("Punkte: " + punkte, 10, lanePixelHoehe * 14, textStift);
    }
}
