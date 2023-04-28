package helpers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads all the images in the game:
 * Background images,
 * Tiles for the level,
 * Player and enemy sprites,
 * Overlays for paused, level completed
 */
public class ImageLoader {

    public static BufferedImage loadImage(String fileName) {
		BufferedImage image = null;
		// Try to load the image from the input-stream
		try (InputStream is = ImageLoader.class.getResourceAsStream(fileName)) {

			// Read the image only if it exists
			if (is != null)
				image = ImageIO.read(is);
			else
				System.err.println("Error: Image not found\n" + fileName + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return the image
		return image;
    }

}
