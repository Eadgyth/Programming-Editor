package eg.console;

import java.awt.Color;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.event.CaretListener;

import javax.swing.border.LineBorder;

//--Eadgyth--//
import eg.Constants;
import eg.ui.IconFiles;
import eg.utils.UiComponents;

/**
 * The console panel with a text area to write to and to read from and a toolbar.
 * Initially, the text area is not editable.
 */
public class ConsolePanel {

   private final Color areaFontColor = new Color(60, 60, 60);

   private final JPanel    consolePnl = new JPanel(new BorderLayout());
   private final JTextArea area       = new JTextArea();
   private final JToolBar  toolbar;

   private final JButton   setCmdBt   = new JButton("Cmd...");
   private final JButton   runBt      = new JButton(IconFiles.RUN_CMD_ICON);
   private final JButton   runEadBt   = new JButton(IconFiles.EADGYTH_ICON_SET);
   private final JButton   stopBt     = new JButton(IconFiles.STOP_PROCESS_ICON);
   private final JButton   clearBt    = new JButton(IconFiles.CLEAR_ICON);
   private final JButton   closeBt    = new JButton(IconFiles.CLOSE_ICON);
   private final JScrollPane scroll   = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

   public ConsolePanel() {
      scroll.setViewportView(area);
      scroll.setBorder(null);
      area.setBorder(Constants.EMPTY_BORDER);
      area.setFont(Constants.VERDANA_PLAIN_8);
      area.setForeground(areaFontColor);
      area.setEditable(false);
      toolbar = createToolbar();
      consolePnl.setBorder(Constants.GRAY_BORDER);
      consolePnl.add(toolbar, BorderLayout.NORTH);
      consolePnl.add(scroll, BorderLayout.CENTER);
      runBt.setEnabled(false);
      stopBt.setEnabled(false);
      clearBt.addActionListener(e -> area.setText(""));
   }

   /**
    * @return  this JPanel that includes the scrolled text area
    * and the toolbar
    */
   public JPanel consolePnl() {
      return consolePnl;
   }

   /**
    * Sets the cursor position in this text area
    *
    * @param pos  the position
    */
   public void setCaret(int pos) {
      if (!area.isEditable()) {
         throw new IllegalStateException("The text area is set uneditable");
      }
      area.setCaretPosition(pos);
   }

   /**
    * Sets the cursor position in this text area although it is
    * currently uneditable
    *
    * @param pos  the position
    */
   public void setCaretUneditable(int pos) {
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
      area.requestFocusInWindow();
   }

   /**
    * Sets the active state in which this text area is editable,
    * the stop button is enabled and the clear button disabled
    *
    * @param isActive  true for the active, false for the inactive
    * state
    */
   public void setActive(boolean isActive) {
      area.setEditable(isActive);
      area.setFocusable(isActive);
      clearBt.setEnabled(!isActive);
      stopBt.setEnabled(isActive);
   }

   public void enableRunBt(boolean isEnabled) {
      runBt.setEnabled(isEnabled);
   }

   public void addKeyListen(KeyListener keyListener) {
      area.addKeyListener(keyListener);
   }

   public void addCaretListen(CaretListener caretListener) {
      area.addCaretListener(caretListener);
   }
   
   /**
    * Adds an action listener to the button designated to closing
    * this console panel
    *
    * @param al  the {@code ActionListener}
    */
   public void closeAct(ActionListener al) {
      closeBt.addActionListener(al);
   }

   public void setCmdAct(ActionListener al) {
      setCmdBt.addActionListener(al);
   }

   public void setRunAct(ActionListener al) {
      runBt.addActionListener(al);
   }

   public void setRunEadAct(ActionListener al) {
      runEadBt.addActionListener(al);
   }

   public void setStopAct(ActionListener al) {
      stopBt.addActionListener(al);
   }

   //
   //--private--/
   //

   private JToolBar createToolbar() {
      JButton[] bts = new JButton[] {
         setCmdBt, runBt, runEadBt, stopBt, clearBt, closeBt
      };
      String[] tooltips = new String[] {
         "Run a new system command",
         "Run a previous system command",
         "Run a new Eadgyth",
         "Quit current process",
         "Clear the console",
         "Close the console"
      };
      return UiComponents.lastBtRightToolbar(bts, tooltips);
   }
}
