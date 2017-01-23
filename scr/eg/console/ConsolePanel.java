package eg.console;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.border.LineBorder;

import javax.swing.event.CaretListener;

import java.awt.*;
import java.awt.event.*;

import java.awt.event.KeyListener;

//--Eadgyth--//
import eg.ui.IconFiles;
import eg.Constants;
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
   private final JButton   runBt      = new JButton(IconFiles.runConsIcon);
   private final JButton   runEadBt   = new JButton(IconFiles.eadgythIcon);
   private final JButton   stopBt     = new JButton(IconFiles.stopProcessIcon);
   private final JButton   clearBt    = new JButton(IconFiles.clearIcon);
   private final JButton   closeBt    = new JButton(IconFiles.closeIcon);

   private final JScrollPane scroll   = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

   public ConsolePanel() {
      scroll.setViewportView(area);
      scroll.setBorder(null);

      area.setBorder(new LineBorder(Color.WHITE, 5));
      area.setFont(Constants.VERDANA_PLAIN_11);
      area.setForeground(areaFontColor);
      area.setEditable(false);

      runBt.setEnabled(false);
      stopBt.setEnabled(false);

      toolbar = createToolbar();
      clearAct();

      consolePnl.setBorder(Constants.LOW_ETCHED);
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
    * @param text  the text that is set in this text area 
    */
   public void setText(String text) {    
      area.setText(text);
   }

   /**
    * Adds the specified text to the text displayed in this text area
    * @param text  the text that is added to the text in this text
    * area
    */
   public void appendText(String text) {
      area.append(text);
   }
   
   /**
    * Adds an action listener to the button designated to closing
    * this console panel
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

   /**
    * Allows writing in this text area
    * @param isActive  true to allow writing in this text area and to
    * inactivate the clear and stop buttons
    */ 
   void setActive(boolean isActive) {
      area.setEditable(isActive);
      area.setFocusable(isActive);
      clearBt.setEnabled(!isActive);
      stopBt.setEnabled(isActive);
   }
   
   /**
    * Enables the run button
    */
   void enableRunBt(boolean isEnabled) {
      runBt.setEnabled(isEnabled);
   }
   
   /**
    * Adds a key listener to this text area
    */
   void addKeyListen(KeyListener keyListener) {
      area.addKeyListener(keyListener);
   }
   
   /**
    * Adds a caret listener to this text area
    */
   void addCaretListen(CaretListener caretListener) {
      area.addCaretListener(caretListener);
   }

   /**
    * Adds an action listener to the button designated to enter a
    * a command
    */
   void setCmdAct(ActionListener al) {
      setCmdBt.addActionListener(al);
   }

   /**
    * Adds an action listener to the button designated to run a
    * a command
    */
   void runAct(ActionListener al) {
      runBt.addActionListener(al);
   }
   
   /**
    * Adds an action listener to the button designated to run Eadgyth
    */
   void runEadAct(ActionListener al) {
      runEadBt.addActionListener(al);
   }

   /**
    * Adds an action listener to the button designated to stop a
    * a command
    */
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
      return UiComponents.toolbarLastBtRight(bts, tooltips);
   }

   private void clearAct() {
      clearBt.addActionListener(e -> area.setText(""));
   }
}
