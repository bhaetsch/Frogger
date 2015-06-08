package winf114.waksh.de.frogger;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.ArrayList;

public class GameActivity extends Activity implements SurfaceHolder.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //TODO memo to self: renderpausen: logcat:GC_FOR_ALLOC freed 7576K, 32% free 17140K/25000K, paused 81ms, total 90ms
    //TODO bei kollision mit dem rand wird der sterbende frosch ausserhalb des bildes angezeigt

    SharedPreferences sharedPref;
    boolean usePlayServices = false;
    GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;

    ArrayList<Spielobjekt> spielobjekte;
    LebensAnzeige lebensAnzeige;
    ZeitAnzeige zeitAnzeige;
    ToterFrosch toterFrosch;
    Frosch frosch;
    Prinzessin prinzessin;
    private Auto auto01;
    private Auto auto02;
    private Auto auto03;
    private Auto auto04;
    private Auto auto05;
    private Auto auto06;
    private Auto auto07;
    private KrokodilKopf krokodilKopf01;
    private Baum krokodil01;
    private Baum baum02;
    Baum baum03;
    Baum baum04;
    private Baum baum05;
    private Baum baum06;
    private Baum baum07;
    private Baum baum08;
    private Baum baum09;
    private Baum baum10;
    private Baum baum11;
    private Baum baum12;
    private Baum baum13;
    private Schlange schlange01;
    private Schlange schlange02;
    private Ziel ziel01;
    private Ziel ziel02;
    private Ziel ziel03;
    private Ziel ziel04;
    private Ziel ziel05;
    Blume blume;
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
    protected void onStart() {
        Log.d("GameActivity", "onStart");
        super.onStart();

        sharedPref = this.getSharedPreferences("winf114.waksh.de.Frogger.Settings", Context.MODE_PRIVATE);
        usePlayServices = sharedPref.getBoolean(getString(R.string.str_opt_playServices), usePlayServices);

        if (usePlayServices) {
            if (mGoogleApiClient == null) {
                // Create the Google Api Client with access to the Play Games services
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        .setViewForPopups(findViewById(android.R.id.content))
                        .build();
            }
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        Log.d("GameActivity", "onStop");
        super.onStop();

        if (usePlayServices) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("GameActivity", "onConnected");
        // The player is signed in.
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GameActivity", "onConnectionSuspended");
        if (usePlayServices) {
            // Attempt to reconnect
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GameActivity", "onConnectionFailed");
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (usePlayServices || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mGoogleApiClient.connect();
                }
            } else {
                Log.d("GameActivity", "There was an issue with sign-in, please try again later.");
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (usePlayServices && resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in failed
                usePlayServices = false;
                Log.d("GameActivity", "Unable to sign in.");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.str_opt_playServices), false);
                editor.commit();
            }
        }
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
        spielobjekte.add(krokodil01 = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 7, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.krokodil));
        spielobjekte.add(krokodilKopf01 = new KrokodilKopf(krokodil01, Farbe.krokodilKopf));
        spielobjekte.add(baum08 = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 14, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum09 = new Baum(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, 4, Farbe.baum));

        //LANE 3
        hindernisBreite = FP.objektPixelBreite * 6;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 2 + FP.lanePadding;
        spielobjekte.add(baum02 = new Baum(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 3, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 4
        hindernisBreite = FP.objektPixelBreite * 6;
        hindernisGeschw = 1;
        lanePositionY = FP.lanePixelHoehe * 3 + FP.lanePadding;
        spielobjekte.add(baum03 = new Baum(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum12 = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 8, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum13 = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 16, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(schlange02 = new Schlange(FP.schlangenFlaeche.left, FP.lanePixelHoehe * 3 + FP.schlangenPadding, 2, baum03, Farbe.schlange));

        //LANE 5
        hindernisBreite = FP.objektPixelBreite * 5;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 4 + FP.lanePadding;
        spielobjekte.add(baum04 = new Baum(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum10 = new Baum(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 10, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 6
        hindernisBreite = FP.objektPixelBreite * 3;
        hindernisGeschw = 2;
        lanePositionY = FP.lanePixelHoehe * 5 + FP.lanePadding;
        spielobjekte.add(baum05 = new Baum(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum06 = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 7, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(baum07 = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 14, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        //LANE 7
        spielobjekte.add(schlange01 = new Schlange(FP.schlangenFlaeche.left, FP.lanePixelHoehe * 6 + FP.schlangenPadding, 2, null, Farbe.schlange));

        //LANE 8
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = FP.lanePixelHoehe * 7 + FP.lanePadding;
        spielobjekte.add(auto01 = new Auto(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 9
        hindernisBreite = FP.objektPixelBreite * 4;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 8 + FP.lanePadding;
        spielobjekte.add(auto02 = new Auto(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 10
        hindernisBreite = FP.objektPixelBreite * 3;
        hindernisGeschw = 3;
        lanePositionY = FP.lanePixelHoehe * 9 + FP.lanePadding;
        spielobjekte.add(auto03 = new Auto(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 11
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = -5;
        lanePositionY = FP.lanePixelHoehe * 10 + FP.lanePadding;
        spielobjekte.add(auto04 = new Auto(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));
        spielobjekte.add(auto07 = new Auto(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 6, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        //LANE 12
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = FP.lanePixelHoehe * 11 + FP.lanePadding;
        spielobjekte.add(auto05 = new Auto(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));
        spielobjekte.add(auto06 = new Auto(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 5, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));


        //LANE 1 Ziele
        spielobjekte.add(ziel01 = new Ziel(FP.startPositionX, FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel02 = new Ziel(FP.startPositionX + (3 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel03 = new Ziel(FP.startPositionX - (3 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel04 = new Ziel(FP.startPositionX + (6 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel05 = new Ziel(FP.startPositionX - (6 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        //</editor-fold>

        //Frosch
        spielobjekte.add(frosch = new Frosch(FP.startPositionX, FP.startPositionY, FP.objektPixelBreite, FP.objektPixelHoehe, FP.froschGeschwY, FP.froschGeschwX, Farbe.frosch, usePlayServices, this));
        prinzessin = new Prinzessin(baum04, Farbe.prinzessin);
        blume = new Blume(ziel01, ziel02, ziel03, ziel04, ziel05);
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

    private void programmiereKnöpfe() {
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
        blume.draw(canvas);
        toterFrosch.draw(canvas);

        //Punkteanzeige und 4 Textfelder(positioniert) als Kontrollanzeige
        textStift.setTextSize(FP.smallTextSize);
        canvas.drawText("GCmax|avg: " + mainThread.gameCycleMessung + " (ms)", 10, FP.lanePixelHoehe * 16, textStift);
        canvas.drawText("RCmax|avg: " + renderCycleMessung + " (ms)", 10, FP.lanePixelHoehe * 16 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText(SpielWerte.levelZeit(), FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 14 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText("Leben", FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 15 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText(SpielWerte.textAnzeige(), FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 16, textStift);
        textStift.setTextSize(FP.largeTextSize);
        canvas.drawText(SpielWerte.punkte(), 10, FP.lanePixelHoehe * 14, textStift);
        canvas.drawText(SpielWerte.level(), 10, FP.lanePixelHoehe * 15, textStift);
    }
}
