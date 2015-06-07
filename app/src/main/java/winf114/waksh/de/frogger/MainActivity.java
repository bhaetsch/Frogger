package winf114.waksh.de.frogger;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Highscore highscore;

    SharedPreferences sharedPref;
    boolean usePlayServices = false;

    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity", "onStart");
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
    protected void onResume() {
        Log.d("MainActivity", "onResume");
        super.onResume();

        if (!usePlayServices) {
            /* Liest den Highscore aus dem App-Speicher ein */
            highscore = new Highscore(this);
        }
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "onStop");
        super.onStop();

        if (usePlayServices) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("MainActivity", "onConnected");
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MainActivity", "onConnectionSuspended");

        if (usePlayServices) {
            // Attempt to reconnect
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("MainActivity", "onConnectionFailed");
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
                show_toast("There was an issue with sign-in, please try again later.");
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (usePlayServices && resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in failed.
                highscore = new Highscore(this);
                usePlayServices = false;
                show_toast("Unable to sign in, using local Highscores!");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.str_opt_playServices), false);
                editor.commit();
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

    /* Wird aufgerufen, wenn der Highscore-Button gedrückt wird und startet die HighscoreActivity */
    public void onclick_highscore(View view) {
        if (usePlayServices && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkI2-engsYVEAIQAQ"), 0);
        } else {
            while (highscore.getHighscoreString() == null) {  // Wartet, bis die String-Liste erstellt ist
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //Exception
                }
            }
            Intent intent = new Intent(this, HighscoreActivity.class);
            intent.putExtra("highscoreString", highscore.getHighscoreString()); // Übergibt die String-Liste an die HighscoreActivity
            startActivity(intent);
        }
    }

    /* Wird aufgerufen, wenn der Settings-Button gedrückt wird und zeigt einen Toast */
    public void onclick_settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /* Wird aufgerufen, wenn der Credits-Button gedrückt wird und zeigt einen Tost */
    public void onclick_credits(View view) {
        show_toast("Credits");
    }

    /* Wird aufgerufen, wenn der Exit-Button gedrückt wird und beendet die App */
    public void onclick_exit(View view) {
        show_toast("beende Frogger");
        this.finish();
    }
}
