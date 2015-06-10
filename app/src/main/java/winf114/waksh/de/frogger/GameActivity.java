package winf114.waksh.de.frogger;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.SurfaceView;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.ArrayList;

public class GameActivity extends Activity implements SurfaceHolder.Callback, GestureDetector.OnGestureListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //TODO memo to self: renderpausen: logcat:GC_FOR_ALLOC freed 7576K, 32% free 17140K/25000K, paused 81ms, total 90ms
    //TODO bei kollision mit dem rand wird der sterbende frosch ausserhalb des bildes angezeigt

    /* Erkennung der Wischgesten */
    private GestureDetector gestureDetector;

    /* Berechnung der Renderzeiten */
    private ZeitMessung renderCycleMessung;

    /* Darstellung und Brechnung der Spielinhalte */
    private Paint textStift;
    private Hintergrund hintergrund;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MainThread mainThread;

    /* Highscore-System */
    private SharedPreferences sharedPref;
    boolean usePlayServices = false;
    GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;

    /* Spielobjekte */
    ArrayList<Spielobjekt> spielobjekte;
    LebensAnzeige lebensAnzeige;
    ZeitAnzeige zeitAnzeige;
    ToterFrosch toterFrosch;
    Frosch frosch;
    Prinzessin prinzessin;
    Blume blume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /* Soft-Buttons und Statusleiste ausblenden */
        aktiviereImmersiveFullscreen();

        /* surfaceView ist die Spieloberfläche, surfaceHolder ist ein abstraktes Interface für die Oberflächennutzung */
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        /* Wischgesten-Erkennung an die Activity binden */
        gestureDetector = new GestureDetector(this, this);

        /* Berechnung der Renderzeiten */
        renderCycleMessung = new ZeitMessung();

        /* Thread für Spielberechnungen erstellen */
        mainThread = new MainThread(surfaceHolder, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* Prüft, ob Google Play Services für die Highscores verwendet werden sollen */
        sharedPref = this.getSharedPreferences("winf114.waksh.de.Frogger.Settings", Context.MODE_PRIVATE);
        usePlayServices = sharedPref.getBoolean(getString(R.string.str_opt_playServices), usePlayServices);

        /* Versucht ggf. eine Verbindung zu den Google Play Services aufzubauen */
        if (usePlayServices) {
            if (mGoogleApiClient == null) {
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
        super.onStop();

        /* Beendet ggf. die Verbindung zu den Google Play Services */
        if (usePlayServices) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        /* Baut ggf. erneut eine Verbindung zu den Google Play Services auf */
        if (usePlayServices) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /* Wird der Verbindungsfehler schon bearbeitet? */
        if (mResolvingConnectionFailure) {
            return;
        }

        /* Versuch den Verbindungsfehler zu beheben */
        if (usePlayServices || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mResolvingConnectionFailure = true;
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiClient.connect();
                }
            } else {
                show_toast("There was an issue with sign-in, please try again later.");
            }
        }
    }

    /* Wird implizit durch connectionResult.startResolutionForResult aufgerufen, sobald ein Result zurückgegeben wird  */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (usePlayServices && resultCode == RESULT_OK) {
                /* erneuter Verbindungsversuch */
                mGoogleApiClient.connect();
            } else {
                /* Fallback auf lokale Highscores */
                usePlayServices = false;
                show_toast("Unable to sign in, using local Highscores!");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.str_opt_playServices), false);
                editor.commit();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /* grundlegende Spielparameter und Hintergrund erzeugen */
        FP.erstelleSpielParameter(width, height);
        hintergrund = new Hintergrund(width, FP.lanePixelHoehe);

        /* Stift und Farbe und für die Textanzeigen erzeugen */
        SpielWerte.setTextAnzeige(height + ":" + width + ":" + FP.objektPixelBreite + ":" + FP.froschGeschwX);
        textStift = new Paint();
        textStift.setColor(Farbe.text);

        /* grundlegende Objekte erzeugen */
        toterFrosch = new ToterFrosch(Farbe.deadFrosch);
        lebensAnzeige = new LebensAnzeige();
        zeitAnzeige = new ZeitAnzeige();
        spielobjekte = new ArrayList<>();

        /* temporäre Objekte erzeugen */
        Baum krokodil;
        Baum baum01;
        Baum baum02;
        Ziel ziel01;
        Ziel ziel02;
        Ziel ziel03;
        Ziel ziel04;
        Ziel ziel05;

        /* Lane 1 erzeugen (Ziele) */
        spielobjekte.add(ziel01 = new Ziel(FP.startPositionX, FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel02 = new Ziel(FP.startPositionX + (3 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel03 = new Ziel(FP.startPositionX - (3 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel04 = new Ziel(FP.startPositionX + (6 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));
        spielobjekte.add(ziel05 = new Ziel(FP.startPositionX - (6 * FP.objektPixelBreite), FP.objektPixelBreite, FP.lanePixelHoehe, Farbe.zielLeer));

        /* Lane 2 erzeugen */
        int hindernisBreite = FP.objektPixelBreite * 3;
        int hindernisGeschw = 4;
        int lanePositionY = FP.lanePixelHoehe + FP.lanePadding;
        spielobjekte.add(krokodil = new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 7, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.krokodil));
        spielobjekte.add(new KrokodilKopf(krokodil, Farbe.krokodilKopf));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 14, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, 4, Farbe.baum));

        /* Lane 3 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 6;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 2 + FP.lanePadding;
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 3, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        /* Lane 4 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 6;
        hindernisGeschw = 1;
        lanePositionY = FP.lanePixelHoehe * 3 + FP.lanePadding;
        spielobjekte.add(baum01 = new Baum(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 8, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 16, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Schlange(FP.schlangenFlaeche.left, FP.lanePixelHoehe * 3 + FP.schlangenPadding, 2, baum01, Farbe.schlange));

        /* Lane 5 erzeugen*/
        hindernisBreite = FP.objektPixelBreite * 5;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 4 + FP.lanePadding;
        spielobjekte.add(baum02 = new Baum(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 10, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        /* Lane 6 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 3;
        hindernisGeschw = 2;
        lanePositionY = FP.lanePixelHoehe * 5 + FP.lanePadding;
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 7, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));
        spielobjekte.add(new Baum(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 14, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.baum));

        /* Lane 7 erzeugen */
        spielobjekte.add(new Schlange(FP.schlangenFlaeche.left, FP.lanePixelHoehe * 6 + FP.schlangenPadding, 2, null, Farbe.schlange));

        /* Lane 8 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = FP.lanePixelHoehe * 7 + FP.lanePadding;
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        /* Lane 9 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 4;
        hindernisGeschw = -2;
        lanePositionY = FP.lanePixelHoehe * 8 + FP.lanePadding;
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        /* Lane 10 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 3;
        hindernisGeschw = 3;
        lanePositionY = FP.lanePixelHoehe * 9 + FP.lanePadding;
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        /* Lane 11 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = -5;
        lanePositionY = FP.lanePixelHoehe * 10 + FP.lanePadding;
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.right, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.right + FP.objektPixelBreite * 6, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        /* Lane 12 erzeugen */
        hindernisBreite = FP.objektPixelBreite * 2;
        hindernisGeschw = 4;
        lanePositionY = FP.lanePixelHoehe * 11 + FP.lanePadding;
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.left, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));
        spielobjekte.add(new Auto(FP.erweiterteSpielFlaeche.left + FP.objektPixelBreite * 5, lanePositionY, hindernisBreite, FP.objektPixelHoehe, hindernisGeschw, Farbe.auto));

        /* Frosch erzeugen */
        spielobjekte.add(frosch = new Frosch(FP.startPositionX, FP.startPositionY, FP.objektPixelBreite, FP.objektPixelHoehe, FP.froschGeschwY, FP.froschGeschwX, Farbe.frosch, usePlayServices, this));

        /* Prinzessin und Blume erzeugen */
        prinzessin = new Prinzessin(baum02, Farbe.prinzessin);
        blume = new Blume(ziel01, ziel02, ziel03, ziel04, ziel05);

        /* Spiel-Routinen starten */
        SpielWerte.startLevel();
        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /* Versucht den mainThread zu beenden und blockiert solange die Activity */
        boolean retry = true;
        while (retry) {
            try {
                mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        /* mainThread anhalten und Activity beenden */
        mainThread.setRunning(false);
        this.finish();
    }

    /* Soft-Buttons und Statusleiste ausblenden */
    private void aktiviereImmersiveFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    /* Wischgesten-Erkennung */
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        /* Wisch nach rechts */
        if (event1.getX() < event2.getX() && ((event1.getY() > event2.getY() && event1.getY() - event2.getY() < event2.getX() - event1.getX()) || (event1.getY() < event2.getY() && event2.getY() - event1.getY() < event2.getX() - event1.getX()))) {
            frosch.setMoved();
            frosch.setRichtung(richtung.rechts);
            return true;
        }
        /* Wisch nach links */
        if (event1.getX() > event2.getX() && ((event1.getY() > event2.getY() && event1.getY() - event2.getY() < event1.getX() - event2.getX()) || (event1.getY() < event2.getY() && event2.getY() - event1.getY() < event1.getX() - event2.getX()))) {
            frosch.setMoved();
            frosch.setRichtung(richtung.links);
            return true;
        }
        /* Wisch nach unten */
        if (event1.getY() < event2.getY()) {
            frosch.setMoved();
            frosch.setRichtung(richtung.zurueck);
            return true;
        }
        /* Wisch nach oben */
        if (event1.getY() > event2.getY()) {
            frosch.setMoved();
            frosch.setRichtung(richtung.vor);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    /* Erstellt einen "Toast" und zeigt ihn an */
    private void show_toast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /* Wrapper für Render-Routine zur Messung der Renderzeit, wird vom MainThread aufgerufen */
    protected void onDraw(Canvas canvas) {
        renderCycleMessung.start();
        renderGame(canvas);
        renderCycleMessung.end();
    }

    /* Render-Routine */
    private void renderGame(Canvas canvas) {
        /* schwarze Fläche erstellen und dann Hintergrund, Lebensanzeige und Zeitanzeige darauf zeichnen */
        canvas.drawColor(Color.BLACK);
        hintergrund.draw(canvas);
        lebensAnzeige.draw(canvas);
        zeitAnzeige.draw(canvas);

        /* Spielobjekte zeichnen */
        for (Spielobjekt s : spielobjekte) {
            s.draw(canvas);
        }

        /* werden nicht immer angezeigt */
        prinzessin.draw(canvas);
        blume.draw(canvas);
        toterFrosch.draw(canvas);

        /* Ergebnisse der Zeitmessungen, Zeit- und Lebensanzeige und Textausgabe zeichnen */
        textStift.setTextSize(FP.smallTextSize);
        canvas.drawText("GCmax|avg: " + mainThread.gameCycleMessung + " (ms)", 10, FP.lanePixelHoehe * 16, textStift);
        canvas.drawText("RCmax|avg: " + renderCycleMessung + " (ms)", 10, FP.lanePixelHoehe * 16 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText(SpielWerte.levelZeit(), FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 14 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText("Leben", FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 15 - (FP.lanePixelHoehe / 2), textStift);
        canvas.drawText(SpielWerte.textAnzeige(), FP.startPositionX + (FP.objektPixelBreite / 2), FP.lanePixelHoehe * 16, textStift);

        /* Punkte- und Levelanzeige zeichnen */
        textStift.setTextSize(FP.largeTextSize);
        canvas.drawText(SpielWerte.punkte(), 10, FP.lanePixelHoehe * 14, textStift);
        canvas.drawText(SpielWerte.level(), 10, FP.lanePixelHoehe * 15, textStift);
    }
}
