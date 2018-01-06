package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--/
import eg.Languages;
import eg.utils.LinesFinder;
import eg.document.TextDocument;

/**
 * The highlighting of text elements in the text contained in
 * <code>TextDocument</code>.<br>
 */
public class SyntaxHighlighter {

   private final SyntaxSearcher searcher = new SyntaxSearcher();
   private final TextDocument textDoc;

   private Highlighter hl;
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
    * @param textDoc  the reference to {@link TextDocument}
    */
   public SyntaxHighlighter(TextDocument textDoc) {
      this.textDoc = textDoc;
   }

   /**
    * Selects a <code>Highlighter</code> based on the language
    *
    * @param lang  a language in {@link Languages}
    */
   public void selectHighlighter(Languages lang) {
      Highlighter hl = null;
      switch(lang) {
         case JAVA:
            hl = new JavaHighlighter();
            break;
         case HTML:
            hl = new HTMLHighlighter();
            break;
         case JAVASCRIPT:
            hl = new JavascriptHighlighter();
            break;
         case CSS:
            hl = new CSSHighlighter();
            break;
         case PERL:
            hl = new PerlHighlighter();
            break;
         default:
            if (this.hl != null) {
               textDoc.setAllCharAttrBlack();
            }
      }
      this.hl = hl;
   }

   /**
    * Highlights text using the assigned <code>Highlighter</code>
    *
    * @param text  the entire text in the document
    * @param section  the section to be highlighted. This may be a
    * single line or as well a multiline section. Equals text to
    * highlight the entire document
    * @param pos  the position where a change happened. 0 if the entire
    * document is highlighted
    * @param posStart  the position where section starts
    */
   public void highlight(String text, String section, int pos, int posStart) {
      this.text = text;
      this.section = section;
      this.pos = pos;
      this.posStart = posStart;
      isTypeMode = text.length() > section.length();
      isMultiline = LinesFinder.isMultiline(section);
      hl.highlight(searcher);
   }

   /**
    * The search of text elements for highlighting.<br>
    * Is created in outer class and has no public constructor
    */
   public class SyntaxSearcher {

      private SyntaxSearcher() {}

      /**
       * Sets the section of text that is to be highlighted to
       * black and plain
       */
      public void setCharAttrBlack() {
         textDoc.setCharAttrBlack(posStart, section.length());
      }

     /**
       * Highlights keywords. Highlighting may require that keywords are
       * whole words.
       *
       * @param keys  the array of keywords
       * @param reqWord  the boolean that, if true, indicates that keywords
       * must be words
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void keywords(String[] keys, boolean reqWord, SimpleAttributeSet set) {
         for (String s : keys) {
            key(s, set, reqWord);
         }
      }

     /**
       * Highlights keywords. Highlighting requires that keywords are whole
       * words and that an opening brace is or is rather not found somewhere
       * in front of keywords
       *
       * @param keys  the array of keywords
       * @param nonWordStart  the array of characters that do not precede
       * a highlighted keyword in addition to letters and digits. Can be null
       * @param inBraces  the boolean value that indicates if an opening
       * brace must (true) or must not (false) be found somewhere in front
       * of keywords
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void keywords(String[] keys, char[] nonWordStart, boolean inBraces,
            SimpleAttributeSet set) {

         for (String s : keys) {
            key(s, set, nonWordStart, inBraces);
         }
      }

      /**
       * Highlights variables that start with one of the characters in
       * <code>startChars</code> and end with one of the characters in
       * <code>endChars</code>
       *
       * @param startChars  the array start characters
       * @param endChars  the array of end characters
       * @param set  the <code>SimpleAttributeSet</code> set on
       * the keywords
       */
      public void signedVariables(char[] startChars, char[] endChars,
            SimpleAttributeSet set) {

         for (char c : startChars) {
            signedVariable(c, endChars);
         }
      }

      /**
       * Highlights variables that start with one of the characters in
       * <code>startChars</code> and end with one of the characters in
       * <code>endChars</code>. Highlighting requires that an opening
       * brace is or is rather not found somewhere in front of variables
       *
       * @param startChars  the array start characters
       * @param endChars  the array of end characters
       * @param inBraces  the boolean value that indicates if an opening
       * brace must (true) or must not (false) be found somewhere in front
       * of variables
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void signedVariables(char[] startChars, char[] endChars,
            boolean inBraces, SimpleAttributeSet set) {

         for (char c : startChars) {
            signedVariable(c, endChars, inBraces);
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
       * Highlights sections embedded in an html document by setting a
       * temporary <code>Highlighter</code> (for javascript or css).
       * The strings mark the start and end tags of embedded sections
       *
       * @param hlTemp  the {@link Highlighter}
       * @param startTag  the start tag
       * @param endTag  the end tag
       */
      public void embedInHtml(Highlighter hlTemp, String startTag, String endTag) {
         Highlighter curr = hl;
         hl = hlTemp;
         embedInHtml(startTag, endTag);
         hl = curr;
      }

