package objects;

import helpers.ImageLoader;
import main.Game;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static objects.Bullet.*;
import static objects.Cannon.*;
import static objects.Coin.COIN_SIZE;
import static objects.HealthPowerup.HEALTH_SIZE;
import static objects.Lava.*;
import static objects.Pipe.*;
import static objects.Platform.PLATFORM_H;
import static objects.Platform.PLATFORM_W;
import static ui.Menu.*;

public class ObjectManager {

    // ====== Coins =======
    private final BufferedImage[] coinImages            = new BufferedImage[4];
    private static final BufferedImage COIN_IMAGES      = ImageLoader.loadImage("/sprites/coin.png");
    public static final int PIXEL_SIZE                  = 16;

    // ====== Questions =======
    private final BufferedImage[][] questionImages      = new BufferedImage[2][4];
    private static final BufferedImage QUESTION_IMAGES  = ImageLoader.loadImage("/sprites/question.png");

    // ====== Platforms =======
    private static final BufferedImage PLATFORM_IMAGES  = ImageLoader.loadImage("/sprites/platform.png");

    // ====== Lava =======
    private static final BufferedImage LAVA_IMAGES      = ImageLoader.loadImage("/sprites/lava.png");

    // ====== Sparkle =======
    private static final int SPARKLE_W = 100;
    private static final int SPARKLE_H = 100;
    private static final BufferedImage SPARKLE_IMAGES   = ImageLoader.loadImage("/sprites/sparkle.png");
    private final BufferedImage[] sparkleImages         = new BufferedImage[7];

    // ====== Break brick =======
    private static final BufferedImage BREAK_BRICK_IMAGES = ImageLoader.loadImage("/sprites/brick-explosion.png");
    private final BufferedImage[] breakImages = new BufferedImage[4];

    // ====== Pipes =======
    private static final BufferedImage PIPE_IMAGES      = ImageLoader.loadImage("/sprites/pipe.png");

    // ====== Cannons ======
    private static final BufferedImage CANNON_IMAGES    = ImageLoader.loadImage("/sprites/cannon.png");
    private final BufferedImage[] cannonImages          = new BufferedImage[7];

    // ====== Bullets ======
    private static final BufferedImage BULLET_IMAGE     = ImageLoader.loadImage("/sprites/bullet.png");
    private final BufferedImage[] bulletImages          = new BufferedImage[8];

    // ====== Power-ups ======
    private static final BufferedImage POWERUP_HEALTH   = ImageLoader.loadImage("/sprites/health-powerup.png");
    public static final ArrayList<HealthPowerup> healths = new ArrayList<>();

    // ====== Flags ======
    private static final BufferedImage FLAG_IMAGES      = ImageLoader.loadImage("/sprites/flag.png");
    private final BufferedImage[] flagImages            = new BufferedImage[3];

