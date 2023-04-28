package objects;

import constants.ObjectConstants.ObjectType;
import helpers.SoundLoader;
import main.Game;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static main.Game.TILES_SIZE;
import static main.Game.TILES_SIZE_DEFAULT;

public class Brick extends GameObject {

    // list of hit bricks
    private final List<Brick> hitBricks = new ArrayList<>();

    // break hitbox
    private final Rectangle2D.Float breakBox;
    private static final float OFFSET = 2 * Game.SCALE;
    private static final float H = 10; // hitbox below the brick has a small height

    public Brick(int x, int y, ObjectType objectType) {
        super(x, y, objectType);


        initHitbox(x + OFFSET, y + TILES_SIZE - OFFSET, TILES_SIZE_DEFAULT - OFFSET, H);
        breakBox = new Rectangle2D.Float(x, y, TILES_SIZE, TILES_SIZE);
    }

    public void update(Player player, Level level, Brick b) {
        updateBreakingAnimation(b);
        removeBrickFromGame(player, level, b);
    }

    Rectangle2D intersection;

    private void removeBrickFromGame(Player player, Level level, Brick b) {
        if (b.isActive())
            if (player.getHitbox().intersects(b.getHitbox()) && player.getAirSpeed() < 0) {
                player.resetAirSpeed();
                hitBricks.add(b);
                b.setBreaking(true);

                // Determine which brick to delete by comparing shared surface area.
                Brick largestContact = null;
                double areaTemp = 0;
                for (Brick hb: hitBricks) {
                    intersection = hb.getHitbox().createIntersection(player.getHitbox());
                    double area = (intersection.getWidth()*intersection.getHeight());
                    if (area > areaTemp) {
                        largestContact = hb;
                    }
                }
                if (largestContact != null) {
                    int tileY = (int) (largestContact.getHitbox().y / TILES_SIZE);
                    int tileX = (int) (largestContact.getHitbox().x / TILES_SIZE);
                    level.getLevelData()[tileY][tileX] = 91;
                    largestContact.setActive(false);
                }
                SoundLoader.playSound("/sounds/brick.wav", 0.8);
            }
    }

    private void updateBreakingAnimation(Brick b) {
        if (isBreaking) {
            animationTick++;
            if (animationTick >= ANIMATION_SPEED / 2) {
                animationTick = 0;
                animationIndex++;
            }
            // at final break image, disable the animation!
            if (animationIndex == 4) {
                b.setBreaking(false);
            }
        }
    }

    // break-box for animation

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

}