      /**
       * Highlights braces in gray and bold
       */
      public void bracesGray() {
         key("{", Attributes.GRAY_BOLD, false);
         key("}", Attributes.GRAY_BOLD, false);
      }

      /**
       * Highlights brackets in blue and bold
       */
      public void bracketsBlue() {
         key("(", Attributes.BLUE_BOLD, false);
         key(")", Attributes.BLUE_BOLD, false);
      }

      /**
       * Highlights text quoted with single or double quotation marks
       * in orange.<br>
       * This method must not be used for html and must be called after the
       * methods that search for keywords, braces and brackets.
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
       * Highlighs block comments in green
       *
       * @param blockCmntStart  the string that marks the start of comments
       * @param blockCmntEnd  the string that marks the end of comments
       */
      public void blockComments(String blockCmntStart, String blockCmntEnd) {
         blockCommentsImpl(blockCmntStart, blockCmntEnd);
      }

      /**
       * Returns the boolean that indicates if this position where a change
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

      private void key(String key, SimpleAttributeSet set, boolean reqWord) {
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

      private void key(String key, SimpleAttributeSet set, char[] nonWordStart,
            boolean inBraces) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(key, start);
            if (start != -1) {
               int absStart = start + posStart;
               int lastBlockStart
                     = SyntaxUtils.lastBlockStart(text, absStart, "{", "}");

               boolean ok = ((inBraces && lastBlockStart != -1)
                     || (!inBraces && lastBlockStart == -1))
                     && SyntaxUtils.isWord(section, start, key.length(), nonWordStart);

               if (ok) {
                  textDoc.setCharAttr(start + posStart, key.length(), set);
               }
               start += key.length();
            }
         }
      }

      private void signedVariable(char sign, char[] endChars) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(sign, start);
            int length;
            if (start != -1) {
               if (SyntaxUtils.isWordStart(section, start, null)) {
                  length = SyntaxUtils.wordLength(section, start, endChars);
                  textDoc.setCharAttr(start + posStart, length, Attributes.PURPLE_PLAIN);
                  start += length;
               }
               else {
                  start++;
               }
            }
         }
      }

      private void signedVariable(char sign, char[] endChars, boolean inBraces) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(sign, start);
            int length;
            if (start != -1) {
               int absStart = start + posStart;
               int lastBlockStart
                     = SyntaxUtils.lastBlockStart(text, absStart, "{", "}");

               boolean ok = ((inBraces && lastBlockStart != -1)
                     || (!inBraces && lastBlockStart == -1));

               if (ok) {
                  length = SyntaxUtils.wordLength(section, start, endChars);
                  textDoc.setCharAttr(absStart, length, Attributes.BLUE_PLAIN);
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
                  if (section.length() > endPos && ' ' == section.charAt(endPos)) {
                     for (String s : attributes) {
                         htmlAttribute(s);
                     }
                     quotedLineWise(true);
                  }
               }
               boolean isEndTag = !isStartTag && start > 1
                     && '/' == section.charAt(start - 1)
                     && '<' == section.charAt(start - 2);

               if (SyntaxUtils.isWordEnd(section, endPos) && (isStartTag || isEndTag)) {
                  textDoc.setCharAttr(start + posStart, tag.length(), Attributes.BLUE_BOLD);
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
               int lastTagStart = SyntaxUtils.lastBlockStart(text, absStart, "<", ">");
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

      private void embedInHtml(String startTag, String endTag) {
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
                     highlight(text, section, pos, innerStart);
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
         removedFirstBlockCmntStart(blockCmntStart, blockCmntEnd);
         int start = innerStart;
         int end;
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
                     removedBlockCmntStart(end + blockCmntEnd.length(),
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

      private void removedFirstBlockCmntStart(String blockCmntStart,
            String blockCmntEnd) {

         if (!isTypeMode) {
            return;
         }
         int firstEnd = SyntaxUtils.nextBlockEnd(text, innerStart,
               blockCmntStart, blockCmntEnd);

         if (innerEnd > 0 && firstEnd > innerEnd) {
            firstEnd = -1;
         }
         if (firstEnd != -1) {
            String toUncomment = text.substring(innerStart, firstEnd + 2);
            uncommentBlock(toUncomment, innerStart);
         }
      }

      private void removedBlockCmntStart(int endPos, String blockCmntStart,
            String blockCmntEnd) {

         if (!isTypeMode) {
            return;
         }
         int lastStart = SyntaxUtils.lastBlockStart(text, endPos,
               blockCmntStart, blockCmntEnd);

         int nextEnd = SyntaxUtils.nextBlockEnd(text, endPos,
               blockCmntStart, blockCmntEnd);

         if (innerEnd > 0 && nextEnd > innerEnd) {
            nextEnd = -1;
         }
         if (nextEnd != -1 && lastStart == -1) {
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
   }
}
