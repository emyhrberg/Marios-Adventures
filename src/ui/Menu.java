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

    // ====== Game Variables ======
    public static float SCALE          ;
    public static int TILES_SIZE_DEFAULT  ;
    public static int TILES_SIZE          ;
    public static int TILES_IN_WIDTH       ;
    public static int TILES_IN_HEIGHT      ;
    public static int GAME_WIDTH           ;
    public static int GAME_HEIGHT        ;
    private int userW;

    // ====== Constructor ======
    public Menu(Game game) {
        super(game);
        initScale();
        loadButtons();
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

    public int getUserW() {
        return userW;
    }

    private static final BufferedImage BACKGROUND_IMAGE   = ImageLoader.loadImage("/ui/menu-bg.jpg");
    private static final BufferedImage TITLE_IMAGE        = ImageLoader.loadImage("/ui/menu-title.png");
    private final MenuButton[] buttons                    = new MenuButton[2];

    private void loadButtons() {
        int startY = (int) (300 * SCALE);
        int quitY = (int) (380 * SCALE);

        buttons[0] = new MenuButton(0, startY);
        buttons[1] = new MenuButton(1, quitY);
    }

    public void update() {
        for (MenuButton button : buttons)
            button.update();
    }

    public void draw(Graphics g) {
        // Draw bg image
        g.drawImage(BACKGROUND_IMAGE, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);

        // Draw bg title
        int w = (int) (1280 / 2 * SCALE);
        int h = (int) (720 / 2 * SCALE);
        int y = (int) (40 * SCALE);
        int x = GAME_WIDTH / 2 - w / 2;
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
            if (isButtonInsideBounds(e, b) && b.isMousePressButton())
                buttonPressed(b);

        for (MenuButton button : buttons) {
            button.setMouseOverButton(false);
            button.setMousePressButton(false);
        }
    }

    private void buttonPressed(MenuButton b) {
        if (b.getButtonIndex() == 0)
            game.setGameState(constants.GameState.PLAYING);
        if (b.getButtonIndex() == 1)
            System.exit(0);
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