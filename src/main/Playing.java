package main;

import helpers.FontLoader;
import helpers.ImageLoader;
import objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import static constants.Direction.*;
import static constants.GameState.*;
import static main.Player.PLAYER_HEIGHT;
import static main.Player.PLAYER_WIDTH;
import static objects.Coin.coinCount;
import static ui.Menu.*;

/**
 * Playing is where the player plays the game
 * The playing state initializes classes for the game, including player, levels and more
 * This class also shows overlays for example paused or game over depending on the game state
 */
public class Playing extends GameState {

    // ====== Variables ======
    private static final float PLAYER_SCALE = 1.33f;
    private final Player player;
    private Point spawnPoint;
    private int levelOffset;
    private int shakeOffset;
    private long lastSec;
    private boolean movingLeft, movingRight;
    private static final int START_T = 300;
    private int t = START_T;
    private static final Font CUSTOM_FONT = FontLoader.loadFont("/fonts/mario2d.ttf");

    private final EnemyManager enemyManager;
    private final LevelManager levelManager;
    private final ObjectManager objectManager;

    // Draw Background
    private static final BufferedImage SKY = ImageLoader.loadImage("/ui/sky.png");
    private static final BufferedImage BIG_CLOUDS = ImageLoader.loadImage("/ui/big-clouds.png");
    private static final BufferedImage SMALL_CLOUDS = ImageLoader.loadImage("/ui/small-clouds.png");
    private static final BufferedImage FOREST = ImageLoader.loadImage("/ui/forest.png");

    // Draw HUD
    private static final BufferedImage C_ICON = ImageLoader.loadImage("/ui/c-icon.png");
    private static final BufferedImage H_ICON = ImageLoader.loadImage("/ui/h-icon.png");

    // ====== Constructor ======
    public Playing(Game game) {
        super(game);
        initNums();

        // Init classes
        levelManager    = new LevelManager();
        player          = new Player(PLAYER_WIDTH * PLAYER_SCALE * SCALE, PLAYER_HEIGHT * PLAYER_SCALE * SCALE, game);
        enemyManager    = new EnemyManager();
        objectManager   = new ObjectManager();

        // Set level and spawn for player class
        player.setLevel(levelManager.getLevel());
        resetSpawnPoint();
    }

    // ====== Update methods ======

    public void update() {
        // Update the player and enemies
        player.update();
        enemyManager.update(levelManager.getLevel(), player);
        objectManager.update(levelManager.getLevel(), player);

        // Update playing stuff
        updateLevelOffset();
        updatePlayerOutsideLevel();
        updateFinalPointState();
        updateShake();
        updateCountdownTimer();
    }

    private void updateLevelOffset() {
        // Get player X position
        int playerX = (int) player.hitbox.x;

        // Update level offset with player and half the game width to center the player
        levelOffset = playerX - GAME_WIDTH / 2;

        // Reset level offset if at the leftmost of the map
        if (levelOffset < 0)
            levelOffset = 0;

        // Reset level offset if at the rightmost of the map
        final int maxLevelOffset = levelManager.getLevel().getMaxLevelOffset();
        if (levelOffset > maxLevelOffset)
            levelOffset = maxLevelOffset;
    }

    private void updatePlayerOutsideLevel() {
        int playerY = (int) player.hitbox.y / TILES_SIZE;
        int bottomY = TILES_IN_HEIGHT;
        boolean isPlayerBelowLevel = playerY >= bottomY + 3;

        if (isPlayerBelowLevel)
            game.setGameState(GAME_OVER);
    }

    private void updateShake() {
        shakeOffset = 0;

        if (player.isHit()) {
            // The maximum amount of pixels to shake the screen
            final int shakeAmount = 10;
            shakeOffset = (int) (Math.random() * shakeAmount * 2) - shakeAmount;
        }
    }

    private void updateFinalPointState() {
        // Get the X and Y position for the player and "final point"
        int finalX = levelManager.getLevel().getFinalPoint().x / TILES_SIZE;
        int finalY = levelManager.getLevel().getFinalPoint().y / TILES_SIZE;
        int playerX = (int) player.getHitbox().x / TILES_SIZE;
        int playerY = (int) player.getHitbox().y / TILES_SIZE;

        // Player is inside the final position, meaning
        // Exact X position
        // Exact Y position on the bottom or up to 3 tiles above it.
        if (playerX == finalX) {
            if (playerY >= finalY - 3 || playerY <= finalY) {
                setLevelCompleted();
            }
        }
    }

    private void updateCountdownTimer() {
        long sec = System.currentTimeMillis() / 1000;
        if (sec != lastSec) {
            t--;
            lastSec = sec;
        }
    }

    // ====== Draw ======

    private BufferedImage offScreenImage;

