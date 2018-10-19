package eg.ui;

import java.awt.Color;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.event.CaretListener;

//--Eadgyth--//
import eg.Constants;
import eg.utils.UIComponents;

/**
 * Defines the panel which contains a text area for messages and for
 * reading in text and also a toolbar.
 * <p>
 * Initially, the text area is not editable and not focusable and also
 * buttons in the toolbar are disabled.
 */
public class ConsolePanel {

   private final Color areaFontColor = new Color(60, 60, 60);

   private final JPanel    content  = new JPanel(new BorderLayout());
   private final JTextArea area     = new JTextArea();
   private final JToolBar  toolbar;
   private final JButton   setCmdBt = new JButton("Cmd...");
   private final JButton   runBt    = new JButton(IconFiles.RUN_CMD_ICON);
   private final JButton   stopBt   = new JButton(IconFiles.STOP_PROCESS_ICON);
   private final JButton   closeBt  = new JButton(IconFiles.CLOSE_ICON);
   private final JScrollPane scroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         
   private boolean isActive = false;

   public ConsolePanel() {
      scroll.setViewportView(area);
      scroll.setBorder(Constants.MATTE_TOP);
      area.setBorder(Constants.EMPTY_BORDER_5);
      area.setFont(Constants.SANSSERIF_PLAIN_8);
      area.setForeground(areaFontColor);
      area.setEditable(false);
      area.setFocusable(false);
      toolbar = createToolbar();
      content.setBorder(Constants.GRAY_BORDER);
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
    * Sets the boolean that specifies if the console is in the
    * active or inactive state. This text area is editable and
    * focusable and also this stop button is enabled in the active
    * state.
    *
    * @param isActive  the boolean value; true for active, false
    * for inactive
    */
   public void setActive(boolean isActive) {
      area.setEditable(isActive);
      area.setFocusable(isActive);
      stopBt.setEnabled(isActive);
      this.isActive = isActive;
   }
   
   /**
    * Returns the boolean that indicates if the active state is set.
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
         throw new IllegalStateException(
               "The text area is set uneditable");
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
         throw new IllegalStateException(
               "The text area is set uneditable");
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
    * Sets the listener for actions to close this console panel
    *
    * @param al  the {@code ActionListener}
    */
   public void setCloseAct(ActionListener al) {
      closeBt.addActionListener(al);
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
         setCmdBt, runBt, stopBt, closeBt
      };
      String[] tooltips = new String[] {
         "Enter and run a system command",
         "Run a previous system command",
         "Quit the current process",
         "Close the console"
      };
      return UIComponents.toolBar(bts, tooltips);
   }
}
