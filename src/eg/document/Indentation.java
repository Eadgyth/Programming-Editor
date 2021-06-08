package eg.document;

//--Eadgyth--/
import eg.document.styledtext.EditableText;

/**
 * The auto-indentation
 */
public class Indentation {

   private final EditableText txt;
   private final StringBuilder joinTabs = new StringBuilder();
   private final StringBuilder joinSpaces = new StringBuilder();
   private final StringBuilder indent = new StringBuilder();

   private String indentUnit = "";
   private int indentLength = 0;
   private boolean useTabs;
   private boolean curlyBracketMode;

   /**
    * @param txt  the EditableText
    */
   public Indentation(EditableText txt) {
      this.txt = txt;
   }

   /**
    * Sets the indentation mode
    *
    * @param indentUnit  the indent unit which consists of spaces
    * @param useTabs  true to use tabs; false to use spaces for
    * indentation
    */
   public void setMode(String indentUnit, boolean useTabs) {
      if (indentUnit == null || indentUnit.isEmpty()
            || !indentUnit.trim().isEmpty()) {

         throw new IllegalArgumentException(
               "The indent unit does not consist of white spaces");
      }
      this.indentUnit = indentUnit;
      this.useTabs = useTabs;
      indentLength = indentUnit.length();
   }

   /**
    * Enables or disables curly bracket mode
    *
    * @param b  true to enable, false to disable
    */
   public void enableCurlyBracketMode(boolean b) {
      curlyBracketMode = b;
   }

   /**
    * Returns the indent unit
    *
    * @return  the indent unit
    */
   public String indentUnit() {
      return indentUnit;
   }

   /**
    * Returns if tabs are used for indentation
    *
    * @return  true if tabs, false if spaces are used
    */
   public boolean useTabs() {
      return useTabs;
   }

   /**
    * Maintains or adjusts (with curly bracket mode) the indentation
    *
    * @param pos  the position
    */
   public void adjustIndent(int pos) {
      char charAtPos = txt.text().charAt(pos);
      if ('\n' == charAtPos) {
         indent(pos);
      }
      else if (curlyBracketMode && '}' == charAtPos) {
         outdent(pos);
      }
   }

   //
   //--private--/
   //

   private void indent(int pos) {
      indent.setLength(0);
      int length = indentLengthAt(pos);
      int remainder = 0;
      if (useTabs) {
         remainder = length % indentLength;
         length = length / indentLength;
         indent.append(joinTabs(length));
      }
      else {
         indent.append(joinSpaces(length));
      }
      if (curlyBracketMode && pos >= 1 && '{' == txt.text().charAt(pos - 1)) {
         if (useTabs) {
            indent.append('\t');
         }
         else {
            indent.append(indentUnit);
         }
      }
      if (remainder > 0) {
         indent.append(joinSpaces(remainder));
      }
      txt.insert(pos + 1, indent.toString());
   }

   private void outdent(int pos) {
      int lineStart = lineStart(pos - 1);
      if (pos == lineStart) {
         return;
      }
      String line = line(lineStart, pos);
      for (int i = 0; i < line.length(); i++) {
         char c = line.charAt(i);
         if (c != '\t' && c != ' ') {
            return;
         }
      }
      int length = outdentPos(pos);
      if (length < 0) {
         return;
      }
      indent.setLength(0);
      int remainder = 0;
      if (useTabs) {
         remainder = length % indentLength;
         length = length / indentLength;
         indent.append(joinTabs(length));
         if (remainder > 0) {
            indent.append(joinSpaces(remainder));
         }
      }
      else {
         indent.append(joinSpaces(length));
      }
      txt.remove(lineStart, pos - lineStart);
      txt.insert(lineStart, indent.toString());
   }

   private int outdentPos(int pos) {
      int outdentPos = 0;
      int lastOpeningPos = txt.text().lastIndexOf('{', pos - 1);
      int lastClosingPos = txt.text().lastIndexOf('}', pos - 1);
      int indentAtLastBrace = 0;
      if (lastOpeningPos > lastClosingPos) {
         indentAtLastBrace = indentLengthAt(lastOpeningPos);
         outdentPos = indentAtLastBrace;
      }
      else if (lastClosingPos > lastOpeningPos) {
         indentAtLastBrace = indentLengthAt(lastClosingPos);
         outdentPos = indentAtLastBrace - indentLength;
      }
      return outdentPos;
	}

   private int indentLengthAt(int pos) {
      int countTabs = 0;
      int countSpaces = 0;
      if (pos > 1) {
         String prevLine = line(lineStart(pos - 1), pos);
         for (int i = 0; i < prevLine.length(); i++) {
            char c = prevLine.charAt(i);
            if (c == ' ') {
               countSpaces++;
            }
            else if (c == '\t') {
               countTabs++;
               if (countSpaces < indentLength) {
                  countSpaces = 0;
               }
            }
            else {
               break;
            }
         }
      }
      return countTabs * indentLength + countSpaces;
   }

   private int lineStart(int pos) {
      return txt.text().lastIndexOf('\n', pos) + 1;
   }

   private String line(int lineStart, int pos) {
       return txt.text().substring(lineStart, pos);
   }

   private String joinTabs(int length) {
      joinTabs.setLength(0);
      for (int i = 0; i < length; i++) {
         joinTabs.append('\t');
      }
      return joinTabs.toString();
   }

   private String joinSpaces(int length) {
      joinSpaces.setLength(0);
      for (int i = 0; i < length; i++) {
         joinSpaces.append(' ');
      }
      return joinSpaces.toString();
   }
}
