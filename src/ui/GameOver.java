package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static constants.GameState.MENU;
import static ui.Menu.*;


/**
 * When in game over state, this class handles drawing game over image
 */
public class GameOver extends GameState {

    private static final BufferedImage GAME = ImageLoader.loadImage("/ui/game.png");
    private static final BufferedImage OVER = ImageLoader.loadImage("/ui/over.png");
    private static final int W = 300;
    private static final int H = 100;
    private static final int Y = GAME_HEIGHT / 2 - H / 2;
    private static final int GAME_X_INIT = -W;
    private static final int OVER_X_INIT = GAME_WIDTH;
    private int gameX = GAME_X_INIT;
    private int overX = OVER_X_INIT;
    private static final float SPEED = (int) (10 * SCALE);
    private static final int DIST = (int) (670 * SCALE);
    private static final int GAME_MAX_DIST = GAME_X_INIT + DIST;
    private static final int OVER_MAX_DIST = OVER_X_INIT - DIST;
    private long firstCheckTime;
    private int alpha = 0;

    // ====== Constructor ======
    public GameOver(Game game) {
        super(game);
    }

    public void drawGameOver(Graphics g) {
        if (game.isKeyNotAllowed()) {
//            return;
        }
        if (game.isDrawNotAllowed())
            return;

        // set initial settings for game over screen
        if (game.isFirstTime()) {
            gameX = GAME_X_INIT;
            overX = OVER_X_INIT;
            alpha = 0;
            firstCheckTime = System.currentTimeMillis();
            game.setFirstTime(false);
        }

        fadeToBlack(g);

        // wait for one second
        if (System.currentTimeMillis() - firstCheckTime <= 1000)
            return;

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

    private void fadeToBlack(Graphics g) {
        alpha += 5;
        if (alpha > 255)
            alpha = 255;

        // Draw a rectangle with opacity
        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                game.setGameState(MENU);
                break;
            case KeyEvent.VK_ENTER:
                game.getPlaying().resetGameGoToPlaying();
                break;
        }
    }

    public void mousePressed(MouseEvent e) {
        game.getPlaying().resetGameGoToPlaying();
    }

}