package eg.projects;

import java.io.File;
import java.io.IOException;

//--Eadgyth--//
import eg.utils.FileUtils;
import eg.utils.JOptions;

/**
 * Represents a project to write a webpage in HTML
 */
public final class HtmlActions extends ProjectConfig implements ProjectActions {

   private final static String F_SEP = File.separator;
   
   HtmlActions(String suffix) {
      super("." + suffix);
   }
   
   /**
    * Creates a basic {@link SettingsWin}.
    */
   @Override
   public void createSettingsWin() { 
      SettingsWin setWin = SettingsWin.basicWindow("Name of an HTML file");
      setSettingsWin(setWin);
   }
   
   /**
    * Shows the html document in the default file browser
    * @param filename  the name of the html source file
    */
   @Override
   public void runProject(String filename) {
      File htmlFile = htmlFile(filename);
      String fileStr = htmlFile.toString();
      boolean ok = htmlFile.exists() && fileStr.endsWith(getSourceSuffix());
      if (ok) {
         try{
            if (java.awt.Desktop.isDesktopSupported()) {
               java.awt.Desktop.getDesktop().open(htmlFile);
            }
         }
         catch (IOException e) {
            FileUtils.logStack(e);
         }
      }
      else {
         JOptions.warnMessage("This file is not html code or not found in the"
               + " project's root direcory");
      }
   }
   
   private File htmlFile(String filename) {      
      return new File(getProjectPath() + F_SEP + filename);
   }
}
