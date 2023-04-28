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

    // ====== Images ======
    private static final BufferedImage BACKGROUND_IMAGE   = ImageLoader.loadImage("/ui/menu-bg.jpg");
    private static final BufferedImage TITLE_IMAGE        = ImageLoader.loadImage("/ui/menu-title.png");

    // ====== Title Position ======
    private static final int TITLE_WIDTH    = (int) (1280 / 2 * Game.SCALE);
    private static final int TITLE_HEIGHT   = (int) (720 / 2 * Game.SCALE);
    private static final int TITLE_X        = Game.GAME_WIDTH / 2 - TITLE_WIDTH / 2;
    private static final int TITLE_Y        = (int) (40 * Game.SCALE);

    // ====== Buttons ======
    private static final int START_BUTTON_Y = (int) (300 * Game.SCALE);
    private static final int QUIT_BUTTON_Y  = (int) (370 * Game.SCALE);
    private static final int BUTTON_COUNT   = 2;
    private final MenuButton[] buttons      = new MenuButton[BUTTON_COUNT];

    // ====== Constructor ======
    public Menu(Game game) {
        super(game);
        loadButtons();
    }

    private void loadButtons() {
        buttons[0] = new MenuButton(0, START_BUTTON_Y, constants.GameState.PLAYING, game);
        buttons[1] = new MenuButton(1, QUIT_BUTTON_Y, constants.GameState.QUIT, game);
    }

    /**
     * Updates the buttons in the menu by iterating through them and calling the update function
     */
    public void update() {
        for (MenuButton button : buttons)
            button.update();
    }

    public void draw(Graphics g) {
        // Draw bg image
        g.drawImage(BACKGROUND_IMAGE, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        // Draw bg title
        g.drawImage(TITLE_IMAGE, TITLE_X, TITLE_Y, TITLE_WIDTH, TITLE_HEIGHT, null);

        // Draw buttons
        for (MenuButton button : buttons)
            button.draw(g);
    }

    private boolean isButtonInsideBounds(MouseEvent e, MenuButton mb) {
        return mb.getButtonBounds().contains(e.getX(), e.getY());
    }

    public void updateMousePress(MouseEvent e) {
        for (MenuButton button : buttons) {
            if (isButtonInsideBounds(e, button)) {
                button.setMousePressButton(true);
            }
        }
    }

    public void updateMouseReleased(MouseEvent e) {
        for (MenuButton button : buttons) {
            if (isButtonInsideBounds(e, button)) {
                if (button.isMousePressButton()) {
                    button.updateGameState();
                }
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for (MenuButton button : buttons)
            button.resetMouseAction();
    }

    public void setMouseOverButton(MouseEvent e) {
        for (MenuButton button : buttons) {
            button.setMouseOverButton(isButtonInsideBounds(e, button));
        }
    }

    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER  -> game.setGameState(constants.GameState.PLAYING);
            case KeyEvent.VK_ESCAPE -> System.exit(0);
        }
    }
}