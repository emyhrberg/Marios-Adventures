package main;

import helpers.FontLoader;
import helpers.ImageLoader;
import objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import static constants.Direction.*;
import static constants.GameState.*;
import static main.Game.*;
import static main.Player.PLAYER_HEIGHT;
import static main.Player.PLAYER_WIDTH;
import static objects.Coin.coinCount;

/**
 * Playing is where the player plays the game
 * The playing state initializes classes for the game, including player, levels and more
 * This class also shows overlays for example paused or game over depending on the game state
 */
public class Playing extends State {

    // ====== Variables ======
    private final Player player;
    private int levelOffset;
    private int shakeOffset;
    private boolean movingLeft, movingRight;

    private final EnemyManager enemyManager;
    private final LevelManager levelManager;
    private final ObjectManager objectManager;

    // Drawing background sky, hills, clouds
    private static final BufferedImage SKY = ImageLoader.loadImage("/images/bg_sky.png");
    private static final BufferedImage HILLS = ImageLoader.loadImage("/images/hills.png");
    private static final BufferedImage CLOUDS = ImageLoader.loadImage("/images/bg_clouds.png");
    private static final int HILLS_WIDTH = 320*3;
    private static final int HILLS_HEIGHT = 192*3;
    private static final int CLOUDS_WIDTH = 640/2;
    private static final int CLOUDS_HEIGHT = 360/2;

    // Drawing mario
    private static final BufferedImage MARIO = ImageLoader.loadImage("/images/icon.png");
    private static final int MARIO_W = (int) (19*SCALE*2);
    private static final int MARIO_H = (int) (19*SCALE*2);
    private static final int MARIO_X = (int) (MARIO_W + 10 * SCALE);
    private static final int MARIO_Y = (int) (MARIO_H / 1.5);

    // Drawing health
    private static final Font CUSTOM_FONT = FontLoader.loadFont("o.ttf");
    private static final BufferedImage HEART = ImageLoader.loadImage("/images/heart.png");
    private static final BufferedImage HEALTH_BAR = ImageLoader.loadImage("/images/health_bar.png");
    private static final int HEART_WIDTH = 90/2;
    private static final int HEART_HEIGHT = 90/2;
    private static final int BAR_WIDTH = 510/2;
    private static final int BAR_HEIGHT = 50/2;
    private static final int HEALTH_BAR_X = (int) (80 * SCALE);
    private static final int HEALTH_BAR_Y = (int) (50 * SCALE);
    private static final int HEALTH_RED_X = HEALTH_BAR_X + 5;
    private static final int HEALTH_RED_Y = HEALTH_BAR_Y + 5;
    private static final int HEALTH_RED_W = BAR_WIDTH - 10;
    private static final int HEALTH_RED_H = BAR_HEIGHT - 10;
    private static final int HEART_X = (int) (80 * SCALE) - HEART_WIDTH / 2;
    private static final int HEART_Y = (int) (50 * SCALE) + (BAR_HEIGHT / 2 - HEART_HEIGHT / 2);

    // ====== Constructor ======
    public Playing(Game game) {
        super(game);

        // Init classes
        levelManager    = new LevelManager();
        player          = new Player(PLAYER_WIDTH * SCALE * 1.33f, PLAYER_HEIGHT * SCALE * 1.33f, game);
        enemyManager    = new EnemyManager();
        objectManager   = new ObjectManager();

        // Set level for player class
        // Set spawn point
        player.setLevel(levelManager.getLevel());
        setCurrentLevelSpawnPoint();
    }

    // ====== Update methods ======

    public void update() {
        // Update the player and enemies
        player.update();
        enemyManager.update(levelManager.getLevel(), player);
        objectManager.update(levelManager.getLevel(), player);

        // Update game
        updateLevelOffset();
        updatePlayerOutsideLevel();
        updateFinalPointState();
        updateShake();
    }

