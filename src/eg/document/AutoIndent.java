package eg.document;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;

/**
 * The auto-indentation which works with spaces.<br>
 * Created in {@link TypingEdit}
 */
public class AutoIndent {

   private final TextDocument textDoc;

   private String indentUnit;
   private int indentLength;
   private String currentIndent;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    */
   public AutoIndent(TextDocument textDoc) {
      this.textDoc = textDoc;
   }

   /**
    * Sets the indent unit which consists of a certain number of
    * spaces
    *
    * @param indentUnit  the indent unit
    */
   public void setIndentUnit(String indentUnit) {
      if (indentUnit == null || !indentUnit.matches("[\\s]+")) {
         throw new IllegalArgumentException(
               "Argument indentUnit must consist of spaces");
      }
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();
   }

   /**
    * Gets this indent unit
    *
    * @return  this indent unit
    */
   public String getIndentUnit() {
      return indentUnit;
   }
   
   /** 
    * Indents a new line by the indentation of the previous line and increases
    * the indentation by one indent unit when an opening brace was typed before
    * pressing enter
    *
    * @param text  the text in the document
    * @param pos  the position where the text change happened
    */
   public void indent(String text, int pos) {
      if ('\n' != text.charAt(pos)) {
         return;
      }
      String currIndent = currentIndent(text, pos);
      if (pos > 1) {
         if ('{' == text.charAt(pos - 1)) {
            currIndent += indentUnit;
         }
      }
      textDoc.insertStr(pos + 1, currIndent);
      currentIndent = currIndent;
   }

   /**
    * Reduces the indentation by one indent unit if a closing brace is typed
    *
    * @param text  the text in the document
    * @param pos  the position where text change happened
    */
   public void closedBracketIndent(String text, int pos) {
      if (pos >= indentLength) {
         if ('}' == text.charAt(pos)) {
            if (text.substring(pos - indentLength, pos).equals(indentUnit)) {
               textDoc.removeStr(pos - indentLength, indentLength);
            }
         }
      }
   }

   private String currentIndent(String text, int pos) {
      String currIndent = "";
      int lineStart = -1;
      if (pos > 1) {
         lineStart = text.lastIndexOf("\n", pos - 1) + 1;
         char[] line = text.substring(lineStart, pos).toCharArray();
         for (int i = 0; i < line.length && line[i] == ' '; i++) {
            currIndent += " ";
         }
      }
      return currIndent;
   }
}
