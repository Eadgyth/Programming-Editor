package eg.syntax;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

//--Eadgyth--/
import eg.utils.LinesFinder;

/**
 * The search and coloring of different syntax elements
 */
public class SyntaxSearch {

   private final StyledDocument doc;
   private final SimpleAttributeSet normalSet;

   private Colorable colorable;
   private String text = "";
   private String toColor = "";
   private int pos;
   private int posStart;
   private boolean isTypeMode = false;
   private boolean isMultiline = true;
   private boolean isBlockCmnt = true;
   private int innerStart = 0;
   private int innerEnd = 0;

   /**
    * @param doc  the <code>StyledDocument</code> that contains
    * the text to color
    * @param normalSet  the <code>SimpleAttributeSet</code> that has the
    * normal attributes, that is black and plain
    */
   public SyntaxSearch(StyledDocument doc, SimpleAttributeSet normalSet) {
      this.doc = doc;
      this.normalSet = normalSet;
   }

   /**
    * Sets a <code>Colorable</code> that uses this methods to color
    * syntax elements. If null the text is not colored or any coloring is
    * removed if a <code>Colorable</code> had been set before
    *
    * @param colorable  a {@link Colorable} object
    */
   public void setColorable(Colorable colorable) {
      if (this.colorable != null && colorable == null) {
         setAllCharAttrBlack();
      }
      this.colorable = colorable;
   }

   /**
    * Colors text in this document using this <code>Colorable</code>.
    *
    * @param text  the entire text in the document
    * @param toColor  the line (or multiline section) that is colored. Equals
    * <code>text</code> to scan the entire text
    * @param pos  the position where a change happened.
    * @param posStart  the position where <code>toColor</code>
    * starts
    */
   public void color(String text, String toColor, int pos, int posStart) {
      this.text = text;
      this.toColor = toColor;
      this.pos = pos;
      this.posStart = posStart;
      isTypeMode = !text.equals(toColor);
      isMultiline = LinesFinder.isMultiline(toColor);
      colorable.color(this);
   }

   /**
    * (Re-)colors this section of text that is to be colored in
    * black and (re-)sets any characters shown in bold to plain
    */
   public void setCharAttrBlack() {
      setCharAttr(posStart, toColor.length(), normalSet);
   }