    private void updateLevelOffset() {
        // Get player X position
        int playerX = (int) player.getHitbox().x;

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
        int finalX = levelManager.getLevel().getFinalPoint().x / TILES_SIZE;
        int finalY = levelManager.getLevel().getFinalPoint().y / TILES_SIZE;
        int playerX = (int) player.getHitbox().x / TILES_SIZE;
        int playerY = (int) player.getHitbox().y / TILES_SIZE;

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

    // ====== Draw methods ======

    public void draw(Graphics g) {
        // Draw background
        drawSky(g);
        drawClouds(g);
        drawHills(g);

        // Draw UI
        if (game.getGameState() == PLAYING) {
            drawMario(g);
            drawHealthText(g);
            drawCoinText(g);
            drawTimeText(g);
        }

        // Draw game
        levelManager.draw(g, levelOffset);
        enemyManager.draw(g, levelOffset);
        objectManager.draw(g, levelOffset);
        player.draw(g, levelOffset);
    }

    private void drawSky(Graphics g) {
        g.drawImage(SKY,0,0, GAME_WIDTH, Game.GAME_HEIGHT,null);
    }

    private void drawHills(Graphics g) {
        // Hill moving speed
        int levelOffsetMult = (int) (levelOffset * 0.2);
        int y = Game.GAME_HEIGHT - HILLS_HEIGHT;

        // Draw 10 seamless hills
        for (int i = 0; i < 10; i++)
            g.drawImage(HILLS, i * HILLS_WIDTH - levelOffsetMult, y, HILLS_WIDTH, HILLS_HEIGHT, null);
    }

    private void drawClouds(Graphics g) {
        // Cloud moving speed
        int levelOffsetMult = (int) (levelOffset * 0.2);
        int y = Game.GAME_HEIGHT - HILLS_HEIGHT + HILLS_HEIGHT / 3;

        // Draw 10 seamless clouds
        for (int i = 0; i < 10; i++)
            g.drawImage(CLOUDS, i * CLOUDS_WIDTH - levelOffsetMult, y, CLOUDS_WIDTH, CLOUDS_HEIGHT, null);
    }

    private void drawMario(Graphics g) {
        g.drawImage(MARIO, MARIO_X, MARIO_Y,MARIO_W, MARIO_H,null);
    }

    private void drawHealthText(Graphics g) {
        // text
        final String health;
        if (player.getHealth() < 10) {
            health = "0" + player.getHealth();
        } else {
            health = String.valueOf(player.getHealth());
        }

        // draw x
        g.setFont(CUSTOM_FONT);
        g.setFont(g.getFont().deriveFont(18f));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(health);
        int h = fm.getHeight();
        int x = MARIO_X + w + 24;
        int y = MARIO_Y + h*2 - 4;
        g.setColor(new Color(224, 224, 224));
        g.drawString("x", x, y);

        // draw health
        g.setFont(g.getFont().deriveFont(32f));
        g.setColor(new Color(224, 224, 224));
        g.drawString(health, x + 24, y);
    }

    private void drawCoinText(Graphics g) {
        // text shape
        String coins;
        if (coinCount < 10) {
            coins = "Coins: 0" + coinCount;
        } else {
            coins = "Coins: " + coinCount;
        }

        // size
        g.setFont(CUSTOM_FONT);
        g.setFont(g.getFont().deriveFont(32f));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(coins);
        int h = fm.getHeight();
        int x = GAME_WIDTH / 2 - w / 2;
        int y = MARIO_Y + h;

        // draw health
        g.setColor(new Color(224, 224, 224));
        g.drawString(coins, x, y);
    }

    private void drawTimeText(Graphics g) {
        int t = game.getTime();
        String time;
        if (t < 100 && t >= 10) {
            time = "0" + t;
        } else if (t < 10) {
            time = "00" + t;
        } else {
            time = String.valueOf(t);
        }
        g.setFont(CUSTOM_FONT);

        // text position
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(time);
        int h = fm.getHeight();
        int x = GAME_WIDTH - MARIO_X - w - 24;
        int y = MARIO_Y + h;

        // draw white
        g.setColor(new Color(224, 224, 224));
        g.drawString(time, x, y);
    }

    // ====== Reset methods ======

    private void setCurrentLevelSpawnPoint() {
        final Point spawnPoint = levelManager.getLevel().getSpawnPoint();
        player.getHitbox().x = spawnPoint.x;
        player.getHitbox().y = spawnPoint.y;
    }

    public void resetGame() {
        player.resetPlayer();
        enemyManager.resetEnemies();
        objectManager.resetAllObjects();
        setCurrentLevelSpawnPoint();

        // set new level data where we reset all the bricks (temp value 91, to its default value of 26)
        int[][] levelData = levelManager.getLevel().getLevelData();
        for (int i = 0; i < levelData.length; i++)
            for (int j = 0; j < levelData[i].length; j++)
                if (levelData[i][j] == 91)
                    levelData[i][j] = 26;

        levelManager.getLevel().setLevelData(levelManager.getLevel().getLevelData());
    }

    public void resetGameGoToMenu() {
        resetGame();
        game.setGameState(MENU);
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

        resetGame();

        // Start playing
        game.setGameState(PLAYING);

    }

    public void setLevelCompleted() {
        // calculate grade by factors speed and coins
        // A below 80 seconds and 30 coins
        if (coinCount >= 30 && game.getTime() >= 220) {
            System.out.println("Grade: A");
        } else if (coinCount >= 20 && game.getTime() >= 200) {
            System.out.println("Grade: B");
        } else if (coinCount >= 10 && game.getTime() >= 150) {
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

    public void pressedMouse(MouseEvent e) {
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
