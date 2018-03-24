package eg.document;

/**
 * The auto-indentation which works with spaces.
 * <p>
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
      if (indentUnit == null
            || (indentUnit.length() > 0 && !indentUnit.matches("[\\s]+"))) {

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
    * Maintains or adjusts the indentation
    *
    * @param text  the text
    * @param pos  the position
    */
   public void adjustIndent(String text, int pos) {
      if ('\n' == text.charAt(pos)) {
         indent(text, pos);
      }
      else if (pos >= indentLength && '}' == text.charAt(pos)) {
         outdent(text, pos);
      }
   }

   //
   //--private--/
   //

   private void indent(String text, int pos) {
      String currIndent = currentIndentAt(text, pos);
      if (pos > 1 && '{' == text.charAt(pos - 1)) {
         currIndent += indentUnit;
      }
      textDoc.insert(pos + 1, currIndent);
   }

   private void outdent(String text, int pos) {
      int outdentPos = pos - indentLength;
      boolean ok = isOutdent(text, pos)
            && text.substring(outdentPos, pos).equals(indentUnit);

      if (ok) {
         textDoc.remove(outdentPos, indentLength);
      }
   }

   private boolean isOutdent(String text, int pos) {
      int lastOpeningPos = text.lastIndexOf('{', pos - 1);
      int lastClosingPos = text.lastIndexOf('}', pos - 1);
      String indentAtChange = currentIndentAt(text, pos);
      String indentAtBraceAhead;
      if (lastOpeningPos > lastClosingPos) {
         indentAtBraceAhead = currentIndentAt(text, lastOpeningPos);
         return indentAtChange.length()
               - indentAtBraceAhead.length() >= indentLength;
      }
      else {
         indentAtBraceAhead = currentIndentAt(text, lastClosingPos);
         return indentAtChange.length() >= indentAtBraceAhead.length();
      }
   }

   private String currentIndentAt(String text, int pos) {
      String currIndent = "";
      int lineStart;
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
