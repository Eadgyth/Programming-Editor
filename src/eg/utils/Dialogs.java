package eg.utils;

import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;

//--Eadgyth--//
import eg.ui.IconFiles;

/**
 * Static methods to show dialogs using <code>JOptionPane</code>
 */
public class Dialogs {

   /**
    * Shows an information message
    *
    * @param message  the information message
    * @param title  the title for the dialog. Can be Null.
    */
   public static void infoMessage(String message, String title) {
      JOptionPane.showMessageDialog (null, message, title,
            JOptionPane.PLAIN_MESSAGE, IconFiles.INFO_ICON);
   }

   /**
    * Shows a warning message
    *
    * @param message  the warning message
    */
   public static void warnMessage(String message) {
      JOptionPane.showMessageDialog(null, message, null,
            JOptionPane.PLAIN_MESSAGE, IconFiles.WARNING_ICON);
   }

   /**
    * Shows a warning message that is shown on top of all windows
    *
    * @param message  the warning message
    */
   public static void warnMessageOnTop(String message) {
      final JDialog dialog = new JDialog();
      dialog.setAlwaysOnTop(true);
      JOptionPane.showMessageDialog(dialog, message, "",
            JOptionPane.PLAIN_MESSAGE, IconFiles.WARNING_ICON);
   }

   /**
    * Shows an error message
    *
    * @param message  the error message
    * @param title  the title for the dialog
    */
   public static void errorMessage(String message, String title) {
      JOptionPane.showMessageDialog(null, message, title,
            JOptionPane.PLAIN_MESSAGE, IconFiles.ERROR_ICON);
   }

   /**
    * Shows a confirmation dialog with Yes, No and Cancel options
    *
    * @param message  the message for the dialog
    * @return  the Yes, No or Cancel option specified in
    * <code>JOptionPane</code>
    */
   public static int confirmYesNoCancel(String message) {
      int result = JOptionPane.showConfirmDialog(null, message, null,
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      return result;
   }

   /**
    * Shows a confirmation dialog with Yes and No options
    *
    * @param message  the message for the dialog
    * @return  the Yes or No option specified in <code>JOptionPane</code>
    */
   public static int confirmYesNo(String message) {
      int result = JOptionPane.showConfirmDialog(null, message, null,
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

      return result;
   }

    /**
    * Shows a warning confirmation dialog with Yes and No options
    *
    * @param message  the message for the dialog
    * @return  the Yes or No option specified in <code>JOptionPane</code>
    */
   public static int warnConfirmYesNo(String message) {
      int res = JOptionPane.showConfirmDialog(null, message, null,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                IconFiles.WARNING_ICON);

      return res;
   }

   /**
    * Shows a dialog with options selectable in a JComboBox
    *
    * @param message  the message for the dialog
    * @param title  the title for the dialog
    * @param options  the array of options that are selectable
    * @param preselected  the option that is preselected. Can be Null
    * @param isWarning  true to show a warning icon, false to show no
    * icon
    * @return  the element selected from <code>options</code> if ok is
    * clicked, null otherwise
    */
   public static String comboBoxOpt(String message, String title,
         String[] options, String preselected, boolean isWarning) {

      JComboBox cBox = new JComboBox(options);
      if (preselected != null) {
         cBox.setSelectedItem(preselected);
      }
      cBox.setFont(eg.Constants.SANSSERIF_PLAIN_9);
      JPanel pnl = new JPanel(new GridLayout(2, 1));
      JLabel lb = new JLabel(message);
      lb.setFont(new Font("Arial", Font.PLAIN, ScreenParams.scaledSize(9)));
      lb.setBorder(eg.Constants.EMPTY_BORDER_5);
      pnl.add(lb);
      JPanel holdCBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdCBox.add(cBox);
      pnl.add(holdCBox);
      int messageType;
      int res;
      if (isWarning) {
         res = JOptionPane.showConfirmDialog(null, pnl, title,
               JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
               IconFiles.WARNING_ICON);
      }
      else {
         res = JOptionPane.showConfirmDialog(null, pnl, title,
               JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      }
      if (JOptionPane.YES_OPTION == res) {
         return options[cBox.getSelectedIndex()];
      }
      else {
         return null;
      }
   }

   /**
    * Shows a dialog with the option to enter text in a text field
    *
    * @param message  the message for the dialog
    * @param title  the title for the dialog
    * @param initText  the text that is initially shown in the text field
    * @return  the string entered in the text field if ok is clicked,
    * null otherwise
    */
   public static String textFieldInput(String message, String title,
         String initText) {

      Object resObj = JOptionPane.showInputDialog(null, message, title,
            JOptionPane.PLAIN_MESSAGE, null, null, initText);

      if (resObj != null) {
         return resObj.toString();
      }
      else {
         return null;
      }
   }
}
