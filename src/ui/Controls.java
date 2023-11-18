package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static constants.GameState.MENU;
import static constants.GameState.PLAYING;
import static ui.Menu.GAME_HEIGHT;
import static ui.Menu.GAME_WIDTH;

public class Controls extends GameState {

    private final BufferedImage CONTROLS_IMG = ImageLoader.loadImage("/ui/controls.png");

    public Controls(Game game) {
        super(game);
    }

    public void draw(Graphics g) {
        // bg
        if (game.getPrevState() == MENU) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }

        int w = CONTROLS_IMG.getWidth();
        int h = CONTROLS_IMG.getHeight();
        int x = GAME_WIDTH / 2 - w / 2;
        int y = GAME_HEIGHT / 2 - h / 2;
        g.drawImage(CONTROLS_IMG, x, y, w, h, null); // volume text
    }

    public void update() {
        // empty
    }

    public void mousePressed(MouseEvent e) {
        exitControls();
    }

    public void keyPressed(KeyEvent e) {
        exitControls();
    }

    private void exitControls() {
        // any key will enter menu if in menu
        // user is in playing and presses key -> will go back to playing
        if (game.getPrevState() == MENU)
            game.setGameState(MENU);
        else
            game.setGameState(PLAYING);
    }
}
