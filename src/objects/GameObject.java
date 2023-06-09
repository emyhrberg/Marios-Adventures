package objects;


import java.awt.*;
import java.awt.geom.Rectangle2D;

import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static ui.Menu.SCALE;

public class GameObject {

    // Object position
    protected final int x;
    protected final int y;
    protected final ObjectType objectType;
    protected Rectangle2D.Float hitbox;

    // Object properties
    protected static final int ANIMATION_SPEED = 20;
    protected boolean active = true;
    protected int animationTick, animationIndex;
    protected boolean isSparkle = false;
    protected boolean isBreaking = false;
    protected boolean isHit = false;

    public GameObject(int x, int y, ObjectType objectType) {
        this.x = x;
        this.y = y;
        this.objectType = objectType;
    }

    protected void updateAnimationTick() {
        animationTick++;

        // Reset animation tick and update animation index
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            animationIndex++;

            // Reset animation index when reached all images
            if (animationIndex >= getSpriteAmount(objectType))
                animationIndex = 0;
        }
    }

    public void resetObject() {
        animationIndex = 0;
        animationTick = 0;
        active = true;
        isHit = false;
        isSparkle = false;
        isBreaking = false;
        spawn = false;
    }

    protected boolean spawn;


    // Hitboxes

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    protected void initHitbox(float x, float y, float width, float height) {
        hitbox = new Rectangle2D.Float(x, y, width * SCALE, height * SCALE);
    }

    protected void drawHitbox(Graphics g, int levelOffset) {
        // draw hitbox
        g.setColor(new Color(48, 124, 234,100));
        g.fillRect((int) (hitbox.x - levelOffset), (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);

        // draw stroke
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(2)); // set stroke width
        g2d.setColor(Color.BLACK); // set stroke color
        g2d.drawRect((int) hitbox.x - levelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height); // draw the rectangle outline
    }

    protected void drawSomeBox(Rectangle2D.Float box, Color color, Graphics g, int levelOffset) {
        // draw hitbox
        g.setColor(color);
        g.fillRect((int) (box.x - levelOffset), (int) box.y, (int) box.width, (int) box.height);

        // draw stroke
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(2)); // set stroke width
        g2d.setColor(Color.BLACK); // set stroke color
        g2d.drawRect((int) box.x - levelOffset, (int) box.y, (int) box.width, (int) box.height); // draw the rectangle outline
    }

    // Getters nd setters

    public void setBreaking(boolean breaking) {
        isBreaking = breaking;
        animationTick = 0;
        animationIndex = 0;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
        animationTick = 0;
        animationIndex = 0;
    }

    public boolean isSparkle() {
        return isSparkle;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
