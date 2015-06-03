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
import java.util.Date;
import java.util.ListIterator;

/**
 * Created by bhaetsch on 03.06.2015.
 */
public class Highscore {
    final String FILENAME = "frogger_highscore";
    ArrayList<HighscoreEintrag> highscore;
    Context ctx;

    public Highscore(Context context) {
        this.ctx = context;
        if (readHighscore() == null) {
            highscore = new ArrayList<HighscoreEintrag>(10);
            for (int i = 0; i < 10; i++) {
                highscore.add(new HighscoreEintrag(0, new Date().getTime()));
                writeHighscore(highscore);
            }
        } else {
            highscore = readHighscore();
        }
    }

    public ArrayList<HighscoreEintrag> readHighscore() {
        Log.d("Highscore", "readHighscore");
        ArrayList<HighscoreEintrag> toReturn = null;
        try {
            FileInputStream fis = ctx.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            toReturn = (ArrayList<HighscoreEintrag>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.e("Highscore", "Fehler beim Lesen aus dem Speicher");
            Log.e("Highscore", e.getMessage());
        }
        return toReturn;
    }

    public ArrayList<String> readHighscoreString() {
        Log.d("Highscore", "readHighscoreString");
        ArrayList<String> toReturn = null;
        try {
            FileInputStream fis = ctx.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<HighscoreEintrag> temp = (ArrayList<HighscoreEintrag>) ois.readObject();
            toReturn = mapHighscoreEintragToStringCollection(temp);
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.e("Highscore", "Fehler beim Lesen aus dem Speicher");
            Log.e("Highscore", e.getMessage());
        }
        return toReturn;
    }

    public abstract class CollectionTransformer<E, F> {
        abstract F transform(E e);

        public ArrayList<F> transform(ArrayList<E> list) {
            ArrayList<F> newList = new ArrayList<F>();
            for (E e : list) {
                newList.add(transform(e));
            }
            return newList;
        }
    }

    public ArrayList<String> mapHighscoreEintragToStringCollection(ArrayList<HighscoreEintrag> list) {
        CollectionTransformer transformer = new CollectionTransformer<HighscoreEintrag, String>() {
            @Override
            String transform(HighscoreEintrag e) {
                return e.toString();
            }
        };
        return transformer.transform(list);
    }

    public void writeHighscore(ArrayList<HighscoreEintrag> highscore) {
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

    public void compareScore(HighscoreEintrag score) {
        Log.d("Highscore", "compareScore");
        boolean newhs = false;
        HighscoreEintrag nhs = new HighscoreEintrag(0, new Date().getTime());
        for (HighscoreEintrag hs : highscore) {
            if (score.compareTo(hs) != -1) {
                Log.d("Highscore", "vorher: " + highscore.get(1));
                newhs = true;
                nhs = hs;
                break;
            }
        }
        if (newhs) {
            highscore.remove(nhs);
            highscore.add(score);
            Collections.sort(highscore, Collections.reverseOrder());
            highscore.trimToSize();
            writeHighscore(highscore);
            Log.d("Highscore", "nachher: " + highscore.get(1));
        }
    }
}
