package objects;

import helpers.ImageLoader;
import helpers.SoundLoader;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static constants.Direction.UP;
import static constants.ObjectConstants.ObjectType.BULLET_TYPE;
import static main.Entity.GRAVITY;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static objects.Brick.BRICK_H;
import static objects.Brick.BRICK_W;
import static objects.Bullet.*;
import static objects.Cannon.*;
import static objects.Coin.*;
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
    private int coinCount;

    // ====== Questions =======
    private final BufferedImage[][] questionImages      = new BufferedImage[2][4];
    private static final BufferedImage QUESTION_IMAGES  = ImageLoader.loadImage("/images/sprites_question.png");

    // ====== Platforms =======
    private static final BufferedImage PLATFORM_IMAGES  = ImageLoader.loadImage("/images/sprites_platform.png");

    // ====== Lava =======
    private static final BufferedImage LAVA_IMAGES      = ImageLoader.loadImage("/images/sprites_lava.png");

    // ====== Sparkle =======
    private static final BufferedImage SPARKLE_IMAGES   = ImageLoader.loadImage("/images/sprites_sparkle.png");
    private final BufferedImage[] sparkleImages         = new BufferedImage[7];

    // ====== Pipes =======
    private static final BufferedImage PIPE_IMAGES      = ImageLoader.loadImage("/images/sprites_pipe.png");

    // ====== Cannons ======
    private static final BufferedImage CANNON_IMAGES    = ImageLoader.loadImage("/images/sprites_cannon.png");
    private final BufferedImage[] cannonImages          = new BufferedImage[7];

    // ====== Bullet ======
    private static final BufferedImage BULLET_IMAGE     = ImageLoader.loadImage("/images/sprites_bullet.png");
    private final BufferedImage[] bulletImages          = new BufferedImage[8];
    private final List<Bullet> bullets = new ArrayList<>();

    // ====== Bricks ======
    private static final BufferedImage BRICK_IMAGE      = ImageLoader.loadImage("/images/sprites_brick.png");

    // ====== Power-ups ======
    private static final BufferedImage POWERUP_HEALTH   = ImageLoader.loadImage("/images/powerup_health.png");
    private final ArrayList<HealthPowerup> healths = new ArrayList<>();

    // ====== Game values ======
    private List<Coin> coins = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private List<Lava> lava = new ArrayList<>();
    private List<Pipe> pipes = new ArrayList<>();
    private List<Cannon> cannons = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();

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

        // Init cannons
        for (int i = 0; i < 7; i++) {
            cannonImages[i] = CANNON_IMAGES.getSubimage(CANNON_W_DEF * i, 0, CANNON_W_DEF, CANNON_H_DEF);
        }

        // Init bullets
        for (int i = 0; i < 8; i++) {
            bulletImages[i] = BULLET_IMAGE.getSubimage(BULLET_W_DEF * i, 0, BULLET_W_DEF, BULLET_H_DEF);
        }
    }

    // ====== Update ======

    public void update(Level level, Player player) {
        updateCoins(level, player);
        updateQuestions(level, player);
        updatePlatforms(level, player);
        updateLava(level, player);
        updateCannons(level);
        updateBullets(level, player);
        updateBricks(level, player);
        updateHealths(level, player);
        pipes = level.getPipes();
    }

    private void updateQuestions(Level level, Player player) {
        questions = level.getQuestions();

        for (Question q : questions)
            if (q.isActive()) {
                // update question regularly
                q.update();

                // handle player question collision
                if (q.hitbox.intersects(player.getHitbox()) && player.getAirSpeed() < 0) {

                    // Bounce question up
                    q.setPushYDir(UP);

                    // Question collision first time!
                    if (!q.isHit()) {
                        healths.add(new HealthPowerup((int) q.hitbox.x, (int) q.hitbox.y));
                    }
                }
            }
    }

    private void updateLava(Level level, Player player) {
        lava = level.getLava();

        for (Lava l: lava)
            if (player.getHitbox().intersects(l.hitbox)) {
                player.setInLava(true);
                player.setJumpHeight(0.9f * SCALE);
                player.jump();
                player.setHealth(0);
                player.setInLava(false);
                player.setCanJump(true);
            }
    }

    private void updateBricks(Level level, Player player) {
        bricks = level.getBricks();

        for (Brick b: bricks) {
            b.update(b);
        }
    }

    private void updatePlatforms(Level level, Player player) {
        platforms = level.getPlatforms();

        for (Platform p : platforms) {
            if (p.getBottom().intersects(player.getHitbox())) {
                // player is colliding with bottom of platform. make player fall down
                player.setAirSpeed(1 * SCALE);
                if (!p.getBottomLine().intersects(player.getHitboxTop())) {
                    player.getHitbox().x = p.getXOfClosestHitbox(player);
                }
            } else if (p.getTop().intersects(player.getHitbox())) {
                player.bindPlatform(p);
            } else {
                player.unbindPlatform();
            }
            p.update(player, level);
        }
    }

    private void updateHealths(Level level, Player player) {
        for (HealthPowerup h : healths) {
            if (h.isActive()) {
                h.update(level);

                if (h.getHitbox().intersects(player.getHitbox())) {
                    h.setActive(false);
                    if (player.getHealth() < player.getMaxHealth())
                        player.setHealth(player.getHealth() + 20);
                }
            }
        }
    }

    private void updateCoins(Level level, Player player) {
        coins = level.getCoins();

        for (Coin c : coins)
            if (c.isActive()) {

                if (c.hitbox.intersects(player.getHitbox())) {
                    if (!c.isHit()) {
                        c.setHit(true);
                        coinCount++;
                        SoundLoader.playAudio("coin.wav", 0.5);
                        c.setSparkle(true);
                    }
                }
                c.update(c);
            }
    }

    private void updateCannons(Level level) {
        cannons = level.getCannons();

        for (Cannon c: cannons) {
            if (c.animationIndex == 4 && c.animationTick == 0 && c.isShootAllowed()) {
                // Add a bullet
                bullets.add(new Bullet((int) c.hitbox.x, (int) c.hitbox.y, BULLET_TYPE));
                c.setLastCannonShot(System.currentTimeMillis());
            }

            c.update();
        }
    }

    private void updateBullets(Level level, Player player) {
        for (Bullet b : bullets) {
            if (b.isActive()) {
                b.update();

                if (b.hitbox.intersects(player.getHitbox())) {
                    player.hitByBullet(b);
                    b.setActive(false);
                } else if (b.isBulletHittingLevel(b, level)) {
                    b.setActive(false);
                }
            }
        }
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
            if (b.isActive()) {
                int x = (int) b.hitbox.x - levelOffset;
                int y = (int) b.hitbox.y;

                if (b.isSparkle) {
                    g.drawImage(sparkleImages[b.animationIndex],x+7,y+7,(SPARKLE_DRAW_W),SPARKLE_DRAW_H,null);
                } else {
                    g.drawImage(BRICK_IMAGE, x, y, BRICK_W, BRICK_H, null);
                }

                b.drawHitbox(g, levelOffset);
                b.drawBottom(g, levelOffset);
            }
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

    private void drawPlatforms(Graphics g, int levelOffset) {
        for (Platform p : platforms)  {
            int x = (int) p.hitbox.x - levelOffset;
            int y = (int) p.hitbox.y + PLATFORM_Y_OFFSET;
            g.drawImage(PLATFORM_IMAGES, x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT, null);
//            p.drawHitbox(g, levelOffset);
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
                        g.drawImage(sparkleImages[q.animationIndex],x + 10,y-TILES_SIZE,SPARKLE_DRAW_W,SPARKLE_DRAW_H,null);
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
                    g.drawImage(sparkleImages[c.animationIndex],x,y,SPARKLE_DRAW_W,SPARKLE_DRAW_H,null);
                } else {
                    g.drawImage(coinImages[c.animationIndex],x,y, COIN_SIZE,COIN_SIZE,null);
                }

                // debug
//                c.drawHitbox(g, levelOffset);
            }
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
