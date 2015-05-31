package winf114.waksh.de.frogger;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class Frosch extends Spielobjekt {

    private int geschwindigkeitVertikal;
    protected int geschwindigkeitHorizontal;
    private boolean moved;
    private richtung r;
    private GameActivity gameActivity;

    // TODO Felder einpacken
    boolean imWasser;
    boolean istTot;
    boolean hitTree;


    public Frosch(int x, int y, int breite, int hoehe, int geschwindigkeitVertikal, int geschwindigkeitHorizontal, int farbe, GameActivity gameActivity) {
        super(x, y, breite, hoehe, farbe);

        this.gameActivity = gameActivity;
        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
        this.geschwindigkeitVertikal = geschwindigkeitVertikal;
        moved = false;
        istTot = false;
        hitTree = false;
        imWasser = false;
    }

    public void move() {
        if (hitTree) {
            this.setX(this.getX() + geschwindigkeitHorizontal);
            setZeichenBereich();
        }
        if (moved) {
            geschwindigkeitHorizontal = gameActivity.froschGeschwX;
            move(r);
        }
    }

    public void move(richtung r) {
        switch (r) {
            case vor:
                this.setY(this.getY() - geschwindigkeitVertikal);
                if (this.getY() < gameActivity.lanePixelHoehe * 6) {
                    imWasser = true;
                }
                break;
            case zurueck:
                this.setY(this.getY() + geschwindigkeitVertikal);
                if (this.getY() > gameActivity.lanePixelHoehe * 6) {
                    imWasser = false;
                    hitTree = false;
                }
                break;
            case links:
                this.setX(this.getX() - geschwindigkeitHorizontal);
                break;
            case rechts:
                this.setX(this.getX() + geschwindigkeitHorizontal);
                break;
        }
        setZeichenBereich();
        moved = false;
    }

    public void gewinnt() {
        gameActivity.punkte += 100;
        resetFrosch();
    }

    public void sterben() {
        gameActivity.tode++;
        istTot = true;
        gameActivity.deadFrosch.setX(getX());
        gameActivity.deadFrosch.setY(getY());
        gameActivity.deadFrosch.setZeichenBereich();
        resetFrosch();
    }

    public void resetFrosch() {
        geschwindigkeitHorizontal = gameActivity.froschGeschwX;
        hitTree = false;
        imWasser = false;
        setX(gameActivity.startPositionX);
        setY(gameActivity.startPositionY);
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
}