package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.Color;

/**
 * The search and coloring of different syntax elements
 */
public class SyntaxSearch {

   private final static Color BLUE   = new Color(20, 30, 255);
   private final static Color RED    = new Color(240, 0, 50);
   private final static Color GREEN  = new Color(80, 190, 80);
   private final static Color GRAY   = new Color(30, 30, 30);
   private final static Color ORANGE = new Color(230, 100, 50);
   private final static Color PURPLE = new Color(130, 30, 250);

   private final SimpleAttributeSet normalSet;
   private final SimpleAttributeSet redPlainSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet redBoldSet     = new SimpleAttributeSet();
   private final SimpleAttributeSet bluePlainSet   = new SimpleAttributeSet();
   private final SimpleAttributeSet blueBoldSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet greenPlainSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet grayBoldSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet orangePlainSet = new SimpleAttributeSet();
   private final SimpleAttributeSet purplePlainSet = new SimpleAttributeSet();

   private final StyledDocument doc;

   private Colorable colorable;
   private boolean isTypeMode = false;
   private String text = "";
   private String toColor = "";
   private int pos;
   private int posStart;
   private boolean isBlockCmnt = true;
   private int innerStart = 0;
   private int innerEnd = 0;

   /**
    * @param doc  the <code>StyledDocument</code> that contains
    * the text to color
    * @param set  the <code>SimpleAttributeSet</code> that has the
    * normal (black, plain) style
    */
   public SyntaxSearch(StyledDocument doc, SimpleAttributeSet set) {
      this.doc = doc;
      normalSet = set;
      setStyles();
   }

   /**
    * Sets a <code>Colorable</code> that uses this methods to
    * color syntax elements
    *
    * @param colorable  a {@link Colorable} object
    */
   public void setColorable(Colorable colorable) {
      this.colorable = colorable;
   }

   /**
    * Sets the text parameters for coloring
    *
    * @param text  the entire text in the document
    * @param toColor  the part of <code>text</code> this is
    * colored
    * @param pos  the position where a change happened
    * @param posStart  the position where <code>toColor</code>
    * starts
    */
   public void setTextParams(String text, String toColor, int pos,
         int posStart) {

      this.isTypeMode = text != toColor;
      this.text = text;
      this.toColor = toColor;
      this.pos = pos;
      this.posStart = posStart;
      colorable.color(this);
   }

