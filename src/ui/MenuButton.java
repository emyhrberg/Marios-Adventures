package ui;

import helpers.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ui.Menu.GAME_WIDTH;
import static ui.Menu.SCALE;

/**
 * Initializes GUI for buttons on the menu
 * Listens for mouse events, handles game state switches and displays images
 */
public class MenuButton {

    // ====== Button Variables ======
    private static final BufferedImage BUTTON_IMAGES = ImageLoader.loadImage("/ui/menu-buttons.png");
    private static final int BUTTON_W = 605;
    public static final int BUTTON_H = 123;

    // ====== Button Animation ======
    private final BufferedImage[] animations = new BufferedImage[3];
    private Rectangle bounds;
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
        for (int i = 0; i < animations.length; i++)
            animations[i] = BUTTON_IMAGES.getSubimage(i * BUTTON_W, buttonIndex * BUTTON_H, BUTTON_W, BUTTON_H);
    }

    private void initButtonBounds() {
        int w = (int) (BUTTON_W / 2 * SCALE);
        int h = (int) (BUTTON_H / 2 * SCALE);
        int x = GAME_WIDTH / 2 - w / 2;
        bounds = new Rectangle(x, y, w, h);
    }

    public void draw(Graphics g) {
        int w = (int) (BUTTON_W / 2 * SCALE);
        int h = (int) (BUTTON_H / 2 * SCALE);
        int x = GAME_WIDTH / 2 - w / 2;
        g.drawImage(animations[mouseIndex], x, y, w, h, null);
    }

    public void update() {
        mouseIndex = 0;
        if (mouseOverButton)
            mouseIndex = 1;
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

    public Rectangle getBounds() {
	    return bounds;
    }

    public void setMouseOverButton(final boolean mouseOverButton) {
	    this.mouseOverButton = mouseOverButton;
    }

    public void setMousePressButton(final boolean mousePressButton) {
	    this.mousePressButton = mousePressButton;
    }
}
