package overlays;

import helpers.ImageLoader;
import main.Game;
import main.State;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static main.Game.*;

/**
 * When in game over state, this class handles drawing game over image
 */
public class GameOverOverlay extends State {

    private static final BufferedImage GAME = ImageLoader.loadImage("/images/overlay_game.png");
    private static final BufferedImage OVER = ImageLoader.loadImage("/images/overlay_over.png");
    private static final int W = 408;
    private static final int H = 128;
    private static final int Y = GAME_HEIGHT / 2 - H / 2;
    private static final int GAME_X_INIT = -W;
    private static final int OVER_X_INIT = GAME_WIDTH;
    private int gameX = GAME_X_INIT;
    private int overX = OVER_X_INIT;
    private static final float SPEED = (int) (10 * SCALE);
    private static final int DIST = (int) (480 * SCALE);
    private static final int GAME_MAX_DIST = GAME_X_INIT + DIST;
    private static final int OVER_MAX_DIST = OVER_X_INIT - DIST;

    // ====== Constructor ======
    public GameOverOverlay(Game game) {
	    super(game);
    }

    public void drawGameOver(Graphics g) {
        if (!game.isDrawAllowed())
            return;
        if (game.isFirstTime()) {
            gameX = GAME_X_INIT;
            overX = OVER_X_INIT;
            System.out.println("game start: " + gameX);
            System.out.println("over start: " + overX);
            game.setFirstTime(false);
        }

        // game text
        gameX += SPEED;
        if (gameX >= GAME_MAX_DIST)
            gameX = GAME_MAX_DIST;
        g.drawImage(GAME, gameX, Y, W, H, null);

        // over text
        overX -= SPEED;
        if (overX <= OVER_MAX_DIST)
            overX = OVER_MAX_DIST;
        g.drawImage(OVER, overX, Y, W, H, null);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameGoToMenu();
            case KeyEvent.VK_ENTER -> game.getPlaying().resetGameGoToPlaying();
        }
    }

}
