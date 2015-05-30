package winf114.waksh.de.frogger;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class Frosch extends Spielobjekt{

    int geschwindigkeitVertikal;
    int geschwindigkeitHorizontal;
    boolean moved;
    richtung r;
    GameActivity gameActivity;


    public Frosch(int x, int y, int breite, int hoehe, int geschwindigkeitVertikal, int geschwindigkeitHorizontal,int farbe, GameActivity gameActivity) {
        super(x, y, breite, hoehe, farbe);

        this.gameActivity = gameActivity;
        this.geschwindigkeitHorizontal = geschwindigkeitHorizontal;
        this.geschwindigkeitVertikal = geschwindigkeitVertikal;
        setZeichenBereich();
        moved = false;
    }

    public void move(){
        if(moved == true){
            move(r);
        }
    }

    public void move(richtung r) {
        switch(r){
            case vor:
                this.setY(this.getY()-this.getHoehe() - geschwindigkeitVertikal);
                break;
            case zurueck:
                this.setY(this.getY() + this.getHoehe() + geschwindigkeitVertikal);
                break;
            case links:
                this.setX(this.getX()-this.getBreite() - geschwindigkeitHorizontal);
                break;
            case rechts:
                this.setX(this.getX()+this.getBreite() + geschwindigkeitHorizontal);
                break;
        }
        setZeichenBereich();
        moved = false;
    }

    public void sterben(){
        this.setX(gameActivity.startPositionX);
        this.setY(gameActivity.startPositionY);
        this.setZeichenBereich();
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(zeichenBereich, zeichenStift);
    }





}