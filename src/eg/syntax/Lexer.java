package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import java.awt.Color;

import eg.utils.Finder;

/**
 * The search and coloring of different syntax elements
 */
public class Lexer {

   private final SimpleAttributeSet keyRedSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet keyBlueSet = new SimpleAttributeSet();
   private final SimpleAttributeSet cmntSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet      = new SimpleAttributeSet();
   private final SimpleAttributeSet brBlueSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet normalSet  = new SimpleAttributeSet();;
   private final StyledDocument doc;

   private Colorable colorable;
   private String allText = "";
   private String toColor = "";
   private int pos;
   private int posStart;
   private boolean isBlockCmnt = true;
   private boolean isTypeMode = false;

   /**
    * Creates a Lexer
    *
    * @param doc  the <code>StyledDocument</code> that contains
    * the text to color
    */
   public Lexer(StyledDocument doc) {
      this.doc = doc;
      setStyles();
   }

   /**
    * Sets a <code>Colorable</code>
    *
    * @param colorable  a {@link Colorable} object
    */
   public void setColorable(Colorable colorable) {
      this.colorable = colorable;
   }

   /**
    * Sets the text to be colored
    *
    * @param allText  the entire text of the document
    * @param toColor  the text section that is to be colored
    * @param pos  the pos within the document where a change happened
    * @param posStart  the pos within the document where
    * <code>toColor</code> starts
    */
   public void setTextToColor(String allText, String toColor, int pos,
         int posStart) {

      this.allText = allText;
      this.toColor = toColor;
      this.pos = pos;
      this.posStart = posStart;
   }

   /**
    * Enables type mode.
    * If enabled, coloring may take place in sections (single lines)
    * of the document taking into account, however, corrections that
    * need multiline analysis (i.e. commenting/uncommenting of
    * block comments).
    *
    * @param isEnabled  true to enable type mode
    */
   public void enableTypeMode(boolean isEnabled) {
      this.isTypeMode = isEnabled;
   }
   
   /**
    * (Re-)colors in black this section of text that is to be colored
    */
   public void setCharAttrBlack() {
      doc.setCharacterAttributes(posStart, toColor.length(), normalSet, false);
   }
   
   /**
    * (Re-)colors in black the entire text
    */
   public void setAllCharAttrBlack() {
      doc.setCharacterAttributes(0, doc.getLength(), normalSet, false);
   }

   /**
    * Searches and colors in red a keyword
    *
    * @param key  the keyword
    * @param reqWord  if the keyword must be a word
    */
   public void keywordRed(String key, boolean reqWord) {
      string(key, keyRedSet, reqWord);
   }

   /**
    * Searches and colors in blue a keyword
    *
    * @param key  the keyword
    * @param reqWord  if the keyword must be a word
    */
   public void keywordBlue(String key, boolean reqWord) {
      string(key, keyBlueSet, reqWord);
   }

   /**
    * Searches a bracket and displays it in bold
    *
    * @param bracket  the bracket
    */
   public void bracket(String bracket) {
      string(bracket, brSet, false);
   }

   /**
    * Searches a bracket and displays it in bold and blue
    *
    * @param bracket  the bracket
    */
   public void bracketBlue(String bracket) {
      string(bracket, brBlueSet, false);
   }

