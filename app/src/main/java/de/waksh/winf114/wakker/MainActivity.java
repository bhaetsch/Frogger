package de.waksh.winf114.wakker;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /* Highscore-System */
    private Highscore highscore;
    private SharedPreferences sharedPref;
    private boolean usePlayServices;
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* Prüft, ob Google Play Services für die Highscores verwendet werden sollen */
        sharedPref = this.getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        usePlayServices = sharedPref.getBoolean(getString(R.string.str_main_playServices), false);


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

            /* Button für Achievements sichtbar machen */
            Button acButton = (Button)findViewById(R.id.btn_achievements);
            acButton.setVisibility(View.VISIBLE);
        } else {
            /* Button für Achievements unsichtbar machen, wenn Google Play Services in den Einstellungen deaktiviert wurden */
            Button acButton = (Button)findViewById(R.id.btn_achievements);
            acButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Liest den Highscore aus dem App-Speicher ein, falls die Google Play Services nicht verwendet werden sollen */
        if (!usePlayServices) {
            highscore = new Highscore(this);
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
                show_toast(getString(R.string.str_main_tryagain));
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
                highscore = new Highscore(this);
                usePlayServices = false;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.str_main_playServices), false);
                editor.commit();
                show_toast(getString(R.string.str_main_local));
            }
        }
    }

    /* Erstellt einen "Toast" und zeigt ihn an */
    private void show_toast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /* Wird aufgerufen, wenn der Play-Button gedrückt wird und startet die GameActivity */
    public void onclick_play(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /* Wird aufgerufen, wenn der Highscore-Button gedrückt wird */
    public void onclick_highscore(View view) {
        if (usePlayServices && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            /* ruft den Highscore von den Google Play Services ab, falls dieser genutzt werden soll */
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_highscore)), 0);
        } else {
            /* lädt den lokalen Highscore aus dem App-Speicher, falls dieser genutzt werden soll */
            while (highscore.getHighscoreString() == null) {  // Wartet, bis die String-Liste erstellt ist
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //Exception
                }
            }
            /* startet die HighscoreActivity mit den geladenen Highscores */
            Intent intent = new Intent(this, HighscoreActivity.class);
            intent.putExtra("highscoreString", highscore.getHighscoreString()); // Übergibt die String-Liste an die HighscoreActivity
            startActivity(intent);
        }
    }

    /* Wird aufgerufen, wenn der Achievements-Button gedrückt wird */
    public void onclick_achievements(View view) {
        if (usePlayServices && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            /* Achievements abrufen, wenn möglich */
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 0);
        } else {
            /* Fehlermeldung in Toast anzeigen */
            show_toast(getString(R.string.str_main_tryagain));
        }
    }

    /* Wird aufgerufen, wenn der Instructions-Button gedrückt wird und startet die InstructionsActivity */
    public void onclick_instructions(View view) {
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    /* Wird aufgerufen, wenn der Settings-Button gedrückt wird und startet die SettingsActivity */
    public void onclick_settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /* Wird aufgerufen, wenn der Credits-Button gedrückt wird und startet die CreditsActivity */
    public void onclick_credits(View view) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    /* Wird aufgerufen, wenn der Exit-Button gedrückt wird und beendet die App */
    public void onclick_exit(View view) {
        this.finish();
    }
}
