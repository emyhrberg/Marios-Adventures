package main;

import helpers.FontLoader;
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

/**
 * Playing is where the player plays the game
 * The playing state initializes classes for the game, including player, levels and more
 * This class also shows overlays for example paused or game over depending on the game state
 */
public class Playing extends GameState {

    // ====== Variables ======
    private static final float PLAYER_SCALE = 1.33f * Game.SCALE;
    private final Player player;
    private Point spawnPoint;
    private int levelOffset;
    private int shakeOffset;
    private long lastSec;
    private boolean movingLeft, movingRight;
    private static final int START_T = 300;
    private int t = START_T;
    private static final Font CUSTOM_FONT = FontLoader.loadFont("/fonts/inside-out.ttf");

    private final EnemyManager enemyManager;
    private final LevelManager levelManager;
    private final ObjectManager objectManager;

    // Drawing background
    private static final BufferedImage SKY = ImageLoader.loadImage("/ui/sky.png");
    private static final BufferedImage BIG_CLOUDS = ImageLoader.loadImage("/ui/big-clouds.png");
    private static final BufferedImage SMALL_CLOUDS = ImageLoader.loadImage("/ui/small-clouds.png");
    private static final BufferedImage FOREST = ImageLoader.loadImage("/ui/forest.png");

    // Drawing UI
    private static final BufferedImage MARIO = ImageLoader.loadImage("/ui/mario-icon.png");
    private static final BufferedImage COIN = ImageLoader.loadImage("/ui/coin-icon.png");
    private static final int MARIO_W = (int) (19*Game.SCALE*2);
    private static final int MARIO_H = (int) (19*Game.SCALE*2);
    private static final int MARIO_X = (int) (MARIO_W + 10 * Game.SCALE);
    private static final int MARIO_Y = (int) (MARIO_H / 1.5);
    private static final int COIN_Y = (int) (MARIO_H / 1.5 + MARIO_H + 10 * Game.SCALE);
    private static final int TOP_Y = (int) (MARIO_Y + MARIO_H - 4 * Game.SCALE);
    private static final int X_NEXT_TO_ICON = (int) (MARIO_X + MARIO_W + 4 * Game.SCALE);
    private static final float sizeX = 21 * Game.SCALE;
    private static final float size00 = 35 * Game.SCALE;

    // ====== Constructor ======
    public Playing(Game game) {
        super(game);

        // Init classes
        levelManager    = new LevelManager();
        player          = new Player(PLAYER_WIDTH * PLAYER_SCALE, PLAYER_HEIGHT * PLAYER_SCALE, game);
        enemyManager    = new EnemyManager();
        objectManager   = new ObjectManager();

        // Set level for player class
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
        levelOffset = playerX - Game.GAME_WIDTH / 2;

        // Reset level offset if at the leftmost of the map
        if (levelOffset < 0)
            levelOffset = 0;

        // Reset level offset if at the rightmost of the map
        final int maxLevelOffset = levelManager.getLevel().getMaxLevelOffset();
        if (levelOffset > maxLevelOffset)
            levelOffset = maxLevelOffset;
    }

