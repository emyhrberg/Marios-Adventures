package helpers;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class loads a custom font
 */
public class FontLoader {

    public static Font loadFont(String fontName) {
        try (InputStream is = FontLoader.class.getResourceAsStream(fontName)) {

            // No font resource found, return standard
            if (is == null)
                return new Font("Arial", Font.PLAIN, 30);

            // Create and register the custom font
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(32f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
