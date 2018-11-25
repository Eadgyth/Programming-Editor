package eg.ui;

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

/**
 * Defines the panel which contains a text area for messages and for
 * reading in text and also a toolbar.
 * <p>
 * Initially, the text area is not editable and not focusable and also
 * buttons in the toolbar are disabled.
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
         
   private boolean isActive = false;

   public ConsolePanel() {
      content = UIComponents.grayBorderedPanel();
      content.setLayout(new BorderLayout());
      area.setFont(Fonts.SANSSERIF_PLAIN_8);
      area.setEditable(false);
      area.setFocusable(false);
      BackgroundTheme bt = BackgroundTheme.givenTheme();
      area.setBackground(bt.background());
      area.setForeground(bt.normalForeground());
      area.setBorder(new LineBorder(bt.background(), 5));
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
    * Sets the active or the inactive state. Active means that text area
    * is editable and focusable and also that this stop button is enabled.
    *
    * @param isActive  true for the active, false for the inactive sate
    */
   public void setActive(boolean isActive) {
      area.setEditable(isActive);
      area.setFocusable(isActive);
      stopBt.setEnabled(isActive);
      this.isActive = isActive;
   }
   
   /**
    * Returns if the active state is set.
    * <p>
    * The active state should indicate that this text area is used by
    * a started process.
    *
    * @see #setActive(boolean)
    * @return  the boolean value, true if the active state is set
    */
   public boolean isActive() {
      return isActive;
   }

   /**
    * Sets the cursor position in this text area
    *
    * @param pos  the position
    */
   public void setCaret(int pos) {
      if (!isActive) {
         throw new IllegalStateException("The text area is set uneditable");
      }
      area.setCaretPosition(pos);
   }

   /**
    * Sets the cursor position in this text area although it is
    * currently uneditable (inactive state)
    *
    * @param pos  the position
    */
   public void setCaretWhenUneditable(int pos) {
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
      area.setText(text);
   }

   /**
    * Adds the specified text to the text in this text area
    *
    * @param text  the text
    */
   public void appendText(String text) {
      area.append(text);
   }
   
   /**
    * Adds the specified text after it is formatted such that it is
    * bordered by double angle brackets and the line separator is
    * appended.
    *
    * @param text  the text
    */
   public void appendTextBr(String text) {
      area.append("<<" + text + ">>\n");
   }

   /**
    * Gets the text in this text area
    *
    * @return  the text
    */
   public String getText() {
      return area.getText();
   }

   /**
    * Asks this text area to gain focus
    */
   public void focus() {
      if (!isActive) {
         throw new IllegalStateException("The text area is set uneditable");
      }
      area.requestFocusInWindow();
   }

   /**
    * Sets the boolean that specifies if run actions are enabled (true)
    * or disabled
    *
    * @param b  the boolean  value
    */
   public void enableRunBt(boolean b) {
      runBt.setEnabled(b);
   }
   
   /**
    * Enables actions to set a start command
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
