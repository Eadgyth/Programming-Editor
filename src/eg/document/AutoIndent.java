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
   private boolean isActive;
   private String in = "";

   AutoIndent(EditArea editArea) {
      this.editArea = editArea;
      editArea.textArea().addKeyListener(listener);
   }

   void enableIndent(boolean isEnabled) {
      indent = "";
      isActive = isEnabled;
   }

   void changeIndentUnit(String indentUnit) {
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();
   }

   String getIndentUnit() {
      return indentUnit;
   }
   
   void setText(String text) {
      in = text;
   }

   void closeBracketIndent(int pos) {
      if (pos > 2) {
         String atPos = in.substring(pos, pos + 1);
         if ("}".equals(atPos)) {
            if (in.substring(pos - indentLength, pos).equals(indentUnit)) {   
               editArea.removeStr(pos - indentLength, indentLength);
            }   
         }
      }
   }
   
   //
   //--private
   //

   private void setCurrentIndent(int pos) {
      String currIndent = currentIndent(pos);
      if (pos > 1) {
         String atPrevPos = in.substring(pos - 2, pos - 1);
         if (atPrevPos.equals("{")) {
            currIndent += indentUnit;
         }
      }
      this.indent = currIndent;
   }

   private String currentIndent(int pos) {
      String currIndent = "";
      //
      // -2 to skip the new return after pressing enter
      int lineStart = -1;;
      if (pos > 1) {
         lineStart = in.lastIndexOf("\n", pos - 2);
      }
      if (pos > 1) {
         char[] line = in.substring(lineStart + 1, pos).toCharArray();
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
         if (isActive && isEnter && key == KeyEvent.VK_ENTER) {
            setCurrentIndent(pos);
            editArea.insertStr(pos, indent);            
         }
         isEnter = false;
      }
   };
}
