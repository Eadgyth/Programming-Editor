package eg;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.Icon;

import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileSystemView;

import java.io.File;

/**
 * Defines a <code>JFileChooser</code> which can be initialized in
 * different ways
 */
public class FileChooser {

   private final JFrame frame = new JFrame();
   private JFileChooser ch = null;
   private File currentDir;

   public FileChooser() {
      ch = new JFileChooser();
   }

   /**
    * @param startingDir  the directory initially shown
    */
   public FileChooser(String startingDir) {
      currentDir = new File(startingDir);
      ch = new JFileChooser(currentDir);
   }

   /**
    * Initializes the chooser to open a file
    */
   public void initOpenFileChooser() {
      ch.setDialogTitle("Open");
      ch.setApproveButtonText("Open");
      ch.setAcceptAllFileFilterUsed(true);
      ch.setFileSelectionMode(JFileChooser.FILES_ONLY);
      setIcons(ch);
   }

   /**
    * Initializes the chooser to save a file
    */
   public void initSaveFileChooser() {
      ch.setDialogTitle("Save file");
      ch.setAcceptAllFileFilterUsed(true);
      setIcons(ch);
   }

   /**
    * Initializes the chooser to select a file or directory
    */
   public void initSelectFileOrDirectoryChooser() {
      ch.setDialogTitle("Select file/directory");
      ch.setApproveButtonText("Select");
      ch.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      setIcons(ch);
   }

   /**
    * Opens the chooser to select a file to open
    *
    * @return  the file; null if cancel was clicked or the chooser
    * window was closed
    */
   public File selectedFile() {
      File f = null;
      int res = ch.showOpenDialog(frame);
      if (res == JFileChooser.APPROVE_OPTION) {
         f = ch.getSelectedFile();
         currentDir = f.getParentFile();
         ch.setCurrentDirectory(currentDir);
      }
      return f;
   }

   /**
    * Opens the chooser to select a file to save
    *
    * @param presetFile  the filename that is shown in the text field
    * to specify a file. Can be Null or the empty string
    * @return  the file; null if cancel was clicked or the chooser
    * window was closed
    */
   public File selectedFileToSave(String presetFile) {
      File f = null;
      if (presetFile != null && presetFile.length() > 0) {
         File toSet = new File(presetFile);
         ch.setSelectedFile(toSet);
      }
      int res = ch.showSaveDialog(frame);
      if (res == JFileChooser.APPROVE_OPTION) {
         f = ch.getSelectedFile();
         currentDir = f.getParentFile();
         ch.setCurrentDirectory(currentDir);
      }
      return f;
   }

   /**
    * Opens the chooser to select a file or directory
    *
    * @return  the file or directory; null if cancel was clicked or the chooser
    * window was closed
    */
   public File selectedFileOrDirectory() {
      File f = null;
      int res = ch.showOpenDialog(frame);
      if (res == JFileChooser.APPROVE_OPTION) {
         f = ch.getSelectedFile();
         if (f.isFile()) {
            currentDir = f.getParentFile();
         }
         else {
            currentDir = f;
         }
         ch.setCurrentDirectory(currentDir);
      }
      return f;
   }

   /**
    * Sets the directory for the chooser
    *
    * @param dir  the directory
    */
   public void setDirectory(String dir) {
      File f = new File(dir);
      if (currentDir != null && f.equals(currentDir)) {
         return;
      }
      currentDir = f;
      ch.setCurrentDirectory(currentDir);
      if (ch.getFileSelectionMode() == JFileChooser.FILES_AND_DIRECTORIES) {
         ch.setSelectedFile(currentDir);
      }
   }

   /**
    * Returns the directory selected most recently
    *
    * @return  the directory
    */
   public String currentDir() {
      return currentDir.toString();
   }

   //
   //--private--/
   //

   private void setIcons(JFileChooser ch) {
      if ("Metal".equals(UIManager.getLookAndFeel().getName())) {
         ch.setFileView(new FileView() {

            @Override
            public Icon getIcon(File f) {
               return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
         });
      }
   }
}
