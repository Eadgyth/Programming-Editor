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
   
   public static void errorMessage(String message) {
      JOptionPane.showMessageDialog(null, message, "",
           JOptionPane.ERROR_MESSAGE );
   }

   public static void warnMessageToFront(String message) {
      final JDialog dialog = new JDialog();
      dialog.setAlwaysOnTop(true);
      JOptionPane.showMessageDialog(dialog, message, "",
            JOptionPane.WARNING_MESSAGE);
   }

   /**
    * Shows a 'confirm dialog' and returns the integer that is the Yes,
    * the No or the Cancel option as specified in
    * <code>JOptionPane</code>.
    * The dialog is set to be always on top and has focus.
    *
    * @param message  the message for the dialog
    * @return  the integer that is the Yes, the No or the Cancel option
    * as specified in <code>JOptionPane</code>
    */
   public static int confirmYesNoCancel(String message) {
      final JDialog dialog = new JDialog();
      dialog.setAlwaysOnTop(true);
      int result = JOptionPane.showConfirmDialog(dialog, message, "",
            JOptionPane.YES_NO_CANCEL_OPTION);
      return result;
   }

   /**
    * Shows a 'confirm dialog' and returns the integer that is the Yes
    * or the No option as specified in <code>JOptionPane</code>.
    * The dialog is set to be always on top and has focus.
    *
    * @param message  the message for the dialog
    * @return  the integer that is either the Yes or the No option
    * specified in <code>JOptionPane</code>
    */
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
    * that is shown in the dialog
    *
    * @param message  the message for the dialog
    * @param title  the title for the dialog
    * @param options  the array of options shown in the combo box
    * @param initOption  the option that is initially selected
    * @param isQuestion  true to show a question icon, false to show no icon
    * @return  the text in the selected item of the <code>JComboBox</code>
    * or null if ok wasn't clicked
    */
   public static String comboBoxRes(String message, String title,
         String[] options, String initOption, boolean isQuestion) {

      JComboBox cBox = new JComboBox(options);
      cBox.setSelectedItem(initOption);
      cBox.setFont(eg.Constants.SANSSERIF_PLAIN_9);
      JPanel pnl = new JPanel(new GridLayout(2, 1));
      JLabel lb = new JLabel(message);
      lb.setBorder(eg.Constants.EMPTY_BORDER);
      pnl.add(lb);
      JPanel holdCBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdCBox.add(cBox);
      pnl.add(holdCBox);
      int messageType = JOptionPane.PLAIN_MESSAGE;
      if (isQuestion) {
         messageType = JOptionPane.QUESTION_MESSAGE;
      }
      int res = JOptionPane.showConfirmDialog(null, pnl, title,
            JOptionPane.OK_CANCEL_OPTION, messageType);
      if (res == JOptionPane.YES_OPTION) {
         return options[cBox.getSelectedIndex()];
      }
      else {
         return null;
      }
   }

   /**
    * Returns the text entered in the text field shown in the dialog
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
