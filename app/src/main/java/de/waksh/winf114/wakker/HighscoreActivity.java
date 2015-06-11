package de.waksh.winf114.wakker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by bhaetsch on 03.06.2015.
 */
public class HighscoreActivity extends Activity {
    ArrayList<String> highscoreString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Übernimmt die aus der MainActivity übergebene String-Liste */
        highscoreString = getIntent().getExtras().getStringArrayList("highscoreString");

        /* Der Adapter verbindet die Highscore-Daten aus der String-Liste mit der ListView */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, highscoreString);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}