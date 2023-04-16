package entities;

import helpers.ImageLoader;
import main.Level;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static main.Game.SCALE;

/**
 * Handles all enemies
 * Updates the enemies position
 * Updates the enemies animations
 * Set level completed if all enemies are dead
 */
public class EnemyManager {

    // ====== Sharks =======
    private static final int ROWS 						= 5;
    private static final int IMAGES_IN_ROW 				= 7;
    private final BufferedImage[][] sharkImages			 = new BufferedImage[ROWS][IMAGES_IN_ROW];
    private static final BufferedImage SHARK_IMG 		= ImageLoader.loadImage("sprites_shark.png");
    private static final int SHARK_X_OFFSET 			= (int) (12 * SCALE);
    private static final int SHARK_Y_OFFSET 			= (int) (10 * SCALE);
	public static final int SHARK_WIDTH_DEFAULT        	= 34;
	public static final int SHARK_HEIGHT_DEFAULT       	= 30;
	public static final int SHARK_WIDTH                	= (int) (SHARK_WIDTH_DEFAULT * 1.5 * SCALE);
	public static final int SHARK_HEIGHT              	= (int) (SHARK_HEIGHT_DEFAULT * 1.5 * SCALE);

    // ====== Game values ======
	private List<Shark> sharks = new ArrayList<>();
    private boolean anyEnemyAlive = true;

    // ====== Constructor ======
    public EnemyManager() {
		initSharks();
    }

    public void update(Level level, Player player) {
		// Update enemy data
		sharks = level.getSharks();

		// By default, no enemy is alive
		anyEnemyAlive = false;

		// Update every individual shark
		for (Shark s : sharks)
			if (s.isEnemyAlive()) {
				s.update(level, player);
				anyEnemyAlive = true;
			}
    }

    public void dealDamageToEnemy(Player player) {
		for (Shark s : sharks)
			if (player.attackBox.intersects(s.getHitbox()) && s.isEnemyAlive())
				s.reduceEnemyHealth(player);
    }

    public void resetEnemies() {
		if (sharks == null)
			return;
		for (Shark s : sharks)
			s.resetEnemy();
    }

    // ====== Animations draw ======

    public void draw(Graphics g, int levelOffset) {
		drawSharks(g, levelOffset);
    }

    private void drawSharks(Graphics g, int levelOffset) {
		if (sharks == null)
			return;

		for (Shark s : sharks)
			if (s.isEnemyAlive()) {
				int x = (int) s.getHitbox().x - levelOffset - SHARK_X_OFFSET + s.getImageFlipX();
				int y = (int) (s.getHitbox().y - SHARK_Y_OFFSET + s.getPushDrawOffset());
				int w = SHARK_WIDTH * s.getImageFlipWidth();

				// Get the proper image representing the right action
				final BufferedImage img = sharkImages[s.getEnemyAction().ordinal()][s.getAnimationIndex()];

				// Draw the image of the shark
				g.drawImage(img, x, y, w, SHARK_HEIGHT, null);

				// Draw hitboxes for debugging
//				s.drawHitbox(g, levelOffset);
//				s.drawAttackBox(g, levelOffset);
			}
    }

     // ====== Animations init ======

    private void initSharks() {
		// Loop through the 2D array of animation images
		for (int y = 0; y < ROWS; y++)
			for (int x = 0; x < IMAGES_IN_ROW; x++) {
				int spriteX = SHARK_WIDTH_DEFAULT * x;
				int spriteY = SHARK_HEIGHT_DEFAULT * y;
				sharkImages[y][x] = SHARK_IMG.getSubimage(spriteX, spriteY, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
			}
    }

    // ====== Getters ======

    public boolean isAnyEnemyAlive() {
		return anyEnemyAlive;
    }
}
