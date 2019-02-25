package eg.console;

import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.border.LineBorder;

import javax.swing.event.CaretListener;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.FunctionalAction;
import eg.utils.Dialogs;
import eg.ui.IconFiles;
import eg.ui.UIComponents;
import eg.ui.Fonts;

/**
 * Defines the panel which contains the text area that functions as the
 * console and a toolbar for adding actions to run commands.
 */
public class ConsolePanel {

   private final JPanel content;
   private final JTextArea area = new JTextArea();
   private final JToolBar toolbar;
   private final JButton setCmdBt = new JButton("Cmd...");
   private final JButton runBt = new JButton(IconFiles.RUN_CMD_ICON);
   private final JButton stopBt = new JButton(IconFiles.STOP_PROCESS_ICON);
   private final JButton closeBt = UIComponents.undecoratedButton();
   private final JScrollPane scroll = UIComponents.scrollPane();

   private boolean unlocked = false;

   public ConsolePanel() {
      content = UIComponents.grayBorderedPanel();
      content.setLayout(new BorderLayout());
      area.setFont(Fonts.SANSSERIF_PLAIN_8);
      area.setEditable(false);
      area.setFocusable(false);
      BackgroundTheme theme = BackgroundTheme.givenTheme();
      area.setBackground(theme.background());
      area.setForeground(theme.normalForeground());
      area.setBorder(new LineBorder(theme.background(), 5));
      area.setCaretColor(theme.normalForeground());
      scroll.setViewportView(area);
      toolbar = createToolbar();
      content.add(toolbar, BorderLayout.NORTH);
      content.add(scroll, BorderLayout.CENTER);
      setCmdBt.setEnabled(false);
      runBt.setEnabled(false);
      stopBt.setEnabled(false);
   }

   /**
    * Gets this JPanel which contains the text area and the toolbar
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Returns if the console is or is not currently used by a task.
    * If it is in use (the unlocked state is set) a dialog is shown.
    *
    * @return  true if writing is permitted, false otherwise
    */
   public boolean canWrite() {
      boolean b = !unlocked;
      if (!b) {
         Dialogs.errorMessage("A current task is not finished.", null);
      }
      return b;
   }

   /**
    * Sets the active and unlocked or the inactive state and locked
    * state. Active means that this text area is editable
    * and focusable and also that the stop button is enabled.
    *
    * @param b  true for the active (and unlocked), false for the
    * inactive (and locked) state
    */
   public void setUnlockedAndActive(boolean b) {
      if (b) {
         checkUnlockPermission();
      }
      setActive(b);
      unlocked = b;
   }

   /**
    * Sets the unlocked or locked state
    *
    * @param b  true for the unlocked, false for the locked state
    */
   public void setUnlocked(boolean b) {
      if (b) {
         checkUnlockPermission();
      }
      unlocked = b;
   }
   
   /**
    * Keeps the active state depending on the specified boolean value.
    * False does not set the lock state. 
    *
    * @param b  true to keep in active state, false otherwise
    */
   public void keepActive(boolean b) {
      if (b) {
         checkWritePermission();
      }
      setActive(b);
   }

   /**
    * Sets the cursor position in this text area
    *
    * @param pos  the position
    */
   public void setCaret(int pos) {
      checkWritePermission();
      area.setCaretPosition(pos);
   }

   /**
    * Sets the cursor position in this text area although it is
    * currently uneditable (in inactive state)
    *
    * @param pos  the position
    */
   public void setCaretWhenUneditable(int pos) {
      checkWritePermission();
      area.setEditable(true);
      area.setCaretPosition(pos);
      area.setEditable(false);
   }

   /**
    * Sets the specified text in this text area
    *
    * @param text  the text
    */
   public void setText(String text) {
      checkWritePermission();
      area.setText(text);
   }

   /**
    * Adds the specified text to the text in this text area
    *
    * @param text  the text
    */
   public void appendText(String text) {
      checkWritePermission();
      area.append(text);
   }

   /**
    * Adds the specified text after it is formatted such that it starts
    * with double angle brackets and ends with the line separator
    *
    * @param text  the text
    */
   public void appendTextBr(String text) {
      checkWritePermission();
      area.append(">> " + text + "\n");
   }

   /**
    * Gets the text in this text area
    *
    * @return  the text
    */
   public String getText() {
      checkWritePermission();
      return area.getText();
   }

   /**
    * Asks this text area to gain focus
    */
   public void focus() {
      checkWritePermission();
      area.requestFocusInWindow();
   }

   /**
    * Enables or disables actions to run a process
    *
    * @param b  true to enable, fasle to disable
    */
   public void enableRunBt(boolean b) {
      runBt.setEnabled(b);
   }

   /**
    * Enables actions to enter a start command
    */
   public void enableSetCmdBt() {
      setCmdBt.setEnabled(true);
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
    * Sets the action for closing the <code>EditToolPanel</code>
    * to this closing button
    *
    * @param act  the action
    */
   public void setClosingAct(FunctionalAction act) {
      closeBt.setAction(act);
   }

   /**
    * Sets the listener for actions to set a command
    *
    * @param al  the {@code ActionListener}
    */
   public void setCmdAct(ActionListener al) {
      setCmdBt.addActionListener(al);
   }

   /**
    * Sets the listener for actions to run a command
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

   private void checkWritePermission() {
      if (!unlocked) {
         throw new IllegalStateException("The console is not unlocked");
      }
   }

   private void checkUnlockPermission() {
      if (unlocked) {
         throw new IllegalStateException("The console is already unlocked");
      }
   }
   
   private void setActive(boolean b) {
      area.setEditable(b);
      area.setFocusable(b);
      stopBt.setEnabled(b);
   }

   private JToolBar createToolbar() {
      JButton[] bts = new JButton[] {
         setCmdBt, runBt, stopBt
      };
      String[] tooltips = new String[] {
         "Enter and run a system command",
         "Run a previous system command",
         "Quit the current process"
      };
      return UIComponents.toolBar(bts, tooltips, closeBt);
   }
}
