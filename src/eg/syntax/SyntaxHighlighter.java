package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--
import eg.utils.LinesFinder;
import eg.document.TextDocument;

/**
 * The highlighting of text elements in the text contained in
 * <code>TextDocument</code>.<br>
 * Class requires a {@link Highlighter} which calls (selected) methods
 * in this object of {@link SyntaxHighlighter.SyntaxSearcher}.
 */
public class SyntaxHighlighter {

   private final SyntaxSearcher searcher;
   private Highlighter hl;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    */
   public SyntaxHighlighter(TextDocument textDoc) {
      searcher = new SyntaxSearcher(textDoc);
   }

   /**
    * Sets a <code>Highlighter</code>
    *
    * @param hl  the {@link Highlighter}
    * @throws IllegalArgumentException  if <code>hl</code> is null
    */
   public void setHighlighter(Highlighter hl) {
      if (hl == null) {
         throw new IllegalArgumentException("The Highlighter reference is null");
      }
      this.hl = hl;
   }

   /**
    * Highlights text
    *
    * @param text  the entire text in the document
    * @param section  the section to be highlighted. Null or equal to
    * text to highlight the entire text
    * @param pos  the position where a change happened. 0 if the entire
    * text is highlighted
    * @param posStart  the position where section starts
    * @throws NullPointerException  if no {@link Highlighter} is set
    */
   public void highlight(String text, String section, int pos, int posStart) {
      searcher.setTextParams(text, section, pos, posStart);
      hl.highlight(searcher);
   }

   /**
    * The search of text elements for setting character attributes in
    * <code>TextDocument</code>.<br>
    * Class is created in the enclosing class and has no public constructor.
    */
   public class SyntaxSearcher {

      private final TextDocument textDoc;

      private String text = "";
      private String section = "";
      private int pos;
      private int posStart;
      private boolean isTypeMode = false;
      private boolean isMultiline = true;
      private boolean isHighlightBlockCmnt = true;
      private int innerStart = 0;
      private int innerEnd = 0;

      /**
       * Sets the section of text that is to be highlighted to
       * black and plain
       */
      public void setCharAttrBlack() {
         textDoc.setCharAttrBlack(posStart, section.length());
      }

     /**
       * Highlights keywords
       *
       * @param keys  the array of keywords
       * @param reqWord  the boolean that, if true, indicates that keywords
       * must be whole words
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void keywords(String[] keys, boolean reqWord, SimpleAttributeSet set) {
         for (String s : keys) {
            key(s, reqWord, set);
         }
      }

      /**
       * Highlights keywords if these are whole words and if an opening
       * brace is or is rather not found ahead of the keywords, depending
       * on the specfied <code>openingBrace</code>
       *
       * @param keys  the array of keywords
       * @param nonWordStart  the array of characters that the keyword must
       * not be preceded with, in addition to digits and letters. Can be null
       * @param openingBrace  true if an opening brace must be found ahead of
       * keywords, false if rather no opening brace or a closing brace must be
       * found
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void keywords(String[] keys, char[] nonWordStart, boolean openingBrace,
            SimpleAttributeSet set) {

         for (String s : keys) {
            key(s, nonWordStart, openingBrace, set);
         }
      }

      /**
       * Highlights an extensible keyword if this is a whole word and if
       * an opening brace is or is rather not found ahead of the keyword,
       * depending on the specfied <code>openingBrace</code>
       *
       * @param keyBase  the base keyword
       * @param keyExtensions  the array of strings that may extend the keyword
       * @param nonWordStart  the array of characters that the keyword must not
       * be preceded with, in addition to digits and letters. Can be null
       * @param openingBrace  true if an opening brace must be found ahead of
       * keywords, false if rather no opening brace or a closing brace must be
       * found
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void extensibleKeyword(String keyBase, String[] keyExtensions,
            char[] nonWordStart, boolean openingBrace, SimpleAttributeSet set) {

         key(keyBase, keyExtensions, nonWordStart, openingBrace, set);
      }

      /**
       * Highlights variables that start with one of the characters in
       * <code>startChars</code> and end with one of the characters in
       * <code>endChars</code>
       *
       * @param startChars  the array start characters
       * @param endChars  the array of end characters
       * @param set  the <code>SimpleAttributeSet</code> set on
       * the variables
       */
      public void signedVariables(char[] startChars, char[] endChars,
            SimpleAttributeSet set) {

         for (char c : startChars) {
            signedVariable(c, endChars, set);
         }
      }

