package winf114.waksh.de.frogger;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by bhaetsch on 03.06.2015.
 */
public class Highscore {
    ArrayList<HighscoreEintrag> highscore;          // Highscore als HighscoreEintrag-Liste
    ArrayList<String> highscoreString;              // Highscore als String-Liste
    Context ctx;                                    // aufrufende Activity (für Zugriff auf Dateisystem benötigt)

    public Highscore(Context context) {
        this.ctx = context;
        startReadHighscore();
        startReadHighscoreString();
    }

    /* Ruft readHighscore() als neuen Thread auf */
    public void startReadHighscore() {
        new Thread() {
            public void run() {
                readHighscore();
            }
        }.start();
    }

    /* Liest den Highscore als HighscoreEintrag-Liste aus dem App-Speicher */
    public synchronized void readHighscore() {
        try {
            FileInputStream fis = ctx.openFileInput(FP.HIGHSCORE_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            highscore = (ArrayList<HighscoreEintrag>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            if (highscore == null) {
                highscore = new ArrayList<HighscoreEintrag>(10);
                for (int i = 0; i < 10; i++) {
                    highscore.add(new HighscoreEintrag(0, new Date().getTime()));
                }
                writeHighscore(highscore);
            }
        }
    }

    /* Ruft readHighscoreString() als neuen Thread auf */
    public void startReadHighscoreString() {
        new Thread() {
            public void run() {
                readHighscoreString();
            }
        }.start();
    }

    /* Liest den Highscore als String-Liste aus dem App-Speicher */
    public synchronized void readHighscoreString() {
        ArrayList<String> toReturn = null;
        try {
            FileInputStream fis = ctx.openFileInput(FP.HIGHSCORE_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<HighscoreEintrag> temp = (ArrayList<HighscoreEintrag>) ois.readObject();
            highscoreString = mapHighscoreEintragToStringCollection(temp);
            ois.close();
            fis.close();
        } catch (Exception e) {
        }
    }

    /* Ruft CollectionTransformer<> auf, um die HighscoreEintrag-Liste in eine String-Liste zu transformieren */
    public ArrayList<String> mapHighscoreEintragToStringCollection(ArrayList<HighscoreEintrag> list) {
        CollectionTransformer transformer = new CollectionTransformer<HighscoreEintrag, String>() {
            @Override
            String transform(HighscoreEintrag e) {
                return e.toString();
            }
        };
        return transformer.transform(list);
    }

    /* Transformiert Collections einer Klasse in Collections einer anderen Klasse */
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

    /* Schreibt den Highscore in den App-Speicher */
    public void writeHighscore(ArrayList<HighscoreEintrag> highscore) {
        try {
            FileOutputStream fos = ctx.openFileOutput(FP.HIGHSCORE_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(highscore);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
        }
    }

    /* Wird aufgerufen, wenn der Frosch stirbt und startet compareScore() als neuen Thread */
    public void startCompareScore(HighscoreEintrag score) {
        final HighscoreEintrag temp = score;
        new Thread() {
            public void run() {
                compareScore(temp);
            }
        }.start();
    }

    /*  Vergleicht den Score mit den gespeicherten Highscores und schreibt ggf. einen neuen Highscore */
    public synchronized void compareScore(HighscoreEintrag score) {
        boolean newhs = false;
        for (HighscoreEintrag hs : highscore) { // Vergleicht den Score mit den Highscores
            if (score.compareTo(hs) != -1) {
                newhs = true;
                break;
            }
        }
        if (newhs) {
            highscore.add(score);   // Fügt den Score zu den Highscores hinzu
            Collections.sort(highscore, Collections.reverseOrder());    // Sortiert die Highscores absteigend
            if (highscore.size() > 10) {    // Kürzt die Highscores auf 10 Einträge
                ArrayList<HighscoreEintrag> highscoreNew = new ArrayList<HighscoreEintrag>();
                for (int i = 0; i < 10; i++) {
                    highscoreNew.add(highscore.get(i));
                }
                writeHighscore(highscoreNew);
            } else {                            // Speichert die Highscores
                writeHighscore(highscore);
            }
        }
    }

    /* Gibt den Highscore als HighscoreEintrag-Liste zurück */
    public ArrayList<HighscoreEintrag> getHighscore() {
        return highscore;
    }

    /* Gibt den Highscore als String-Liste zurück */
    public ArrayList<String> getHighscoreString() {
        return highscoreString;
    }
}
