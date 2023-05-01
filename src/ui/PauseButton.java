package ui;

import helpers.ImageLoader;
import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.GAME_WIDTH;
import static ui.Paused.isMuted;

/**
 * Initializes GUI for buttons on the menu
 * Listens for mouse events, handles game state switches and displays images
 */
public class PauseButton {

    // ====== Button Variables ======
    private static final BufferedImage BUTTON_IMAGES = ImageLoader.loadImage("/ui/pause-buttons.png");
    private static final BufferedImage UNMUTE_IMAGES = ImageLoader.loadImage("/ui/pause-buttons-unmute.png");
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 80;
    private static final int GAME_CENTER = GAME_WIDTH / 2 - BUTTON_W / 2;
    private final BufferedImage[] animations = new BufferedImage[3];
    private final BufferedImage[] unmuteAni = new BufferedImage[3];
    private Rectangle buttonBounds;
    private final float y;

    // ====== Index for buttons ======
    private final int buttonIndex;
    private int mouseIndex;
    private boolean mouseOverButton;
    private boolean mousePressButton;

    // ====== Constructor ======
    public PauseButton(int buttonIndex, float y) {
        this.buttonIndex = buttonIndex;
        this.y = y;
        initButtonImages();
        initButtonBounds();
        initUnmuteImages();
    }

    private void initButtonImages() {
        for (int i = 0; i < animations.length; i++)
            animations[i] = BUTTON_IMAGES.getSubimage(i * BUTTON_W, buttonIndex * BUTTON_H, BUTTON_W, BUTTON_H);
    }

    private void initUnmuteImages() {
        for (int i = 0; i < animations.length; i++)
            unmuteAni[i] = UNMUTE_IMAGES.getSubimage(i * BUTTON_W, buttonIndex * BUTTON_H, BUTTON_W, BUTTON_H);
    }

    private void initButtonBounds() {
        buttonBounds = new Rectangle(GAME_CENTER, (int) y, (int) (BUTTON_W * Game.SCALE), (int) (BUTTON_H * Game.SCALE));
    }

    public void draw(Graphics g) {
        if (isMuted) {
            g.drawImage(unmuteAni[mouseIndex], GAME_CENTER, (int) y, (int) (BUTTON_W * Game.SCALE), (int) (BUTTON_H * Game.SCALE), null);
        } else {
            g.drawImage(animations[mouseIndex], GAME_CENTER, (int) y, (int) (BUTTON_W * Game.SCALE), (int) (BUTTON_H * Game.SCALE), null);
        }
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
