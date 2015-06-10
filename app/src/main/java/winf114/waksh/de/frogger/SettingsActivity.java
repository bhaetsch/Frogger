package winf114.waksh.de.frogger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

public class SettingsActivity extends Activity {
    SharedPreferences sharedPref;
    CheckBox checkBox;
    boolean usePlayServices = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /* öffnet einen Link auf die SharedPreferences im App-Speicher und ruft den gespeicherten Wert für "usePlayServices" ab */
        sharedPref = this.getSharedPreferences("winf114.waksh.de.Frogger.Settings", Context.MODE_PRIVATE);
        usePlayServices = sharedPref.getBoolean(getString(R.string.str_opt_playServices), usePlayServices);

        /* setzt die CheckBox auf den abgerufenen Wert */
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(usePlayServices);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /* öffnet einen Link auf die SharedPreferences im App-Speicher und speichert den aktuellen Wert der CheckBox */
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.str_opt_playServices), checkBox.isChecked());
        editor.commit();
    }
}
