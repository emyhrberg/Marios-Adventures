package objects;

import constants.ObjectConstants.ObjectType;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static main.Game.SCALE;

public class Brick extends GameObject {

    // size
    public static final int BRICK_W_D = 40;
    public static final int BRICK_H_D = 40;
    public static final int BRICK_W = (int) (BRICK_W_D * SCALE);
    public static final int BRICK_H = (int) (BRICK_H_D * SCALE);

    public Brick(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        float off = 2 * SCALE;

        initHitbox(x + off, y + BRICK_H - off, BRICK_W_D - off, 10);
    }

    public void drawBottom(Graphics g, int levelOffset) {
        // draw hitbox
        g.setColor(new Color(255, 51, 215,100));
        g.fillRect((int) (hitbox.x - levelOffset), (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);

        // draw stroke
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(2)); // set stroke width
        g2d.setColor(Color.BLACK); // set stroke color
        g2d.drawRect((int) hitbox.x - levelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height); // draw the rectangle outline
    }

    public void update(Brick b) {
        updateBrickAnimation(b);
    }

    private void updateBrickAnimation(Brick b) {
        animationTick++;

        // Sparkle anim
        if (isSparkle) {
            if (animationTick >= ANIMATION_SPEED / 2) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, disable the animation!
            if (animationIndex == 7) {
                b.setSparkle(false);
            }
        }
    }

}
