package objects;

import entities.Player;
import helpers.ImageLoader;
import main.Level;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static main.Game.TILES_SIZE;

public class ObjectManager {

    // ====== Coins =======
    private static final int IMAGES_IN_ROW 				= 4;
    private final BufferedImage[] coinImages            = new BufferedImage[IMAGES_IN_ROW];
    private static final BufferedImage COIN_ATLAS       = ImageLoader.loadImage("sprites_coin.png");
    public static final int PIXEL_SIZE                  = 16;
    private int coinCount;

    // ====== Questions =======
    private final BufferedImage[] questionImages        = new BufferedImage[IMAGES_IN_ROW];
    private static final BufferedImage QUESTION_ATLAS   = ImageLoader.loadImage("sprites_question.png");

    // ====== Game values ======
    private List<Coin> coins = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

    public ObjectManager() {
        initObjects();
    }

    private void initObjects() {
        // Loop through the 2D array of animation images
        for (int i = 0; i < IMAGES_IN_ROW; i++) {
            coinImages[i] = COIN_ATLAS.getSubimage(PIXEL_SIZE * i, 0, PIXEL_SIZE, PIXEL_SIZE);
            questionImages[i] = QUESTION_ATLAS.getSubimage(PIXEL_SIZE * i, 0, PIXEL_SIZE, PIXEL_SIZE);
        }
    }

    public void update(Level level, Player player) {
        updateCoins(level, player);
        updateQuestions(level, player);
    }

    private void updateQuestions(Level level, Player player) {
        questions = level.getQuestions();

        for (Question q : questions)
            if (q.isActive()) {

                if (q.hitbox.intersects(player.getHitbox())) {
                    System.out.println("question intersect");
                }

                q.update();
            }
    }

    private void updateCoins(Level level, Player player) {
        coins = level.getCoins();

        for (Coin c : coins)
            if (c.isActive()) {

                if (c.hitbox.intersects(player.getHitbox())) {
                    // Player picked up a coin!
                    c.setActive(false);
                    coinCount++;
                    System.out.println(coinCount);
                }

                c.update();
            }
    }

    public void draw(Graphics g, int levelOffset) {
        drawCoins(g, levelOffset);
        drawQuestion(g, levelOffset);
    }

    private void drawQuestion(Graphics g, int levelOffset) {
        for (Question q : questions)  {
            if (q.isActive()) {
                int x = (int) q.hitbox.x - levelOffset;
                int y = (int) q.hitbox.y;
                g.drawImage(questionImages[q.getAnimationIndex()],x,y, TILES_SIZE, TILES_SIZE,null);

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
                g.drawImage(coinImages[c.getAnimationIndex()],x,y, TILES_SIZE, TILES_SIZE,null);

                // debug
//                c.drawHitbox(g, levelOffset);
            }
        }
    }

    public void resetAllObjects() {
        for (Question q : questions)
            q.resetObject();

        for (Coin c : coins)
            c.resetObject();
    }


}
