package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import java.awt.Color;

//--Eadgyth--//
import eg.utils.Finder;

/**
 * The search and coloring of different syntax elements
 */
public class Lexer {

   private final SimpleAttributeSet keyRedSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet keyBlueSet = new SimpleAttributeSet();
   private final SimpleAttributeSet cmntSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet      = new SimpleAttributeSet();
   private final SimpleAttributeSet blueBoldSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet normalSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet htmlValSet = new SimpleAttributeSet();
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
      string(bracket, blueBoldSet, false);
   }

   /**
    * Searches and colors in blue variables that start with a sign
    * (like $ in Perl)
    *
    * @param sign  the character that marks a variable
    * @param end  the array of characters that mark the end of the
    * variable
    */
   public void signedVariable(String sign,  char[] end) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length = 0;
         if (start != -1) {
            if (SyntaxUtils.isWordStart(toColor, start)) {
               length = varLength(start, end);
               setCharAttrKeyBlue(start + posStart, length);
               start += length;
            }
            else {
               start++;
            }
         }
      }
   }

   /**
    * Searches and colors in blue a html tag
    *
    * @param tag  the tag
    */
   public void htmlTag(String tag) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(tag, start);
         if (start != -1) {
            if (SyntaxUtils.isTagStart(toColor, start)
                  && SyntaxUtils.isTagEnd(toColor, tag.length(), start)) {
 
               setCharAttrBlueBold(start + posStart, tag.length());
            }
            start += tag.length();
         }
      }
   }
   
   /**
    * Searched and colors an html attribute
    *
    * @param attr  the attribute 
    */
   public void htmlAttr(String attr) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(attr, start);
         if (start != -1) {
            int absStart = start + posStart;
            boolean ok = (SyntaxUtils.isWord(toColor, start, attr.length())
                  & isInBlock("<", ">", absStart))
                  && !SyntaxUtils.isTagStart(toColor, start);
            if (ok) {
               setCharAttrKeyRed(absStart, attr.length());
            }
            start += attr.length();
         }
      }
   }

   /**
    * Searches and colors in brown quoted text. The quote mark is ignored
    * if a backslash precedes it
    *
    * @param quoteMark  the quotation mark, i.e either single or double
    * quote
    * @param isHtml  if the quotation is evaluated in a html file
    */
   public void quotedLineWise(String quoteMark, boolean isHtml) {
      if (Finder.countLines(toColor) > 1) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            quoted(s, posStart + sum, quoteMark, isHtml);
            sum += s.length() + 1;
         }
      }
      else {
         quoted(toColor, posStart, quoteMark, isHtml);
      }
   }

   /**
    * Searches and colors in green line comments
    *
    * @param lineCmnt  the String that represents the start of a line
    * comment
    * @param exception  the character that disables the line comment
    * when it precedes <code>lineCmt</code>. The null character to skip
    * any exception
    */
   public void lineComments(String lineCmnt, char exception) {
      int start = 0;
      boolean isException = false;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (exception != '\0' && start > 0) {
               isException = false;
               isException = toColor.charAt(start - 1) == exception;
            }
            int length = 0;
            if (!isException 
                  && !SyntaxUtils.isInQuotes(toColor, start, lineCmnt.length())) {

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
            if (isTypeMode && !isException) {
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
            if (!SyntaxUtils.isInQuotes(allText, start, blockStart.length())) {
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
    * Returns if this pos where a change happened is found in a block of
    * text that is delimited by the specified start and end signals
    *
    * @param blockStart  the String that defines the block start
    * @param blockEnd  the String that defines the block end
    * @return  if this pos is found in a certain block of text
    */
   public boolean isInBlock(String blockStart, String blockEnd) {
      return isInBlock(blockStart, blockEnd, pos);
   }

   //
   //--used in this package or in this class--
   //

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
            boolean ok = !reqWord || SyntaxUtils.isWord(toColor, start, str.length());
            if (ok) {
               doc.setCharacterAttributes(start + posStart, str.length(), set, false);
            }
            start += str.length();
         }
      }
   }

   private void quoted(String toColor, int posStart, String quoteMark,
         boolean isHtml) {

      boolean isSingleQuote = quoteMark.equals("\'");
      boolean notQuoted = true;
      int start = 0;
      int end = 0;
      int length = 0;
      while (start != -1 && end != -1) {
         start = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start);
         if (start != -1) {
            if (isSingleQuote) {
               notQuoted = SyntaxUtils.isNotQuoted(toColor, start);
            }
            end = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start + 1);
            if (end != -1) {
               if (isSingleQuote) {
                  notQuoted = notQuoted && SyntaxUtils.isNotQuoted(toColor, end);
               }
               length = end - start + 1;
               if (notQuoted) {
                  if (!isHtml) {
                     doc.setCharacterAttributes(start + posStart, length, strLitSet, false);
                  }
                  else {
                     if (isInBlock("<", ">", start + posStart)) {
                        doc.setCharacterAttributes(start + posStart, length, htmlValSet, false);
                     }
                     else if (isInBlock("<script>", "</script>", start + posStart)) {
                        doc.setCharacterAttributes(start + posStart, length, strLitSet, false);
                     }
                  }
               }
               start += length + 1;
            }
         }
      }
   }
   
   private boolean isInBlock(String blockStart, String blockEnd, int pos) {
      int lastStart = SyntaxUtils.lastBlockStart(allText, pos, blockStart,
            blockEnd);
      int nextEnd = SyntaxUtils.nextBlockEnd(allText, pos, blockStart,
            blockEnd);
      return lastStart != -1 & nextEnd != -1;
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
                blockStart.length())) {
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

   private void setCharAttrKeyBlue(int start, int length) {
      doc.setCharacterAttributes(start, length, keyBlueSet, false);
   }
   
   private void setCharAttrBlueBold(int start, int length) {
      doc.setCharacterAttributes(start, length, blueBoldSet, false);
   }

   private void setCharAttrKeyRed(int start, int length) {
      doc.setCharacterAttributes(start, length, keyRedSet, false);
   }
   
   private int varLength(int pos, char[] end) {
      boolean found = false;      
      int i;
      for (i = pos + 1; i < toColor.length() && !found; i++) {                     
         for (int j = 0; j < end.length; j++) {
            if (i == pos + 1) {
               if (toColor.charAt(i) == ' ') {
                  found = true;
                  break;
               }
            }
            else {
               if (toColor.charAt(i) == end[j]) {
                  found = true;
                  i--;
                  break;
               }
            }
         }
      }
      return i - pos;
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

      Color blueBold = new Color(20, 30, 255);
      StyleConstants.setForeground(blueBoldSet, blueBold);
      StyleConstants.setBold(blueBoldSet, true);

      Color bracketGray = new Color(20, 30, 50);
      StyleConstants.setForeground(brSet, bracketGray);
      StyleConstants.setBold(brSet, true);

      Color strLitOrange = new Color(230, 140, 50);
      StyleConstants.setForeground(strLitSet, strLitOrange );
      StyleConstants.setBold(strLitSet, false);

      Color htmlValPurple = new Color(148, 0, 211);
      StyleConstants.setForeground(htmlValSet, htmlValPurple);
      StyleConstants.setBold(htmlValSet, false);
   }
}
