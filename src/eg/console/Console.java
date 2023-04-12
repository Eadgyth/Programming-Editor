package eg.console;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

//--Eadgyth--/
import eg.ui.ConsolePanel;
import eg.ui.IconFiles;
import eg.utils.Dialogs;

/**
 * Represents the console which consists of a text area and buttons
 * for running and terminating commands.
 * <p>
 * The console can be locked, unlocked or unlocked active. Active
 * means that the text area is additionally editable and the 'stop'
 * button enabled.
 */
public class Console {

   private final JTextArea area = new JTextArea();
   private final JButton enterCmdBt = new JButton(IconFiles.ENTER_CMD_ICON);
   private final JButton runBt = new JButton(IconFiles.RUN_CMD_ICON);
   private final JButton stopBt = new JButton(IconFiles.STOP_PROCESS_ICON);
   private final JButton[] bts = new JButton[] {
      enterCmdBt, runBt, stopBt
   };
   private final String[] tooltips = new String[] {
      "Enter and run a system command",
      "Run a previous system command",
      "Quit the current process"
   };

   private boolean unlocked = false;
   private boolean active = false;

   /**
    * @param consPnl  the ConsolePanel in the main window which this
    * text area and buttons are added to
    */
   public Console(ConsolePanel consPnl) {
      consPnl.initContent(area, bts, tooltips);
      area.setEditable(false);
      area.setLineWrap(true);
      runBt.setEnabled(false);
      enterCmdBt.setEnabled(false);
      stopBt.setEnabled(false);
   }

   /**
    * Sets the unlocked state. If the console is already unlocked a
    * warning dialog is shown.
    *
    * @return  true if the unlocked state can be set; false if
    * already set
    */
   public boolean setUnlocked() {
      if (!isLocked()) {
         return false;
      }
      unlocked = true;
      return true;
   }

   /**
    * Sets the unlocked active state. If the console is already
    * unlocked a warning dialog is shown.
    *
    * @return  true if the unlocked state can be set; false if
    * already set
    */
   public boolean setUnlockedActive() {
      if (!isLocked()) {
         return false;
      }
      unlocked = true;
      active = true;
      stopBt.setEnabled(true);
      area.setEditable(true);
      area.getCaret().setVisible(true);
      area.requestFocusInWindow();
      return true;
   }

   /**
    * Sets the inactive but not locked state
    * @throws  IllegalStateException  if the console is not set active
    */
   public void setInactive() {
      if (!active) {
         throw new IllegalStateException("The console is not set active");
      }
      active = false;
      stopBt.setEnabled(false);
      area.setEditable(false);
      area.getCaret().setVisible(false);
   }

   /**
    * Returns if the console is unlocked active
    *
    * @return  true if unlocked active; false otherwise
    */
   public boolean isUnlockedActive() {
      return active && unlocked;
   }

   /**
    * Sets the locked state
    * @throws  IllegalStateException  if the console is not unlocked
    */
   public void setLocked() {
      checkWritePermission();
      unlocked = false;
   }

   /**
    * Sets the cursor position
    *
    * @param pos  the position
    * @throws  IllegalStateException  if the console is not unlocked
    */
   public void setCaret(int pos) {
      checkWritePermission();
      area.setCaretPosition(pos);
   }

   /**
    * Returns the caret or selection start position
    *
    * @return  the position
    * @throws  IllegalStateException  if the console is not unlocked
    */
   public int caretPosition() {
      checkWritePermission();
      return area.getSelectionStart();
   }

   /**
    * Sets the specified text
    *
    * @param text  the text
    * @throws  IllegalStateException  if the console is not unlocked
    */
   public void setText(String text) {
      checkWritePermission();
      area.setText(text);
   }

   /**
    * Appends the specified text
    *
    * @param text  the text
    * @throws  IllegalStateException  if the console is not unlocked
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
    * @throws  IllegalStateException  if the console is not unlocked
    */
   public void appendTextBr(String text) {
      checkWritePermission();
      area.append(">> " + text + "\n");
   }

   /**
    * Returns the current text
    *
    * @return  the text
    * @throws  IllegalStateException  if the console is not unlocked
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
    * @param b  true to enable, false to disable
    */
   public void enableRunBt(boolean b) {
      runBt.setEnabled(b);
   }

   /**
    * Adds a <code>KeyListener</code> to this text area
    *
    * @param keyListener  the KeyListener
    */
   public void addKeyListener(KeyListener keyListener) {
      area.addKeyListener(keyListener);
   }

   /**
    * Sets the listener for actions to enter and run a new command
    *
    * @param al  the ActionListener
    */
   public void setEnterCmdAct(ActionListener al) {
      enterCmdBt.addActionListener(al);
   }

   /**
    * Sets the listener for actions to run a previous command
    *
    * @param al  the ActionListener
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
}
