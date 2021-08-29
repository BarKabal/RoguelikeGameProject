package map.items;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class item {

    public BufferedImage getPng() {
        try {
            String name = "src/graphics/items/" + this.getClass().getName().substring(10) + ".png";
            return ImageIO.read(new File(name));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
