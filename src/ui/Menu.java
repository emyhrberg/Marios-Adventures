package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static constants.GameState.OPTIONS;
import static constants.GameState.PLAYING;
import static ui.PauseButton.BUTTON_H;

/**
 * The Menu class represents the main menu of the game
 * Contains two buttons: Play and Quit which the user can click to start playing the game or exit game
 * Also loads GUI of background image and game title
 */
public class Menu extends GameState {

    // ====== Game Variables ======
    public static float SCALE;
    public static int TILES_SIZE_DEFAULT;
    public static int TILES_SIZE;
    public static int TILES_IN_WIDTH;
    public static int TILES_IN_HEIGHT;
    public static int GAME_WIDTH;
    public static int GAME_HEIGHT;
    private int userW;

    private final BufferedImage bgImage = ImageLoader.loadImage("/ui/menu-bg.jpg");
    private final BufferedImage titleImage = ImageLoader.loadImage("/ui/menu-title.png");
    private final MenuButton[] buttons = new MenuButton[3];

    // ====== Constructor ======
    public Menu(Game game) {
        super(game);
        initScale();
        initButtons();
    }

    private void initScale() {
        userW = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        if (userW == 1920) {
            SCALE = 1.5f;
        } else if (userW == 1280) {
            SCALE = 1.0f;
        } else {
            SCALE = 1.0f;
        }
        TILES_SIZE_DEFAULT  = 40;
        TILES_SIZE          = (int) (TILES_SIZE_DEFAULT * SCALE);
        TILES_IN_WIDTH      = 32;
        TILES_IN_HEIGHT     = 18;
        GAME_WIDTH          = TILES_SIZE * TILES_IN_WIDTH;
        GAME_HEIGHT         = TILES_SIZE * TILES_IN_HEIGHT;
    }

    private void initButtons() {
        final int PADDING 	= BUTTON_H;
        final int CENTER 	= GAME_HEIGHT / 2 - BUTTON_H / 2;

        buttons[0] = new MenuButton(0, CENTER);
        buttons[1] = new MenuButton(1, CENTER + PADDING);
        buttons[2] = new MenuButton(2, CENTER + 2 * PADDING);
    }

    public void update() {
        for (MenuButton button : buttons)
            button.update();
    }

    public void draw(Graphics g) {
        // Draw bg image
        g.drawImage(bgImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);

        // Draw bg title
        int w = (int) (640 * SCALE);
        int h = (int) (360 * SCALE);
        int y = (int) (40 * SCALE);
        int x = GAME_WIDTH / 2 - w / 2;
        g.drawImage(titleImage, x, y, w, h, null);

        // Draw buttons
        for (MenuButton button : buttons)
            button.draw(g);
    }

    private void selectButton(MenuButton b) {
        // PLAY
        if (b.getButtonIndex() == 0)
            game.setGameState(PLAYING);

        // OPTIONS
        if (b.getButtonIndex() == 1)
            game.setGameState(OPTIONS);

        // QUIT
        if (b.getButtonIndex() == 2)
            System.exit(0);
    }

    private boolean isButtonInsideBounds(MouseEvent e, MenuButton mb) {
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    public void mouseClicked(MouseEvent e) {
        for (MenuButton button : buttons)
            if (isButtonInsideBounds(e, button))
                button.setMousePressButton(true);
    }

    public void mouseReleased(MouseEvent e) {
        for (MenuButton b : buttons)
            if (isButtonInsideBounds(e, b) && b.isMousePressButton())
            {
                selectButton(b);
            }

        // reset button hover and press state
        for (MenuButton b : buttons) {
            b.setMouseOverButton(false);
            b.setMousePressButton(false);
        }
    }

    public void mouseMoved(MouseEvent e) {
        for (MenuButton button : buttons)
            button.setMouseOverButton(isButtonInsideBounds(e, button));
    }

    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                game.setGameState(PLAYING);
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    public int getUserW() {
        return userW;
    }

}