package main;

import helpers.ImageLoader;
import helpers.SoundLoader;

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
	private final BufferedImage[][] sharkImages			= new BufferedImage[ROWS][IMAGES_IN_ROW];
	private static final BufferedImage SHARK_IMAGE 		= ImageLoader.loadImage("/entities/shark.png");
	private static final int SHARK_X_OFFSET 			= (int) (12 * SCALE);
	private static final int SHARK_Y_OFFSET 			= (int) (10 * SCALE);
	public static final int SHARK_WIDTH_DEFAULT        	= 34;
	public static final int SHARK_HEIGHT_DEFAULT       	= 30;
	public static final int SHARK_WIDTH                	= (int) (SHARK_WIDTH_DEFAULT * 1.5 * SCALE);
	public static final int SHARK_HEIGHT              	= (int) (SHARK_HEIGHT_DEFAULT * 1.5 * SCALE);

	// ====== Plants =======
	private static final BufferedImage PLANT_IMAGE 		= ImageLoader.loadImage("/entities/plant.png");
	private static final int PLANT_ROWS 				= 2;
	private final BufferedImage[] plantImages			= new BufferedImage[PLANT_ROWS];
	public static final int PLANT_WIDTH_DEFAULT			= 16;
	public static final int PLANT_HEIGHT_DEFAULT		= 32;
	public static final int PLANT_WIDTH					= (int) (PLANT_WIDTH_DEFAULT *2* SCALE);
	public static final int PLANT_HEIGHT				= (int) (PLANT_HEIGHT_DEFAULT *2* SCALE);
	public static final int PLANT_X_OFFSET				= PLANT_WIDTH / 2;

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
				sharkImages[y][x] = SHARK_IMAGE.getSubimage(SHARK_WIDTH_DEFAULT * x, SHARK_HEIGHT_DEFAULT * y, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
			}

		// Init plants
		for (int i = 0; i < 2; i++)
			plantImages[i] = PLANT_IMAGE.getSubimage(PLANT_WIDTH_DEFAULT*i, 0, PLANT_WIDTH_DEFAULT, PLANT_HEIGHT_DEFAULT);
	}

	// ====== Update ======

	public void update(Level level, Player player) {
		// Update enemy data
		sharks = level.getSharks();
		plants = level.getPlants();

		// Update every individual shark
		for (Shark s : sharks)
			if (s.isEnemyAlive()) {
				s.update(level, player);
			}

		// Update every individual plant
		for (Plant p : plants) {
			p.update(player);
		}
	}

	public void attackEnemyIfHit(Player player) {
		for (Shark s : sharks) {
			if (s.isEnemyAlive() && player.attackBox.intersects(s.hitbox)) {
				s.reduceEnemyHealth(player);
				SoundLoader.playAudio("/audio/ouchenemy.wav", 0.7);
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
				float x = s.getHitbox().x - levelOffset - SHARK_X_OFFSET + s.getImageFlipX();
				float y = s.getHitbox().y - SHARK_Y_OFFSET;
				float w = SHARK_WIDTH * s.getImageFlipWidth();

				// Get the proper image representing the right action
				final BufferedImage img = sharkImages[s.sharkAction.ordinal()][s.animationIndex];

				// Draw the image of the shark
				g.drawImage(img, (int) x, (int) y, (int) w, SHARK_HEIGHT, null);

//				s.drawHitbox(g, levelOffset);
//				s.drawAttackBox(g, levelOffset);
			}
	}

	private void drawPlants(Graphics g, int levelOffset) {
		for (Plant p : plants) {
			float x = p.getHitbox().x - levelOffset - PLANT_X_OFFSET;
			float y = p.getHitbox().y;

			// Get the proper image representing the right action
			final BufferedImage img = plantImages[p.animationIndex];

			// Draw the image of the shark
			g.drawImage(img, (int) x, (int) y, PLANT_WIDTH, PLANT_HEIGHT, null);

			// Draw hitboxes for debugging
//			p.drawAttackBox(g, levelOffset);
		}
	}

}
