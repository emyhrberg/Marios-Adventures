package ui;

import main.Game;
import main.GameState;

import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static constants.GameState.MENU;
import static constants.GameState.PLAYING;
import static ui.Menu.*;

public class Options extends GameState {

    // pos
    private final int w;
    private final int h;
    private final int x;
    private final int y;

    // volume bar
    private final Rectangle bounds;
    private boolean dragging = false;

    // volume levels
    private double percentVolume = 0.2;
    private int volume = 20;
    public static float actualVolume = 20 * 0.5f - 44;

    public Options(Game game) {
        super(game);

        w = (int) (400 * SCALE);
        h = (int) (30 * SCALE);
        x = GAME_WIDTH / 2 - w / 2;
        y = GAME_HEIGHT / 2 - h / 2;
        bounds = new Rectangle(x, y, w, h);

        System.out.println("set to initial: " + volume);
    }

    public void draw(Graphics g) {
        // bg
        g.setColor(new Color(50,77,120, 120));
        g.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);

        // text
        String v = "Volume: " + volume;
        g.setFont(g.getFont().deriveFont(72f));
        g.setColor(new Color(244,244,244));
        int textH = g.getFontMetrics().getHeight();
        int textW = g.getFontMetrics().stringWidth(v);
        g.drawString(v, x + w / 2 - textW / 2, y - textH * 2);

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
        g.setColor(new Color(48,242,3));
        g.fillRoundRect(x, y, (int) (w * percentVolume), h, arc, arc);
    }

    public void mousePressed(MouseEvent e) {
        if (bounds.contains(e.getPoint()))
            updateVolume(e);
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
        else if (percentVolume <= 0.01) {
            percentVolume = 0;
            if (game.getMenuClip() != null && game.getMenuClip().isActive()) {
                FloatControl control = (FloatControl) game.getMenuClip().getControl(FloatControl.Type.MASTER_GAIN);
                control.setValue(control.getMinimum());
                System.out.println("0! set to: " + 0 + " | which is: " + volume);
                return;
            }
        }

        // convert to 0-100 volume
        volume = (int) (percentVolume * 100);

        // convert to -60 to 8 volume
        actualVolume = volume * 0.5f - 44;

        // Check if the audio clip is currently playing
        if (game.getMenuClip() != null && game.getMenuClip().isActive()) {
            FloatControl gainControl = (FloatControl) game.getMenuClip().getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(actualVolume);
            System.out.println("set to: " + actualVolume + " | which is: " + volume);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (game.getPrevState() == MENU)
                game.setGameState(MENU);
            else
                game.setGameState(PLAYING);
        }
    }

    public void update() {

    }
}
