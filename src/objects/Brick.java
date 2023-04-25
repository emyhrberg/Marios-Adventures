package objects;

import constants.ObjectConstants.ObjectType;
import helpers.SoundLoader;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static main.Game.*;

public class Brick extends GameObject {

    // list of hit bricks
    private final List<Brick> hitBricks = new ArrayList<>();

    // break hitbox
    private final Rectangle2D.Float breakBox;

    public Brick(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        float off = 2 * SCALE;
        float height = 10; // hitbox below the brick has a small height

        initHitbox(x + off, y + TILES_SIZE - off, TILES_SIZE_DEFAULT - off, height);
        breakBox = new Rectangle2D.Float(x, y, TILES_SIZE, TILES_SIZE);
    }

    public void update(Level level, Player player, Brick b) {
        updateBreakingAnimation(b);
        removeBrickFromGame(level, player, b);
    }

    private void removeBrickFromGame(Level level, Player player, Brick b) {
        if (b.isActive())
            if (player.getHitbox().intersects(b.getHitbox()) && player.getAirSpeed() < 0) {
                hitBricks.add(b);
                player.setAirSpeed(0);
                b.setBreaking(true);

                // Determine which brick to delete by comparing shared surface area.
                Brick largestContact = null;
                double areaTemp = 0;
                for (Brick b2: hitBricks) {
                    Rectangle2D intersection = b2.getHitbox().createIntersection(player.getHitbox());
                    double area = (intersection.getWidth()*intersection.getHeight());
                    if (area > areaTemp) {
                        largestContact = b2;
                    }
                }
                if (largestContact != null) {
                    largestContact.setActive(false);
                    float tileY = largestContact.getHitbox().y / TILES_SIZE;
                    float tileX = largestContact.getHitbox().x / TILES_SIZE;
                    level.getLevelData()[(int) (tileY)][(int) (tileX)] = 91;
                }
                SoundLoader.playAudio("/audio/brick.wav", 0.8);
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
