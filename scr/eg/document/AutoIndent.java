package eg.document;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import java.awt.event.ActionEvent;

//--Eadgyth--//
import eg.Preferences;
import eg.utils.FileUtils;

/**
 * The auto indentation.
 * <p>
 * The indention of a previous line is added to a new line, is increased
 * upon typing an opening curly brackets and reduced upon typing a closing
 * curly bracked.
 */
class AutoIndent {

   private final static Preferences PREFS = new Preferences();
   private final JTextPane textArea;
   private final StyledDocument doc;
   private final SimpleAttributeSet normalSet;

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
      IndentAction indAct = new IndentAction();
      KeyStroke ksEnter = KeyStroke.getKeyStroke("released ENTER");
      textArea.getInputMap(JComponent.WHEN_FOCUSED).put(ksEnter, indAct);
   }

   String getIndentUnit() {
      return indentUnit;
   }

   void resetIndent() {
      indent = "";
   }

   /**
    * Assigns to this the indentation unit and the indentation
    * length. Saves the indentation unit to preferences.
    */
   void changeIndentUnit(String indentUnit) {
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();    
      PREFS.storePrefs("indentUnit", indentUnit);
   }  

   /**
    * Assigns to this indent the indentation unit at the current line
    * and adds another unit if the symbol before the current position 
    * is and open bracket.
    */
   void openBracketIndent(String in, int pos) {
      String currIndent = currentIndent(in, pos);
      String atPrevPos = in.substring(pos - 1, pos);
      if (atPrevPos.equals("{")) {
         currIndent += indentUnit;
      }
      this.indent = currIndent;
   }

   /**
    * Reduces indentation by one indentation unit if a close bracket
    * was typed and at least one indent unit is detected before the
    * bracket 
    */
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

   /*
    * Returns the indentation at the current line
    */
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
   
   private class IndentAction extends AbstractAction {
      
      @Override
      public void actionPerformed(ActionEvent e) {
         int pos = textArea.getCaretPosition();
         try {
            doc.insertString(pos, indent, normalSet);            
         }
         catch (BadLocationException ble) {
            FileUtils.logStack(ble);
         }
      }
   }
}