      /**
       * Highlights variables that start with one of the characters in
       * <code>startChars</code> and end with one of the characters in
       * <code>endChars</code> and if an opening brace is or is rather
       * not found ahead of the keywords, depending on the specfied
       * <code>openingBrace</code>
       *
       * @param startChars  the array start characters
       * @param endChars  the array of end characters
       * @param openingBrace  true if an opening brace must be found ahead of
       * keywords, false if rather no opening brace or a closing brace must be
       * found
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * variables
       */
      public void signedVariables(char[] startChars, char[] endChars,
            boolean openingBrace, SimpleAttributeSet set) {

         for (char c : startChars) {
            signedVariable(c, endChars, openingBrace, set);
         }
      }

      /**
       * Highlights html tags with attributes. Tags are shown in blue
       * and bold, attributes in red and attribute values in purple
       *
       * @param tags  the array of html tags
       * @param attributes  the array of html attributes
       */
      public void htmlTags(String[] tags,  String[] attributes) {
         for (String s : tags) {
            htmlTag(s, attributes);
         }
      }

      /**
       * Highlights sections embedded in html code (Javascript or CSS)
       *
       * @param startTag  the start tag of embedded sections
       * @param endTag  the end tag of embedded sections
       * @param hlSection  the {@link Highlighter} for embedded sections
       */
      public void embeddedInHtml(String startTag, String endTag, Highlighter hlSection) {
         embedInHtmlImpl(startTag, endTag, hlSection);
      }

      /**
       * Highlights braces in gray and bold
       */
      public void bracesGray() {
         key("{", false, Attributes.GRAY_BOLD);
         key("}", false, Attributes.GRAY_BOLD);
      }

      /**
       * Highlights brackets in blue and bold
       */
      public void bracketsBlue() {
         key("(", false, Attributes.BLUE_BOLD);
         key(")", false, Attributes.BLUE_BOLD);
      }

      /**
       * Highlights text quoted with single or double quotation marks
       * in orange
       */
      public void quotedText() {
         quotedLineWise(false);
      }

      /**
       * Highlights line comments in green
       *
       * @param lineCmntStart  the string that marks the start of a line
       * comment
       * @param nonCmntStart  the array of characters that disable
       * highlighting if one of these precede <code>lineCmtStart</code>.
       * Can be null.
       */
      public void lineComments(String lineCmntStart, char[] nonCmntStart) {
         lineCommentsImpl(lineCmntStart, nonCmntStart);
      }

      /**
       * Highlights block comments in green
       *
       * @param blockCmntStart  the string that marks the start of comments
       * @param blockCmntEnd  the string that marks the end of comments
       */
      public void blockComments(String blockCmntStart, String blockCmntEnd) {
         blockCommentsImpl(blockCmntStart, blockCmntEnd);
      }

      /**
       * Returns the boolean that is true if this position where a change
       * happened is found in a block of text that is delimited by the
       * specified strings
       *
       * @param blockStart  the block start
       * @param blockEnd  the block end
       * @return  the boolean value
       */
      public boolean isInBlock(String blockStart, String blockEnd) {
         return isInBlock(blockStart, blockEnd, pos);
      }

      //
      //--private--
      //

