package objects;

import helpers.ImageLoader;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static main.Game.TILES_SIZE;
import static main.Game.TILES_SIZE_DEFAULT;
import static objects.Bullet.*;
import static objects.Cannon.*;
import static objects.Coin.COIN_SIZE;
import static objects.HealthPowerup.HEALTH_SIZE;
import static objects.Lava.*;
import static objects.Pipe.PIPE_HEIGHT;
import static objects.Pipe.PIPE_WIDTH;
import static objects.Platform.*;

public class ObjectManager {

    // ====== Coins =======
    private final BufferedImage[] coinImages            = new BufferedImage[4];
    private static final BufferedImage COIN_IMAGES      = ImageLoader.loadImage("/images/sprites_coin.png");
    public static final int PIXEL_SIZE                  = 16;

    // ====== Questions =======
    private final BufferedImage[][] questionImages      = new BufferedImage[2][4];
    private static final BufferedImage QUESTION_IMAGES  = ImageLoader.loadImage("/images/sprites_question.png");

    // ====== Platforms =======
    private static final BufferedImage PLATFORM_IMAGES  = ImageLoader.loadImage("/images/sprites_platform.png");

    // ====== Lava =======
    private static final BufferedImage LAVA_IMAGES      = ImageLoader.loadImage("/images/sprites_lava.png");

    // ====== Sparkle =======
    private static final int SPARKLE_W_DEF = 100;
    private static final int SPARKLE_H_DEF = 100;
    private static final BufferedImage SPARKLE_IMAGES   = ImageLoader.loadImage("/images/sprites_sparkle.png");
    private final BufferedImage[] sparkleImages         = new BufferedImage[7];

    // ====== Break brick =======
    private static final BufferedImage BREAK_BRICK_IMAGES = ImageLoader.loadImage("/images/sprites_break_brick.png");
    private final BufferedImage[] breakImages = new BufferedImage[4];

    // ====== Pipes =======
    private static final BufferedImage PIPE_IMAGES      = ImageLoader.loadImage("/images/sprites_pipe.png");

    // ====== Cannons ======
    private static final BufferedImage CANNON_IMAGES    = ImageLoader.loadImage("/images/sprites_cannon.png");
    private final BufferedImage[] cannonImages          = new BufferedImage[7];

    // ====== Bullets ======
    private static final BufferedImage BULLET_IMAGE     = ImageLoader.loadImage("/images/sprites_bullet.png");
    private final BufferedImage[] bulletImages          = new BufferedImage[8];
    public static final List<Bullet> bullets = new ArrayList<>();

    // ====== Power-ups ======
    private static final BufferedImage POWERUP_HEALTH   = ImageLoader.loadImage("/images/powerup_health.png");
    public static final ArrayList<HealthPowerup> healths = new ArrayList<>();

    // ====== Flags ======
    private static final BufferedImage FLAG_IMAGES      = ImageLoader.loadImage("/images/sprites_flag.png");
    private final BufferedImage[] flagImages            = new BufferedImage[3];

    // ====== List of objects ======
    private List<Coin> coins            = new ArrayList<>();
    private List<Question> questions    = new ArrayList<>();
    private List<Platform> platforms    = new ArrayList<>();
    private List<Lava> lava             = new ArrayList<>();
    private List<Pipe> pipes            = new ArrayList<>();
    private List<Cannon> cannons        = new ArrayList<>();
    private List<Brick> bricks          = new ArrayList<>();
    private List<Flag> flags           = new ArrayList<>();


    public ObjectManager() {
        initObjects();
    }

