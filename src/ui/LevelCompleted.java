package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static ui.Menu.*;

/**
 * When in level completed state, this class handles drawing a level completed image
 */
public class LevelCompleted extends GameState {

    // ======= Variables =======
    private static final BufferedImage img = ImageLoader.loadImage("ui/level-completed.png");

    // ====== Constructor ======
    public LevelCompleted(Game game) {
	    super(game);
    }

    public void drawLevelCompleted(Graphics g) {
        int w = (int) (1920 * SCALE);
        int h = (int) (1080 * SCALE);
        int x = GAME_WIDTH / 2 - w / 2;
        int y = GAME_HEIGHT / 2 - h / 2;
        g.drawImage(img, x, y, w, h, null);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE :
                game.getPlaying().resetGameGoToMenu();
                break;
            case KeyEvent.VK_ENTER :
                game.getPlaying().resetGameLoadNextLevel();
                break;
        }
    }
}
