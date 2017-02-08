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

   public FileChooserSave(String startingDir) {
      setLaf();
      chooser.setCurrentDirectory(new File(startingDir));
      chooser.setAcceptAllFileFilterUsed(true);
      chooser.setDialogTitle("Eadgyth - Save as");
   }

   /**
    * Returns a File object if ok is clicked and null if cancel was
    * clicked or the window was closed.
    * @param presetFile  the file that is preselected. If null
    * or the empty String the preset dir is taken from recent dir
    * in prefs
    * @return  the file to save
    */  
   public File fileToSave(String presetFile) {
      File fileToSave = null;

      if (presetFile != null && presetFile.length() > 0) {
         File toSet = new File(presetFile);
         chooser.setSelectedFile(toSet);
      }
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