   /**
    * Searches and colors in blue variables that are identified by
    * a sign (like $)
    *
    * @param sign  the character that marks that a word is variable
    */
   public void signedVariable(String sign) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length = 0;
         if (start != -1 && SyntaxUtils.isWordStart(toColor, start)) {
            length = signedVariableLength(toColor.substring(start));
            setCharAttrKeyBlue(start + posStart, length);
         }
         start += length;
      }
   }

   /**
    * Searches and colors in blue a html tag
    *
    * @param key  the keyword that is part of a tag
    */
   public void tag(String key) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(key, start);
         if (start != -1) {
            if (isTagStart(toColor, start)
                  && isTagEnd(toColor, key.length(), start)) {
               setCharAttrKeyBlue(start + posStart, key.length());
            }
            start += key.length();
         }
      }
   }

   /**
    * Searches and colors in brown quoted text where a quoted section
    * does not span several lines (a method for a quoted block of text
    * is still missing!).
    *
    * @param quoteMark  the quotation mark, i.e either single or double
    * quote
    * @param blockStart  the String that represents the start of a text
    * block where the String literal must be found in. Null to
    * ignore any ocurrence in a block
    * @param blockEnd  the String that represents the end of a text block
    * where the String literal must be found in. Not null if
    * '{@code blockStart}'
    * @param escape  the escape character to skip the quote sign. May be
    * null to not evaluate an escape character
    */
   public void quotedLineWise(String quoteMark,
         String blockStart, String blockEnd, String escape) {

      if (!isTypeMode & Finder.countLines(toColor) > 1) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            quoted(s, posStart + sum, quoteMark, blockStart, blockEnd, escape);
            sum += s.length() + 1;
         }
      }
      else {
         quoted(toColor, posStart, quoteMark, blockStart, blockEnd, escape);
      }
   }

   /**
    * Searches and colors in green line comments
    *
    * @param lineCmnt  the String that represents the start of a line
    * comment
    */
   public void lineComments(String lineCmnt) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            int length = 0;
            if (!SyntaxUtils.isInQuotes(toColor, start, lineCmnt)) {
               int lineEnd = toColor.indexOf("\n", start + 1);
               if (lineEnd != -1) {
                  length = lineEnd - start;
               }
               else {
                  length = toColor.length() - start;
               }
               doc.setCharacterAttributes(start + posStart, length,
                     cmntSet, false);
            }
            if (isTypeMode) {
               break;
            }
            else {
               start += length + 1;
            }
         }
      }
   }

   /**
    * Searches and colors in green block comments
    *
    * @param blockStart  the String that represents the start of a block
    * @param blockEnd  the String that represents the end of a block
    */
   public void blockComments(String blockStart, String blockEnd) {

      if (!isBlockCmnt) {
         return;
      }

      removedFirstBlockStart(allText, blockStart, blockEnd);

      int start = 0;
      while (start != -1) {
         start = allText.indexOf(blockStart, start);
         int end = 0;
         if (start != -1) {
            int length = 0;
            if (!SyntaxUtils.isInQuotes(allText, start, blockStart)) {
               end = SyntaxUtils.nextBlockEnd(allText, start + 1,
                     blockStart, blockEnd);
               if (end != -1) {
                  length = end - start + blockEnd.length();
                  doc.setCharacterAttributes(start, length, cmntSet, false);
                  removedBlockStart(allText, end + blockEnd.length(),
                         blockStart, blockEnd);
               }
               else {
                  removedBlockEnd(allText, start, blockStart);
               }
            }
            start += length + 1;
         }
      }
   }
   
   /**
    * Returns if this pos is found in a block of text that is
    * delimited by the specified start and end signals
    *
    * @param blockStart  the String that defines the block start
    * @param blockEnd  the String that defines the block end
    * @return  if the specified pos is found in a certain block
    */
   public boolean isInBlock(String blockStart, String blockEnd) {
       return SyntaxUtils.isInBlock(allText, pos, blockStart, blockEnd);
   }

   void color() {
       colorable.color(this);
   }

   //
   //--private methods--
   //

   private void string(String str, SimpleAttributeSet set, boolean reqWord) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(str, start);
         if (start != -1) {
            boolean ok = !reqWord || SyntaxUtils.isWord(toColor, str, start);
            if (ok) {
               doc.setCharacterAttributes(start + posStart, str.length(), set, false);
            }
            start += str.length();
         }
      }
   }

   private void quoted(String toColor, int posStart, String quoteMark,
         String blockStart, String blockEnd, String escape) {

      int start = 0;
      int lastStart;
      int end = 0;
      while (start != -1 && end != -1) {
         start = toColor.indexOf(quoteMark, start);
         if (escape != null) {
            while (SyntaxUtils.isEscaped(toColor, start)) {
               start = toColor.indexOf(quoteMark, start + 1);
            }
         }
         if (start != -1) {
            end = toColor.indexOf(quoteMark, start + 1);
            while (escape != null && SyntaxUtils.isEscaped(toColor, end)) {
               end = toColor.indexOf(quoteMark, end + 1);
            }
            int length = 0;
            if (end != -1) {
               length = end - start + 1;
               boolean ok = blockStart == null
                     || SyntaxUtils.isInBlock(toColor, start, blockStart, blockEnd);
               if (quoteMark.equals("\'")) {
                  ok = !SyntaxUtils.isInBlock(toColor, start, "\"", "\"")
                        && !SyntaxUtils.isInBlock(toColor, end, "\"", "\"");
               }
               if (ok) {
                  doc.setCharacterAttributes(start + posStart, length, strLitSet, false);
               }
               start += length + 1;
            }
         }
      }
   }

   private void removedFirstBlockStart(String allText, String blockStart,
         String blockEnd) {

      if (isTypeMode) {
         int firstEnd = SyntaxUtils.nextBlockEnd(allText, 0, blockStart,
               blockEnd);
         if (firstEnd != -1) {
            colSectionExBlock(allText, allText.substring(0, firstEnd + 2), 0);
         }
      }
   }

   private void removedBlockStart(String allText, int pos, String blockStart,
         String blockEnd) {

      if (isTypeMode) {
         int lastStart = SyntaxUtils.lastBlockStart(allText, pos, blockStart,
               blockEnd);
         int nextEnd   = SyntaxUtils.nextBlockEnd(allText, pos, blockStart,
               blockEnd);
         if (nextEnd != -1 && lastStart == -1) {
            String toUncomment = allText.substring(pos, nextEnd + blockEnd.length());
            colSectionExBlock(allText, toUncomment, pos);
         }
      }
   }

   private void removedBlockEnd(String allText, int pos, String blockStart) {
      if (isTypeMode) {
         int nextStart = allText.indexOf(blockStart, pos + 1);
         while (nextStart != -1 && SyntaxUtils.isInQuotes(allText, nextStart,
                blockStart)) {
            nextStart = allText.lastIndexOf(blockStart, nextStart + 1);
         }
         if (nextStart != -1) {
            colSectionExBlock(allText, allText.substring(pos, nextStart), pos);
         }
         else {
            colSectionExBlock(allText, allText.substring(pos), pos);
         }
      }
   }

   private boolean isTagStart(String text, int pos) {
      boolean isTagStart = false;
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         isTagStart = c == '<';
      }
      if (!isTagStart && pos > 1) {
         char c1 = text.charAt(pos - 2);
         char c2 = text.charAt(pos - 1);
         isTagStart = c2 == '/' && c1 == '<';
      }
      return isTagStart;
   }

   private boolean isTagEnd(String text, int length, int pos) {
      int endPos = pos + length;
      if (text.length() > endPos) {
         char c = text.charAt(endPos);
         return c == '>' || c == ' ';
      }
      else {
         return true;
      }
   }

   private int signedVariableLength(String text) {
      char[] c = text.toCharArray();
      int i = 1;
      for (i = 1; i < c.length; i++) {
         if (c[i] == ' ') {
            break;
         }
      }
      return i;
   }
   
   private void setCharAttrKeyBlue(int start, int length) {
      doc.setCharacterAttributes(start, length, keyBlueSet, false);
   }

   private void setCharAttrKeyRed(int start, int length) {
      doc.setCharacterAttributes(start, length, keyRedSet, false);
   }
   
   private void colSectionExBlock(String allText, String section, int pos) {
      if (isTypeMode) {
         enableTypeMode(false);
         isBlockCmnt = false;
         setTextToColor(allText, section, pos, pos);
         color();
         enableTypeMode(true);
         isBlockCmnt = true;
      }
   }

   private void setStyles() {
      StyleConstants.setForeground(normalSet, Color.BLACK);
      StyleConstants.setBold(normalSet, false);

      Color commentGreen = new Color(80, 190, 80);
      StyleConstants.setForeground(cmntSet, commentGreen);
      StyleConstants.setBold(cmntSet, false);

      Color keyRed = new Color(230, 0, 90);
      StyleConstants.setForeground(keyRedSet, keyRed);
      StyleConstants.setBold(keyRedSet, false);

      Color keyBlue = new Color(80, 80, 230);
      StyleConstants.setForeground(keyBlueSet, keyBlue);
      StyleConstants.setBold(keyBlueSet, false);

      Color bracketBlue = new Color(20, 30, 255);
      StyleConstants.setForeground(brBlueSet, bracketBlue);
      StyleConstants.setBold(brBlueSet, true);

      Color bracketGray = new Color(20, 30, 50);
      StyleConstants.setForeground(brSet, bracketGray);
      StyleConstants.setBold(brSet, true);

      Color strLitOrange = new Color(230, 140, 50);
      StyleConstants.setForeground(strLitSet, strLitOrange );
      StyleConstants.setBold(strLitSet, false);
   }
}