    // ====== List of objects ======
    private List<Brick> bricks          = new ArrayList<>();
    private List<Cannon> cannons        = new ArrayList<>();
    private List<Coin> coins            = new ArrayList<>();
    private List<Flag> flags            = new ArrayList<>();
    private List<Lava> lava             = new ArrayList<>();
    private List<Pipe> pipes            = new ArrayList<>();
    private List<Platform> platforms    = new ArrayList<>();
    private List<Question> questions    = new ArrayList<>();

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
            sparkleImages[i] = SPARKLE_IMAGES.getSubimage(SPARKLE_W * i, 0, SPARKLE_W, SPARKLE_H);
        }

        // Init breaks
        for (int i = 0; i < 4; i++) {
            breakImages[i] = BREAK_BRICK_IMAGES.getSubimage(TILES_SIZE_DEFAULT * i, 0, TILES_SIZE_DEFAULT, TILES_SIZE_DEFAULT);
        }

        // Init cannons
        for (int i = 0; i < 7; i++) {
            cannonImages[i] = CANNON_IMAGES.getSubimage(CANNON_W * i, 0, CANNON_W, CANNON_H);
        }

        // Init bullets
        for (int i = 0; i < 8; i++) {
            bulletImages[i] = BULLET_IMAGE.getSubimage(BULLET_W * i, 0, BULLET_W, BULLET_H);
        }

        // Init flags
        for (int i = 0; i < 3; i++) {
            flagImages[i] = FLAG_IMAGES.getSubimage(16 * i, 0, 16, 16);
        }
    }

    // ====== Update ======

    public void update(Level level, Player player) {
        bricks      = level.getBricks();
        cannons     = level.getCannons();
        coins       = level.getCoins();
        flags       = level.getFlags();
        lava        = level.getLava();
        questions   = level.getQuestions();
        pipes       = level.getPipes();
        platforms   = level.getPlatforms();

        for (Brick b : bricks)          b.update(player, level, b);
        for (Bullet b : bullets)        b.update(player, level, b);
        for (Cannon c: cannons)         c.update();
        for (Coin c : coins)            c.update(player, c);
        for (HealthPowerup h : healths) h.update(player, level, h);
        for (Lava l: lava)              l.update(player, l);
        for (Platform p : platforms)    p.update(player, level, p);
        for (Question q : questions)    q.update(player, q);
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
        drawBreakingBrick(g, levelOffset);
        drawFlags(g, levelOffset);
    }

    public void drawPowerups(Graphics g, int levelOffset) {
        for (HealthPowerup h : healths) {
            if (h.isActive()) {
                float x = h.getHitbox().x - levelOffset;
                float y = h.getHitbox().y;
                float w = HEALTH_SIZE * SCALE;
                float hi = HEALTH_SIZE * SCALE;
                g.drawImage(POWERUP_HEALTH, (int) x, (int) y, (int) w, (int) hi, null);

                if (Game.DEBUG) {
                    h.drawHitbox(g, levelOffset);
                }
            }
        }
    }

    private void drawPlatforms(Graphics g, int levelOffset) {
        for (Platform p : platforms)  {
            int x = (int) p.hitbox.x - levelOffset;
            int y = (int) p.hitbox.y;
            int w = (int) (PLATFORM_W * SCALE);
            int h = (int) (PLATFORM_H * SCALE);
            g.drawImage(PLATFORM_IMAGES, x, y, w, h, null);

//            p.drawSomeBox(p.hitbox, new Color(0,255,0,100), g, levelOffset);
//            p.drawSomeBox(p.getTop(), new Color(255,0,255,120), g, levelOffset);

            if (Game.DEBUG) {
                p.drawSomeBox(p.hitbox, Color.GREEN, g, levelOffset);
//                p.drawSomeBox(p.getTop(), Color.BLUE, g, levelOffset);
            }
        }
    }

    private void drawBullets(Graphics g, int levelOffset) {
        if (bullets.size() == 0)
            return;

        for (Bullet b : bullets) {
            if (b.isActive()) {
                float x = b.hitbox.x - levelOffset;
                float y = b.hitbox.y - BULLET_Y_OFF * SCALE;
                float w = BULLET_W * SCALE;
                float h = BULLET_H * SCALE;
                g.drawImage(bulletImages[b.animationIndex], (int) x, (int) y, (int) w, (int) h, null);

                if (Game.DEBUG) {
                    b.drawHitbox(g, levelOffset);
                }
            }
        }
    }

    private void drawBreakingBrick(Graphics g, int levelOffset) {
        for (Brick b : bricks)  {
            if (b.isBreaking) {
                int x = (int) b.getBreakBox().x - levelOffset;
                int y = (int) b.getBreakBox().y;
                g.drawImage(breakImages[b.animationIndex],x,y, TILES_SIZE,TILES_SIZE,null);
            }

            if (Game.DEBUG) {
                b.drawBreakBox(g, levelOffset);
                b.drawHitbox(g, levelOffset);
            }
        }
    }

    private void drawPipes(Graphics g, int levelOffset) {
        for (Pipe p : pipes)  {
            int x = (int) p.hitbox.x - levelOffset;
            int y = (int) p.hitbox.y;
            float w = PIPE_W * PIPE_SCALE * SCALE;
            float h = PIPE_H * PIPE_SCALE * SCALE;
            g.drawImage(PIPE_IMAGES, x, y, (int) w, (int) h, null);

            if (Game.DEBUG) {
                p.drawHitbox(g, levelOffset);
            }
        }
    }

    private void drawCannons(Graphics g, int levelOffset) {
        for (Cannon c : cannons)  {
            int x = (int) c.hitbox.x - levelOffset;
            int y = (int) c.hitbox.y;
            int w = (int) (CANNON_W * CANNON_SCALE * SCALE);
            int h = (int) (CANNON_H * CANNON_SCALE * SCALE);
            g.drawImage(cannonImages[c.animationIndex], x, y, w, h, null);

            if (Game.DEBUG) {
                c.drawHitbox(g, levelOffset);
            }
        }
    }

    private void drawLava(Graphics g, int levelOffset) {
        for (Lava l : lava)  {
            int x = (int) l.hitbox.x - levelOffset;
            int y = (int) (l.hitbox.y - LAVA_Y_OFF * SCALE);
            int w = (int) (LAVA_W * SCALE);
            int h = (int) (LAVA_H * SCALE);
            g.drawImage(LAVA_IMAGES, x, y, w, h, null);

            if (Game.DEBUG) {
                l.drawHitbox(g, levelOffset);
            }
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

                if (Game.DEBUG) {
                    q.drawHitbox(g, levelOffset);
                }
            }
        }
    }

    private void drawCoins(Graphics g, int levelOffset) {
        for (Coin c : coins)  {
            if (c.isActive()) {
                int x = (int) c.hitbox.x - levelOffset;
                int y = (int) c.hitbox.y;
                int w = (int) (COIN_SIZE * SCALE);
                int h = (int) (COIN_SIZE * SCALE);

                if (c.isSparkle()) {
                    g.drawImage(sparkleImages[c.animationIndex], x, y, w, h, null);
                } else {
                    g.drawImage(coinImages[c.animationIndex], x, y, w, h, null);
                }

                if (Game.DEBUG) {
                    c.drawSomeBox(c.hitbox, new Color(100,255,50,100), g, levelOffset);
                }
            }
        }
    }

    private void drawFlags(Graphics g, int levelOffset) {
        for (Flag f : flags)  {
            int x = (int) (f.hitbox.x - levelOffset - 23 * SCALE);
            int y = (int) f.hitbox.y;
            g.drawImage(flagImages[f.animationIndex],x,y,TILES_SIZE,TILES_SIZE,null);


            if (Game.DEBUG) {
//                f.drawSomeBox(f.hitbox, new Color(100,255,50,100), g, levelOffset);
            }
        }
    }

    public void resetAllObjects() {
        for (Question q : questions) q.resetObject();
        for (Coin c : coins) c.resetObject();
        for (Brick b : bricks) b.resetObject();

        bullets.clear();
        healths.clear();
    }

    public void scaleUp() {
        for (Question q : questions)    q.initHitbox(q.hitbox.x * SCALE, q.hitbox.y * SCALE, q.hitbox.width * SCALE, q.hitbox.height * SCALE);
        for (Coin c : coins)            c.initHitbox(c.hitbox.x * SCALE, c.hitbox.y * SCALE, c.hitbox.width * SCALE, c.hitbox.height * SCALE);
        for (Brick b : bricks)          b.initHitbox(b.hitbox.x * SCALE, b.hitbox.y * SCALE, b.hitbox.width * SCALE, b.hitbox.height * SCALE);
//        for (Bullet b : bullets)
        for (Cannon c: cannons)         c.initHitbox(c.hitbox.x * SCALE, c.hitbox.y * SCALE, c.hitbox.width * SCALE, c.hitbox.height * SCALE);
        for (Coin c : coins)            c.initHitbox(c.hitbox.x * SCALE, c.hitbox.y * SCALE, c.hitbox.width * SCALE, c.hitbox.height * SCALE);
//        for (HealthPowerup h : healths)
        for (Lava l: lava)              l.initHitbox(l.hitbox.x * SCALE, l.hitbox.y * SCALE, l.hitbox.width * SCALE, l.hitbox.height * SCALE);
        for (Pipe p: pipes)             p.initHitbox(p.hitbox.x * SCALE, p.hitbox.y * SCALE, p.hitbox.width * SCALE, p.hitbox.height * SCALE);
        for (Platform p : platforms)    p.initHitbox(p.hitbox.x * SCALE, p.hitbox.y * SCALE, p.hitbox.width * SCALE, p.hitbox.height * SCALE);
        for (Question q : questions)    q.initHitbox(q.hitbox.x * SCALE, q.hitbox.y * SCALE, q.hitbox.width * SCALE, q.hitbox.height * SCALE);
    }

}
