package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import java.awt.Color;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--//
import eg.utils.LinesFinder;

/**
 * The search and coloring of different syntax elements
 */
public class Coloring {

   private final static Color BLUE   = new Color(20, 30, 255);
   private final static Color RED    = new Color(240, 0, 50);
   private final static Color GREEN  = new Color(80, 190, 80);
   private final static Color GRAY   = new Color(30, 30, 30);
   private final static Color PURPLE = new Color(150, 30, 250);
   private final static Color ORANGE = new Color(230, 102, 0);

   private final SimpleAttributeSet normalSet;

   private final SimpleAttributeSet redPlainSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet bluePlainSet   = new SimpleAttributeSet();
   private final SimpleAttributeSet blueBoldSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet greenPlainSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet grayBoldSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet orangePlainSet = new SimpleAttributeSet();
   private final SimpleAttributeSet purplePlainSet = new SimpleAttributeSet();

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
    * @param set  the <code>SimpleAttributeSet</code> that has the
    * normal (black, plain) style
    */
   public Coloring(StyledDocument doc, SimpleAttributeSet set) {
      this.doc = doc;
      normalSet = set;
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
    * Colors a subset of lines or the entire text (in any case the entire
    * text is scanned for block comments). To scan the entire text
    * <code>section</code> is equal to <code>allText</code> or null.
    *
    * @param allText  the entire text of the document
    * @param section  a section of <code>allText</code>. If null
    * <code>allText</code> is used. If it does not start at a line start or
    * does not end at line end the full lines are built
    * @param pos  the pos within the document where a change happened.
    */
   public void colorMultipleLines(String allText, String section, int pos) {
      int posStart = 0;
      if (section == null) {
         section = allText;
      }
      else {
         section = LinesFinder.allLinesAtPos(allText, section, pos);
         posStart = LinesFinder.lastNewline(allText, pos) + 1;
      }
      setTextToColor(allText, section, pos, posStart);
      color();
   }

   /**
    * Colors the current line where a change happened (the entire text is
    * scanned for block comments though).
    *
    * @param allText  the entire text of the document
    * @param pos  the pos within document where a change happened
    */
   public void colorLine(String allText, int pos) {
      String toColor = LinesFinder.lineAtPos(allText, pos);
      int posStart = LinesFinder.lastNewline(allText, pos) + 1;
      setTextToColor(allText, toColor, pos, posStart);
      color();
   }

   /**
    * (Re-)colors in black this section of text that is to be colored
    */
   public void setCharAttrBlack() {
      setCharAttr(posStart, toColor.length(), normalSet);
   }

   /**
    * (Re-)colors in black the entire text
    */
   public void setAllCharAttrBlack() {
      setCharAttr(0, doc.getLength(), normalSet);
   }

   /**
    * Searches and colors keywords in red
    *
    * @param keys  the array of keywords
    * @param reqWord  if the keyword must be a word
    */
   public void keywordsRed(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, redPlainSet, reqWord);
      }
   }

   /**
    * Searches and colors keywords in blue
    *
    * @param keys  the array of keywords
    * @param reqWord  if the keyword must be a word
    */
   public void keywordsBlue(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, bluePlainSet, reqWord);
      }
   }

   /**
    * Searches and colors variables that start with a sign in blue
    * (like $ in Perl)
    *
    * @param signs  the array of characters that marks a variable
    * @param endChars  the array of characters that mark the end of the
    * variable
    */
   public void signedKeywordsBlue(String[] signs, char[] endChars) {
      for (String s : signs) {
         signedVariable(s, endChars);
      }
   }

   /**
    * Searches and colors html tags in blue
    *
    * @param tags  the array of tags
    */
   public void htmlTags(String[] tags) {
      for (String s : tags) {
         htmlTag(s);
      }
   }

   /**
    * Searched and colors keywords in red in a html document
    *
    * @param keys  the array of keywords
    * @param blockStart  the String that represents the start of a block
    * where the keyword must be found in
    * @param blockEnd  the String that represents the end of a block
    * where the keyword must be found in
    */
   public void htmlKeywords(String[] keys, String blockStart, String blockEnd) {
       for (String s : keys) {
          htmlKeyword(s, blockStart, blockEnd);
       }
   }

   /**
    * Searches the braces and displays them in bold gray
    */
   public void bracesGray() {
      key("{", grayBoldSet, false);
      key("}", grayBoldSet, false);
   }

   /**
    * Searches the brackets and displays them in bold blue
    */
   public void bracketsBlue() {
      key("(", blueBoldSet, false);
      key(")", blueBoldSet, false);
   }

   /**
    * Searches and colors quoted text in orange. The quote mark is ignored
    * if a backslash precedes it
    */
   public void quotedText() {
      quotedLineWise(false);
   }

   /**
    * Searches and colors quoted text in a html document, separating between
    * values of attributes and quotes text in script parts. The quote
    * mark is ignored if a backslash precedes it
    */
   public void quotedTextHtml() {
      quotedLineWise(true);
   }

   /**
    * Searches and colors line comments in green
    *
    * @param lineCmnt  the String that represents the start of a line
    * comment
    * @param exception  the character that disables the line comment
    * when it precedes <code>lineCmt</code>. The null character to skip
    * any exception
    */
   public void lineComments(String lineCmnt, char exception) {
      lineComments(lineCmnt, exception, false);
   }

   /**
    * Searches and colors line comments in green in javascript section
    * of an html document
    */
   public void lineCommentsJavascriptInHtml() {
      lineComments("//", '\0', true);
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
      int length = 0;
      while (start != -1) {
         start = allText.indexOf(blockStart, start);
         int end = 0;
         if (start != -1) {
            length = 0;
            if (!SyntaxUtils.isInQuotes(allText, start, blockStart.length())) {
               end = SyntaxUtils.nextBlockEnd(allText, start + 1,
                     blockStart, blockEnd);
               if (end != -1) {
                  length = end - start + blockEnd.length();
                  setCharAttr(start, length, greenPlainSet);
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
   //--private methods--//
   //

   private void setTextToColor(String allText, String toColor, int pos,
         int posStart) {

      this.allText = allText;
      this.toColor = toColor;
      this.pos = pos;
      this.posStart = posStart;
      isTypeMode = toColor != null;
   }

   private void color() {
       colorable.color(this);
   }

   private void key(String str, SimpleAttributeSet set, boolean reqWord) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(str, start);
         if (start != -1) {
            boolean ok = !reqWord || SyntaxUtils.isWord(toColor, start, str.length());
            if (ok) {
               setCharAttr(start + posStart, str.length(), set);
            }
            start += str.length();
         }
      }
   }

   private void htmlKeyword(String keyword, String blockStart, String blockEnd) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(keyword, start);
         if (start != -1) {
            int absStart = start + posStart;
            boolean ok = (SyntaxUtils.isWord(toColor, start, keyword.length())
                  & isInBlock(blockStart, blockEnd, absStart))
                  && !SyntaxUtils.isTagStart(toColor, start);
            if (ok) {
               setCharAttr(absStart, keyword.length(), redPlainSet);
            }
            start += keyword.length();
         }
      }
   }

   private void quotedLineWise(boolean isHtml) {
      if (toColor.indexOf("\n") != -1) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            quoted(s, posStart + sum, "\"", isHtml);
            quoted(s, posStart + sum, "\'", isHtml);
            sum += s.length() + 1;
         }
      }
      else {
         quoted(toColor, posStart, "\"", isHtml);
         quoted(toColor, posStart, "\'", isHtml);
      }
   }

   private void quoted(String toColor, int lineStart, String quoteMark, boolean isHtml) {
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
                  setQuoteAttr(start, lineStart, length, isHtml);
               }
               start += length + 1;
            }
         }
      }
   }

   private void setQuoteAttr(int pos, int lineStart, int length, boolean isHtml) {
      int absStart = pos + lineStart;
      if (isHtml) {
         if (isInBlock("<", ">", absStart)) {
            setCharAttr(absStart, length, purplePlainSet);
         }
         else if (isInBlock("<script>", "</script>", absStart)) {
            setCharAttr(absStart, length, orangePlainSet);
         }
      }
      else {
         setCharAttr(absStart, length, orangePlainSet);
      }
  }


   private void htmlTag(String tag) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(tag, start);
         if (start != -1) {
            if (SyntaxUtils.isHtmlTag(toColor, start, start + tag.length())) {
               setCharAttr(start + posStart, tag.length(), blueBoldSet);
            }
            start += tag.length();
         }
      }
   }

   private void signedVariable(String sign,  char[] endChars) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length = 0;
         if (start != -1) {
            if (SyntaxUtils.isWordStart(toColor, start)) {
               length = SyntaxUtils.wordLength(toColor, start, endChars);
               setCharAttr(start + posStart, length, bluePlainSet);
               start += length;
            }
            else {
               start++;
            }
         }
      }
   }

   private void lineComments(String lineCmnt, char exception, boolean isHtml) {
      final boolean isException = exception != '\0';
      boolean ok = true;
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (isException && start > 0) {
               ok = toColor.charAt(start - 1) != exception;
            }
            if (isHtml) {
               ok = ok && isInBlock("<script>", "</script>", start + posStart);
            }
            int length = 0;
            if (ok && !SyntaxUtils.isInQuotes(toColor, start, lineCmnt.length())) {
               int lineEnd = toColor.indexOf("\n", start + 1);
               if (lineEnd != -1) {
                  length = lineEnd - start;
               }
               else {
                  length = toColor.length() - start;
               }
               setCharAttr(start + posStart, length, greenPlainSet);
            }
            start += length + 1;
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
                blockStart.length())) {
            nextStart = allText.indexOf(blockStart, nextStart + 1);
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
         isBlockCmnt = false;
         setTextToColor(allText, section, pos, pos);
         color();
         isBlockCmnt = true;
      }
   }

   private boolean isInBlock(String blockStart, String blockEnd, int pos) {
      int lastStart = SyntaxUtils.lastBlockStart(allText, pos, blockStart,
            blockEnd);
      int nextEnd = SyntaxUtils.nextBlockEnd(allText, pos, blockStart,
            blockEnd);
      return lastStart != -1 & nextEnd != -1;
   }

   private void setCharAttr(int start, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(start, length, set, false);
   }

   private void setStyles() {
      StyleConstants.setForeground(redPlainSet, RED);
      StyleConstants.setBold(redPlainSet, false);

      StyleConstants.setForeground(bluePlainSet, BLUE);
      StyleConstants.setBold(bluePlainSet, false);

      StyleConstants.setForeground(blueBoldSet, BLUE);
      StyleConstants.setBold(blueBoldSet, true);

      StyleConstants.setForeground(greenPlainSet, GREEN);
      StyleConstants.setBold(greenPlainSet, false);

      StyleConstants.setForeground(purplePlainSet, PURPLE);
      StyleConstants.setBold(purplePlainSet, false);

      StyleConstants.setForeground(grayBoldSet, GRAY);
      StyleConstants.setBold(grayBoldSet, true);

      StyleConstants.setForeground(orangePlainSet, ORANGE);
      StyleConstants.setBold(orangePlainSet, false);
   }
}
