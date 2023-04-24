package main;

import objects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static constants.ObjectConstants.ObjectType.*;
import static main.Game.TILES_SIZE;

/**
 * This class constructs a level which contains levelData, a 2D array
 */
public class Level {

    // Tiles between 50 and 100 are transparent
    public static final ArrayList<Integer> transparentTiles = IntStream.rangeClosed(50, 100).boxed().collect(Collectors.toCollection(ArrayList::new));

    // ====== Red Values ======
    public static final int DOWN_SLOPE = 87;
    public static final int UP_SLOPE = 88;

    // ====== Green Values ======
    private static final int SHARK = 0;
    private static final int PLAYER	= 1;
    private static final int FINAL_POINT = 2;
    private static final int PLANT = 3;

    // ====== Blue Values ======
    private static final int COIN = 0;
    private static final int QUESTION = 1;
    private static final int PLATFORM = 2;
    private static final int LAVA = 3;
    private static final int PIPE = 4;
    private static final int CANNON = 5;
    private static final int BRICK_BLUE = 6;

    // ====== Objects ======
    private final List<Coin> coins = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Lava> lava = new ArrayList<>();
    private final List<Pipe> pipes = new ArrayList<>();
    private final List<Cannon> cannons = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();

    // ====== Enemies ======
    private final List<Shark> sharks = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();

    // ====== Other ======
    private int[][] levelData;
    private Point spawnPoint = new Point();
    private Point finalPoint = new Point();
    private final int maxLevelOffset;

    // ====== Constructor ======
    public Level(BufferedImage image) {
        // Init all level data from RGB values
        initLevelData(image);

        // Initialize max level offset for the level; the maximum distance in pixels for the level
        maxLevelOffset = TILES_SIZE * (image.getWidth() - Game.TILES_IN_WIDTH);
    }

    private void initLevelData(BufferedImage image) {
        // Create empty arrays for level data and enemy data
        levelData = new int[image.getHeight()][image.getWidth()];

        // Go through all pixels in the level and add all level data
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                // Get color values from each pixel
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Change red data to transparent tile if drawing with a red value beyond the tileset
                if (red > 100)
                    red = 90;

                // Add red data
                levelData[y][x] = red;

                // Add green data
                if (green == PLAYER)
                    spawnPoint = new Point(x * TILES_SIZE, y * TILES_SIZE);
                if (green == FINAL_POINT)
                    finalPoint = new Point(x * TILES_SIZE, y * TILES_SIZE);
                if (green == SHARK)
                    sharks.add(new Shark(x * TILES_SIZE, y * TILES_SIZE));
                if (green == PLANT)
                    plants.add(new Plant(x * TILES_SIZE, y * TILES_SIZE));

                // Add blue data
                if (blue == COIN)
                    coins.add(new Coin(x * TILES_SIZE, y * TILES_SIZE, COIN_TYPE));
                if (blue == QUESTION)
                    questions.add(new Question(x * TILES_SIZE, y * TILES_SIZE, QUESTION_TYPE));
                if (blue == PLATFORM)
                    platforms.add(new Platform(x * TILES_SIZE, y * TILES_SIZE, PLATFORM_TYPE));
                if (blue == LAVA)
                    lava.add(new Lava(x * TILES_SIZE, y * TILES_SIZE, LAVA_TYPE));
                if (blue == PIPE)
                    pipes.add(new Pipe(x * TILES_SIZE, y * TILES_SIZE, PIPE_TYPE));
                if (blue == CANNON)
                    cannons.add(new Cannon(x * TILES_SIZE, y * TILES_SIZE, CANNON_TYPE));
                if (blue == BRICK_BLUE)
                    bricks.add(new Brick(x * TILES_SIZE, y * TILES_SIZE, BRICK_TYPE));

                // Print level data
                System.out.printf("%02d ", levelData[y][x]);
            }
    	    System.out.println(" ");
        }
    }

    // ====== Getters ======

    public void setLevelData(int[][] levelData) {
        this.levelData = levelData;
    }

    public void printLevelData(int[][] levelData) {
        for (int[] row : levelData) {
            for (int element : row) {
                System.out.printf("%02d ", element);
            }
            System.out.println(); // Move to next line after each row
        }
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public List<Cannon> getCannons() {
        return cannons;
    }

    public List<Pipe> getPipes() {
        return pipes;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Lava> getLava() {
        return lava;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public List<Shark> getSharks() {
        return sharks;
    }

    public int[][] getLevelData() {
	    return levelData;
    }

    public int getMaxLevelOffset() {
	    return maxLevelOffset;
    }

    public Point getFinalPoint() {
	    return finalPoint;
    }

    public Point getSpawnPoint() {
	    return spawnPoint;
    }
}
