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

    // break hitbox
    private final Rectangle2D.Float breakBox;

    public Brick(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        float off = 2 * SCALE;
        float height = 10; // hitbox below the brick has a small height

        initHitbox(x + off, y + BRICK_H - off, BRICK_W_D - off, height);
        breakBox = new Rectangle2D.Float(x, y, BRICK_W, BRICK_H);
    }

    public Rectangle2D.Float getBreakBox() {
        return breakBox;
    }

    protected void drawBreakBox(Graphics g, int levelOffset) {
        // draw hitbox
        g.setColor(new Color(177, 51, 241,100));
        g.fillRect((int) (breakBox.x - levelOffset), (int) breakBox.y, (int) breakBox.width, (int) breakBox.height);

        // draw stroke
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(2)); // set stroke width
        g2d.setColor(Color.BLACK); // set stroke color
        g2d.drawRect((int) breakBox.x - levelOffset, (int) breakBox.y, (int) breakBox.width, (int) breakBox.height); // draw the rectangle outline
    }

    public void update(Brick b) {
        updateBrickAnimation(b);
    }

    private void updateBrickAnimation(Brick b) {
        animationTick++;

        // Sparkle anim
        if (isBreaking) {
            if (animationTick >= ANIMATION_SPEED) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, disable the animation!
            if (animationIndex == 4) {
                b.setBreaking(false);
            }
        }
    }

}
