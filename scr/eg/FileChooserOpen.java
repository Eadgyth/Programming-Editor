package eg;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.Icon;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileSystemView;

import java.io.File;

/**
 * Select a file in JFileChooser
 */
public class FileChooserOpen {

   private final JFrame frame = new JFrame();
   private JFileChooser chooser = null;

   FileChooserOpen(String startingDir) {
      setLaf();
      File recent = new File(startingDir);
      chooser.setCurrentDirectory(recent);
      chooser.setAcceptAllFileFilterUsed(false);
      chooser.setApproveButtonText("Open");
      chooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "",
            "txt", "java", "pl", "pm", "properties", "html", "htm"));
      chooser.setDialogTitle("Eadgyth - Open");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
   }

   /**
    * @return  the file selected in the file chooser or null if
    * cancel was clicked or the chooser window closed
    */
   public File chosenFile() {
      File chosenFile = null;
      if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
         chosenFile = chooser.getSelectedFile();
         chooser.setCurrentDirectory(chosenFile.getParentFile());       
      }
      return chosenFile;
   }

   private void setLaf() {
      //
      // disable to create a new folder. Makes sense if this is created
      // before FileChooserSave 
      UIManager.put("FileChooser.readOnly", Boolean.TRUE);
      chooser = new JFileChooser();
      if ("Metal".equals(UIManager.getLookAndFeel().getName())) {       
         chooser.setFileView(new FileView() {

            @Override
            public Icon getIcon(File f) {
               return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
        });
      }
      UIManager.put("FileChooser.readOnly", Boolean.FALSE);
   }
}
