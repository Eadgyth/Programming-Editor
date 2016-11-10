package eg.ui;

import java.io.File;
import java.io.IOException;

/**
 * Opens the Help file in the default browser upon creating
 * an object of this class
 */
public class Help {

   private final static String FILE_SEP = File.separator;

   public Help(){
      try{
         File htmlFile = new File("Resources" + FILE_SEP + "Help.html");
         //java.awt.Desktop.getDesktop().browse(htmlFile.toURI());
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(htmlFile);
         }
      }
      catch (IOException e) {}
   }
}