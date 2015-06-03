package winf114.waksh.de.frogger;


import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by bhaetsch on 03.06.2015.
 */
public class HighscoreEintrag implements Serializable, Comparable<HighscoreEintrag> {
    private int score;
    private String datum;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public HighscoreEintrag(int score, long datum) {
        this.score = score;
        this.datum = simpleDateFormat.format(datum);
    }

    @Override
    public int compareTo(HighscoreEintrag another) {
        if (score > another.score)
            return 1;
        if (score < another.score)
            return -1;
        return 0;
    }

    @Override
    public String toString() {
        return datum + "        ->        " + score;
    }
}
