package eg.ui;

import javax.swing.ImageIcon;
import java.net.URL;

/**
 * The creation of an icon
 */
public class IconBuilder {

   /**
    * Creates an <code>ImageIcon</code>
    *
    * @param path  the path of the file that contains the icon
    * @return  ImageIcon
    */
   public ImageIcon createIcon(String path) {
      URL imgURL = getClass().getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL);
      }
      else {
         throw new IllegalArgumentException(
               "No image file was found in the specified path");
      }
   }
}
