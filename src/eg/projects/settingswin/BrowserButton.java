package eg.projects.settingswin;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.io.File;

//--Eadgyth--/
import eg.FileChooser;

/**
 * Defines a button which is pressed to select a file or directory
 * in a file chooser and to set the name of the file or directory in
 * a text field
 */
public class BrowserButton {
   
   private final FileChooser fc;
   private final JTextField tf;
   private final JButton bt = new JButton("...");
   
   /**
    * @param fc  the file chooser to select a file or directory
    * @param tf  the text field that displays the name of the
    * selected file or directory
    */
   public BrowserButton(FileChooser fc, JTextField tf) {
      this.fc = fc;
      this.tf = tf;
      bt.addActionListener(e -> setText());
      bt.setFocusable(false);
   }
   
   /**
    * Adds this <code>JButton</code> to the specified
    * <code>JPanel</code>
    *
    * @param pnl  the JPanel
    */
   public void addButton(JPanel pnl) {
      pnl.add(bt);
   }
   
   //
   //--private--/
   //
   
   private void setText() {
      File file = fc.selectedFileOrDirectory();
      if (file != null) {
         String text = file.getName();
         tf.setText(text);
         tf.requestFocusInWindow();
      }
   }
}
