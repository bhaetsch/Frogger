package winf114.waksh.de.frogger;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by bhaetsch on 25.05.2015.
 */
public class Frosch {
    private int x;                  //horizontale Position der linken oberen Ecke
    private int y;                  //vertikale Position der linken oberen Ecke
    private int breite;             //in Pixeln
    private int hoehe;              //in Pixeln
    private Bitmap bitmap; // the actual bitmap
    private boolean touched; // if frog is touched/picked up

    public Frosch(Bitmap bitmap, int x, int y, int breite, int hoehe) {
        this.bitmap = bitmap;
        this.setX(x);
        this.setY(y);
        this.setBreite(breite);
        this.setHoehe(hoehe);
    }

    public void move(richtung r) {
        switch(r){
            case vor:
                this.setY(this.getY()-this.getHoehe());
                break;
            case zurueck:
                this.setY(this.getY() + this.getHoehe());
                break;
            case links:
                this.setX(this.getX()-this.getBreite());
                break;
            case rechts:
                this.setX(this.getX()+this.getBreite());
                break;
        }
    }

    public boolean collidesWithHindernis(Hindernis h) {
        if (h.getY() > this.getY() + this.getHoehe() || this.getY() > h.getY() + h.getHoehe() || h.getX() > this.getX() + this.getBreite() || this.getX() > h.getX() + h.getBreite()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean collidesWithSchwimmer(Schwimmer s) {
        if (s.getY() > this.getY() + this.getHoehe() || this.getY() > s.getY() + s.getHoehe() || s.getX() > this.getX() + this.getBreite() || this.getX() > s.getX() + s.getBreite()) {
            return false;
        } else {
            return true;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBreite() {
        return breite;
    }

    public void setBreite(int breite) {
        this.breite = breite;
    }

    public int getHoehe() {
        return hoehe;
    }

    public void setHoehe(int hoehe) {
        this.hoehe = hoehe;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public void handleActionDown(int eventX, int eventY) {
        if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth()/2))) {
            if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2))) {
                // droid touched
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}