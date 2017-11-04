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

   private String indentUnit = "";
   private int indentLength = 0;

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
               "The indent unit must consist of spaces");
      }
      this.indentUnit = indentUnit;
      indentLength = indentUnit.length();
   }

   /**
    * Returns the currently set indent unit
    *
    * @return  the indent unit
    */
   public String getIndentUnit() {
      return indentUnit;
   }

   /**
    * Does indentation if the character at the specified position
    * is a newline. The indentation corresponds to the indentation
    * at the previous line or is increased by this indent unit if
    * an opening brace precedes the newline character
    *
    * @param text  the text
    * @param pos  the position
    */
   public void indent(String text, int pos) {
      if ('\n' != text.charAt(pos)) {
         return;
      }
      String currIndent = currentIndent(text, pos);
      if (pos > 1 && '{' == text.charAt(pos - 1)) {
         currIndent += indentUnit;
      }
      textDoc.insert(pos + 1, currIndent);
   }

   /**
    * Reduces the indentation by this indent unit if the character at
    * the specified position is a closing brace
    *
    * @param text  the text
    * @param pos  the position
    */
   public void closedBracketIndent(String text, int pos) {
      if (pos >= indentLength) {
         if ('}' == text.charAt(pos)) {
            if (text.substring(pos - indentLength, pos).equals(indentUnit)) {
               textDoc.remove(pos - indentLength, indentLength);
            }
         }
      }
   }

   //
   //--private--/
   //

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