    public void drawBlur(Graphics g) {
        // Create off-screen image with the same dimensions as the game screen
        if (offScreenImage == null || offScreenImage.getWidth() != GAME_WIDTH || offScreenImage.getHeight() != GAME_HEIGHT) {
            offScreenImage = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }

        // Get graphics object of the off-screen image
        Graphics2D g2d = offScreenImage.createGraphics();

        // Draw all game elements onto the off-screen image
        drawGame(g2d);

        // Apply blur effect to the off-screen image
        applyBlurEffect();

        // Draw the blurred image onto the main graphics context
        g.drawImage(offScreenImage, 0, 0, null);

        // Add black opacity
        g.setColor(new Color(0,0,0,150));
        g.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);

        // Dispose of the off-screen graphics object to release system resources
        g2d.dispose();
    }

    public void draw(Graphics g) {
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        drawSky(g);
        drawForest(g);
        drawSmallClouds(g);
        drawBigClouds(g);
        drawHUD(g);
        objectManager.drawPowerups(g, levelOffset);
        levelManager.draw(g, levelOffset);
        enemyManager.draw(g, levelOffset);
        objectManager.draw(g, levelOffset);
        player.draw(g, levelOffset);
    }

    private void applyBlurEffect() {
        // Define blur matrix
        final float a = 0.1f;
        final float b = 0.2f;
        float[] matrix = {
                a, a, a,
                a, b, a,
                a, a, a
        };

        // Create blur filter
        Kernel kernel = new Kernel(3, 3, matrix);
        ConvolveOp blur = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        // Apply blur filter to the off-screen image
        offScreenImage = blur.filter(offScreenImage, null);
    }

    private void drawSky(Graphics g) {
        int x = (int) (-levelOffset * 0.17);
        int y = 0;
        int w = (int) (4320  * SCALE);
        int h = GAME_HEIGHT;
        for (int i = 0; i < 8; i++) {
            g.drawImage(SKY, x + i * w, y, w, h, null);
        }

    }

    // ====== Background ======

    private void drawForest(Graphics g) {
        int x = (int) (-levelOffset * 0.17);
        int y = (int) (230 * SCALE);
        int w = (int) (1024 / 2 * SCALE);
        int h = (int) (1024 / 2 * SCALE);
        for (int i = 0; i < 8; i++) {
            g.drawImage(FOREST, x + i * w, y, w, h, null);
        }
    }

    float bigX;
    float smallX;
    float incr;
    private static final int CLOUDS_W = 1500;

    private void drawSmallClouds(Graphics g) {
        // set opacity
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        if (levelOffset == 0 || levelOffset == levelManager.getLevel().getMaxLevelOffset() || player.direction == STILL) {
//            smallX -= 0.08f;
//            incr += 0.08f;
        } else {
//            smallX = -levelOffset * 0.08f - incr;
        }

        smallX = -levelOffset * 0.08f;

        for (int i = 0; i < 4; i++) {
            g.drawImage(SMALL_CLOUDS, (int) smallX + i * CLOUDS_W, 25, null);
        }

        // reset opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawBigClouds(Graphics g) {

        // cloud direction
        if (levelOffset == 0 || levelOffset == levelManager.getLevel().getMaxLevelOffset() || player.direction == STILL) {
//            bigX -= 0.5f;
//            incr += 0.5f;
        } else {
//            bigX = -levelOffset * 0.25f - incr;
        }

        bigX = -levelOffset * 0.25f;

        // draw 4 clouds
        for (int i = 0; i < 4; i++) {
            g.drawImage(BIG_CLOUDS, (int) bigX + i * CLOUDS_W, 80, null);
        }
    }


    private static final BufferedImage NUMS = ImageLoader.loadImage("/ui/nums.png");
    private static final BufferedImage NUMS_Y = ImageLoader.loadImage("/ui/nums-yellow.png");
    private static final BufferedImage healthX = ImageLoader.loadImage("/ui/x.png");
    private static final BufferedImage[] nums = new BufferedImage[10];
    private static final BufferedImage[] numsY = new BufferedImage[10];
    private static final int numW = 315;
    private static final int numH = 300;

    private void initNums() {
        for (int i = 0; i < 10; i++) {
            nums[i] = NUMS.getSubimage(numW * i, 0, numW, numH);
            numsY[i] = NUMS_Y.getSubimage(numW * i, 0, numW, numH);
        }
    }

    private void drawHUD(Graphics g) {
        int icon = (int) (72 * SCALE);
        int w = (int) (316 / 7 * SCALE);
        int h = (int) (300 / 7 * SCALE);
        int x = icon / 2;
        int y = icon / 3;

        // ========== HEALTH ===========
        g.drawImage(H_ICON, x, y, icon, icon, null);
        g.drawRect(x, y, icon, icon);

        // 1
        x += 0.75*icon;
        y += (icon - h);
        g.drawImage(healthX, x, y, w, h, null);
        g.drawRect(x, y, w, h);

        // 2
        x += 0.7*w;
        g.drawImage(nums[0], x, y, w, h, null);
        g.drawRect(x, y, w, h);

        // 3
        x += 0.7*w;
        g.drawImage(nums[player.health], x, y, w, h, null);
        g.drawRect(x, y, w, h);

        // ========== COIN ===========
        y = icon / 3 + icon;
        x = icon / 2;
        g.drawImage(C_ICON, x, y, icon, icon, null);
        g.drawRect(x, y, icon, icon);

        // 1
        x += 0.9*icon;
        y += h / 2;
        g.drawImage(numsY[coinCount / 10], x, y, w, h, null);
        g.drawRect(x, y, w, h);

        // 2
        x += 0.65*w;
        g.drawImage(numsY[coinCount % 10], x, y, w, h, null);
        g.drawRect(x, y, w, h);

        // Draw countdown?
        String countdown = (t < 100 && t >= 10) ? "0" + t : (t < 10) ? "00" + t : String.valueOf(t);

    }
    // ====== Reset methods ======

    private boolean savedSpawn = false;
    private Point savedSpawnPoint;

    private void resetSpawnPoint() {
        if (savedSpawn)
            spawnPoint = savedSpawnPoint;
        else
            spawnPoint = levelManager.getLevel().getSpawnPoint();

        player.getHitbox().x = spawnPoint.x;
        player.getHitbox().y = spawnPoint.y;
    }

    private void resetLevelData() {
        // set new level data where we reset all the bricks (temp value 91, to its default value of 26)
        int[][] levelData = levelManager.getLevel().getLevelData();
        for (int i = 0; i < levelData.length; i++)
            for (int j = 0; j < levelData[i].length; j++)
                if (levelData[i][j] == 91)
                    levelData[i][j] = 26;
    }

    public void resetGame() {
        player.resetPlayer();
        enemyManager.resetEnemies();
        objectManager.resetAllObjects();
        resetSpawnPoint();
        resetLevelData();
        coinCount = 0;
        t = START_T;
    }

    public void resetGameGoToMenu() {
        final int nextLevel = levelManager.getLevelIndex() + 1;
        levelManager.setLevelIndex(nextLevel);
        player.setLevel(levelManager.getLevel());

        // Reset
        savedSpawn = false;
        resetGame();
        game.setGameState(MENU);
    }

    public void saveSpawnPoint() {
        savedSpawnPoint = new Point((int) player.getHitbox().x, (int) player.getHitbox().y);
        savedSpawn = true;
    }

    public void resetGameGoToPlaying() {
        resetGame();
        game.setGameState(PLAYING);
    }

    public void resetGameLoadNextLevel() {
        // Load next level
        final int nextLevel = levelManager.getLevelIndex() + 1;
        levelManager.setLevelIndex(nextLevel);
        player.setLevel(levelManager.getLevel());

        // Reset
        savedSpawn = false;
        resetGame();
        game.setGameState(PLAYING);
    }

    public void setLevelCompleted() {
        // Set level complete or game complete, if at the last level
        final int currentLevel = game.getPlaying().getLevelManager().getLevelIndex();
        final int last = game.getPlaying().getLevelManager().getAmountOfLevels() - 1;
        if (currentLevel == last)
            game.setGameState(GAME_COMPLETED);
        else
            game.setGameState(LEVEL_COMPLETED);
    }

    // ====== Key and Mouse events ======

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                movingLeft = true;
                if (movingRight)
                    player.setDirection(STILL);
                else
                    player.setDirection(LEFT);
                break;
            case KeyEvent.VK_D:
                movingRight = true;
                if (movingLeft)
                    player.setDirection(STILL);
                else
                    player.setDirection(RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                player.setJumping(true);
                player.setHoldingSpace(true);
                break;
            case KeyEvent.VK_K:
                player.setAttacking(true);
                break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_ESCAPE:
                game.setGameState(PAUSED);
                break;
        }

    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            // Key released; Stop moving by setting the variables to false
            case KeyEvent.VK_A:
                movingLeft = false;
                break;
            case KeyEvent.VK_D:
                movingRight = false;
                break;
            case KeyEvent.VK_SPACE:
                player.setJumping(false);
                player.setCanJump(true);
                player.setHoldingSpace(false);
                break;
        }

        // Handle when key released, set new direction
        if (movingLeft)
            player.setDirection(LEFT);
        else if (movingRight)
            player.setDirection(RIGHT);
        else
            player.setDirection(STILL);
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            player.setAttacking(true);
    }

    // ====== Getters ======

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

}