      private void key(String key, boolean reqWord, SimpleAttributeSet set) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(key, start);
            if (start != -1) {
               boolean ok = !reqWord
                     || SyntaxUtils.isWord(section, start, key.length(), null);

               if (ok) {
                  textDoc.setCharAttr(start + posStart, key.length(), set);
               }
               start += key.length();
            }
         }
      }

      private void key(String key, char[] nonWordStart, boolean inBraces,
            SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(key, start);
            if (start != -1) {
               int absStart = start + posStart;
               boolean ok = SyntaxUtils.testLastBrace(text, absStart, inBraces)
                     && SyntaxUtils.isWord(section, start, key.length(),
                           nonWordStart);

               if (ok) {
                  textDoc.setCharAttr(absStart, key.length(), set);
               }
               start += key.length();
            }
         }
      }

      private void key(String keyBase, String[] keyExtensions, char[] nonWordStart,
            boolean inBraces, SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(keyBase, start);
            int length = keyBase.length();
            if (start != -1) {
               int absStart = start + posStart;
               boolean ok = SyntaxUtils.testLastBrace(text, absStart, inBraces)
                     && SyntaxUtils.isWord(section, start, keyBase.length(),
                           nonWordStart);

               if (ok) {
                  length += keyExtension(keyExtensions, start + keyBase.length(), set);
                  textDoc.setCharAttr(absStart, length, set);
               }
               start += length;
            }
         }
      }
      
      private int keyExtension(String[] keyExtensions, int extStart,
            SimpleAttributeSet set) {

         int length = 0;
         for (String s : keyExtensions) {
            boolean found = SyntaxUtils.isWordEnd(section, extStart + s.length())
                  && extStart == section.indexOf(s, extStart);

            if (found) {
               length = s.length();
               break;
            }
         }
         return length;
      }
               
      private void signedVariable(char sign, char[] endChars,
            SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(sign, start);
            int length;
            if (start != -1) {
               if (SyntaxUtils.isWordStart(section, start, null)) {
                  length = SyntaxUtils.wordLength(section, start, endChars);
                  textDoc.setCharAttr(start + posStart, length, set);
                  start += length;
               }
               else {
                  start++;
               }
            }
         }
      }

      private void signedVariable(char sign, char[] endChars, boolean inBraces,
            SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(sign, start);
            int length;
            if (start != -1) {
               int absStart = start + posStart;
               boolean ok = SyntaxUtils.testLastBrace(text, absStart, inBraces);
               if (ok) {
                  length = SyntaxUtils.wordLength(section, start, endChars);
                  textDoc.setCharAttr(absStart, length, set);
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
            start = section.toLowerCase().indexOf(tag, start);
            if (start != -1) {
               boolean isStartTag = start > 0 && '<' == section.charAt(start - 1);
               int endPos = start + tag.length();
               if (isStartTag) {
                  boolean applyAttributes = section.length() > endPos
                        && ' ' == section.charAt(endPos);

                  if (applyAttributes) {
                     for (String s : attributes) {
                         htmlAttribute(s);
                     }
                     quotedLineWise(true);
                  }
               }
               boolean isEndTag = !isStartTag && start > 1
                     && '/' == section.charAt(start - 1)
                     && '<' == section.charAt(start - 2);

               boolean ok = SyntaxUtils.isWordEnd(section, endPos)
                     && (isStartTag || isEndTag);

               if (ok) {
                  textDoc.setCharAttr(start + posStart, tag.length(),
                        Attributes.BLUE_BOLD);
               }
               start += tag.length();
            }
         }
      }

      private void htmlAttribute(String keyword) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(keyword, start);
            if (start != -1) {
               int absStart = start + posStart;
               int lastTagStart
                     = SyntaxUtils.lastBlockStart(text, absStart, "<", ">");
               int lastTagEndStart = text.lastIndexOf("</", absStart);
               int endPos = start + keyword.length();
               char before = section.charAt(start - 1);
               char after = '\0';
               if (endPos < section.length()) {
                  after = section.charAt(endPos);
               }
               boolean ok = lastTagStart != -1 && lastTagStart > lastTagEndStart
               && (' ' == before || '"' == before || '\'' == before)
               && ('\0' == after || ' ' == after || '=' == after || '>' == after);

               if (ok) {
                  textDoc.setCharAttr(absStart, keyword.length(), Attributes.RED_PLAIN);
               }
               start += keyword.length();
            }
         }
      }

      private void embedInHtmlImpl(String startTag, String endTag, Highlighter hlSection) {
         int start = 0;
         while (start != -1) {
            start = text.indexOf(startTag, start);
            if (start != -1) {
               int length = 0;
               int end = SyntaxUtils.nextBlockEnd(text, start + 1, startTag, endTag);
               if (end != -1) {
                  innerStart = SyntaxUtils.nextBlockEnd(text, start + 1, "<", ">") ;
                  innerEnd = end;
                  if (innerStart != -1 ) {
                     String section = text.substring(innerStart, end);
                     setTextParams(text, section, pos, innerStart);
                     hlSection.highlight(this);
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
            String[] chunkArr = section.split("\n");
            int sum = 0;
            for (String s : chunkArr) {
               quoted(s, posStart + sum, SyntaxUtils.DOUBLE_QUOTE, isHtml);
               quoted(s, posStart + sum, SyntaxUtils.SINGLE_QUOTE, isHtml);
               sum += s.length() + 1;
            }
         }
         else {
            quoted(section, posStart, SyntaxUtils.DOUBLE_QUOTE, isHtml);
            quoted(section, posStart, SyntaxUtils.SINGLE_QUOTE, isHtml);
         }
      }

      private void quoted(String section, int lineStart, char quoteMark,
            boolean isHtml) {

         final boolean isSingleQuote = quoteMark == SyntaxUtils.SINGLE_QUOTE;
         int start = 0;
         int end = 0;
         while (start != -1 && end != -1) {
            start = SyntaxUtils.nextNotEscaped(section, quoteMark, start);
            if (start != -1) {
               boolean notQuoted; // double quotes outdo single quotes
               int length = 0;
               notQuoted = !isSingleQuote
                     || !SyntaxUtils.isInQuotes(section, start, SyntaxUtils.DOUBLE_QUOTE);

               end = SyntaxUtils.nextNotEscaped(section, quoteMark, start + 1);
               if (end != -1) {
                  notQuoted = !isSingleQuote
                         || (notQuoted
                         && !SyntaxUtils.isInQuotes(section, end, SyntaxUtils.DOUBLE_QUOTE));

                  if (notQuoted) {
                     length = end - start + 1;
                     int absStart = start + lineStart;
                     if (isHtml) {
                        int lastTagStart
                              = SyntaxUtils.lastBlockStart(text, absStart, "<", ">");
                        if (lastTagStart != -1) {
                           textDoc.setCharAttr(absStart, length, Attributes.PURPLE_PLAIN);
                        }
                     }
                     else {
                        textDoc.setCharAttr(absStart, length, Attributes.ORANGE_PLAIN);
                     }
                  }
               }
               start += length + 1;
            }
         }
      }

      private void lineCommentsImpl(String lineCmntStart, char[] nonCmntStart) {
         final boolean useNonCmnt = nonCmntStart != null;
         int start = 0;
         while (start != -1) {
            boolean ok = true;
            int length = 0;
            start = section.indexOf(lineCmntStart, start);
            if (start != -1) {
               if (start > 0 && useNonCmnt) {
                  char c = section.charAt(start - 1);
                  for (char non : nonCmntStart) {
                     ok = c != non;
                     if (!ok) {
                        break;
                     }
                  }
               }
               if (ok && !isPositionInQuotes(start)) {
                  int lineEnd = section.indexOf("\n", start + 1);
                  if (lineEnd != -1) {
                     length = lineEnd - start;
                  }
                  else {
                     length = section.length() - start;
                  }
                  textDoc.setCharAttr(start + posStart, length,
                        Attributes.GREEN_PLAIN);
               }
               start += length + 1;
            }
         }
      }

      private void blockCommentsImpl(String blockCmntStart, String blockCmntEnd) {
         if (!isHighlightBlockCmnt) {
            return;
         }
         int start = innerStart;
         int end;
         removedBlockCmntStart(start, start, blockCmntStart, blockCmntEnd);
         while (start != -1) {
            start = text.indexOf(blockCmntStart, start);
            int length = 0;
            if (innerEnd > 0 && start >= innerEnd - blockCmntStart.length()) {
               start = -1;
            }
            if (start != -1) {
               if (!SyntaxUtils.isBorderedByQuotes(text, start,
                    blockCmntStart.length())) {

                  end = SyntaxUtils.nextBlockEnd(text, start + 1,
                        blockCmntStart, blockCmntEnd);

                  if (innerEnd > 0 && end >= innerEnd - blockCmntEnd.length()) {
                     end = -1;
                  }
                  if (end != -1) {
                     length = end - start + blockCmntEnd.length();
                     textDoc.setCharAttr(start, length, Attributes.GREEN_PLAIN);
                     removedBlockCmntStart(end + blockCmntEnd.length(), start,
                           blockCmntStart, blockCmntEnd);
                  }
                  else {
                     removedBlockCmntEnd(start, blockCmntStart);
                  }
               }
               start += length + 1;
            }
         }
      }

      private void removedBlockCmntStart(int endPos, int lastStart,
            String blockCmntStart, String blockCmntEnd) {

         if (!isTypeMode) {
            return;
         }
         int nextEnd = SyntaxUtils.nextBlockEnd(text, endPos, blockCmntStart,
               blockCmntEnd);

         if (innerEnd > 0 && nextEnd > innerEnd) {
             nextEnd = -1;
         }
         if (nextEnd != -1) {
            String toUncomment = text.substring(endPos,
                  nextEnd + blockCmntEnd.length());

            uncommentBlock(toUncomment, endPos);
         }
      }

      private void removedBlockCmntEnd(int startPos, String blockCmntStart) {
          if (!isTypeMode) {
            return;
         }
         int nextStart = text.indexOf(blockCmntStart, startPos + 1);
         while (nextStart != -1 && SyntaxUtils.isBorderedByQuotes(text,
               nextStart, blockCmntStart.length())) {

            nextStart = text.indexOf(blockCmntStart, nextStart + 1);
         }
         if (innerEnd > 0 && nextStart > innerEnd) {
            nextStart = -1;
         }
         String toUncomment;
         if (nextStart != -1) {
            toUncomment = text.substring(startPos, nextStart);
         }
         else {
            if (innerEnd > 0) {
               toUncomment = text.substring(startPos, innerEnd);
            }
            else {
               toUncomment = text.substring(startPos);
            }
         }
         uncommentBlock(toUncomment, startPos);
      }

      private void uncommentBlock(String section, int pos) {
         if (isTypeMode) {
            isHighlightBlockCmnt = false;
            highlight(text, section, pos, pos);
            isHighlightBlockCmnt = true;
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
            line = LinesFinder.lineAtPos(section, pos);
            relStart = pos - LinesFinder.lastNewline(section, pos);
         }
         else {
            line = section;
            relStart = pos;
         }
         return SyntaxUtils.isInQuotes(line, relStart, SyntaxUtils.DOUBLE_QUOTE)
               || SyntaxUtils.isInQuotes(line, relStart, SyntaxUtils.SINGLE_QUOTE);
      }

      private void setTextParams(String text, String section, int pos, int posStart) {
         this.text = text;
         if (section == null) {
            section = text;
         }
         this.section = section;
         this.pos = pos;
         this.posStart = posStart;
         isTypeMode = text.length() > section.length();
         isMultiline = LinesFinder.isMultiline(section);
      }

      private SyntaxSearcher(TextDocument textDoc) {
         this.textDoc = textDoc;
      }
   }
}
