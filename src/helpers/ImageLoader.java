package helpers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads all the images in the game, for example:
 * Background images,
 * Tiles for the level,
 * Player and enemy sprites,
 * Overlays for paused, level completed
 */
public class ImageLoader {

	public static BufferedImage loadImage(String fileName) {
		try (InputStream is = ImageLoader.class.getResourceAsStream(fileName)) {
			if (is == null) {
				System.err.println("Error: Image not found\n" + fileName + "\n");
				return null; // handle image not found
			}
			return ImageIO.read(is); // read and return the image
		} catch (IOException e) {
			e.printStackTrace();
			return null; // handle image loading error
		}
	}

}
