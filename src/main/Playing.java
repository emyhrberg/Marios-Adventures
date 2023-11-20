package main;

import helpers.ImageLoader;
import objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
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
    private int levelOffset;
    private boolean movingLeft, movingRight;

    // Game managers
    private final EnemyManager enemyManager;
    private final LevelManager levelManager;
    private final ObjectManager objectManager;

    // Draw background: Sky, forest, clouds
    private static final BufferedImage SKY = ImageLoader.loadImage("/ui/sky.png");
    private static final BufferedImage BIG_CLOUDS = ImageLoader.loadImage("/ui/big-clouds.png");
    private static final BufferedImage SMALL_CLOUDS = ImageLoader.loadImage("/ui/small-clouds.png");
    private static final BufferedImage FOREST = ImageLoader.loadImage("/ui/forest.png");
    private BufferedImage offScreenImage;

    // Drawing HUD new
    private static final Font CUSTOM_FONT = helpers.FontLoader.loadFont("/fonts/inside-out.otf");
    private static final BufferedImage MARIO = ImageLoader.loadImage("/ui/mario-icon.png");
    private static final BufferedImage COIN = ImageLoader.loadImage("/ui/coin-icon.png");
    private static final int MARIO_W = (int) (19*SCALE*2);
    private static final int MARIO_H = (int) (19*SCALE*2);
    private static final int MARIO_X = (int) (MARIO_W + 10 * SCALE);
    private static final int MARIO_Y = (int) (MARIO_H / 1.5);
    private static final int COIN_Y = (int) (MARIO_H / 1.5 + MARIO_H + 10 * SCALE);
    private static final int TOP_Y = (int) (MARIO_Y + MARIO_H - 4 * SCALE);
    private static final int X_NEXT_TO_ICON = (int) (MARIO_X + MARIO_W + 4 * SCALE);
    private static final float sizeX = 28 * SCALE;
    private static final float size00 = 40 * SCALE;

    // ====== Constructor ======
    public Playing(Game game) {
        super(game);

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
        levelOffset = Math.max(Math.min((int) player.getHitbox().x - GAME_WIDTH / 2, levelManager.getLevel().getMaxLevelOffset()), 0);

        // Update playing stuff
        updatePlayerOutsideLevel();
        updateFinalPointState();
    }

    private void updatePlayerOutsideLevel() {
        int playerY = (int) player.hitbox.y / TILES_SIZE;
        int bottomY = TILES_IN_HEIGHT;
        boolean isPlayerBelowLevel = playerY >= bottomY + 3;

        if (isPlayerBelowLevel)
            game.setGameState(GAME_OVER);
    }

    private void updateFinalPointState() {
        // Get the X and Y position for the player and "final point"
        int finalX = levelManager.getLevel().getFinalPoint().x / TILES_SIZE;
        int finalY = levelManager.getLevel().getFinalPoint().y / TILES_SIZE;
        int playerX = (int) player.getHitbox().x / TILES_SIZE;
        int playerY = (int) player.getHitbox().y / TILES_SIZE;

        // Player is inside the final position, meaning
        // Exact X position ( + 1 for player hitbox width offset)
        // Exact Y position on the bottom or up to 3 tiles above it.
        if (playerX + 1 == finalX && playerY <= finalY && playerY + 3 >= finalY)
            setLevelOrGameCompleted();
    }

    private void setLevelOrGameCompleted() {
        // Set level complete or game complete, if at the last level
        final int currentLevel = game.getPlaying().getLevelManager().getLevelIndex();
        final int last = game.getPlaying().getLevelManager().getAmountOfLevels() - 1;
        if (currentLevel == last)
            game.setGameState(GAME_COMPLETED);
        else
            game.setGameState(LEVEL_COMPLETED);
    }

    // ====== Draw ======

    public void draw(Graphics g) {
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        // Draw background
        drawSky(g);
        drawForest(g);
        drawSmallClouds(g);
        drawBigClouds(g);

        // Draw mario and coin icon
        g.drawImage(MARIO, MARIO_X, MARIO_Y,MARIO_W, MARIO_H,null);
        g.drawImage(COIN, MARIO_X, COIN_Y,MARIO_W, MARIO_H,null);

        // Draw mario and coin text
        drawHealthCount(g);
        drawCoinCount(g);

        // Draw game
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
        g.setColor(new Color(0,0,0,220));
        g.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);

        // Dispose of the off-screen graphics object to release system resources
        g2d.dispose();
    }

    // ====== Background ======

    private void drawSky(Graphics g) {
        int x = (int) (-levelOffset * 0.17);
        int y = 0;
        int w = (int) (4320  * SCALE);
        int h = GAME_HEIGHT;
        for (int i = 0; i < 8; i++) {
            g.drawImage(SKY, x + i * w, y, w, h, null);
        }
    }

    private void drawForest(Graphics g) {
        int x = (int) (-levelOffset * 0.17);
        int y = (int) (230 * SCALE);
        int w = (int) (1024 / 2 * SCALE);
        int h = (int) (1024 / 2 * SCALE);
        for (int i = 0; i < 8; i++) {
            g.drawImage(FOREST, x + i * w, y, w, h, null);
        }
    }

    private void drawSmallClouds(Graphics g) {
        // set opacity
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        final int x = (int) (-levelOffset * 0.1f * SCALE);
        final int y = (int) (25 * SCALE);
        g.drawImage(SMALL_CLOUDS, x, y, null);

        // reset opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawBigClouds(Graphics g) {
        final int x = (int) (-levelOffset * 0.25f * SCALE);
        final int y = (int) (80 * SCALE);
        g.drawImage(BIG_CLOUDS, x, y, null);
    }

    private void drawHealthCount(Graphics g) {


        // X
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform originalTransform = g2d.getTransform();

        g.setFont(CUSTOM_FONT.deriveFont(sizeX));
        int w = g.getFontMetrics().stringWidth("x");
        TextLayout tl = new TextLayout("x", g.getFont(), g2d.getFontRenderContext());
        Shape shape = tl.getOutline(null);
        AffineTransform transform = AffineTransform.getTranslateInstance(X_NEXT_TO_ICON, TOP_Y);
        g2d.transform(transform);

        // draw black X
        g2d.setStroke(new BasicStroke(5f));
        g2d.setColor(new Color(5, 5, 5));
        g2d.draw(shape);

        // draw white X
        g2d.setColor(new Color(224, 224, 224));
        g2d.fill(shape);
        g2d.setTransform(new AffineTransform());

        // HEALTH
        final String health = (player.getHealth() < 10) ? "0" + player.getHealth() : String.valueOf(player.getHealth());
        g.setFont(g.getFont().deriveFont(size00));
        int x2 = (int) (X_NEXT_TO_ICON + w + 2 * SCALE);
        TextLayout tl2 = new TextLayout(health, g.getFont(), g2d.getFontRenderContext());
        Shape shape2 = tl2.getOutline(null);
        AffineTransform transform2 = AffineTransform.getTranslateInstance(x2, TOP_Y);
        g2d.transform(transform2);

        // draw black
        g2d.setStroke(new BasicStroke(5f));
        g2d.setColor(new Color(5, 5, 5));
        g2d.draw(shape2);

        // draw white
        g2d.setColor(new Color(224, 224, 224));
        g2d.fill(shape2);

        // restore the original transform
        g2d.setTransform(originalTransform);
//        g2d.setTransform(new AffineTransform());
//        g2d.dispose();
    }

    private void drawCoinCount(Graphics g) {
        String coins = (coinCount <= 9) ? "0" + coinCount : String.valueOf(coinCount);

        Graphics2D g2d = (Graphics2D) g;

        AffineTransform originalTransform = g2d.getTransform();

        g.setFont(g.getFont().deriveFont(size00));
        TextLayout tl = new TextLayout(coins, g.getFont(), g2d.getFontRenderContext());
        Shape shape2 = tl.getOutline(null);
        int y = (int) (COIN_Y + MARIO_H - 4 * SCALE);
        AffineTransform transform = AffineTransform.getTranslateInstance(X_NEXT_TO_ICON, y);
        g2d.transform(transform);

        // draw black
        g2d.setStroke(new BasicStroke(5f));
        g2d.setColor(new Color(5, 5, 5));
        g2d.draw(shape2);

        // draw white
        g2d.setColor(new Color(224, 224, 224));
        g2d.fill(shape2);

        // restore the original transform
        g2d.setTransform(originalTransform);
    }

    // ====== Reset methods ======

    private void resetSpawnPoint() {
        // get current level spawn point
        Point spawnPoint = levelManager.getLevel().getSpawnPoint();

        // set player position to level spawn point
        player.getHitbox().x = spawnPoint.x;
        player.getHitbox().y = spawnPoint.y;

//        System.out.println("spawn point: " + spawnPoint);
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
        resetGame();
        game.setGameState(PLAYING);
    }

    public void resetGameAndStartAtFirstLevel() {
        levelManager.setLevelIndex(0);
        player.setLevel(levelManager.getLevel());

        // Reset
        resetGame();
        game.setGameState(MENU);
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
