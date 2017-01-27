package eg.document;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import javax.swing.JComponent;
import javax.swing.JTextPane;

import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.FileUtils;

class AutoIndent {

   private final static Preferences PREFS = new Preferences();
   private final JTextPane textArea;
   private final StyledDocument doc;
   private final SimpleAttributeSet normalSet;

   private boolean isEnterPressed = false;
   private String indentUnit;
   private int indentLength;  
   private String indent = "";

   AutoIndent(JTextPane textArea, StyledDocument doc,
         SimpleAttributeSet normalSet) {

      this.textArea = textArea;
      this.doc = doc;
      this.normalSet = normalSet;

      PREFS.readPrefs();
      indentUnit = PREFS.getProperty("indentUnit");
      indentLength = indentUnit.length();
      textArea.addKeyListener(listener);
   }

   String getIndentUnit() {
      return indentUnit;
   }

   void resetIndent() {
      indent = "";
   }

   void changeIndentUnit(String indentUnit) {
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();    
      PREFS.storePrefs("indentUnit", indentUnit);
   }  

   void openBracketIndent(String in, int pos) {
      String currIndent = currentIndent(in, pos);
      String atPrevPos = in.substring(pos - 1, pos);
      if (atPrevPos.equals("{")) {
         currIndent += indentUnit;
      }
      this.indent = currIndent;
   }

   void closeBracketIndent(String in, int pos) {
      int lastReturn = 0;
      if (pos > 0) {
         String atPos = in.substring(pos, pos + 1);
         if ("}".equals(atPos)) {
            if (in.substring(pos - indentLength, pos).equals(indentUnit)) {   
               removeIndent(pos - indentLength, indentLength);
            }   
         }
      }
   }

   private String currentIndent(String in, int pos) {
      String currIndent = "";
      //
      // -1 to skip the new return after pressing enter
      int lastReturn = in.lastIndexOf("\n", pos - 1);
      if (lastReturn != -1) {
         char[] line = in.substring(lastReturn + 1, pos).toCharArray();
         for (int i = 0; i < line.length && line[i] == ' '; i++) {
            currIndent += " ";
         }
      }
      return currIndent;
   }

   private void removeIndent(int pos, int length) {
      try {
         doc.remove(pos, length);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
   
   KeyListener listener = new KeyAdapter() {
      //
      // Detecting enter pressed makes sure that that
      // pressing enter in another window does inactivate
      // the insertString method in keyReleased
      boolean isEnter = false;

      @Override
      public void keyPressed(KeyEvent e) {
         int key = e.getKeyCode();
         if (key == KeyEvent.VK_ENTER) {
            isEnter = true;
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
         int pos = textArea.getCaretPosition();
         int key = e.getKeyCode();
         try {       
            if (isEnter && key == KeyEvent.VK_ENTER) {
               doc.insertString(pos, indent, normalSet);            
            }
         }
         catch (BadLocationException ble) {
            ble.printStackTrace();
         }
         isEnter = false;
      }
   };
}
