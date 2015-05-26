package winf114.waksh.de.frogger;

import java.util.ArrayList;
import android.view.SurfaceHolder;
import android.graphics.Canvas;


/**
 * Created by bhaetsch on 25.05.2015.
 */
public class MainThread extends Thread {
    private boolean running;
    private ArrayList<Spielobjekt> spielobjekte = new ArrayList<Spielobjekt>();

    private SurfaceHolder surfaceHolder;
    private MainGamePanel gamePanel;

    @Override
    public void run() {
        while (isRunning()) {
            //TODO
            // senseless comentary

            Canvas canvas;

            while (running) {
                canvas = null;
                // try locking the canvas for exclusive pixel editing on the surface
                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        // update game state
                        // draws the canvas on the panel
                        this.gamePanel.onDraw(canvas); //kein Fehler!
                    }
                } finally {
                    // in case of an exception the surface is not left in
                    // an inconsistent state
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                } // end finally
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;


    }
}
