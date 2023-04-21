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
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static objects.Coin.*;
import static objects.Lava.*;
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

    // ====== Game values ======
    private List<Coin> coins = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private List<Lava> lava = new ArrayList<>();

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
            sparkleImages[i] = SPARKLE_IMAGES.getSubimage(100 * i, 0, 100, 100);
        }
    }

    public void update(Level level, Player player) {
        updateCoins(level, player);
        updateQuestions(level, player);
        updatePlatforms(level, player);
        updateLava(level, player);
    }

    private void updateLava(Level level, Player player) {
        lava = level.getLava();

        for (Lava l: lava)
            if (player.getHitbox().intersects(l.hitbox)) {
                player.setInLava(true);
                player.setHealth(0);
                player.setInLava(false);
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

                    // Question collision first time!
                    if (!q.isHit()) {
                        // todo create an animation of coins sparkling right above the question mark
                        // go through a few animation images and then disappear
                        SoundLoader.playAudio("coin.wav", 0.5);
                        coinCount++;
                    }
                }
            }
    }

    private void updateCoins(Level level, Player player) {
        coins = level.getCoins();

        for (Coin c : coins)
            if (c.isActive()) {

                if (c.hitbox.intersects(player.getHitbox())) {
                    // Player picked up a coin!
//                    c.setActive(false);
                    coinCount++;
                    SoundLoader.playAudio("coin.wav", 0.5);
                    c.setSparkle(true);
                }

                c.update(c);
            }
    }

    public void draw(Graphics g, int levelOffset) {
        drawCoins(g, levelOffset);
        drawQuestion(g, levelOffset);
        drawPlatforms(g, levelOffset);
        drawLava(g, levelOffset);
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
            p.drawHitbox(g, levelOffset);
        }
    }

    private void drawQuestion(Graphics g, int levelOffset) {
        for (Question q : questions)  {
            if (q.isActive()) {
                int x = (int) (q.hitbox.x - levelOffset);
                int y = (int) (q.hitbox.y + q.getPushDrawOffset());

                // draw 1st or 2nd row in the sprite-sheet depending on the question hit state
                if (q.isHit()) {
                    g.drawImage(questionImages[1][q.getAnimationIndex()],x,y, TILES_SIZE, TILES_SIZE,null);
                } else {
                    g.drawImage(questionImages[0][q.getAnimationIndex()],x,y, TILES_SIZE, TILES_SIZE,null);
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
                    g.drawImage(sparkleImages[c.getAnimationIndex()],x,y,40,40,null);
                } else {
                    g.drawImage(coinImages[c.getAnimationIndex()],x,y,COIN_WIDTH,COIN_HEIGHT,null);
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
    }


}
