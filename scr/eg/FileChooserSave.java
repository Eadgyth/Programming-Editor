package eg;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.Icon;

import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileSystemView;

import java.io.File;

/**
 * Specify a file to save in JFileChooser
 */
public class FileChooserSave {

   private final JFrame frame = new JFrame();
   private final Preferences prefs = new Preferences();
   private JFileChooser chooser;

   FileChooserSave() {
      setLaf();
      prefs.readPrefs();
      File recent = new File(prefs.getProperty("recentPath"));  
      chooser.setCurrentDirectory(recent);
      chooser.setAcceptAllFileFilterUsed(true); 
   }

   /**
    * @return  the file to save
    */  
   public File fileToSave() {
      File fileToSave = null;

      int result = chooser.showSaveDialog(frame);  
      if (result == JFileChooser.APPROVE_OPTION) {
         fileToSave = chooser.getSelectedFile();
         chooser.setCurrentDirectory(fileToSave.getParentFile()); 
      }
      return fileToSave;
   }

   private void setLaf() {
      chooser = new JFileChooser();
      if ("Metal".equals(UIManager.getLookAndFeel().getName())) {
         chooser.setFileView(new FileView(){
            @Override
            public Icon getIcon(File f) {
               return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
        });
      }
   }
}
