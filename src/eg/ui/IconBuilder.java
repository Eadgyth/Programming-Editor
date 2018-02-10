package eg.ui;

import javax.swing.ImageIcon;
import java.net.URL;

public class IconBuilder {
      
   public ImageIcon createIcon(String path) {
      URL imgURL = getClass().getResource(path);
      if (imgURL != null) {
          return new ImageIcon(imgURL);
      }
      else {
         System.err.println("Couldn't find file: " + path);
         return null;
      }
   }
}
