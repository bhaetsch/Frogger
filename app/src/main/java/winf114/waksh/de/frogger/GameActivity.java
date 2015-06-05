package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.SurfaceView;
import android.widget.Button;
import android.graphics.Color;
import android.util.Log;
import android.graphics.Paint;
import java.util.ArrayList;

public class GameActivity extends Activity implements SurfaceHolder.Callback {

    //TODO memo to self: renderpausen: logcat:GC_FOR_ALLOC freed 7576K, 32% free 17140K/25000K, paused 81ms, total 90ms
    //TODO bei kollision mit dem rand wird der sterbende frosch ausserhalb des bildes angezeigt

    //<editor-fold | die Spielobjekte und ihre Liste
    ArrayList<Spielobjekt> spielobjekte;
    LebensAnzeige lebensAnzeige;
    ZeitAnzeige zeitAnzeige;
    ToterFrosch toterFrosch;
    Frosch frosch;
    Prinzessin prinzessin;
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
    Hindernis baum04;
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
    //</editor-fold>

    private ZeitMessung renderCycleMessung;

    //Stift und Hintergrund
    private Paint textStift;
    private Hintergrund hintergrund;

    //SurfaceView und MainThread
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MainThread mainThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // wird automatisch beim erzeugen der Activity aufgerufen

        Log.d("GameActivity", "onCreate");
        super.onCreate(savedInstanceState);

        //surfaceView als Spieloberfläche, surfaceHolder ist ein abstraktes interface für die Oberflächennutzung
        setContentView(R.layout.activity_game);
        aktiviereImmersiveFullscreen();
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        renderCycleMessung = new ZeitMessung();

        //die Knöpfe müssen nach dem Thread erstellt werden!
        mainThread = new MainThread(surfaceHolder, this);
        programmiereKnöpfe();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //wird automatisch aufgerufen sobald das SurfaceView erstellt wird

        Log.d("GameActivity", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //wird automatisch aufgerufen sobald das SurfaceView auf Größe gezogen wird

        Log.d("GameActivity", "surfaceChanged");

        FP.erstelleSpielParameter(width, height);
        hintergrund = new Hintergrund(width, FP.lanePixelHoehe);

        //Stift und Farbe und für die Textanzeigen
        SpielWerte.setTextAnzeige(height + ":" + width + ":" + FP.objektPixelBreite + ":" + FP.froschGeschwX);
        textStift = new Paint();
        textStift.setColor(Farbe.text);

        toterFrosch = new ToterFrosch(Farbe.deadFrosch);
        lebensAnzeige = new LebensAnzeige();
        zeitAnzeige = new ZeitAnzeige();

        //<editor-fold | erstellt alle Hindernisse,die Ziele und die Objektliste>
        spielobjekte = new ArrayList<>();

        // LANE 2
        int hindernisBreite = FP.objektPixelBreite * 3;
        int hindernisGeschw = 4;
        int lanePositionY = FP.lanePixelHoehe + FP.lanePadding;
        spielobjekte.add(baum01 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 7, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum08 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 14, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum09 = new Hindernis(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, 4, Farbe.baum));

        //LANE 3
        hindernisBreite = FP.objektPixelBreite * 6;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 2 + FP.lanePadding;
        spielobjekte.add(baum02 = new Hindernis(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 3, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 4
        hindernisBreite = FP.objektPixelBreite * 4;
        hindernisGeschw = 3;
        lanePositionY = FP.lanePixelHoehe * 3 + FP.lanePadding;
        spielobjekte.add(baum03 = new Hindernis(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, 3, Farbe.baum));
        spielobjekte.add(baum12 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 8, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum13 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 16, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 5
        hindernisBreite = FP.objektPixelBreite * 5;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 4 + FP.lanePadding;
        spielobjekte.add(baum04 = new Hindernis(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum10 = new Hindernis(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 10, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 6
        hindernisBreite = FP.objektPixelBreite * 3;
        hindernisGeschw = 2;
        lanePositionY = FP.lanePixelHoehe * 5 + FP.lanePadding;
        spielobjekte.add(baum05 = new Hindernis(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum06 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 7, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum07 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 14, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 8
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = FP.lanePixelHoehe * 7 + FP.lanePadding;
        spielobjekte.add(auto01 = new Hindernis(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 9
        hindernisBreite = FP.objektPixelBreite * 4;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 8 + FP.lanePadding;
        spielobjekte.add(auto02 = new Hindernis(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 10
        hindernisBreite = FP.objektPixelBreite * 3;
        hindernisGeschw = 3;
        lanePositionY = FP.lanePixelHoehe * 9 + FP.lanePadding;
        spielobjekte.add(auto03 = new Hindernis(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 11
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = -5;
        lanePositionY = FP.lanePixelHoehe * 10 + FP.lanePadding;
        spielobjekte.add(auto04 = new Hindernis(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));
        spielobjekte.add(auto07 = new Hindernis(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 6, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 12
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = FP.lanePixelHoehe * 11 + FP.lanePadding;
        spielobjekte.add(auto05 = new Hindernis(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));
        spielobjekte.add(auto06 = new Hindernis(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 5, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));


        //LANE 1 Ziele
        spielobjekte.add(ziel01 = new Ziel(FP.startPositionX, FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel02 = new Ziel(FP.startPositionX + (3 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel03 = new Ziel(FP.startPositionX - (3 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel04 = new Ziel(FP.startPositionX + (6 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel05 = new Ziel(FP.startPositionX - (6 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        //</editor-fold>

        //Frosch
        spielobjekte.add(frosch = new Frosch(FP.startPositionX, FP.startPositionY, FP.objektPixelBreite, FP.objektPixelHoehe, FP.froschGeschwY, FP.froschGeschwX, Farbe.frosch, this));
        prinzessin = new Prinzessin(baum04, Farbe.prinzessin);
        SpielWerte.startLevel();
        mainThread.setRunning(true);
        mainThread.start();
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

    @Override
    public void onPause() {
        //erklärt duch wiki - activity_lifecycle.png

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
                frosch.setMoved();
                frosch.setRichtung(richtung.vor);
            }
        });
    }

    protected void onDraw(Canvas canvas) {
        //RenderRoutine wird vom MainThread aufgerufen

        renderCycleMessung.start();
        renderGame(canvas);
        renderCycleMessung.end();

    }

    private void renderGame(Canvas canvas) {

        //schwarz und dann Hintergrund u. Lebensanzeige
        canvas.drawColor(Color.BLACK);
        hintergrund.draw(canvas);
        lebensAnzeige.draw(canvas);
        zeitAnzeige.draw(canvas);

        //alle Spielobjekte malen
        for (Spielobjekt s : spielobjekte) {
            s.draw(canvas);
        }

        //Toter Frosch wird nur zeitweise angezeigt
        prinzessin.draw(canvas);
        toterFrosch.draw(canvas);

        //Punkteanzeige und 4 Textfelder(positioniert) als Kontrollanzeige
        textStift.setTextSize(FP.smallTextSize);
        canvas.drawText("GCmax|avg: " + mainThread.gameCycleMessung + " (ms)", 10, FP.lanePixelHoehe * 15, textStift);
        canvas.drawText("RCmax|avg: " + renderCycleMessung + " (ms)", 10, FP.lanePixelHoehe * 15 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText(SpielWerte.levelZeit(), FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 14 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText("Leben", FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 15 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText(SpielWerte.textAnzeige(), FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 16, textStift);
        textStift.setTextSize(FP.largeTextSize);
        canvas.drawText(SpielWerte.punkte(), 10, FP.lanePixelHoehe * 14, textStift);
    }
}
