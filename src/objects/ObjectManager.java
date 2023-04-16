package objects;

import entities.Player;
import helpers.ImageLoader;
import main.Level;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static main.Game.SCALE;

public class ObjectManager {

    // ====== Coins =======
    private static final int ROWS 						= 1;
    private static final int IMAGES_IN_ROW 				= 4;
    private final BufferedImage[] coinImages            = new BufferedImage[IMAGES_IN_ROW];
    private static final BufferedImage COIN_ATLAS       = ImageLoader.loadImage("sprites_coin.png");
    private static final int COIN_X_OFFSET              = 8;
    private static final int COIN_Y_OFFSET              = 4;
    public static final int COIN_WIDTH_DEFAULT          = 16;
    public static final int COIN_HEIGHT_DEFAULT         = 16;
    public static final int COIN_WIDTH                  = (int) (COIN_WIDTH_DEFAULT *2* SCALE);
    public static final int COIN_HEIGHT                 = (int) (COIN_HEIGHT_DEFAULT *2* SCALE);

    // ====== Game values ======
    private List<Coin> coins = new ArrayList<>();

    public ObjectManager() {
        initCoins();
    }

    private void initCoins() {
        // Loop through the 2D array of animation images
        for (int i = 0; i < IMAGES_IN_ROW; i++) {
            coinImages[i] = COIN_ATLAS.getSubimage(COIN_WIDTH_DEFAULT * i, 0, COIN_WIDTH_DEFAULT, COIN_HEIGHT_DEFAULT);
        }
    }

    int coinCount = 0;

    public void update(Level level, Player player) {
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
    }

    private void drawCoins(Graphics g, int levelOffset) {
        for (Coin c : coins)  {
            if (c.isActive()) {
                int x = (int) c.hitbox.x + COIN_X_OFFSET - levelOffset;
                int y = (int) c.hitbox.y + COIN_Y_OFFSET;
                g.drawImage(coinImages[c.getAnimationIndex()],x,y, COIN_WIDTH, COIN_HEIGHT,null);

                // debug
//                c.drawHitbox(g, levelOffset);
            }
        }
    }
}
