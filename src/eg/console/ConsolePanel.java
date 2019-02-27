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

   private final JPanel content = UIComponents.grayBorderedPanel();;
   private final JTextArea area = new JTextArea();
   private final JButton enterCmdBt = new JButton("Cmd...");
   private final JButton runBt = new JButton(IconFiles.RUN_CMD_ICON);
   private final JButton stopBt = new JButton(IconFiles.STOP_PROCESS_ICON);
   private final JButton closeBt = UIComponents.undecoratedButton();

   private boolean unlocked = false;

   public ConsolePanel() {
      init();
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
    * Sets the unlocked state to access the text area. If the unlocked
    * state is already set an error dialog is shown.
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
    * Sets the unlocked and active state to access the text area.
    * Active in addition to unlocked means that the text area is
    * editable and focusable and also that the stop button is enabled.
    * If the unlocked state is already set an error dialog is shown.
    *
    * @return  true if the unlocked state is not set already, false
    * otherwise
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
      unlocked = false;
      setActive(false);
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
    * Sets the cursor position although the inactive state is set
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
    * Sets the specified text
    *
    * @param text  the text
    */
   public void setText(String text) {
      checkWritePermission();
      area.setText(text);
   }

   /**
    * Sets the focus in this text area
    */
   public void focus() {
      checkWritePermission();
      area.requestFocusInWindow();
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
    * Sets the action for closing the console panel to this
    * closing button
    *
    * @param act  the action
    */
   public void setClosingAct(FunctionalAction act) {
      closeBt.setAction(act);
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
         Dialogs.errorMessage("A current task is not finished.", null);
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

   private void init() {
      content.setLayout(new BorderLayout());
      JToolBar toolbar = createToolbar();
      content.add(toolbar, BorderLayout.NORTH);
      JScrollPane scroll = UIComponents.scrollPane();
      scroll.setViewportView(area);
      content.add(scroll, BorderLayout.CENTER);

      area.setFont(Fonts.SANSSERIF_PLAIN_8);
      area.setEditable(false);
      area.setFocusable(false);
      BackgroundTheme theme = BackgroundTheme.givenTheme();
      area.setBackground(theme.background());
      area.setForeground(theme.normalForeground());
      area.setBorder(new LineBorder(theme.background(), 5));
      area.setCaretColor(theme.normalForeground());

      runBt.setEnabled(false);
      enterCmdBt.setEnabled(false);
      stopBt.setEnabled(false);
   }

   private JToolBar createToolbar() {
      JButton[] bts = new JButton[] {
         enterCmdBt, runBt, stopBt
      };
      String[] tooltips = new String[] {
         "Enter and run a system command",
         "Run a previous system command",
         "Quit the current process"
      };
      return UIComponents.toolBar(bts, tooltips, closeBt);
   }
}