   /**
    * Searches keywords for coloring in red. 
    * If the boolean <code>reqWord</code> is true coloring requires
    * that a keyword does not adjoin to a letter or a digit.
    *
    * @param keys  the array of keywords
    * @param reqWord  if  coloring requires that the keyword is a word
    */
   public void keywordsRed(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, Attributes.RED_PLAIN, reqWord);
      }
   }

   /**
    * Searches keywords for coloring in red and displaying in bold.
    * If the boolean <code>reqWord</code> is true coloring requires that
    * a keyword does not adjoin to a letter or a digit.
    *
    * @param keys  the array of keywords
    * @param reqWord  if  coloring requires that the keyword is a word
    */
   public void keywordsRedBold(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, Attributes.RED_BOLD, reqWord);
      }
   }

  /**
    * Searches keywords for coloring in blue. 
    * If the boolean <code>reqWord</code> is true coloring requires that
    * a keyword does not adjoin to a letter or a digit.
    *
    * @param keys  the array of keywords
    * @param reqWord  if  coloring requires that the keyword is a word
    */
   public void keywordsBlue(String[] keys, boolean reqWord) {
      for (String s : keys) {
         key(s, Attributes.BLUE_PLAIN, reqWord);
      }
   }

   /**
    * Searches keywords for coloring in red. Coloring requires that
    * a keyword does not adjoin to a letter or digit and, in addition,
    * is not preceded with a character in <code>nonWordStart</code>.
    * The boolean <code>inBraces</code> indicates that the keyword must
    * be found in region that follows an opening brace (if true) or
    * is outside a region in braces (if false).
    *
    * @param keys  the array of keywords
    * @param nonWordStart  the array of characters that do not precede
    * colored a keyword. Can be null
    * @param inBraces  if coloring requires that a keyword is inside or outside
    * a region in braces
    */
   public void keywordsRed(String[] keys, char[] nonWordStart,
         boolean inBraces) {

      for (String s : keys) {
         key(s, Attributes.RED_PLAIN, nonWordStart, inBraces);
      }
   }


   /**
    * Searches keywords for coloring in blue. Coloring requires that
    * a keyword does not adjoin to a letter or digit and, in addition,
    * is not preceded with a character in <code>nonWordStart</code>.
    * The boolean <code>inBraces</code> indicates that the keyword must
    * be found in region that follows an opening brace (if true) or
    * is outside a region in braces (if false).
    *
    * @param keys  the array of keywords
    * @param nonWordStart  the array of characters that do not precede
    * colored a keyword. Can be null
    * @param inBraces  if coloring requires that a keyword is inside or
    * outside a region in braces
    */
   public void keywordsBlue(String[] keys, char[] nonWordStart,
         boolean inBraces) {

      for (String s : keys) {
         key(s, Attributes.BLUE_PLAIN, nonWordStart, inBraces);
      }
   }

   /**
    * Searches variables that start with one of the characters in
    * <code>startChars</code> and end with one of the characters
    * in <code>endChars</code>. The variables are colored in purple
    *
    * @param startChars  the array start characters
    * @param endChars  the array of end characters
    */
   public void signedVariables(char[] startChars, char[] endChars) {
      for (char c : startChars) {
         signedVariable(c, endChars);
      }
   }

   /**
    * Searches variables that start with one of the characters in
    * <code>startChars</code> and end with one of the characters in
    * <code>endChars</code>. The boolean <code>inBraces</code>
    * indicates that the keyword must be found in region that follows
    * an opening brace (true) or is outside a region in braces (false).
    * The variables are colored in purple.
    *
    * @param startChars  the array start characters
    * @param endChars  the array of end characters
    * @param inBraces  if coloring requires that a keyword is inside or
    * outside a region in braces
    */
   public void signedVariables(char[] startChars, char[] endChars,
         boolean inBraces) {

      for (char c : startChars) {
         signedVariable(c, endChars, inBraces);
      }
   }

   /**
    * Searches and colors html tags with attributes. Tags are colored in
    * blue, atrributes in red and attribute values in purple
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
    * Colors sections embedded in a html document by setting a temporary
    * <code>Colorable</code> (for a script or css). The specified strings
    * mark the start and end tags for the embedded sections
    *
    * @param colTemp  the {@link Colorable}
    * @param startTag  the start tag
    * @param endTag  the end tag
    */
   public void embedInHtml(Colorable colTemp, String startTag, String endTag) {
      Colorable curr = colorable;
      setColorable(colTemp);
      embedInHtml(startTag, endTag);
      setColorable(curr);
   }

   /**
    * Searches braces for coloring in gray and displaying in bold
    */
   public void bracesGray() {
      key("{", Attributes.GRAY_BOLD, false);
      key("}", Attributes.GRAY_BOLD, false);
   }

   /**
    * Searches brackets for coloring in blue and displaying in bold
    */
   public void bracketsBlue() {
      key("(", Attributes.BLUE_BOLD, false);
      key(")", Attributes.BLUE_BOLD, false);
   }

   /**
    * Searches quoted text for coloring in orange. The quote mark is ignored
    * if a backslash precedes it. This method must not be used for html and must
    * be called after the methods that search for keywords, braces and brackets.
    */
   public void quotedText() {
      quotedLineWise(false);
   }

   /**
    * Searches line comments for coloring in green
    *
    * @param lineCmnt  the string that marks the start of a line comment
    * @param notCmntStart  the array of characters that disable coloring
    * if these precede <code>lineCmt</code>. Can be null.
    */
   public void lineComments(String lineCmnt, char[] notCmntStart) {
      lineCommentsImpl(lineCmnt, notCmntStart);
   }

   /**
    * Searches block comments for coloring in green
    *
    * @param blockStart  the start of a block
    * @param blockEnd  the end of a block
    */
   public void blockComments(String blockStart, String blockEnd) {
      blockCommentsImpl(blockStart, blockEnd);
   }

   /**
    * Returns the boolean that indicates if this position where a change
    * happened is found in a block of text that is delimited by the
    * specified strings <code>blockStart</code> and <code>blockEnd</code>
    *
    * @param blockStart  the block start
    * @param blockEnd  the block end
    * @return  the boolean value
    */
   public boolean isInBlock(String blockStart, String blockEnd) {
      return isInBlock(blockStart, blockEnd, pos);
   }

   //
   //--private--/
   //

   private void key(String key, SimpleAttributeSet set, boolean reqWord) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(key, start);
         if (start != -1) {
            boolean ok = !reqWord
                  || SyntaxUtils.isWord(toColor, start, key.length(), null);

            if (ok) {
               setCharAttr(start + posStart, key.length(), set);
            }
            start += key.length();
         }
      }
   }

   private void key(String key, SimpleAttributeSet set, char[] nonWordStart,
         boolean inBraces) {

      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(key, start);
         if (start != -1) {
            int absStart = start + posStart;
            int lastBlockStart
                  = SyntaxUtils.lastBlockStart(text, absStart, "{", "}");

            boolean ok = ((inBraces && lastBlockStart != -1)
                  || (!inBraces && lastBlockStart == -1))
                  && SyntaxUtils.isWord(toColor, start, key.length(), nonWordStart);

            if (ok) {
               setCharAttr(start + posStart, key.length(), set);
            }
            start += key.length();
         }
      }
   }      

   private void signedVariable(char sign, char[] endChars) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length;
         if (start != -1) {
            if (SyntaxUtils.isWordStart(toColor, start, null)) {
               length = SyntaxUtils.wordLength(toColor, start, endChars);
               setCharAttr(start + posStart, length, Attributes.PURPLE_PLAIN);
               start += length;
            }
            else {
               start++;
            }
         }
      }
   }

   private void signedVariable(char sign, char[] endChars,
         boolean inBraces) {

      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(sign, start);
         int length;
         if (start != -1) {
            int absStart = start + posStart;
            int lastBlockStart
                  = SyntaxUtils.lastBlockStart(text, absStart, "{", "}");

            boolean ok = ((inBraces && lastBlockStart != -1)
                  || (!inBraces && lastBlockStart == -1))
                  && SyntaxUtils.isWordStart(toColor, start, null);

            if (ok) {
               length = SyntaxUtils.wordLength(toColor, start, endChars);
               setCharAttr(absStart, length, Attributes.PURPLE_PLAIN);
               start += length;
            }
            else {
               start++;
            }
         }
      }
   }

   private void htmlTag(String tag, String[] attributes) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(tag, start);
         if (start != -1) {
            boolean isStartTag = start > 0 && '<' == toColor.charAt(start - 1);
            int endPos = start + tag.length();
            if (isStartTag) {
               if (toColor.length() > endPos && ' ' == toColor.charAt(endPos)) {
                  for (String s : attributes) {
                      htmlAttribute(s);
                  }
                  quotedLineWise(true);
               }
            }
            boolean isEndTag = !isStartTag && start > 1
                  && '/' == toColor.charAt(start - 1)
                  && '<' == toColor.charAt(start - 2);

            if (SyntaxUtils.isWordEnd(toColor, endPos) && (isStartTag || isEndTag)) {
               setCharAttr(start + posStart, tag.length(), Attributes.BLUE_BOLD);
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
            int lastTagEndStart = text.lastIndexOf("</", absStart);
            int endPos = start + keyword.length();
            int before = toColor.charAt(start - 1);
            int after = '\0';
            if (endPos < toColor.length()) {
               after = toColor.charAt(endPos);
            }
            boolean ok = lastTagStart != -1 && lastTagStart > lastTagEndStart
                  && (' ' == before || '"' == before || '\'' == before)
                  && ('\0' == after || ' ' == after || '=' == after || '>' == after);

            if (ok) {
               setCharAttr(absStart, keyword.length(), Attributes.RED_PLAIN);
            }
            start += keyword.length();
         }
      }
   }

   private void embedInHtml(String startTag, String endTag) {
      int start = 0;
      int length;
      int end;
      while (start != -1) {
         start = text.indexOf(startTag, start);
         if (start != -1) {
            length = 0;
            end = SyntaxUtils.nextBlockEnd(text, start + 1, startTag, endTag);
            if (end != -1) {
               innerStart = SyntaxUtils.nextBlockEnd(text, start + 1, "<", ">") ;
               innerEnd = end;
               if (innerStart != -1 ) {
                  String section = text.substring(innerStart, end);
                  color(text, section, pos, innerStart);
                  length = section.length();
               }
            }
            start += length + 1;
         }
      }
      innerStart = 0;
      innerEnd = 0;
   }

   private void quotedLineWise(boolean isHtml) {
      if (isMultiline) {
         //
         // split because string literals are not colored across lines
         String[] chunkArr = toColor.split("\n");
         int sum = 0;
         for (String s : chunkArr) {
            quoted(s, posStart + sum, SyntaxUtils.DOUBLE_QUOTE, isHtml);
            quoted(s, posStart + sum, SyntaxUtils.SINGLE_QUOTE, isHtml);
            sum += s.length() + 1;
         }
      }
      else {
         quoted(toColor, posStart, SyntaxUtils.DOUBLE_QUOTE, isHtml);
         quoted(toColor, posStart, SyntaxUtils.SINGLE_QUOTE, isHtml);
      }
   }

   private void quoted(String toColor, int lineStart, char quoteMark,
         boolean isHtml) {

      final boolean isSingleQuote = quoteMark == SyntaxUtils.SINGLE_QUOTE;
      int start = 0;
      int end = 0;
      while (start != -1 && end != -1) {
         start = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start);
         if (start != -1) {
            boolean notQuoted = true; // double quotes outdo single quotes
            int length = 0;
            notQuoted = !isSingleQuote
                  || !SyntaxUtils.isInQuotes(toColor, start, SyntaxUtils.DOUBLE_QUOTE);

            end = SyntaxUtils.nextNotEscaped(toColor, quoteMark, start + 1);
            if (end != -1) {
               notQuoted = !isSingleQuote
                      || (notQuoted
                      && !SyntaxUtils.isInQuotes(toColor, end, SyntaxUtils.DOUBLE_QUOTE));

               if (notQuoted) {
                  length = end - start + 1;
                  int absStart = start + lineStart;
                  if (isHtml) {
                     int lastTagStart
                           = SyntaxUtils.lastBlockStart(text, absStart, "<", ">");
                     if (lastTagStart != -1) {
                        setCharAttr(absStart, length, Attributes.PURPLE_PLAIN);
                     }
                  }
                  else {
                     setCharAttr(absStart, length, Attributes.ORANGE_PLAIN);
                  }
               }
            }
            start += length + 1;
         }
      }
   }

   private void lineCommentsImpl(String lineCmnt, char[] notCmntStart) {
      final boolean useNotCmnt = notCmntStart != null;
      int start = 0;
      while (start != -1) {
         boolean ok = true;
         int length = 0;
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (start > 0 && useNotCmnt) {
               char c = toColor.charAt(start - 1);
               for (char not : notCmntStart) {
                  ok = c != not;
                  if (!ok) {
                     break;
                  }
               }
            }
            if (ok && !isPositionInQuotes(start)) {
               int lineEnd = toColor.indexOf("\n", start + 1);
               if (lineEnd != -1) {
                  length = lineEnd - start;
               }
               else {
                  length = toColor.length() - start;
               }
               setCharAttr(start + posStart, length, Attributes.GREEN_PLAIN);
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
      int end;
      while (start != -1) {
         start = text.indexOf(blockStart, start);
         int length = 0;
         if (innerEnd > 0 && start >= innerEnd - blockStart.length()) {
            start = -1;
         }
         if (start != -1) {
           if (!SyntaxUtils.isBorderedByQuotes(text, start, blockStart.length())) {
               end = SyntaxUtils.nextBlockEnd(text, start + 1, blockStart, blockEnd);
               if (innerEnd > 0 && end >= innerEnd - blockEnd.length()) {
                  end = -1;
               }
               if (end != -1) {
                  length = end - start + blockEnd.length();
                  setCharAttr(start, length, Attributes.GREEN_PLAIN);
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
         int firstEnd = SyntaxUtils.nextBlockEnd(text, innerStart, blockStart, blockEnd);
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
         int lastStart = SyntaxUtils.lastBlockStart(text, endPos, blockStart, blockEnd);
         int nextEnd = SyntaxUtils.nextBlockEnd(text, endPos, blockStart, blockEnd);
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
         while (nextStart != -1 && SyntaxUtils.isBorderedByQuotes(text, nextStart,
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
         color(text, section, pos, pos);
         isBlockCmnt = true;
      }
   }

   private boolean isInBlock(String blockStart, String blockEnd, int pos) {
      int lastStart = SyntaxUtils.lastBlockStart(text, pos, blockStart, blockEnd);
      int nextEnd = SyntaxUtils.nextBlockEnd(text, pos, blockStart, blockEnd);
      return lastStart != -1 & nextEnd != -1;
   }

   private boolean isPositionInQuotes(int pos) {
      String line;
      int relStart;
      if (isMultiline) {
         line = LinesFinder.lineAtPos(toColor, pos);
         relStart = pos - LinesFinder.lastNewline(toColor, pos);
      }
      else {
         line = toColor;
         relStart = pos;
      }
      return SyntaxUtils.isInQuotes(line, relStart, SyntaxUtils.DOUBLE_QUOTE)
            || SyntaxUtils.isInQuotes(line, relStart, SyntaxUtils.SINGLE_QUOTE);
   }

   private void setCharAttr(int start, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(start, length, set, false);
   }

   private void setAllCharAttrBlack() {
      setCharAttr(0, doc.getLength(), normalSet);
   }
}
