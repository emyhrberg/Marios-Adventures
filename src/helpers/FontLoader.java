package helpers;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {

    public static Font loadFont(String fontName) {
        Font font = null;
        try (InputStream is = FontLoader.class.getResourceAsStream("/fonts/" + fontName)) {

            // No font resource found, return standard
            if (is == null)
                return new Font("Arial", Font.PLAIN, 30);

            // Create and register the custom font
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(28f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return font;
    }
}
