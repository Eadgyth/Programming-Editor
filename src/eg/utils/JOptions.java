package eg.utils;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

/**
 * Different static methods to show messages or prompts using
 * {@code JOptionPane}
 */
public class JOptions {

   public static void infoMessage(String message) {  
      JOptionPane.showMessageDialog (null, message, "",
            JOptionPane.INFORMATION_MESSAGE);
   }
   
   public static void titledInfoMessage(String message, String title) {  
      JOptionPane.showMessageDialog (null, message, title,
            JOptionPane.INFORMATION_MESSAGE);
   }
   
   public static void warnMessage(String message) {  
      JOptionPane.showMessageDialog(null, message, "",
           JOptionPane.WARNING_MESSAGE );
   }
   
   public static void warnMessageToFront(String message) {
      final JDialog dialog = new JDialog();
      dialog.setAlwaysOnTop(true);
      JOptionPane.showMessageDialog(dialog, message, "",
            JOptionPane.WARNING_MESSAGE );
   }
   
   public static int confirmYesNoCancel(String message) {
      final JDialog dialog = new JDialog();
      dialog.setAlwaysOnTop(true);
      int result = JOptionPane.showConfirmDialog(dialog, message, "",
            JOptionPane.YES_NO_CANCEL_OPTION );
      return result;
   }
   
   public static int confirmYesNo(String message) {
      final JDialog dialog = new JDialog();
      dialog.requestFocusInWindow();
      dialog.setAlwaysOnTop(true);
      int result = JOptionPane.showConfirmDialog(dialog, message, "",
            JOptionPane.YES_NO_OPTION );
      return result;
   }
   
   public static String comboBoxRes(String message, String title,
         String[] options, String currentVal) {

      String res = (String)JOptionPane.showInputDialog(
           null, message, title, JOptionPane.PLAIN_MESSAGE, null,
           options, currentVal);        
      return res;
   }

   /**
    * @param message  the message for the dialog
    * @param title  the title for the dialog
    * @param init  the text that is initially shown in the text field
    * @return  the string entered in the text field or null if cancel was
    * clicked or the window closed
    */
   public static String dialogRes(String message, String title, String init) {
      JFrame frame = new JFrame(); 
      frame.setAlwaysOnTop(true);
      Object resObj = JOptionPane.showInputDialog(frame, message, title,
            JOptionPane.QUESTION_MESSAGE, null, null, init);
      String res = null;
      if (resObj != null) {
         res = resObj.toString();
      }
      return res;
   }
}
