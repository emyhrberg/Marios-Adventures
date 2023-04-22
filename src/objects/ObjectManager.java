package objects;

import helpers.ImageLoader;
import helpers.SoundLoader;
import main.Game;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static constants.Direction.UP;
import static constants.ObjectConstants.ObjectType.BULLET_TYPE;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static main.Level.transparentTiles;
import static objects.Bullet.*;
import static objects.Cannon.*;
import static objects.Coin.*;
import static objects.Lava.*;
import static objects.Pipe.PIPE_HEIGHT;
import static objects.Pipe.PIPE_WIDTH;
import static objects.Platform.*;

public class ObjectManager {

    // ====== game =======
    private Game game;

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

    // ====== Game values ======
    private List<Coin> coins = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private List<Lava> lava = new ArrayList<>();
    private List<Pipe> pipes = new ArrayList<>();
    private List<Cannon> cannons = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    public ObjectManager(Game game) {
        this.game = game;
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
    }

    // ====== Update ======

    public void update(Level level, Player player) {
        updateCoins(level, player);
        updateQuestions(level, player);
        updatePlatforms(level, player);
        updateLava(level, player);
        updateCannons(level);
        updateBullets(level, player);
        pipes = level.getPipes();
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

    private void updateQuestions(Level level, Player player) {
        questions = level.getQuestions();

        for (Question q : questions)
            if (q.isActive()) {
                // update question regularly
                q.update();

                // handle player question collision
                if (q.hitbox.intersects(player.getHitbox()) && player.getAirSpeed() < 0) {

                    // Bounce question up
                    q.setPushBackOffsetDir(UP);

                    // Question collision first time, create a sparkle and increase coin count!
                    if (!q.isHit()) {
                        SoundLoader.playAudio("coin.wav", 0.5);
                        coinCount++;
                        q.setSparkle(true);
                        q.animationIndex = 0;
                    }
                }
            }
    }

    private void updateCoins(Level level, Player player) {
        coins = level.getCoins();

        for (Coin c : coins)
            if (c.isActive()) {

                if (c.hitbox.intersects(player.getHitbox())) {
                    coinCount++;
                    SoundLoader.playAudio("coin.wav", 0.5);
                    c.setSparkle(true);
                }

                c.update(c);
            }
    }

    private void updateCannons(Level level) {
        cannons = level.getCannons();

        for (Cannon c: cannons) {
            if (c.animationIndex == 4 && c.animationTick == 0) {
                // Add a bullet
                bullets.add(new Bullet((int) c.hitbox.x - BULLET_X_OFFSET, (int) c.hitbox.y - BULLET_Y_OFFSET, BULLET_TYPE));
            }

            c.update();
        }
    }

    private void updateBullets(Level level, Player player) {
        for (Bullet b : bullets) {
            if (b.isActive()) {
                b.updateBulletPos();

                if (b.hitbox.intersects(player.getHitbox())) {
                    player.hitByBullet(20, b);
                    b.setActive(false);
                } else if (isBulletHittingLevel(b, level)) {
                    b.setActive(false);
                }
            }
        }
    }

    private boolean isBulletHittingLevel(Bullet b, Level level) {
        int bulletX = (int) (b.hitbox.x / TILES_SIZE);
        int bulletY = (int) (b.hitbox.y / TILES_SIZE);

        return !transparentTiles.contains(level.getLevelData()[bulletY][bulletX]);
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
    }

    private void drawBullets(Graphics g, int levelOffset) {
        for (Bullet b : bullets) {
            if (b.isActive()) {
                int x = (int) b.hitbox.x - levelOffset;
                int y = (int) b.hitbox.y;
                g.drawImage(BULLET_IMAGE, x, y, BULLET_W, BULLET_H, null);
                b.drawHitbox(g, levelOffset);
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
            int x = (int) c.hitbox.x - CANNON_X_OFFSET - levelOffset;
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
                int y = (int) (q.hitbox.y + q.getPushDrawOffset());

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
                    g.drawImage(coinImages[c.animationIndex],x,y,COIN_WIDTH,COIN_HEIGHT,null);
                }


                // debug
//                c.drawHitbox(g, levelOffset);
            }
        }
    }

    public void resetAllObjects() {
        for (Question q : questions) {
            q.resetObject();
            q.setHit(false);
        }

        for (Coin c : coins) {
            c.resetObject();
            c.setSparkle(false);
        }

        bullets.clear();
    }

}
