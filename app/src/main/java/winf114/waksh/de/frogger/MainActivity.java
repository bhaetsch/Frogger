package winf114.waksh.de.frogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.Context;

import java.util.ArrayList;

public class MainActivity extends Activity {
    Highscore highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity", "onResume");
        super.onResume();

        /* Liest den Highscore aus dem App-Speicher ein */
        highscore = new Highscore(this);
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

    /* Wird aufgerufen, wenn der Settings-Button gedrückt wird und zeigt einen Toast */
    public void onclick_settings(View view) {
        show_toast("Settings");
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
