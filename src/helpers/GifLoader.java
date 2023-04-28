package helpers;

import main.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.net.URL;

/**
 * GifLoader is a helper class used to load gifs to the game
 * Good reference for loading gifs in javax.swing: https:// stackoverflow.com/questions/49826647/java-problems-with-gif-in-label
 */
public class GifLoader {
    // ====== Variables ======
    private final ImageIcon imageIcon;
    private final AffineTransform transform;
    private final Dimension position;

    // ====== Constructor ======
    public GifLoader(ImageIcon imageIcon, AffineTransform transform, Dimension position) {
		this.imageIcon = imageIcon;
		this.transform = transform;
		this.position = position;
    }

    public static GifLoader loadGif(String fileName) {
		// Load URL
		URL url = ImageLoader.class.getResource(fileName);
		if (url == null) {
			System.err.println("Error: Gif not found\n" + fileName);
			return null;
		}

		// Load image
		ImageIcon imageIcon = new ImageIcon(url);

		// Scale image to fill screen
		double width 	= Game.GAME_WIDTH / (double) imageIcon.getIconWidth();
		double height 	= Game.GAME_HEIGHT / (double) imageIcon.getIconHeight();
		double scale 	= Math.max(width, height);
		double x 	= (Game.GAME_WIDTH - imageIcon.getIconWidth() * scale) / 2;
		double y 	= (Game.GAME_HEIGHT - imageIcon.getIconHeight() * scale) / 2;
		AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
		Dimension position = new Dimension((int) x, (int) y);

		// Return image and centering info
		return new GifLoader(imageIcon, transform, position);
    }

    // ====== Getters ======

    public ImageIcon getImageIcon() {
		return imageIcon;
    }

    public AffineTransform getTransform() {
		return transform;
    }

    public Dimension getPosition() {
		return position;
    }
}
