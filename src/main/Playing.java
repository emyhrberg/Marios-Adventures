package main;

import helpers.FontLoader;
import helpers.ImageLoader;
import objects.ObjectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static constants.Direction.*;
import static constants.GameState.*;
import static main.Game.GAME_WIDTH;
import static main.Game.SCALE;
import static main.Player.PLAYER_HEIGHT;
import static main.Player.PLAYER_WIDTH;

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
    private boolean playerDying = false;

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

    // Drawing health and level
    private static final Font CUSTOM_FONT = FontLoader.loadFont("a.ttf");
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
    private static final int LEVEL_X = (int) (GAME_WIDTH - 150 * SCALE);
    private static final int LEVEL_Y = (int) (50 * SCALE);

    // ====== Constructor ======
    public Playing(Game game) {
        super(game);

        // Init classes
        levelManager    = new LevelManager();
        player          = new Player(0,0,PLAYER_WIDTH * SCALE * 1.33f, PLAYER_HEIGHT * SCALE * 1.33f, game);
        player.setLevel(levelManager.getLevel());
        enemyManager    = new EnemyManager();
        objectManager   = new ObjectManager(game);

        // Set spawn point
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
        updateAnyEnemiesAlive();
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
        int finalX = levelManager.getLevel().getFinalPoint().x;
        int finalY = levelManager.getLevel().getFinalPoint().y;
        int playerX = (int) player.getHitbox().x;
        int playerY = (int) player.getHitbox().y;

        // Check if player is in the middle of final X and in the same Y
        boolean isPlayerInsideFinalX = Math.abs(finalX - playerX) <= Game.TILES_SIZE / 2;
        boolean isPlayerInsideFinalY = Math.abs(finalY - playerY) <= 1;

        if (isPlayerInsideFinalX && isPlayerInsideFinalY)
            setLevelCompleted();
    }

    private void updateAnyEnemiesAlive() {
        if (!enemyManager.isAnyEnemyAlive())
            setLevelCompleted();
    }

    // ====== Draw methods ======

    public void draw(Graphics g) {
        drawSky(g);
        drawClouds(g);
        drawHills(g);
        drawHealth(g);
        drawHeart(g);
//        drawLevelNumber(g);

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

    private void drawHeart(Graphics g) {
        g.drawImage(HEART,HEART_X,HEART_Y, HEART_WIDTH, HEART_HEIGHT,null);
    }

    private void drawHealth(Graphics g) {
        // Draw black bar around health
        g.drawImage(HEALTH_BAR, HEALTH_BAR_X, HEALTH_BAR_Y, BAR_WIDTH, BAR_HEIGHT,null);

        // Draw red health bar inside image
        int rectWHealth = (int) (HEALTH_RED_W * (player.getHealth() / (float) player.getMaxHealth()));
        g.setColor(Color.RED);
        g.fillRect(HEALTH_RED_X, HEALTH_RED_Y, rectWHealth, HEALTH_RED_H);

        // Draw e.g 80/100 text
        final String health = player.getHealth() + "/" + player.getMaxHealth();
        g.setFont(CUSTOM_FONT);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(health);
        int textHeight = fm.getHeight();
        int textX = HEALTH_RED_X + (HEALTH_RED_W - textWidth) / 2;
        int textY = HEALTH_RED_Y - (textHeight / 2) - 4;
        g.setColor(new Color(15,15,15));
        g.drawString(health, textX, textY);
    }

    private void drawLevelNumber(Graphics g) {
        // Create the level string
        final int currLevel = game.getPlaying().getLevelManager().getLevelIndex() + 1;
        final int amountOfLevels = game.getPlaying().getLevelManager().getAmountOfLevels();
        final String level = "Level: " + currLevel + "/" + amountOfLevels;

        // Draw the level text
        g.setFont(CUSTOM_FONT);
        g.setColor(Color.WHITE);
        g.drawString(level, LEVEL_X, LEVEL_Y);
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
            case KeyEvent.VK_K      -> player.setAttacking(true);
            case KeyEvent.VK_P, KeyEvent.VK_ESCAPE -> game.setGameState(PAUSED);
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE -> {
                player.setJumping(false);
                player.setCanJump(true);
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

    // ====== Setters ======

    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }
}
