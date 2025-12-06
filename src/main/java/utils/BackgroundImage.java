

package utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.border.Border;

/**
 *
 * @author Mariana
 */
public class BackgroundImage implements Border{

    public BufferedImage back;

    public BackgroundImage(){
        try {
            URL imagePath = Objects.requireNonNull(
                    getClass().getResource("/icons/BYB.jpg"),
                    "Background image not found: /icons/BYB.jpg");
            back = ImageIO.read(imagePath);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load background image", ex);
        }
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (back != null) {
            g.drawImage(back, (x + (width - back.getWidth())/2), (y + (height - back.getHeight()) /2), null);
        }

    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,0,0);
    }

    public boolean isBorderOpaque() {
        return false;
    }

}
