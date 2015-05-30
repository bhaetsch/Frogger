package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Rect;


public class GameActivity extends Activity implements SurfaceHolder.Callback{

    final int LANE_HOEHE_PROZENT = 6;       //Höhe einer "Lane" im Spiel in % des Screens
    final int OBJEKT_HOEHE_PROZENT = 80;    //Höhe des Objekts in % der Lane Hoehe

    private int lanePixelHoehe;             //Höhe einer "Lane" im Spiel in Pixeln
    private int objektPixelHoehe;           //Höhe der Objekt (eg.Frosch) im Spiel in Pixeln
    Rect spielFlaeche;
    int lanePadding;
    int froschbreite;
    int startPositionX;
    int startPositionY;

    private MainThread mainThread;

    Frosch frosch;
    private Hindernis auto;
    private Hindernis lkw;
    private Hindernis auto2;
    private Hindernis krad;
    private Hindernis auto3;

    private Hintergrund hintergrund;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        spielFlaeche = new Rect(0,0,0,0);
        mainThread = new MainThread(surfaceHolder, this);

        // 4 Knöpfe und ein Test-Textfeld

        textView = (TextView) findViewById(R.id.textView1);

        Button linksButton = (Button) findViewById(R.id.links);
        linksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Links");
                frosch.moved = true;
                frosch.r = richtung.links;
            }
        });

        Button rechtsButton = (Button) findViewById(R.id.rechts);
        rechtsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Rechts");
                frosch.moved = true;
                frosch.r = richtung.rechts;
            }
        });

        Button untenButton = (Button) findViewById(R.id.unten);
        untenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Unten");
                frosch.moved = true;
                frosch.r = richtung.zurueck;
            }
        });

        Button obenButton = (Button) findViewById(R.id.oben);
        obenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Oben");
                frosch.moved = true;
                frosch.r = richtung.vor;
            }
        });
    }

    // SurfaceView Methoden
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        erstelleSpielParameter(width,height);

        String breiteSTR = Integer.toString(width);
        String hoeheSTR = Integer.toString(height);

        textView.setText(hoeheSTR + ":" + breiteSTR);

        hintergrund = new Hintergrund(width, lanePixelHoehe);


        // xx = XX(linker rand, lane+(zentriert in lane),breiteObjekt,hoeheObjekt, geschw.)
        auto = new Hindernis(-200, lanePixelHoehe * 7 + lanePadding, 200, objektPixelHoehe, 4, Color.parseColor("#750707"));
        lkw = new Hindernis(-400, lanePixelHoehe * 8 + lanePadding, 400, objektPixelHoehe, 2, Color.parseColor("#750707"));
        auto2 = new Hindernis(-250, lanePixelHoehe * 9 + lanePadding, 250, objektPixelHoehe, 3, Color.parseColor("#750707"));
        krad = new Hindernis(-100, lanePixelHoehe * 10 + lanePadding, 100, objektPixelHoehe, 5, Color.parseColor("#750707"));
        auto3 = new Hindernis(-150, lanePixelHoehe * 11 + lanePadding, 150, objektPixelHoehe, 4, Color.parseColor("#750707"));


        // frosch geschwindigkeit abhaengig von lanehoehe

        frosch = new Frosch(startPositionX, startPositionY, froschbreite , objektPixelHoehe, (lanePixelHoehe - objektPixelHoehe),froschbreite/2, Color.parseColor("#9db426"), this);

        mainThread.spielobjekte.add(frosch);
        mainThread.spielobjekte.add(auto);
        mainThread.spielobjekte.add(lkw);
        mainThread.spielobjekte.add(auto2);
        mainThread.spielobjekte.add(krad);
        mainThread.spielobjekte.add(auto3);


    }

    @Override
    public void onBackPressed() {
        mainThread.setRunning(false);
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        mainThread.setRunning(false);
        super.onPause();
    }

    @Override
    public void onStop(){
        mainThread.setRunning(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mainThread.setRunning(false);
        super.onDestroy();
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        hintergrund.draw(canvas);
        frosch.draw(canvas);
        auto.draw(canvas);
        lkw.draw(canvas);
        auto2.draw(canvas);
        krad.draw(canvas);
        auto3.draw(canvas);


    }

    public Rect getSpielFlaeche(){
        return spielFlaeche;
    }

    private void erstelleSpielParameter(int width, int height){
        this.lanePixelHoehe = height * LANE_HOEHE_PROZENT / 100;
        this.objektPixelHoehe = lanePixelHoehe * OBJEKT_HOEHE_PROZENT / 100;
        lanePadding = (lanePixelHoehe - objektPixelHoehe)/2; //zentriert die Obj in den Lanes
        spielFlaeche.set(0,0,width,height * LANE_HOEHE_PROZENT / 100 * 13);
        froschbreite = width / 13;
        startPositionX = width / 2 - (froschbreite/2);
        startPositionY = lanePixelHoehe * 12 + lanePadding;
    }
}
