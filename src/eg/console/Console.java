package eg.console;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

import javax.swing.event.CaretListener;

//--Eadgyth--/
import eg.ui.ConsolePanel;
import eg.ui.IconFiles;
import eg.utils.Dialogs;

/**
 * Represents the console with a text area and buttons.
 * <p>
 * Class can have an unlocked, an unlocked active or a locked state.
 * Setting the 'unlocked' flag is required to use this methods that
 * access the text area. Otherwise these methods throw an exception.
 * Active means that the text area is additionally editable and
 * focusable and also that this button for stopping a process is
 * enabled.
 */
public class Console {

   private final JTextArea area = new JTextArea();
   private final JButton enterCmdBt = new JButton("Cmd...");
   private final JButton runBt = new JButton(IconFiles.RUN_CMD_ICON);
   private final JButton stopBt = new JButton(IconFiles.STOP_PROCESS_ICON);

   private boolean unlocked = false;

   /**
    * @param consPnl  the reference to ConsolePanel which is contained
    * in the main window and which the text area and the buttons are
    * added to
    */
   public Console(ConsolePanel consPnl) {
      JButton[] bts = new JButton[] {
         enterCmdBt, runBt, stopBt
      };
      String[] tooltips = new String[] {
         "Enter and run a system command",
         "Run a previous system command",
         "Forcibly quit the current process"
      };
      consPnl.initContent(area, bts, tooltips);
      area.setEditable(false);
      area.setFocusable(false);
      runBt.setEnabled(false);
      enterCmdBt.setEnabled(false);
      stopBt.setEnabled(false);
   }

   /**
    * Sets the unlocked state. If the unlocked state is already set a
    * warning dialog is shown.
    *
    * @return  true if the unlocked state is not set already, false
    * otherwise
    */
   public boolean setUnlocked() {
      if (!isLocked()) {
         return false;
      }
      unlocked = true;
      return true;
   }

   /**
    * Sets the unlocked and active state
    *
    * @return  true if the unlocked state is not set already, false
    * otherwise
    * @see #setUnlocked
    */
   public boolean setUnlockedAndActive() {
      if (!setUnlocked()) {
         return false;
      }
      setActive(true);
      return true;
   }

   /**
    * Keeps or ends the active state but does not change the
    * unlocked state.
    *
    * @param b  true to keep, false to end the active state
    */
   public void keepActive(boolean b) {
      if (b) {
         checkWritePermission();
      }
      setActive(b);
   }

   /**
    * Sets the locked (and inactive) state
    */
   public void setLocked() {
      checkWritePermission();
      unlocked = false;
      setActive(false);
   }

   /**
    * Sets the focus in this text area
    */
   public void focus() {
      checkWritePermission();
      area.requestFocusInWindow();
   }

   /**
    * Sets the cursor position
    *
    * @param pos  the position
    */
   public void setCaret(int pos) {
      checkWritePermission();
      area.setCaretPosition(pos);
   }

   /**
    * Sets the specified text
    *
    * @param text  the text
    */
   public void setText(String text) {
      checkWritePermission();
      area.setText(text);
   }

   /**
    * Appends the specified text
    *
    * @param text  the text
    */
   public void appendText(String text) {
      checkWritePermission();
      area.append(text);
   }

   /**
    * Appends the specified text after it is formatted such that it
    * starts with two closing angle brackets and ends with the line
    * separator. This output is intended for predefined status/error
    * messages.
    *
    * @param text  the text
    */
   public void appendTextBr(String text) {
      checkWritePermission();
      area.append(">> " + text + "\n");
   }

   /**
    * Gets the current text
    *
    * @return  the text
    */
   public String getText() {
      checkWritePermission();
      return area.getText();
   }

   /**
    * Enables actions to enter a new command
    */
   public void enableEnterCmdBt() {
      enterCmdBt.setEnabled(true);
   }

   /**
    * Enables or disables actions to run a command
    *
    * @param b  true to enable, fasle to disable
    */
   public void enableRunBt(boolean b) {
      runBt.setEnabled(b);
   }

   /**
    * Adds a <code>KeyListener</code> to this text area
    *
    * @param keyListener  the <code>KeyListener</code>
    */
   public void addKeyListener(KeyListener keyListener) {
      area.addKeyListener(keyListener);
   }

   /**
    * Adds a <code>CaretListener</code> to this text area
    *
    * @param caretListener  the <code>CaretListener</code>
    */
   public void addCaretListener(CaretListener caretListener) {
      area.addCaretListener(caretListener);
   }

   /**
    * Sets the listener for actions to enter and run a new command
    *
    * @param al  the {@code ActionListener}
    */
   public void setEnterCmdAct(ActionListener al) {
      enterCmdBt.addActionListener(al);
   }

   /**
    * Sets the listener for actions to run a previous command
    *
    * @param al  the {@code ActionListener}
    */
   public void setRunAct(ActionListener al) {
      runBt.addActionListener(al);
   }

   /**
    * Sets the listener for actions to stop a process
    *
    * @param al  the {@code ActionListener}
    */
   public void setStopAct(ActionListener al) {
      stopBt.addActionListener(al);
   }

   //
   //--private--/
   //

   private boolean isLocked() {
      if (unlocked) {
         Dialogs.warnMessage("A current task is not finished.");
         return false;
      }
      return true;
   }

   private void checkWritePermission() {
      if (!unlocked) {
         throw new IllegalStateException("The console is not unlocked");
      }
   }

   private void setActive(boolean b) {
      area.setEditable(b);
      area.setFocusable(b);
      stopBt.setEnabled(b);
   }
}
