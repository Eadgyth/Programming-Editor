package eg.utils;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;

import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

//--Eadgyth--//
import eg.ui.IconFiles;

/**
 * Static methods to show dialogs using <code>JOptionPane</code>
 */
public class Dialogs {

   private static final Border EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);

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
      return JOptionPane.showConfirmDialog(null, message, null,
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
   }

   /**
    * Shows a confirmation dialog with Yes and No options
    *
    * @param message  the message for the dialog
    * @return  the Yes or No option specified in <code>JOptionPane</code>
    */
   public static int confirmYesNo(String message) {
      return JOptionPane.showConfirmDialog(null, message, null,
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
   }

   /**
    * Shows an info kind of confirmation dialog with Yes and No options
    *
    * @param message  the message for the dialog
    * @return  the Yes or No option specified in <code>JOptionPane</code>
    */
   public static int infoConfirmYesNo(String message) {
      return JOptionPane.showConfirmDialog(null, message, null,
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
            IconFiles.INFO_ICON);
   }

    /**
    * Shows a warning confirmation dialog with Yes and No options
    *
    * @param message  the message for the dialog
    * @return  the Yes or No option specified in <code>JOptionPane</code>
    */
   public static int warnConfirmYesNo(String message) {
      return JOptionPane.showConfirmDialog(null, message, null,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                IconFiles.WARNING_ICON);
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

      JTextField tf = new JTextField();
      tf.setFont(ScreenParams.scaledFontToPlain(tf.getFont(), 8));
      tf.setText(initText);
      tf.addAncestorListener(new AncestorListener() {

         @Override
         public void ancestorRemoved(AncestorEvent e) {
            // not used
         }

         @Override
         public void ancestorMoved(AncestorEvent e) {
            // not used
         }

         @Override
         public void ancestorAdded(AncestorEvent e) {
            tf.requestFocusInWindow();
         }
      });
      JPanel pnl = new JPanel(new BorderLayout());
      JLabel lb = new JLabel(message);
      lb.setFont(ScreenParams.scaledFontToPlain(tf.getFont(), 9));
      lb.setBorder(EMPTY_BORDER);
      pnl.add(lb, BorderLayout.NORTH);
      pnl.add(tf, BorderLayout.CENTER);
      int res = JOptionPane.showConfirmDialog(null, pnl, title,
               JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      if (JOptionPane.YES_OPTION == res) {
         return tf.getText();
      }
      else {
         return null;
      }
   }

   private Dialogs() {}
}
