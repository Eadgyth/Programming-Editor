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
 * Class consists in a text area to write to and to read from and a toolbar.
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
      area.setBorder(new LineBorder(Color.WHITE, 5));
      area.setFont(Constants.VERDANA_PLAIN_8);
      area.setForeground(areaFontColor);
      area.setEditable(false);

      runBt.setEnabled(false);
      stopBt.setEnabled(false);

      toolbar = createToolbar();
      clearAct();

      consolePnl.setBorder(Constants.DARK_BORDER);
      consolePnl.add(toolbar, BorderLayout.NORTH);
      consolePnl.add(scroll, BorderLayout.CENTER);
   }

   /**
    * @return  this JPanel that includes the scrolled text area
    * and the toolbar
    */
   public JPanel consolePnl() {
      return consolePnl;
   }
   
   /**
    * Places the cursor at the specified position
     * @param pos  the position where the caret is set
    */
   public void setCaret(int pos) {
      area.setCaretPosition(pos);
   }

   /**
    * Sets the specified text in this text area
    *
    * @param text  the text that is set in this text area 
    */
   public void setText(String text) {    
      area.setText(text);
   }

   /**
    * Adds the specified text to the text displayed in this text area
    *
    * @param text  the text that is added to the text in this text
    * area
    */
   public void appendText(String text) {
      area.append(text);
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

   /**
    * Returns the text in this text area
    * @return  the text displayed in this text area
    */
   String getText() {
      return area.getText();
   }

   /**
    * Sets this text area active
    */
   void focus() {
      area.requestFocusInWindow();
   }

   /*
    * Allows writing in this text area
    */ 
   void setActive(boolean isActive) {
      area.setEditable(isActive);
      area.setFocusable(isActive);
      clearBt.setEnabled(!isActive);
      stopBt.setEnabled(isActive);
   }

   void enableRunBt(boolean isEnabled) {
      runBt.setEnabled(isEnabled);
   }

   void addKeyListen(KeyListener keyListener) {
      area.addKeyListener(keyListener);
   }

   void addCaretListen(CaretListener caretListener) {
      area.addCaretListener(caretListener);
   }

   void setCmdAct(ActionListener al) {
      setCmdBt.addActionListener(al);
   }

   void runAct(ActionListener al) {
      runBt.addActionListener(al);
   }

   void runEadAct(ActionListener al) {
      runEadBt.addActionListener(al);
   }

   void stopAct(ActionListener al) {
      stopBt.addActionListener(al);
   }

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

   private void clearAct() {
      clearBt.addActionListener(e -> area.setText(""));
   }
}
