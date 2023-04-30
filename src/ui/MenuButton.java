package ui;

import helpers.ImageLoader;
import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.GAME_WIDTH;

/**
 * Initializes GUI for buttons on the menu
 * Listens for mouse events, handles game state switches and displays images
 */
public class MenuButton {

    // ====== Button Variables ======
    private static final BufferedImage BUTTON_IMAGES = ImageLoader.loadImage("/ui/menu-buttons.png");
    private static final int BUTTON_W = 163;
    private static final int BUTTON_H = 80;
    private static final int GAME_CENTER = GAME_WIDTH / 2 - BUTTON_W / 2;

    // ====== Button Animation ======
    private final BufferedImage[] animations = new BufferedImage[3];
    private Rectangle buttonBounds;
    private final int y;

    // ====== Index for buttons ======
    private final int buttonIndex;
    private int mouseIndex;
    private boolean mouseOverButton;
    private boolean mousePressButton;

    // ====== Constructor ======
    public MenuButton(int buttonIndex, int y) {
        this.buttonIndex = buttonIndex;
        this.y = y;
        initButtonImages();
        initButtonBounds();
    }

    private void initButtonImages() {
        for (int i = 0; i < animations.length; i++) {
            int x = i * BUTTON_W;
            int y = buttonIndex * BUTTON_H;
            animations[i] = BUTTON_IMAGES.getSubimage(x, y, BUTTON_W, BUTTON_H);
        }
    }

    private void initButtonBounds() {
	    buttonBounds = new Rectangle(GAME_CENTER, y, (int) (BUTTON_W * Game.SCALE), (int) (BUTTON_H * Game.SCALE));
    }

    public void draw(Graphics g) {
        g.drawImage(animations[mouseIndex], GAME_CENTER, y, (int) (BUTTON_W * Game.SCALE), (int) (BUTTON_H * Game.SCALE), null);
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


    // ====== Getters & Setters ======

    public int getButtonIndex() {
        return buttonIndex;
    }

    public boolean isMousePressButton() {
	    return mousePressButton;
    }

    public Rectangle getButtonBounds() {
	    return buttonBounds;
    }

    public void setMouseOverButton(final boolean mouseOverButton) {
	    this.mouseOverButton = mouseOverButton;
    }

    public void setMousePressButton(final boolean mousePressButton) {
	    this.mousePressButton = mousePressButton;
    }
}
