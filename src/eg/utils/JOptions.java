package eg.utils;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;

import javax.swing.border.EmptyBorder;

/**
 * Different static methods to show messages or prompts using
 * <code>JOptionPane</code>
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
            JOptionPane.YES_NO_OPTION);
      return result;
   }
   
   /**
    * Returns the text in the selected item of the <code>JComboBox</code>
    * in the dialog
    *
    * @param message  the message for the dialog (shown in <code>JLabel</code>).
    * The message should be one-line
    * @param title  the title for the dialog
    * @param options  the array of options shown in the combo box
    * @param initOption  the option that is initially selected
    *
    * @return  the text in the selected item of the <code>JComboBox</code>
    * or null if the dialog is closed or cancel is clicked
    */
   public static String comboBoxRes(String message, String title,
         String[] options, String initOption) {

      JComboBox cBox = new JComboBox(options);
      cBox.setSelectedItem(initOption);
      JPanel pnl = new JPanel();
      JLabel lb = new JLabel(message);
      pnl.add(lb);
      JPanel holdCBox = new JPanel(new FlowLayout(FlowLayout.CENTER));
      holdCBox.add(cBox);
      pnl.add(holdCBox);
      int res = JOptionPane.showConfirmDialog(null,
            pnl, title, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

      if (res == JOptionPane.YES_OPTION) {
         return options[cBox.getSelectedIndex()];
      }
      else {
         return null;
      }     
   }
      

   /**
    * Returns the text entered in the text field in the dialog
    *
    * @param message  the message for the dialog
    * @param title  the title for the dialog
    * @param initText  the text that is initially shown in the text field
    * @return  the string entered in the text field or null if cancel was
    * clicked or the window closed
    */
   public static String dialogRes(String message, String title,
         String initText) {

      JFrame frame = new JFrame(); 
      frame.setAlwaysOnTop(true);
      Object resObj = JOptionPane.showInputDialog(frame, message, title,
            JOptionPane.QUESTION_MESSAGE, null, null, initText);
      String res = null;
      if (resObj != null) {
         res = resObj.toString();
      }
      return res;
   }
}
