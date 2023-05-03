package main;

import helpers.ImageLoader;
import helpers.SoundPlayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static ui.Menu.SCALE;

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
	private final BufferedImage[][] sharkImages			= new BufferedImage[ROWS][IMAGES_IN_ROW];
	private static final BufferedImage SHARK_IMAGE 		= ImageLoader.loadImage("/entities/shark.png");
	private static final int SHARK_X_OFF				= 12;
	private static final int SHARK_Y_OFF	 			= 10;
	public static final float SHARK_SCALE 				= 1.5f;
	public static final int SHARK_W 					= 34;
	public static final int SHARK_H 					= 30;

	// ====== Plants =======
	private static final BufferedImage PLANT_IMAGE 		= ImageLoader.loadImage("/entities/plant.png");
	private static final int PLANT_ROWS 				= 2;
	private final BufferedImage[] plantImages			= new BufferedImage[PLANT_ROWS];
	public static final int PLANT_SCALE 				= 2;
	public static final int PLANT_W						= 16;
	public static final int PLANT_H 					= 32;

	// ====== Game values ======
	private List<Shark> sharks = new ArrayList<>();
	private List<Plant> plants = new ArrayList<>();

	// ====== Constructor ======

	public EnemyManager() {
		initEnemies();
	}

	private void initEnemies() {
		// Init sharks
		for (int y = 0; y < ROWS; y++)
			for (int x = 0; x < IMAGES_IN_ROW; x++)
				sharkImages[y][x] = SHARK_IMAGE.getSubimage(SHARK_W * x, SHARK_H * y, SHARK_W, SHARK_H);

		// Init plants
		for (int x = 0; x < 2; x++)
			plantImages[x] = PLANT_IMAGE.getSubimage(PLANT_W * x, 0, PLANT_W, PLANT_H);
	}

	// ====== Update ======

	public void update(Level level, Player player) {
		// Update enemy data
		sharks = level.getSharks();
		plants = level.getPlants();

		// Update every individual shark
		for (Shark s : sharks)
			if (s.isEnemyAlive())
				s.update(level, player);

		// Update every individual plant
		for (Plant p : plants) {
			p.update(player);
		}
	}

	public void attackEnemyIfHit(Player player) {
		for (Shark s : sharks) {
			if (s.isEnemyAlive() && player.attackbox.intersects(s.hitbox)) {
				s.reduceEnemyHealth(player);
				SoundPlayer.playSound("/sounds/ouchenemy.wav");
			}
		}
	}

	public void resetEnemies() {
		for (Shark s : sharks)
			s.resetEnemy();
	}

	// ====== Animations ======

	public void draw(Graphics g, int levelOffset) {
		drawSharks(g, levelOffset);
		drawPlants(g, levelOffset);
	}

	private void drawSharks(Graphics g, int levelOffset) {
		for (Shark s : sharks)
			if (s.isEnemyAlive()) {
				float x = s.getHitbox().x - levelOffset - SHARK_X_OFF * SCALE + s.getImageFlipX();
				float y = s.getHitbox().y - SHARK_Y_OFF * SCALE;
				float w = SHARK_W * SHARK_SCALE * SCALE * s.getImageFlipWidth();
				float h = SHARK_H * SHARK_SCALE * SCALE;

				// Get the proper image representing the right action
				final BufferedImage img = sharkImages[s.sharkAction.ordinal()][s.animationIndex];

				// Draw the image of the shark
				g.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);

//				s.drawHitbox(g, levelOffset);
//				s.drawAttackBox(g, levelOffset);

				if (Game.DEBUG) {
					s.drawHitbox(g, levelOffset);
					s.drawAttackBox(g, levelOffset);
				}
			}
	}

	private void drawPlants(Graphics g, int levelOffset) {
		for (Plant p : plants) {
			float w = PLANT_W * PLANT_SCALE * SCALE;
			float h = PLANT_H * PLANT_SCALE * SCALE;
			float x = p.getHitbox().x - levelOffset - w / 2;
			float y = p.getHitbox().y;

			// Get the proper image representing the right action
			final BufferedImage img = plantImages[p.animationIndex];

			// Draw the image of the shark
			g.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);

			if (Game.DEBUG) {
				p.drawAttackBox(g, levelOffset);
			}
		}
	}

}
