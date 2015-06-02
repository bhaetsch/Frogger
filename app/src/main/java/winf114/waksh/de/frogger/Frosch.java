package winf114.waksh.de.frogger;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class Frosch extends Spielobjekt {

    private final int geschwindigkeitVertikal;
    int geschwindigkeitHorizontal;
    private boolean moved;
    private richtung r;
    private final GameActivity gameActivity;

    boolean imWasser;
    boolean hitTree;
    boolean imZiel;
    long todesZeitpunkt;
    boolean kuerzlichVerendet;
    private boolean aufStartPosition;
    long levelStartZeitpunkt;



    public Frosch(int x, int y, int breite, int hoehe, int geschwindigkeitVertikal, int geschwindigkeitHorizontal, int farbe, GameActivity gameActivity) {
        super(x, y, breite, hoehe, farbe);

        this.gameActivity = gameActivity;
        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
        this.geschwindigkeitVertikal = geschwindigkeitVertikal;
        moved = false;
        hitTree = false;
        imWasser = false;
        imZiel = false;
        kuerzlichVerendet = false;
        aufStartPosition = true;

    }

    public void move() {
        if (hitTree) {
            this.setX(this.getX() + geschwindigkeitHorizontal);
            setZeichenBereich();
        }
        if (moved) {
            geschwindigkeitHorizontal = FP.froschGeschwX;
            move(r);
        }
    }

    private void move(richtung r) {
        if(!kuerzlichVerendet) {
            switch (r) {
                case vor:
                    this.setY(this.getY() - geschwindigkeitVertikal);
                    if (this.getY() < FP.lanePixelHoehe * 6) {
                        imWasser = true;
                    }
                    aufStartPosition = false;
                    break;
                case zurueck:
                    if (!aufStartPosition) {
                        this.setY(this.getY() + geschwindigkeitVertikal);
                        if (this.getY() > FP.lanePixelHoehe * 6) {
                            imWasser = false;
                            hitTree = false;
                        }
                        aufStartPosition = false;
                    }
                    break;
                case links:
                    this.setX(this.getX() - geschwindigkeitHorizontal);
                    aufStartPosition = false;
                    break;
                case rechts:
                    this.setX(this.getX() + geschwindigkeitHorizontal);
                    aufStartPosition = false;
                    break;
            }
            setZeichenBereich();
        }
        moved = false;
    }

    public void gewinnt() {
        imZiel = true;
        gameActivity.punkte += 100;
        resetFrosch();
    }

    public void sterben() {
        kuerzlichVerendet = true;
        gameActivity.lebensAnzeige.lebenAnzahl--;
        if (gameActivity.lebensAnzeige.lebenAnzahl == 0) {
            gameActivity.lebensAnzeige.lebenAnzahl = 5;
            gameActivity.punkte = 0;

            for (Spielobjekt s : gameActivity.spielobjekte){
                if (s instanceof Ziel){
                    ((Ziel) s).setBesetzt(false);
                }
            }
        }
        todesZeitpunkt = System.currentTimeMillis();
        gameActivity.toterFrosch.anzeigen(getZeichenBereich());
        resetFrosch();
    }

    private void resetFrosch() {
        levelStartZeitpunkt = System.currentTimeMillis();
        geschwindigkeitHorizontal = FP.froschGeschwX;
        hitTree = false;
        imWasser = false;
        aufStartPosition = true;
        setX(FP.startPositionX);
        setY(FP.startPositionY);
        setZeichenBereich();
    }

    public void setGeschwindigkeitHorizontal(int geschwindigkeitHorizontal) {
        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
    }

    public void setMoved() {
        moved = true;
    }

    public void setRichtung(richtung r) {
        this.r = r;
    }

    public long getLevelStartZeitpunkt() {
        return levelStartZeitpunkt;
    }
}