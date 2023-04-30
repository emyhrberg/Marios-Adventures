package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * The Menu class represents the main menu of the game
 * Contains two buttons: Play and Quit which the user can click to start playing the game or exit game
 * Also loads GUI of background image and game title
 */
public class Menu extends GameState {

    private static final BufferedImage BACKGROUND_IMAGE   = ImageLoader.loadImage("/ui/menu-bg.jpg");
    private static final BufferedImage TITLE_IMAGE        = ImageLoader.loadImage("/ui/menu-title.png");
    private final MenuButton[] buttons                    = new MenuButton[2];

    // ====== Constructor ======
    public Menu(Game game) {
        super(game);
        loadButtons();
    }

    private void loadButtons() {
        int startY = (int) (300 * Game.SCALE);
        int quitY = (int) (380 * Game.SCALE);

        buttons[0] = new MenuButton(0, startY);
        buttons[1] = new MenuButton(1, quitY);
    }

    public void update() {
        for (MenuButton button : buttons)
            button.update();
    }

    public void draw(Graphics g) {
        // Draw bg image
        g.drawImage(BACKGROUND_IMAGE, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        // Draw bg title
        int w = (int) (1280 / 2 * Game.SCALE);
        int h = (int) (720 / 2 * Game.SCALE);
        int y = (int) (40 * Game.SCALE);
        int x = Game.GAME_WIDTH / 2 - w / 2;
        g.drawImage(TITLE_IMAGE, x, y, w, h, null);

        // Draw buttons
        for (MenuButton button : buttons)
            button.draw(g);
    }

    private boolean isButtonInsideBounds(MouseEvent e, MenuButton mb) {
        return mb.getButtonBounds().contains(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        for (MenuButton button : buttons)
            if (isButtonInsideBounds(e, button))
                button.setMousePressButton(true);
    }

    public void mouseReleased(MouseEvent e) {
        for (MenuButton b : buttons)
            if (isButtonInsideBounds(e, b) && b.isMousePressButton()) {

                // do stuff depending on button pressed
                if (b.getButtonIndex() == 0) {
                    game.setGameState(constants.GameState.PLAYING);
                } else if (b.getButtonIndex() == 1) {
                    System.exit(0);
                }
                break;
            }

        // Reset buttons
        for (MenuButton button : buttons) {
            button.setMouseOverButton(false);
            button.setMousePressButton(false);
        }
    }

    public void mouseMoved(MouseEvent e) {
        for (MenuButton button : buttons)
            button.setMouseOverButton(isButtonInsideBounds(e, button));
    }

    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                game.setGameState(constants.GameState.PLAYING);
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }
}