   /**
    * (Re-)colors in black this section of text that is to
    * be colored
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
    * Searches keywords and colors them in red
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
    * Searches keywords and colors them in red and displays them in bold
    *
    * @param keys  the array of keywords
    * @param reqWord  if the keyword must be a word
    */
   public void keywordsRedBold(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, redBoldSet, reqWord);
      }
   }

   /**
    * Searches keywords and colors them in blue
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
    * Searches variables that start with a sign and colors them in red
    *
    * @param signs  the array of characters that mark a variable start
    * @param endChars  the array of characters that mark the end of the
    * variable
    */
   public void signedVariables(String[] signs, char[] endChars) {
      for (String s : signs) {
         signedVariable(s, endChars);
      }
   }

   /**
    * Searches and colors html tags. Tags are in blue, atrributes in
    * red and attribute values in bold purple
    *
    * @param tags  the array of tags
    * @param attributes  the array of attributes
    */
   public void htmlTags(String[] tags,  String[] attributes) {
      for (String s : tags) {
         htmlTag(s, attributes);
      }
   }

   /**
    * Searches braces and colors them in bold gray
    */
   public void bracesGray() {
      key("{", grayBoldSet, false);
      key("}", grayBoldSet, false);
   }

   /**
    * Searches brackets and colors them in bold blue
    */
   public void bracketsBlue() {
      key("(", blueBoldSet, false);
      key(")", blueBoldSet, false);
   }

   /**
    * Searches quoted text and colors it in orange. The quote mark is ignored
    * if a backslash precedes it
    */
   public void quotedText() {
      quotedLineWise(false);
   }

   /**
    * Searches line comments and colors them in green
    *
    * @param lineCmnt  the string that marks the start of a line comment
    * @param exceptions  the array of strings that disable the line comment
    * when these precede <code>lineCmt</code>. Null if no exception is destined
    * for line comments
    */
   public void lineComments(String lineCmnt, String[] exceptions) {
      lineCommentsImpl(lineCmnt, exceptions);
   }

   /**
    * Searches block comments and colors them in green
    *
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    */
   public void blockComments(String blockStart, String blockEnd) {
      blockCommentsImpl(blockStart, blockEnd);
   }
   
   /**
    * Colors sections by setting a temporary <code>Colorable</code>. The specified
    * strings mark the starts and ends of sections
    *
    * @param colTemp  the {@link Colorable}
    * @param sectionStart  the mark for the starts
    * @param sectionEnd  the mark for the ends
    */
   public void innerSection(Colorable colTemp, String sectionStart, String sectionEnd) {
      Colorable curr = colorable;
      setColorable(colTemp);
      innerSection(sectionStart, sectionEnd);
      setColorable(curr);
   }

   /**
    * Returns if this position where a change happened is found in a
    * block of text that is delimited by the specified start and end
    * signals
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

   private void key(String key, SimpleAttributeSet set, boolean reqWord) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(key, start);
         if (start != -1) {
            boolean ok = !reqWord
                  || SyntaxUtils.isWord(toColor, start, key.length());

            if (ok) {
               setCharAttr(start + posStart, key.length(), set);
            }
            start += key.length();
         }
      }
   }

   private void htmlTag(String tag, String[] attributes) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(tag, start);
         if (start != -1) {
            boolean isStartTag = start > 0
                  && toColor.charAt(start - 1) == '<';
            boolean isEndTag = !isStartTag
                  && start > 1
                  && (toColor.charAt(start - 1) == '/'
                  & toColor.charAt(start - 2) == '<');
            boolean ok = (isStartTag || isEndTag)
                  && SyntaxUtils.isWord(toColor, start, tag.length());               
             
            if (ok) {
               setCharAttr(start + posStart, tag.length(), blueBoldSet);                           
               if (isStartTag
                     && toColor.length() > start + tag.length()
                     && toColor.charAt(start + tag.length()) == ' ') {

                  for (String s : attributes) {
                      htmlAttribute(s);
                  }
                  quotedLineWise(true);
               }
            }
            start += tag.length();
         }
      }
   }

   private void htmlAttribute(String keyword) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(keyword, start);
         if (start != -1) {
            int absStart = start + posStart;
            int lastTagStart = SyntaxUtils.lastBlockStart(text, absStart, "<", ">");
            boolean ok = SyntaxUtils.isWord(toColor, start, keyword.length())
                  && lastTagStart != -1;

            if (ok) {
               setCharAttr(absStart, keyword.length(), redPlainSet);
            }
            start += keyword.length();
         }
      }
   }

   private void innerSection(String sectionStart, String sectionEnd) {
      int start = 0;
      int length;
      while (start != -1) {
         start = text.indexOf(sectionStart, start);
         if (start != -1) {
            length = 0;           
            int end = SyntaxUtils.nextBlockEnd(text, start + 1,
                 sectionStart, sectionEnd);
            if (end != -1) {
               innerStart = start + sectionStart.length();
               innerEnd = end;
               String section = text.substring(innerStart, end);
               if (isTypeMode && pos >= innerStart && pos <= innerEnd) {
                  colorable.color(this);
               }
               else {
                  setTextParams(text, section, innerStart, innerStart);
               }
               length = section.length();
            }
            start += length + 1;
         }
      }
      innerStart = 0;
      innerEnd = 0;
   }

   private void signedVariable(String sign,  char[] endChars) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length;
         if (start != -1) {
            if (SyntaxUtils.isWordStart(toColor, start)) {
               length = SyntaxUtils.wordLength(toColor, start, endChars);
               setCharAttr(start + posStart, length, purplePlainSet);
               start += length;
            }
            else {
               start++;
            }
         }
      }
   }

   private void quotedLineWise(boolean isHtml) {
      if (toColor.contains("\n")) {
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

   private void quoted(String toColor, int lineStart, String quoteMark,
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
               notQuoted = !SyntaxUtils.isInQuotes(toColor, start);
            }
            end = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start + 1);
            if (end != -1) {
               if (isSingleQuote) {
                  notQuoted = notQuoted && !SyntaxUtils.isInQuotes(toColor, end);
               }
               if (notQuoted) {
                  length = end - start + 1;
                  int absStart = start + lineStart;
                  if (isHtml) {
                     if (isTypeMode || isInBlock("<", ">", absStart)) {
                        setCharAttr(absStart, length, purplePlainSet);
                     }
                  }
                  else {
                     setCharAttr(absStart, length, orangePlainSet);
                  }
               }
            }
            start += length + 1;
         }
      }
   }

   private void lineCommentsImpl(String lineCmnt, String[] exceptions) {
      final boolean isException = exceptions != null;
      boolean ok = true;
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (isException) {
               for (String exc : exceptions) {
                  if (start > exc.length() - 1) {
                     ok = !exc.equals(toColor.substring(start - exc.length(), start));
                     if (!ok) {
                        break;
                     }
                  }
               }
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

   private void blockCommentsImpl(String blockStart, String blockEnd) {
      if (!isBlockCmnt) {
         return;
      }
      removedFirstBlockStart(blockStart, blockEnd);
      int start = innerStart;
      int length;
      while (start != -1) {
         start = text.indexOf(blockStart, start);
         if (innerEnd > 0 && start >= innerEnd - blockStart.length()) {
            start = -1;
         }
         int end;
         if (start != -1) {
            length = 0;
            if (!SyntaxUtils.isInQuotes(text, start, blockStart.length())) {
               end = SyntaxUtils.nextBlockEnd(text, start + 1,
                     blockStart, blockEnd);
               if (innerEnd > 0 && end >= innerEnd - blockEnd.length()) {
                  end = -1;
               }
               if (end != -1) {
                  length = end - start + blockEnd.length();
                  setCharAttr(start, length, greenPlainSet);
                  removedBlockStart(end + blockEnd.length(), blockStart, blockEnd);
               }
               else {
                  removedBlockEnd(start, blockStart);
               }
            }
            start += length + 1;
         }
      }
   }

   private void removedFirstBlockStart(String blockStart, String blockEnd) {
      if (isTypeMode) {
         int firstEnd = SyntaxUtils.nextBlockEnd(text, innerStart, blockStart,
               blockEnd);
         if (innerEnd > 0 && firstEnd > innerEnd) {
            firstEnd = -1;
         }
         if (firstEnd != -1) {
            String toUncomment = text.substring(innerStart, firstEnd + 2);
            uncommentBlock(toUncomment, innerStart);
         }
      }
   }

   private void removedBlockStart(int endPos, String blockStart, String blockEnd) {
      if (isTypeMode) {
         int lastStart = SyntaxUtils.lastBlockStart(text, endPos, blockStart,
               blockEnd);
         int nextEnd   = SyntaxUtils.nextBlockEnd(text, endPos, blockStart,
               blockEnd);
         if (innerEnd > 0 && nextEnd > innerEnd) {
            nextEnd = -1;
         }
         if (nextEnd != -1 && lastStart == -1) {
            String toUncomment = text.substring(endPos, nextEnd + blockEnd.length());
            uncommentBlock(toUncomment, endPos);
         }
      }
   }

   private void removedBlockEnd(int startPos, String blockStart) {
      if (isTypeMode) {
         int nextStart = text.indexOf(blockStart, startPos + 1);
         while (nextStart != -1 && SyntaxUtils.isInQuotes(text, nextStart,
                blockStart.length())) {
            nextStart = text.indexOf(blockStart, nextStart + 1);
         }
         int end = nextStart;
         if (innerEnd > 0) {
            end = innerEnd;
         }
         String toUncomment;
         if (nextStart != -1) {
            toUncomment = text.substring(startPos, end);
         }
         else {
            if (innerEnd > 0) {
               toUncomment = text.substring(startPos, end);
            }
            else {
               toUncomment = text.substring(startPos);
            }
         }
         uncommentBlock(toUncomment, startPos);
      }
   }

   private void uncommentBlock(String section, int pos) {
      if (isTypeMode) {
         isBlockCmnt = false;
         setTextParams(text, section, pos, pos);
         isBlockCmnt = true;
      }
   }

   private boolean isInBlock(String blockStart, String blockEnd, int pos) {
      int lastStart = SyntaxUtils.lastBlockStart(text, pos, blockStart,
            blockEnd);
      int nextEnd = SyntaxUtils.nextBlockEnd(text, pos, blockStart,
            blockEnd);
      return lastStart != -1 & nextEnd != -1;
   }

   private void setCharAttr(int start, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(start, length, set, false);
   }

   private void setStyles() {
      StyleConstants.setForeground(redPlainSet, RED);
      StyleConstants.setBold(redPlainSet, false);
      
      StyleConstants.setForeground(redBoldSet, RED);
      StyleConstants.setBold(redBoldSet, true);

      StyleConstants.setForeground(bluePlainSet, BLUE);
      StyleConstants.setBold(bluePlainSet, false);

      StyleConstants.setForeground(blueBoldSet, BLUE);
      StyleConstants.setBold(blueBoldSet, true);

      StyleConstants.setForeground(greenPlainSet, GREEN);
      StyleConstants.setBold(greenPlainSet, false);

      StyleConstants.setForeground(grayBoldSet, GRAY);
      StyleConstants.setBold(grayBoldSet, true);

      StyleConstants.setForeground(orangePlainSet, ORANGE);
      StyleConstants.setBold(orangePlainSet, false);
      
      StyleConstants.setForeground(purplePlainSet, PURPLE);
      StyleConstants.setBold(purplePlainSet, false);
   }
}
