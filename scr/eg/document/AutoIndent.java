package eg.document;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

import javax.swing.JTextPane;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.FileUtils;
import eg.ui.EditArea;

/**
 * The auto-indentation
 */
class AutoIndent {

   private final EditArea editArea;

   private String indentUnit;
   private int indentLength;  
   private String indent = "";

   AutoIndent(EditArea editArea) {
      this.editArea = editArea;
      editArea.textArea().addKeyListener(listener);
   }

   void resetIndent() {
      indent = "";
   }

   void changeIndentUnit(String indentUnit) {
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();
   }

   String getIndentUnit() {
      return indentUnit;
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
               editArea.removeStr(pos - indentLength, indentLength);
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
         int pos = editArea.textArea().getCaretPosition();
         int key = e.getKeyCode();
         if (isEnter && key == KeyEvent.VK_ENTER) {
            editArea.insertStr(pos, indent);            
         }
         isEnter = false;
      }
   };
}
