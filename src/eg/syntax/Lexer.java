package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import java.awt.Color;

import eg.utils.Finder;

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
   private boolean isBlockCmnt = true;
   private boolean isTypeMode = false;

   /**
    * Creates a Lexer
    *
    * @param doc  the {@code StyledDocument} that is colored
    */
   public Lexer(StyledDocument doc) {
      this.doc = doc;
      setStyles();
   }

   /**
    * Sets a {@code Colorable}
    *
    * @param colorable  a {@link Colorable} object
    */
   public void setColorable(Colorable colorable) {
      this.colorable = colorable;
   }

   /**
    * Controls multiline corrections, particularly the uncommenting of
    * block comments, when only a line or section of text is scanned
    *
    * @param isEnabled  true to enable type mode
    */
   public void enableTypeMode(boolean isEnabled) {
      this.isTypeMode = isEnabled;
   }

   /**
    * (Re-)colors a section of text in black
    *
    * @param start  the position where the recolored text starts
    * @param length  the length of the text to be recolored
    */
   public void setCharAttrBlack(int start, int length) {
      doc.setCharacterAttributes(start, length, normalSet, false);
   }

   /**
    * Colors a portion of text in keyword blue
    *
    * @param start  the position where the recolored text starts
    * @param length  the length of the text to be recolored
    */
   public void setCharAttrKeyBlue(int start, int length) {
      doc.setCharacterAttributes(start, length, keyBlueSet, false);
   }

   /**
    * Searches and colors in red a keyword
    *
    * @param toColor  the text of the document or a section thereof
    * @param key  the keyword
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param reqWord  if the keyword must be a word
    */
   public void keywordRed(String toColor, String key, int pos, boolean reqWord) {
      string(toColor, key, keyRedSet, pos, reqWord);
   }

   /**
    * Searches and colors in blue a keyword
    *
    * @param toColor  the text of the document or a section thereof
    * @param key  the keyword
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param reqWord  if the keyword must be a word
    */
   public void keywordBlue(String toColor, String key, int pos, boolean reqWord) {
      string(toColor, key, keyBlueSet, pos, reqWord);
   }

   /**
    * Searches a bracket and shows it in bold
    *
    * @param toColor  the text of the document or a section thereof
    * @param bracket  the bracket
    * @param pos  the start position of '{@code toColor}' within the entire text
    */
   public void bracket(String toColor, String bracket, int pos) {
      string(toColor, bracket, brSet, pos, false);
   }

   /**
    * Searches a bracket and shows it bold and blue
    *
    * @param toColor  the text of the document or a section thereof
    * @param bracket  the bracket
    * @param pos  the start position of '{@code toColor}' within the entire text
    */
   public void bracketBlue(String toColor, String bracket, int pos) {
      string(toColor, bracket, brBlueSet, pos, false);
   }

   /**
    * Colors string literals in brown
    *
    * @param toColor  the text of the document or a section thereof
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param quoteSign  the quote sign, i.e either single or double quote
    * @param blockStart  the String that represents the start of a text block
    * where the String literal must be found in. Null to ignore any ocurrence
    * in a block
    * @param blockEnd  the String that represents the end of a text block
    * where the String literal must be found in. Not null if '{@code blockStart}'
    * is not null
    */
   public void quoted(String toColor, int pos, String quoteSign,
         String blockStart, String blockEnd) {

      if (!isTypeMode & Finder.countLines(toColor) > 1) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            quotedInLine(s, pos + sum, quoteSign, blockStart, blockEnd);
            sum += s.length() + 1;
         }
      }
      else {
         quotedInLine(toColor, pos, quoteSign, blockStart, blockEnd);
      }
   }

   /**
    * Searches line comments and colors commented lines in green
    *
    * @param toColor  the text of the document or a section thereof
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param lineCmnt  the String that equals the start of a line comment
    */
   public void lineComments(String toColor, int pos, String lineCmnt) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (!SyntaxUtils.isInQuotes(toColor, start, lineCmnt)) {
               int lineEnd = toColor.indexOf("\n", start + 1);
               int length;
               if (lineEnd != -1) {
                  length = lineEnd - start;
               }
               else {
                  length = toColor.length() - start;
               }
               doc.setCharacterAttributes(start + pos, length,
                     cmntSet, false);
            }
            start += 1;
         }
      }
   }

   /**
    * Searches and colors block comments in green
    *
    * @param allText  the entire text
    * @param blockStart  the String that represents the start signal for a block
    * @param blockEnd  the String that represents the end signal for a block
    */
   public void blockComments(String allText, String blockStart, String blockEnd) {
      if (!isBlockCmnt) {
         return;
      }

      removedFirstBlockStart(allText, blockStart, blockEnd);

      int start = 0;
      while (start != -1) {
         start = allText.indexOf(blockStart, start);
         int end = 0;
         if (start != -1) {
            if (!SyntaxUtils.isInQuotes(allText, start, blockStart)) {
               end = SyntaxUtils.nextBlockEnd(allText, start + 1,
                     blockStart, blockEnd);
               if (end != -1) {
                  int length = end - start + blockEnd.length();
                  doc.setCharacterAttributes(start, length, cmntSet, false);
                  removedBlockStart(allText, end + blockEnd.length(),
                         blockStart, blockEnd);
               }
               else {
                  removedBlockEnd(allText, start, blockStart);
               }
            }
            start += 1;
         }
      }
   }

   void color(String allText, String toColor, int pos, int posStart) {
       colorable.color(allText, toColor, pos, posStart, this);
   }

   //
   //--private methods--
   //

   private void string(String toColor, String str, SimpleAttributeSet set,
         int pos, boolean reqWord) {

      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(str, start);
         if (start != -1) {
            boolean ok = !reqWord || SyntaxUtils.isWord(toColor, str, start);
            if (ok) {
               doc.setCharacterAttributes(start + pos, str.length(),
                     set, false);
            }
            start += str.length();
         }
      }
   }

   private void quotedInLine(String line, int pos, String quoteSign,
         String blockStart, String blockEnd) {

      int start = 0;
      int lastStart;
      int end = 0;
      while (start != -1 && end != -1) {
         start = line.indexOf(quoteSign, start);
         if (start != -1 ) {
            end = line.indexOf(quoteSign, start + 1);
            int length = 0;
            if (end != -1 ) {
               length = end - start;
               boolean ok = blockStart == null
                     || SyntaxUtils.isInBlock(line, start, blockStart, blockEnd);
               if (ok) {
                  doc.setCharacterAttributes(start + pos, length + 1,
                        strLitSet, false);
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
         while (nextStart != -1 && SyntaxUtils.isInQuotes(allText, nextStart, blockStart)) {
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

   private void colSectionExBlock(String allText, String section, int pos) {
      if (isTypeMode) {
         enableTypeMode(false);
         isBlockCmnt = false;
         color(allText, section, pos, pos);
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

      Color keyBlue = new Color(80, 80, 200);
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
