package misc;

import constants.GameState;
import helpers.ImageLoader;
import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.GAME_WIDTH;
import static main.Game.SCALE;

/**
 * Initializes GUI for buttons on the menu
 * Listens for mouse events, handles game state switches and displays images
 */
public class MenuButton {

    // ====== Button Variables ======
    private static final BufferedImage BUTTON_IMAGES = ImageLoader.loadImage("/images/menu_button_atlas.png");
    private static final int NUMBER_OF_IMAGES = 3;
    private static final int BUTTON_WIDTH_PIXELS = 125;
    private static final int BUTTON_HEIGHT_PIXELS = 40;
    private static final int BUTTON_WIDTH 		= (int) (1.5 * BUTTON_WIDTH_PIXELS * SCALE);
    private static final int BUTTON_HEIGHT 		= (int) (1.5 * BUTTON_HEIGHT_PIXELS * SCALE);
    private static final int BUTTON_CENTER 		= BUTTON_WIDTH / 2;
    private static final int GAME_CENTER 		= GAME_WIDTH / 2;

    // ====== Button Animation ======
    private BufferedImage[] animations;
    private Rectangle buttonBounds;
    private final int y;

    // ====== Index for buttons ======
    private final int buttonIndex;
    private int mouseIndex;
    private boolean mouseOverButton;
    private boolean mousePressButton;

    // ====== Game ======
    private final Game game;
    private final GameState state;

    // ====== Constructor ======
    public MenuButton(int buttonIndex, int y, GameState state, Game game) {
	this.buttonIndex = buttonIndex;
	this.y = y;
	this.state = state;
	this.game = game;
	initButtonImages();
	initButtonBounds();
    }

    private void initButtonImages() {
        // Not hovering, hovering and pressed are the images
        animations = new BufferedImage[NUMBER_OF_IMAGES];

        // Load button subimage
        for (int i = 0; i < animations.length; i++) {
            int x = i * BUTTON_WIDTH_PIXELS;
            int y = buttonIndex * BUTTON_HEIGHT_PIXELS;
            animations[i] = BUTTON_IMAGES.getSubimage(x, y, BUTTON_WIDTH_PIXELS, BUTTON_HEIGHT_PIXELS);
        }
    }

    private void initButtonBounds() {
	    buttonBounds = new Rectangle(GAME_CENTER - BUTTON_CENTER, y, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    public void draw(Graphics g) {
        int x = GAME_CENTER - BUTTON_CENTER;
        int y = this.y;
        g.drawImage(animations[mouseIndex], x, y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
    }

    public void update() {
        // Default, index 0
        mouseIndex = 0;

        // Mouse over, index 1
        if (mouseOverButton)
            mouseIndex = 1;

        // Mouse pressed, index 2
        if (mousePressButton)
            mouseIndex = 2;
    }

    public void resetMouseAction() {
        mousePressButton = false;
        mouseOverButton = false;
    }

    public void updateGameState() {
        switch (state) {
            case PLAYING 	-> game.setGameState(GameState.PLAYING);
            case QUIT 		-> System.exit(0);
        }
    }

    // ====== Getters ======

    public boolean isMousePressButton() {
	    return mousePressButton;
    }

    public Rectangle getButtonBounds() {
	    return buttonBounds;
    }

    // ====== Setters ======

    public void setMouseOverButton(final boolean mouseOverButton) {
	    this.mouseOverButton = mouseOverButton;
    }

    public void setMousePressButton(final boolean mousePressButton) {
	    this.mousePressButton = mousePressButton;
    }
}
