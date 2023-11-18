package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static constants.GameState.MENU;
import static constants.GameState.PLAYING;
import static ui.Menu.*;

public class Volume extends GameState {

    // pos
    private final int w;
    private final int h;
    private final int x;
    private final int y;

    // volume UI
    private static final BufferedImage VOLUME_TEXT = ImageLoader.loadImage("/ui/volume.png");
    private static final BufferedImage VOLUME_NUMS = ImageLoader.loadImage("/ui/volume-nums.png");
    private static final BufferedImage[] nums = new BufferedImage[10];
    private final Rectangle bounds;
    private boolean dragging = false;

    // volume function
    private double percentVolume = 0.2;
    private int volume = 20;
    public static float actualVolume = 20 * 0.5f - 44;

    public Volume(Game game) {
        super(game);

        // Init volume rectangle bounds
        w = (int) (400 * SCALE);
        h = (int) (30 * SCALE);
        x = GAME_WIDTH / 2 - w / 2;
        y = GAME_HEIGHT / 2 - h / 2;
        bounds = new Rectangle(x, y, w, h);

        // Init volume numbers
        for (int i = 0; i < 10; i++)
            nums[i] = VOLUME_NUMS.getSubimage(64 * i, 0, 64, 84);
    }

    public void draw(Graphics g) {
        // bg
        if (game.getPrevState() == MENU) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }

        // text
        int numW = 64;
        int numH = 84;
        int volumeW = VOLUME_TEXT.getWidth();
        int volumeH = VOLUME_TEXT.getHeight();
        int volumeY = y + h / 2 - volumeH / 2 - h * 2;

        int padding1 = 20;
        int padding2 = -10;
        int leftmostX = x + (w - volumeW - 2 * numW - padding1 * 2 - padding2 * 2) / 2;

        // Draw the 3 rectangles
//        g.drawRect(leftmostX + volumeW + padding1, volumeY, numW, numH);
//        g.drawRect(leftmostX + volumeW + padding1 + numW + padding2, volumeY, numW, numH);
//        g.drawRect(leftmostX + volumeW + padding1 + 2 * numW + padding2 * 2, volumeY, numW, numH);

        // Draw volume text
//        g.drawRect(leftmostX, volumeY, volumeW, volumeH);
        g.drawImage(VOLUME_TEXT, leftmostX, volumeY, volumeW, volumeH, null); // volume text

        // nums
        int num2 = (volume % 100) / 10;
        int num3 = volume % 10;
        if (volume == 100) {
            g.drawImage(nums[volume / 100], leftmostX + volumeW + padding1, volumeY, numW, numH, null);
            g.drawImage(nums[num2], leftmostX + volumeW + padding1 + numW + padding2, volumeY, numW, numH, null);
            g.drawImage(nums[num3], leftmostX + volumeW + padding1 + 2 * numW + padding2 * 2, volumeY, numW, numH, null);
        } else {
            g.drawImage(nums[num2], leftmostX + volumeW + padding1, volumeY, numW, numH, null);
            g.drawImage(nums[num3], leftmostX + volumeW + padding1 + numW + padding2, volumeY, numW, numH, null);
        }

        // background white volume bar
        int arc = 25;
        g.setColor(new Color(210,210,210));
        g.fillRoundRect(x, y, w, h, arc, arc);

        // draw stroke
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(3)); // set stroke width
        g2d.setColor(Color.BLACK); // set stroke color
        g2d.drawRoundRect(x,y,w,h,arc,arc); // draw the rectangle outline

        // fill volume bar
        int stroke = 3;
        int fillX = x + stroke;
        int fillY = y + stroke;
        int fillW;

        // adjust for volume 3, 2, 1, 0
        if (volume == 3) {
            fillW = (int) (w * 0.04) - stroke * 2;
        } else if (volume == 2) {
            fillW = (int) (w * 0.03) - stroke * 2;
        } else if (volume == 1) {
            fillW = (int) (w * 0.02) - stroke * 2;
        } else if (volume == 0) {
            fillW = 0;
        } else {
            fillW = (int) (w * percentVolume) - stroke * 2;
        }
        int fillH = h - stroke * 2;
        g.setColor(new Color(48,242,3));
        g.fillRoundRect(fillX, fillY, fillW, fillH, arc - stroke, arc - stroke);

    }

    public void mousePressed(MouseEvent e) {
        if (bounds.contains(e.getPoint()))
            updateVolume(e);
        else
        if (game.getPrevState() == MENU)
            game.setGameState(MENU);
        else
            game.setGameState(PLAYING);
    }

    public void mouseDragged(MouseEvent e) {
        if (bounds.contains(e.getPoint()))
            dragging = true;

        if (dragging)
            updateVolume(e);
    }

    public void mouseReleased() {
        dragging = false;
    }

    private void updateVolume(MouseEvent e) {
        // get how much of the volume bar is filled
        double x = e.getPoint().getX() - bounds.getX();
        percentVolume = x / w;

        // min and max values for drawing volume bar
        if (percentVolume >= 1)
            percentVolume = 1;
        else if (percentVolume <= 0.00) {
            percentVolume = 0;
        }

        // convert to
        // 0 to 100 volume
        volume = (int) (percentVolume * 100);

        // convert to
        // -60 to 8 volume
        actualVolume = volume * 0.5f - 44;

        // Check if the audio clip is currently playing
        if (game.getMenuClip() != null && game.getMenuClip().isActive()) {
            FloatControl control = (FloatControl) game.getMenuClip().getControl(FloatControl.Type.MASTER_GAIN);

            if (volume <= 0)
                actualVolume = control.getMinimum();

            control.setValue(actualVolume);

//            System.out.println("set to: " + actualVolume + " | which is: " + volume);
        } else if (game.getPlayingClip() != null && game.getPlayingClip().isActive()) {
            FloatControl control = (FloatControl) game.getPlayingClip().getControl(FloatControl.Type.MASTER_GAIN);

            if (volume <= 0)
                actualVolume = control.getMinimum();

            control.setValue(actualVolume);
//            System.out.println("set to: " + actualVolume + " | which is: " + volume);
        }
    }

    public void keyPressed(KeyEvent e) {
        // any key will exit settings
        if (game.getPrevState() == MENU)
            game.setGameState(MENU);
        else
            game.setGameState(PLAYING);

    }

    public void update() {

    }
}
