package eg.ui;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * Opens help and docu sites in the default browser
 */
public class Help {

   /**
    * Shows the help site in the default browser
    */
   public void showHelpSite() {
      try {
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().browse(new URI(
                  "https://eadgyth.github.io/Programming-Editor/help/help.html"));
         }
      }
      catch (IOException | URISyntaxException e) {
         FileUtils.logStack(e);
      }
   }
   
   /**
    * Shows the Docu site in the default browser
    */
   public void showDocuSite() {
      try {
         if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().browse(new URI(
                  "https://eadgyth.github.io/Programming-Editor/"));
         }
      }
      catch (IOException | URISyntaxException e) {
         FileUtils.logStack(e);
      }
   }
}