    private void updatePlayerOutsideLevel() {
        if (player.getHitbox().y - player.getHitbox().height * 2 >= Game.GAME_HEIGHT)
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
        int finalX = levelManager.getLevel().getFinalPoint().x / Game.TILES_SIZE;
        int finalY = levelManager.getLevel().getFinalPoint().y / Game.TILES_SIZE;
        int playerX = (int) player.getHitbox().x / Game.TILES_SIZE;
        int playerY = (int) player.getHitbox().y / Game.TILES_SIZE;

//        System.out.println("X: " + playerX + " | Y: " + playerY);
//        System.out.println("X: " + finalX + " | Y: " + finalY);

        // Player is inside the final position
        // Exact X position
        // At the exact Y position on the bottom or up to 3 tiles above it.
        if (playerX == finalX) {
            if (playerY >= finalY - 3 && playerY <= finalY) {
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
        if (offScreenImage == null || offScreenImage.getWidth() != Game.GAME_WIDTH || offScreenImage.getHeight() != Game.GAME_HEIGHT) {
            offScreenImage = new BufferedImage(Game.GAME_WIDTH, Game.GAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
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
        g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);

        // Dispose of the off-screen graphics object to release system resources
        g2d.dispose();
    }

    public void draw(Graphics g) {
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        // Draw background
        drawSky(g);
        drawForest(g);
        drawSmallClouds(g);
        drawBigClouds(g);

        // Draw UI
        drawMarioIcon(g);
        drawCoinIcon(g);
        drawHealthText(g);
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

    private void drawSky(Graphics g) {
        g.drawImage(SKY,0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT,null);
    }

    // ====== Background ======

    private void drawForest(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)); // Set opacity to 0.5
        int x = (int) (-levelOffset * 0.17);
        int y = (int) (130 * Game.SCALE);
        int w = (int) (1024 / 2 * Game.SCALE);
        int h = (int) (600 / 2 * Game.SCALE);
        for (int i = 0; i < 4; i++) {
            g.drawImage(FOREST, x + i * w, y, w, h, null);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
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

    // ====== UI ======

    private void drawMarioIcon(Graphics g) {
        g.drawImage(MARIO, MARIO_X, MARIO_Y,MARIO_W, MARIO_H,null);
    }

    private void drawHealthText(Graphics g) {
        // X
        Graphics2D g2d = (Graphics2D) g;
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
        int x2 = (int) (X_NEXT_TO_ICON + w + 2 * Game.SCALE);
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
        g2d.setTransform(new AffineTransform());
    }

    private void drawCoinIcon(Graphics g) {
        g.drawImage(COIN, MARIO_X, COIN_Y,MARIO_W, MARIO_H,null);
    }

    private void drawCoinCount(Graphics g) {
        String coins = (coinCount <= 9) ? "0" + coinCount : String.valueOf(coinCount);

        Graphics2D g2d = (Graphics2D) g;
        g.setFont(g.getFont().deriveFont(size00));
        TextLayout tl = new TextLayout(coins, g.getFont(), g2d.getFontRenderContext());
        Shape shape2 = tl.getOutline(null);
        int y = (int) (COIN_Y + MARIO_H - 4 * Game.SCALE);
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
        g2d.setTransform(new AffineTransform());
    }

    private void drawCountdownTimer(Graphics g) {
        String countdown = (t < 100 && t >= 10) ? "0" + t : (t < 10) ? "00" + t : String.valueOf(t);
        Graphics2D g2d = (Graphics2D) g;
        g.setFont(g.getFont().deriveFont(48f));
        TextLayout tl = new TextLayout(countdown, g.getFont(), g2d.getFontRenderContext());
        Shape shape = tl.getOutline(null);
        int w = g.getFontMetrics().stringWidth(countdown);
        int x = Game.GAME_WIDTH - MARIO_X - w;
        AffineTransform transform = AffineTransform.getTranslateInstance(x, TOP_Y);
        g2d.transform(transform);

        // draw black
        g2d.setStroke(new BasicStroke(5f));
        g2d.setColor(new Color(5, 5, 5));
        g2d.draw(shape);

        // draw white
        g2d.setColor(new Color(224, 224, 224));
        g2d.fill(shape);

        // restore the original transform
        g2d.setTransform(new AffineTransform());

    }

    // ====== Reset methods ======

    private void resetSpawnPoint() {
        // Set saved spawn
        if (spawnPoint != null)
            spawnPoint = new Point((int) player.hitbox.x, (int) player.hitbox.y);

        // Otherwise, get spawn point from map
        else
            spawnPoint = levelManager.getLevel().getSpawnPoint();

        // Put player hitbox at the point
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
        spawnPoint = null;
        resetGame();
        game.setGameState(MENU);
    }

    public void resetGameSavePoint() {
        spawnPoint = new Point((int) player.getHitbox().x, (int) player.getHitbox().y);
        resetGame();
        game.setGameState(MENU);
    }

    public void resetGameGoToPlaying() {
        spawnPoint = null;
        resetGame();
        game.setGameState(PLAYING);
    }

    public void resetGameLoadNextLevel() {
        // Load next level
        final int nextLevel = levelManager.getLevelIndex() + 1;
        levelManager.setLevelIndex(nextLevel);

        player.setLevel(levelManager.getLevel()); // todo not necc prob

        resetGame();

        // Start playing
        game.setGameState(PLAYING);
    }

    public void setLevelCompleted() {
        // calculate grade by factors speed and coins
        // A below 80 seconds and 30 coins
        if (coinCount >= 30 && t >= 220) {
            System.out.println("Grade: A");
        } else if (coinCount >= 20 && t >= 200) {
            System.out.println("Grade: B");
        } else if (coinCount >= 10 && t >= 150) {
            System.out.println("Grade: C");
        } else {
            System.out.println("Grade: D");
        }

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
            case KeyEvent.VK_SPACE  -> {
                player.setJumping(true);
                player.setHoldingSpace(true);
            }
            case KeyEvent.VK_A -> {
                movingLeft = true;
                if (movingRight)
                    player.setDirection(STILL);
                else
                    player.setDirection(LEFT);
            }
            case KeyEvent.VK_D -> {
                movingRight = true;
                if (movingLeft)
                    player.setDirection(STILL);
                else
                    player.setDirection(RIGHT);
            }
            case KeyEvent.VK_K -> player.setAttacking(true);
            case KeyEvent.VK_P, KeyEvent.VK_ESCAPE -> game.setGameState(PAUSED);
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE -> {
                player.setJumping(false);
                player.setCanJump(true);
                player.setHoldingSpace(false);
            }
            // Key released; Stop moving by setting the variables to false
            case KeyEvent.VK_A -> movingLeft = false;
            case KeyEvent.VK_D -> movingRight = false;
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
