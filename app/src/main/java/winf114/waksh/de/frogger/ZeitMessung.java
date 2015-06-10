package winf114.waksh.de.frogger;

/**
 * Created by Matzef on 01.06.2015.
 */
class ZeitMessung {

    private final int CYLCLES_AVG = 20;

    private long cycleTimeBegin;
    private long cycleTime;
    private long cycleTimeMax;
    private long cycleTimeAvg;
    private long cycleTimeSum;
    private int cycles;

    public ZeitMessung() {
        cycles = 0;
        cycleTimeMax = 0;
        cycleTimeAvg = 0;
    }

    @Override
    public String toString() {
        return cycleTimeMax + " | " + cycleTimeAvg;
    }

    /* Zeitmessung starten */
    public void start() {
        cycleTimeBegin = System.currentTimeMillis();
    }

    /* Maximum aller Zeitmessungen */
    private void getMax() {
        cycleTime = System.currentTimeMillis() - cycleTimeBegin;
        if (cycleTime > cycleTimeMax) {
            cycleTimeMax = cycleTime;
        }
    }

    /* Mittelwert aller Zeitmessungen */
    private void getAvg() {
        cycleTime = System.currentTimeMillis() - cycleTimeBegin;
        cycleTimeSum = cycleTimeSum + cycleTime;
        if (cycles == CYLCLES_AVG) {
            cycleTimeAvg = cycleTimeSum / cycles;
            cycles = 0;
            cycleTimeSum = 0;
        }
    }

    /* Zeitmessung beenden */
    public void end() {
        cycles++;
        getMax();
        getAvg();
    }
}
