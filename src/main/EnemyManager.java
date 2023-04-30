package main;

import helpers.ImageLoader;
import helpers.SoundLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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
	public static final int SHARK_W 					= 34;
	public static final int SHARK_H 					= 30;
	public static final float SHARK_SCALE 				= 1.5f;

	// ====== Plants =======
	private static final BufferedImage PLANT_IMAGE 		= ImageLoader.loadImage("/entities/plant.png");
	private static final int PLANT_ROWS 				= 2;
	private final BufferedImage[] plantImages			= new BufferedImage[PLANT_ROWS];
	public static final int PLANT_W 					= 16;
	public static final int PLANT_H						 = 32;
	public static final int PLANT_X_OFF = PLANT_W / 2;

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
			for (int x = 0; x < IMAGES_IN_ROW; x++) {
				sharkImages[y][x] = SHARK_IMAGE.getSubimage(SHARK_W * x, SHARK_H * y, SHARK_W, SHARK_H);
			}

		// Init plants
		for (int i = 0; i < 2; i++)
			plantImages[i] = PLANT_IMAGE.getSubimage(PLANT_W *i, 0, PLANT_W, PLANT_H);
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
		for (Plant p : plants)
			p.update(player);
	}

	public void attackEnemyIfHit(Player player) {
		for (Shark s : sharks) {
			if (s.isEnemyAlive() && player.attackbox.intersects(s.hitbox)) {
				s.reduceEnemyHealth(player);
				SoundLoader.playSound("/sounds/ouchenemy.wav", 0.7);
			}
		}
	}

	public void resetEnemies() {
		for (Shark s : sharks)
			s.resetEnemy();
	}

	public void scaleUp() {
		for (Shark s : sharks) {
			s.initSpeed(Shark.SPEED * Game.SCALE);
			s.initHitbox(s.hitbox.x * Game.SCALE, s.hitbox.y * Game.SCALE, s.hitbox.width * Game.SCALE, s.hitbox.height * Game.SCALE);
		}

		for (Plant p : plants) {
			p.initHitbox(p.hitbox.x * Game.SCALE, p.hitbox.y * Game.SCALE, EnemyManager.PLANT_W * Game.SCALE, EnemyManager.PLANT_H);
			p.initAttackBox(p.hitbox.x, p.hitbox.y, EnemyManager.PLANT_W * Game.SCALE, EnemyManager.PLANT_H);
		}
	}

	// ====== Animations ======

	public void draw(Graphics g, int levelOffset) {
		drawSharks(g, levelOffset);
		drawPlants(g, levelOffset);
	}

	private void drawSharks(Graphics g, int levelOffset) {
		for (Shark s : sharks)
			if (s.isEnemyAlive()) {
				float x = s.hitbox.x - levelOffset - SHARK_X_OFF * Game.SCALE + s.getImageFlipX();
				float y = s.hitbox.y - SHARK_Y_OFF * Game.SCALE;
				float w = SHARK_W * Game.SCALE * SHARK_SCALE * s.getImageFlipWidth();
				float h = SHARK_H * Game.SCALE * SHARK_SCALE;

				// Get the proper image representing the right action
				final BufferedImage img = sharkImages[s.sharkAction.ordinal()][s.animationIndex];

				// Draw the image of the shark
				g.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);

				s.drawHitbox(g, levelOffset);

				if (Game.DEBUG) {
					s.drawAttackBox(g, levelOffset);
					s.drawHitbox(g, levelOffset);
				}
			}
	}

	private void drawPlants(Graphics g, int levelOffset) {
		for (Plant p : plants) {
			float x = p.getHitbox().x - levelOffset - PLANT_X_OFF * Game.SCALE;
			float y = p.getHitbox().y;
			float w = PLANT_W * 2;
			float h = PLANT_H * 2;

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