    private void initObjects() {
        // Init coins
        for (int i = 0; i < 4; i++) {
            coinImages[i] = COIN_IMAGES.getSubimage(PIXEL_SIZE * i, 0, PIXEL_SIZE, PIXEL_SIZE);
        }

        // Init questions
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                questionImages[j][i] = QUESTION_IMAGES.getSubimage(PIXEL_SIZE * i, PIXEL_SIZE * j, PIXEL_SIZE, PIXEL_SIZE);
            }
        }

        // Init sparkles
        for (int i = 0; i < 7; i++) {
            sparkleImages[i] = SPARKLE_IMAGES.getSubimage(SPARKLE_W_DEF * i, 0, SPARKLE_W_DEF, SPARKLE_H_DEF);
        }

        // Init breaks
        for (int i = 0; i < 4; i++) {
            breakImages[i] = BREAK_BRICK_IMAGES.getSubimage(TILES_SIZE_DEFAULT * i, 0, TILES_SIZE_DEFAULT, TILES_SIZE_DEFAULT);
        }

        // Init cannons
        for (int i = 0; i < 7; i++) {
            cannonImages[i] = CANNON_IMAGES.getSubimage(CANNON_W_DEF * i, 0, CANNON_W_DEF, CANNON_H_DEF);
        }

        // Init bullets
        for (int i = 0; i < 8; i++) {
            bulletImages[i] = BULLET_IMAGE.getSubimage(BULLET_W_DEF * i, 0, BULLET_W_DEF, BULLET_H_DEF);
        }

        // Init flags
        for (int i = 0; i < 3; i++) {
            flagImages[i] = FLAG_IMAGES.getSubimage(16 * i, 0, 16, 16);
        }
    }

    // ====== Update ======

    public void update(Level level, Player player) {
        flags       = level.getFlags();
        questions   = level.getQuestions();
        bricks      = level.getBricks();
        lava        = level.getLava();
        platforms   = level.getPlatforms();
        coins       = level.getCoins();
        cannons     = level.getCannons();
        pipes       = level.getPipes();

        for (Flag f : flags) f.update();
        for (Question q : questions) q.update(player, q);
        for (Brick b : bricks) b.update(level, player, b);
        for (Lava l: lava) l.update(player, l);
        for (Platform p : platforms) p.update(player, level, p);
        for (Coin c : coins) c.update(player, c);
        for (Cannon c: cannons) c.update(c);
        for (HealthPowerup h : healths) h.update(level, player, h);
        for (Bullet b : bullets) b.update(level, player, b);
    }

    // ====== Draw ======

    public void draw(Graphics g, int levelOffset) {
        drawCoins(g, levelOffset);
        drawQuestion(g, levelOffset);
        drawPlatforms(g, levelOffset);
        drawLava(g, levelOffset);
        drawPipes(g, levelOffset);
        drawBullets(g, levelOffset);
        drawCannons(g, levelOffset);
        drawBricks(g, levelOffset);
        drawHealths(g, levelOffset);
        drawFlags(g, levelOffset);
    }

    private void drawPlatforms(Graphics g, int levelOffset) {
        for (Platform p : platforms)  {
            int x = (int) p.hitbox.x - levelOffset;
            int y = (int) p.hitbox.y;
            g.drawImage(PLATFORM_IMAGES, x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT, null);

            // Debug hitboxes
//            p.drawSomeBox(p.hitbox, Color.GREEN, g, levelOffset);
//            p.drawSomeBox(p.getTop(), Color.BLUE, g, levelOffset);
//            p.drawSomeBox(p.getBottom(), Color.DARK_GRAY, g, levelOffset);
//            p.drawSomeBox(p.getBottomLine(), Color.YELLOW, g, levelOffset);
        }
    }

    private void drawHealths(Graphics g, int levelOffset) {
        for (HealthPowerup h : healths) {
            if (h.isActive()) {
                float x = h.getHitbox().x - levelOffset;
                float y = h.getHitbox().y;
                g.drawImage(POWERUP_HEALTH, (int) x, (int) y, HEALTH_SIZE, HEALTH_SIZE, null);

                // Debug hitbox
//                h.drawHitbox(g, levelOffset);
            }
        }
    }

    private void drawBullets(Graphics g, int levelOffset) {
        for (Bullet b : bullets) {
            if (b.isActive()) {
                float x = b.hitbox.x - levelOffset;
                float y = b.hitbox.y - Y_DRAW_OFF;
                g.drawImage(bulletImages[b.animationIndex], (int)x, (int)y, BULLET_W, BULLET_H, null);
//                b.drawHitbox(g, levelOffset);
            }
        }
    }

    private void drawBricks(Graphics g, int levelOffset) {
        for (Brick b : bricks)  {
            if (b.isBreaking) {
                int x = (int) b.getBreakBox().x - levelOffset;
                int y = (int) b.getBreakBox().y;
                g.drawImage(breakImages[b.animationIndex],x,y, TILES_SIZE,TILES_SIZE,null);
            }

            // Debug
//            b.drawBreakBox(g, levelOffset);
//            b.drawHitbox(g, levelOffset);
        }
    }

    private void drawPipes(Graphics g, int levelOffset) {
        for (Pipe p : pipes)  {
            int x = (int) p.hitbox.x - levelOffset;
            int y = (int) p.hitbox.y;
            g.drawImage(PIPE_IMAGES, x, y, PIPE_WIDTH, PIPE_HEIGHT, null);
//            p.drawHitbox(g, levelOffset);
        }
    }

    private void drawCannons(Graphics g, int levelOffset) {
        for (Cannon c : cannons)  {
            int x = (int) c.hitbox.x - levelOffset;
            int y = (int) c.hitbox.y;
            g.drawImage(cannonImages[c.animationIndex], x, y, CANNON_WIDTH, CANNON_HEIGHT, null);
//            c.drawHitbox(g, levelOffset);
        }
    }

    private void drawLava(Graphics g, int levelOffset) {
        for (Lava l : lava)  {
            int x = (int) l.hitbox.x - levelOffset;
            int y = (int) l.hitbox.y - LAVA_Y_OFFSET;
            g.drawImage(LAVA_IMAGES, x, y, LAVA_WIDTH, LAVA_HEIGHT, null);
//            l.drawHitbox(g, levelOffset);
        }
    }

    private void drawQuestion(Graphics g, int levelOffset) {
        for (Question q : questions)  {
            if (q.isActive()) {
                int x = (int) (q.hitbox.x - levelOffset);
                int y = (int) (q.hitbox.y + q.getPushYDraw());

                // draw 1st or 2nd row in the sprite-sheet depending on the question hit state
                if (q.isHit()) {
                    g.drawImage(questionImages[1][0],x,y, TILES_SIZE, TILES_SIZE,null);
                    if (q.isSparkle()) {
                        g.drawImage(sparkleImages[q.animationIndex],x + 10,y-TILES_SIZE,TILES_SIZE_DEFAULT,TILES_SIZE_DEFAULT,null);
                    }
                } else {
                    g.drawImage(questionImages[0][q.animationIndex],x,y, TILES_SIZE, TILES_SIZE,null);
                }

                // debug
//                q.drawHitbox(g, levelOffset);
            }
        }
    }

    private void drawCoins(Graphics g, int levelOffset) {
        for (Coin c : coins)  {
            if (c.isActive()) {
                int x = (int) c.hitbox.x - levelOffset;
                int y = (int) c.hitbox.y;

                if (c.isSparkle()) {
                    g.drawImage(sparkleImages[c.animationIndex],x,y,TILES_SIZE_DEFAULT,TILES_SIZE_DEFAULT,null);
                } else {
                    g.drawImage(coinImages[c.animationIndex],x,y, COIN_SIZE,COIN_SIZE,null);
                }

                // debug
//                c.drawSomeBox(c.hitbox, new Color(100,255,50,100), g, levelOffset);
            }
        }
    }

    private void drawFlags(Graphics g, int levelOffset) {
        for (Flag f : flags)  {
            int x = (int) f.hitbox.x - levelOffset;
            int y = (int) f.hitbox.y;
            g.drawImage(flagImages[f.animationIndex],x,y,TILES_SIZE,TILES_SIZE,null);

            // debug
//            f.drawSomeBox(f.hitbox, new Color(100,255,50,100), g, levelOffset);
        }
    }

    public void resetAllObjects() {
        for (Question q : questions) {
            q.resetObject();
        }

        for (Coin c : coins) {
            c.resetObject();
        }

        for (Brick b : bricks) {
            b.resetObject();
        }

        bullets.clear();
        healths.clear();
    }

}
