package winf114.waksh.de.frogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bhaetsch on 03.06.2015.
 */
public class Highscore {
    final String FILENAME = "frogger_highscore";
    ArrayList<Integer> highscore;
    Context ctx;

    public Highscore(Context context) {
        this.ctx = context;
        if (readHighscore() == null) {
            highscore = new ArrayList<Integer>(10);
            for (int i = 0; i < 10; i++) {
                highscore.add(0);
                writeHighscore(highscore);
            }
        } else {
            highscore = readHighscore();
        }
    }

    public ArrayList<Integer> readHighscore() {
        Log.d("Highscore", "readHighscore");
        ArrayList<Integer> toReturn = null;
        try {
            FileInputStream fis = ctx.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            toReturn = (ArrayList<Integer>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.e("Highscore", "Fehler beim Lesen aus dem Speicher");
            Log.e("Highscore", e.getMessage());
        }
        return toReturn;
    }

    public void writeHighscore(ArrayList<Integer> highscore) {
        Log.d("Highscore", "writeHighscore");
        try {
            FileOutputStream fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(highscore);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e("Highscore", "Fehler beim Schreiben in den Speicher");
        }
    }

    public void compareScore(int score) {
        Log.d("Highscore", "compareScore");
        boolean newhs = false;
        int nhs = 0;
        for (int hs : highscore) {
            if (score > hs) {
                Log.d("Highscore", "vorher: " + Integer.toString(highscore.get(1)));
                newhs = true;
                nhs = hs;
                break;
            }
        }
        if (newhs) {
            highscore.add(score);
            Collections.sort(highscore, Collections.reverseOrder());
            highscore.trimToSize();
            writeHighscore(highscore);
            Log.d("Highscore", "nachher: " + Integer.toString(highscore.get(1)));
        }
    }
}
