package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.StyledText;

/**
 * The syntax highlighting in the txt.text() contained in
 * <code>StyledText</code>
 */
public class SyntaxHighlighter {

   private final StyledText txt;
   private final SyntaxSearcher searcher;
   private Highlighter hl;

   /**
    * @param txt  the reference to {@link StyledText}
    */
   public SyntaxHighlighter(StyledText txt) {
      this.txt = txt;
      searcher = new SyntaxSearcher();
   }

   /**
    * Sets a <code>Highlighter</code>
    *
    * @param hl  the {@link Highlighter}
    */
   public void setHighlighter(Highlighter hl) {
      if (hl == null) {
         throw new IllegalArgumentException("The Highlighter reference is null");
      }
      searcher.skipQuotedBlkCmntMarks = false;
      hl.setSyntaxSearcher(searcher);
      this.hl = hl;
   }

   /**
    * Highlights text elements in the entire text
    */
   public void highlight() {
      searcher.setTextParams(txt.text(), 0, 0);
      hl.highlight();
   }

   /**
    * Highlights text elements around the position where a change
    * happened. By default the highlighted section is the line that
    * contains <code>chgPos</code>
    *
    * @param chgPos  the position where a change happened
    */
   public void highlight(int chgPos) {
      int lineStart = LinesFinder.lastNewline(txt.text(), chgPos);
      String line = LinesFinder.line(txt.text(), lineStart);
      searcher.setTextParams(line, chgPos, lineStart + 1);
      hl.highlight();
   }

   /**
    * Highlights text elements in a text change that may be multiline
    *
    * @param change  the change to the txt.text()
    * @param chgPos  the position where the change starts
    */
   public void highlight(String change, int chgPos) {
      int linesStart = LinesFinder.lastNewline(txt.text(), chgPos);
      int length = chgPos - linesStart + change.length();
      String lines = LinesFinder.lines(txt.text(), linesStart, length);
      searcher.setTextParams(lines, chgPos, linesStart + 1);
      hl.highlight();
   }

   /**
    * The search and highlighting of text elements. Class is
    * created in the enclosing class and has no public constructor.
    */
   public class SyntaxSearcher {

      private String section = "";
      private int chgPos;
      private int scnStart;
      private boolean isTypeMode = false;
      private boolean isMultiline = true;
      private boolean isHighlightBlockCmnt = true;
      private int innerStart = 0;
      private int innerEnd = 0;
      private boolean skipQuotedBlkCmntMarks = false;
      private int condition = 0;

      /**
       * Sets the condition for testing if a found txt.text() element is
       * valid. The condition is passed to {@link Highlighter#isValid}.
       *
       * @param condition  a freely chosen integer. Default is 0
       */
      public void setCondition(int condition) {
         this.condition = condition;
      }

     /**
      * Modifies the default section to simply the entire txt.text()
      */
      public void setEntireText() {
         scnStart = 0;
         section = txt.text();
      }

      /**
       * Modifies the default section such that it extends to the next
       * unquoted semicolon. The search for the next semicolon also
       * continues so long as {@link Highlighter#isValid} returns false.
       *
       * @param extendForQuoteMarks  the boolean value that is true to
       * extend the section to the region that contains quote marks around
       * the section
       */
      public void setToNextSemicolonSection(boolean extendForQuoteMarks) {
         if (isTypeMode && isHighlightBlockCmnt) {
            int searchStart = scnStart;
            int searchEnd = scnStart + section.length();
            int semicolon = SyntaxUtils.nextUnquoted(txt.text(), ";", searchEnd);
            while (semicolon != -1 && !isValid(semicolon)) {
               semicolon = SyntaxUtils.nextUnquoted(txt.text(), ";",
                     semicolon + 1);
            }
            if (semicolon != -1) {
               searchEnd = semicolon;
            }
            else {
               searchEnd = txt.text().length();
            }
            if (extendForQuoteMarks) {
               int firstQuoteMark = SyntaxUtils.firstQuoteMark(txt.text(),
                     scnStart);

               if (firstQuoteMark != -1) {
                  searchStart = LinesFinder.lastNewline(txt.text(),
                        firstQuoteMark) + 1;
               }
               if (searchEnd < txt.text().length()) {
                  int lastQuoteMark = SyntaxUtils.lastQuoteMark(txt.text(),
                        searchEnd);

                  if (lastQuoteMark != -1) {
                     searchEnd = lastQuoteMark;
                  }
               }
            }
            int end = LinesFinder.nextNewline(txt.text(), searchEnd);
            scnStart = searchStart;
            if (end != -1) {
               section = txt.text().substring(scnStart, end);
            }
            else {
               section = txt.text().substring(scnStart);
            }
         }
      }

      /**
       * Modifies the default section such that it corresponds to markup
       * language elements
       */
      public void setMarkupSection() {
         if (isTypeMode && isHighlightBlockCmnt) {
            int start = SyntaxUtils.lastBlockStart(txt.text(), chgPos, "<", ">",
                  false, false);

            if (start == -1) {
               start = txt.text().lastIndexOf(">", chgPos);
               if (start == -1) {
                  start = 0;
               }
            }
            int end;
            if (isMultiline) {
               end = htmlSectionEnd(scnStart + section.length() + 1);
            }
            else {
               end = htmlSectionEnd(chgPos + 1);
            }
            scnStart = start;
            section = txt.text().substring(start, end);
         }
      }

      /**
       * Resets the attributes in the section that is to be highlighted.
       * By default the section is the line where a change happened or the
       * lines that contain a possibly multiline insertion.
       */
      public void resetAttributes() {
         txt.resetAttributes(scnStart, section.length());
      }

      /**
       * Searches and highlights keywords.<br>(requires that
       * {@link Highlighter#isValid} returns true)
       *
       * @param keys  the array of keywords
       * @param reqWord  the boolean value that is true to require that
       * keywords are whole words
       * @param nonWordStart  the array of characters that the keyword
       * must not be preceded with. Can be null and is ignored if reqWord
       * is false
       * @param set  the SimpleAttributeSet set on the keywords
       */
      public void keywords(String[] keys, boolean reqWord, char[] nonWordStart,
            SimpleAttributeSet set) {

         for (String s : keys) {
            key(s, reqWord, nonWordStart, set);
         }
      }

      /**
       * Searches and highlights the keyword <code>base</code> which may
       * be extended by one of the keywords in <code>extensions</code>.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       *
       * @param base  the base keyword
       * @param extensions  the array of strings that may extend base
       * @param nonWordStart  the array of characters that the keyword
       * must not be preceded with. Can be null
       * @param set  the SimpleAttributeSet set on the keywords
       */
      public void extensibleKeyword(String base, String[] extensions,
            char[] nonWordStart, SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(base, start);
            int length = base.length();
            if (start != -1) {
               int absStart = start + scnStart;
               int endPos = start + base.length();
               boolean ok = SyntaxUtils.isWord(section, start, base.length(),
                        nonWordStart)
                     && isValid(absStart);

               if (ok) {
                  length += extensionLength(extensions, endPos);
                  txt.setAttributes(absStart, length, set);
               }
               start += length;
            }
         }
      }

      /**
       * Searches and highlights variables defined by start and end signs.
       * <br>(requires that {@link Highlighter#isValid} returns true)
       *
       * @param startChars  the array of start characters
       * @param endChars  the array of end characters
       * @param reqWord  the boolean value that is true to require that
       * the variables are whole words
       * @param set  the SimpleAttributeSet set on the variables
       */
      public void signedVariables(char[] startChars, char[] endChars,
             boolean reqWord, SimpleAttributeSet set) {

         for (char c : startChars) {
            signedVariable(c, endChars, reqWord, set);
         }
      }

      /**
       * Searches and highlights opening and closing braces in gray and
       * bold.<br>(requires that {@link Highlighter#isValid} returns true)
       */
      public void braces() {
         key("{", false, null, Attributes.GRAY_BOLD);
         key("}", false, null, Attributes.GRAY_BOLD);
      }

      /**
       * Searches and highlights opening and closing brackets in blue and
       * bold.<br>(requires that {@link Highlighter#isValid} returns true)
       */
      public void brackets() {
         key("(", false, null, Attributes.BLUE_BOLD);
         key(")", false, null, Attributes.BLUE_BOLD);
      }

      /**
       * Searches and highlights quoted txt.text() in single or double
       * quotation marks.<br>(requires that {@link Highlighter#isValid}
       * returns true)
       *
       * @param set  the SimpleAttributeSet set on quoted txt.text()
       */
      public void quoted(SimpleAttributeSet set) {
         quoted(section, scnStart, SyntaxConstants.DOUBLE_QUOTE, set);
         quoted(section, scnStart, SyntaxConstants.SINGLE_QUOTE, set);
      }

      /**
       * Searches and highlights quoted txt.text() in single or double quotation
       * marks in which a quotation must be found inside a line.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       *
       * @param set  the SimpleAttributeSet set on quoted txt.text()
       */
      public void quotedInLine(SimpleAttributeSet set) {
         if (isMultiline) {
            String[] chunkArr = section.split("\n");
            int sum = 0;
            for (String s : chunkArr) {
               quoted(s, scnStart + sum, SyntaxConstants.DOUBLE_QUOTE, set);
               quoted(s, scnStart + sum, SyntaxConstants.SINGLE_QUOTE, set);
               sum += s.length() + 1;
            }
         }
         else {
            quoted(section, scnStart, SyntaxConstants.DOUBLE_QUOTE, set);
            quoted(section, scnStart, SyntaxConstants.SINGLE_QUOTE, set);
         }
      }

      /**
       * Searches and highlights html elements. Tag names are shown
       * in blue and bold, attributes in red and attribute values, if
       * quoted, in purple.
       *
       * @param tags  the array of html tag names
       * @param attributes  the array of html attribute keywords
       */
      public void htmlElements(String[] tags,  String[] attributes) {
         for (String s : tags) {
            htmlElement(s, attributes);
         }
      }
      
      /**
       * Searches sections in an html document where txt.text() elements are
       * highlighted with a temporary <code>Highlighter</code> for CSS
       * or Javascript
       *
       * @param startTag  the start tag
       * @param endTag  the end tag
       * @param hlSection  the {@link Highlighter}
       */
      public void embeddedHtmlSections(String startTag, String endTag,
            Highlighter hlSection) {

         Highlighter hlCurr = hl;
         hl = hlSection;
         setHighlighter(hl);
         embeddedHtmlSections(startTag, endTag);
         hl = hlCurr;
         setHighlighter(hl);
      }

      /**
       * Searches and highlights XML elements. Tags are shown in
       * blue and bold, attributes in red and attribute values, if
       * quoted, in purple.
       */
      public void xmlElements() {
         int start = 0;
         while (start != -1) {
            start = section.indexOf('<', start);
            if (start != -1) {
               int length = 0;
               boolean isEndTag = section.length() > start + 2
                     && section.charAt(start + 1) == '/';
               
               int searchStart = start;
               if (isEndTag) {
                  searchStart++;
               }
               boolean isElName = false;
               if (section.length() > searchStart + 1) {
                  isElName = Character.isLetter(section.charAt(searchStart + 1))
                     || section.charAt(searchStart + 1) == '_';
               }
                  
               if (isElName) {
                  length = SyntaxUtils.wordLength(section, searchStart,
                        SyntaxConstants.XML_TAG_END_CHARS);
   
                  int endPos = searchStart + length;
                  boolean isAttr = section.length() > endPos
                           && (' ' == section.charAt(endPos)
                           || '\n' == section.charAt(endPos));
   
                  if (isAttr) {
                     int absStart = start + scnStart;
                     String el = txt.text().substring(absStart,
                           htmlSectionEnd(absStart + 1));
   
                      for (char c : SyntaxConstants.XML_ATTR_START_CHARS) {
                         xmlAttributes(c, SyntaxConstants.XML_ATTR_END_CHARS,
                               el, absStart);
                     }

                     quoted(el, absStart, SyntaxConstants.SINGLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                     quoted(el, absStart, SyntaxConstants.DOUBLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                  }
                  int colorStart = searchStart + scnStart + 1;
                  txt.setAttributes(colorStart, length - 1, Attributes.BLUE_BOLD);
               }
               start += length + 1;
            }
         }
      }

      /**
       * Searches and highlights line comments in green.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       *
       * @param lineCmntStart  the line comment start
       */
      public void lineComments(String lineCmntStart) {
         int start = 0;
         while (start != -1) {
            int length = 0;
            start = section.indexOf(lineCmntStart, start);
            if (start != -1) {
               int absStart = start + scnStart;
               if (!SyntaxUtils.isQuotedInLine(section, start)
                     && isValid(absStart)) {

                  int lineEnd = section.indexOf("\n", start);
                  if (lineEnd != -1) {
                     length = lineEnd - start;
                  }
                  else {
                     length = section.length() - start;
                  }
                  txt.setAttributes(absStart, length, Attributes.GREEN_PLAIN);
               }
               start += length + 1;
            }
         }
      }

      /**
       * Specifies that block comment marks that are quoted are skipped
       */
      public void setSkipQuotedBlkCmntMarks() {
         skipQuotedBlkCmntMarks = true;
      }

      /**
       * Returns if the position where a change happened is found inside
       * a block comment
       *
       * @param blockCmntStart  the block comment start
       * @param blockCmntEnd  the block comment end
       * @return  the boolean value that is true if inside a block
       * comment; false regardless of the position if the txt.text()
       * change is multiline
       */
      public boolean isInBlockCmnt(String blockCmntStart, String blockCmntEnd) {
         if (isMultiline) {
            return false;
         }
         else {
            return isInBlock(blockCmntStart, blockCmntEnd, chgPos);
         }
      }

      /**
       * Searches and highlights block comments in green
       *
       * @param blockCmntStart  the block comment start
       * @param blockCmntEnd  the block comment end
       */
      public void blockComments(String blockCmntStart, String blockCmntEnd) {
         if (!isHighlightBlockCmnt) {
            return;
         }
         removedBlockCmntStart(innerStart, blockCmntStart, blockCmntEnd);
         int start = innerStart;
         while (start != -1) {
            start = txt.text().indexOf(blockCmntStart, start);
            int length = 0;
            if (innerEnd > 0 && start >= innerEnd - blockCmntStart.length()) {
               start = -1;
            }
            if (start != -1) {
               if (!skipQuotedBlkCmntMarks
                     || !SyntaxUtils.isQuotedInLine(txt.text(), start)) {

                  int end = SyntaxUtils.nextBlockEnd(txt.text(), start + 1,
                        blockCmntStart, blockCmntEnd, skipQuotedBlkCmntMarks, true);

                  if (innerEnd > 0 && end >= innerEnd - blockCmntEnd.length()) {
                     end = -1;
                  }
                  if (end != -1) {
                     length = end - start + blockCmntEnd.length();
                     txt.setAttributes(start, length, Attributes.GREEN_PLAIN);
                     removedBlockCmntStart(end + blockCmntEnd.length(),
                           blockCmntStart, blockCmntEnd);
                  }
                  else {
                     removedBlockCmntEnd(start, blockCmntStart, blockCmntEnd);
                  }
               }
               start += length + 1;
            }
         }
      }

      //
      //--private--/
      //

      private void key(String key, boolean reqWord, char[] nonWordStart,
            SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(key, start);
            if (start != -1) {
               int absStart = start + scnStart;
               boolean ok = (!reqWord || SyntaxUtils.isWord(section, start,
                     key.length(), nonWordStart))
                     && isValid(absStart);

               if (ok) {
                  txt.setAttributes(absStart, key.length(), set);
               }
               start += key.length();
            }
         }
      }

      private void signedVariable(char sign, char[] endChars,
             boolean isWordStart, SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(sign, start);
            if (start != -1) {
               int absStart = start + scnStart;
               boolean ok = (!isWordStart
                        || SyntaxUtils.isWordStart(section, start, null))
                     && isValid(absStart);

               if (ok) {
                  int length = SyntaxUtils.wordLength(section, start, endChars);
                  txt.setAttributes(absStart, length, set);
                  start += length + 1;
               }
               else {
                  start++;
               }
            }
         }
      }

      private int extensionLength(String[] extensions, int extStart) {
         int length = 0;
         for (String s : extensions) {
            boolean found = extStart == section.indexOf(s, extStart)
                  && SyntaxUtils.isWordEnd(section, extStart + s.length());

            if (found) {
               if (s.length() > length) {
                  length = s.length();
               }
            }
         }
         return length;
      }

      private void htmlElement(String tag, String[] attributes) {
         int start = 0;
         while (start != -1) {
            start = section.toLowerCase().indexOf(tag, start);
            if (start != -1) {
               int absStart = start + scnStart;
               boolean isStartTag = start > 0 && '<' == section.charAt(start - 1);
               int endPos = start + tag.length();
               int length = tag.length();
               if (isStartTag) {
                  boolean isAttr = section.length() > endPos
                        && (' ' == section.charAt(endPos)
                        || '\n' == section.charAt(endPos));

                  if (isAttr) {
                     String el = txt.text().substring(absStart,
                           htmlSectionEnd(absStart));
                           
                     length = el.length();

                     for (String s : attributes) {
                         htmlAttributes(s, el, absStart);
                     }
                     quoted(el, absStart, SyntaxConstants.DOUBLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                     quoted(el, absStart, SyntaxConstants.SINGLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                  }
               }
               boolean isEndTag = !isStartTag && start > 1
                     && '/' == section.charAt(start - 1)
                     && '<' == section.charAt(start - 2);

               boolean ok = (isStartTag || isEndTag)
                     && SyntaxUtils.isWordEnd(section, endPos);

               if (ok) {
                  txt.setAttributes(absStart, tag.length(),
                        Attributes.BLUE_BOLD);
               }
               start += length;
            }
         }
      }

      private int htmlSectionEnd(int pos) {
         int end = SyntaxUtils.nextBlockEnd(txt.text(), pos, "<", ">", true, false);
         if (end == -1) {
            end = txt.text().indexOf("<", pos);
            if (end == -1) {
               end = txt.text().length();
            }
         }
         return end;
      }

      private void htmlAttributes(String keyword, String htmlEl, int elStart) {
         int start = 0;
         while (start != -1) {
            start = htmlEl.indexOf(keyword, start);
            if (start != -1) {
               char before = '\0';
               if (start > 0) {
                  before = htmlEl.charAt(start - 1);
               }
               int endPos = start + keyword.length();
               char after = '\0';
               if (endPos < htmlEl.length()) {
                  after = htmlEl.charAt(endPos);
               }
               int absStart = start + elStart;
               boolean ok = isValid(absStart)
                     && (' ' == before || '"' == before || '\'' == before
                        || '\n' == before)
                     && ('\0' == after || ' ' == after || '=' == after
                        || '>' == after || '\n' == after);

               if (ok) {
                  txt.setAttributes(absStart, keyword.length(),
                        Attributes.RED_PLAIN);
               }
               start += keyword.length();
            }
         }
      }

      private void xmlAttributes(char startChar, char[] endChars, String xmlEl,
            int elStart) {

         int start = 0;
         while (start != -1) {
            start = xmlEl.indexOf(startChar, start);
            if (start != -1) {
               int length;
               int absStart = start + elStart;
               length = SyntaxUtils.wordLength(xmlEl, start, endChars);
               txt.setAttributes(absStart + 1, length - 1, Attributes.RED_PLAIN);
               start += length;
            }
         }
      }

      private void embeddedHtmlSections(String startTag, String endTag) {
         int start = 0;
         while (start != -1) {
            start = txt.text().toLowerCase().indexOf(startTag, start);
            if (start != -1) {
               int length = 0;
               int end = SyntaxUtils.nextBlockEnd(txt.text().toLowerCase(),
                     start + 1, startTag, endTag, false, false);

               if (end != -1) {
                  innerStart = SyntaxUtils.nextBlockEnd(txt.text(), start + 1,
                        "<", ">", false, false);

                  if (innerStart != -1) {
                     innerEnd = end;
                     String innerScn = txt.text().substring(innerStart, end);
                     setTextParams(innerScn, chgPos, innerStart);
                     hl.highlight();
                     length = innerScn.length();
                  }
               }
               start += length + 1;
            }
         }
         innerStart = 0;
         innerEnd = 0;
      }

      private void quoted(String scn, int scnStart, char quoteMark,
            SimpleAttributeSet set) {

         final boolean isSingleQuote = quoteMark == SyntaxConstants.SINGLE_QUOTE;
         int start = 0;
         int end = 0;
         while (start != -1 && end != -1) {
            start = SyntaxUtils.nextNonEscaped(scn, quoteMark, start);
            int absStart = start + scnStart;
            if (start != -1) {
               int length = 0;
               boolean ok = (!isSingleQuote
                     || !SyntaxUtils.isQuoted(scn, start,
                           SyntaxConstants.DOUBLE_QUOTE))

                     && isValid(absStart);

               end = SyntaxUtils.nextNonEscaped(scn, quoteMark, start + 1);
               if (end != -1) {
                  ok = ok
                        && ((!isSingleQuote || !SyntaxUtils.isQuoted(scn, end,
                           SyntaxConstants.DOUBLE_QUOTE))

                        && isValid(end + scnStart));

                  if (ok) {
                     length = end - start + 1;
                     txt.setAttributes(absStart, length, set);
                  }
               }
               start += length + 1;
            }
         }
      }

      private void removedBlockCmntStart(int endPos, String blockCmntStart,
            String blockCmntEnd) {

         if (!isTypeMode) {
            return;
         }
         int nextEnd = SyntaxUtils.nextBlockEnd(txt.text(), endPos,
               blockCmntStart, blockCmntEnd, skipQuotedBlkCmntMarks, true);

         if (innerEnd > 0 && nextEnd > innerEnd) {
             nextEnd = -1;
         }
         if (nextEnd != -1) {
            int lastStart = SyntaxUtils.lastBlockStart(txt.text(), endPos,
                  blockCmntStart, blockCmntEnd, skipQuotedBlkCmntMarks, true);

            int lineStart;
            if (lastStart == -1) {
               lineStart = endPos;
            }
            else {
               lineStart = LinesFinder.lastNewline(txt.text(), nextEnd);
            }
            int end = nextEnd + blockCmntEnd.length();
            String toUncomment = txt.text().substring(lineStart, end);
            uncommentBlock(toUncomment, lineStart);
         }
      }

      private void removedBlockCmntEnd(int startPos, String blockCmntStart,
            String blockCmntEnd) {

         if (!isTypeMode) {
            return;
         }
         int nextStart = SyntaxUtils.nextBlockStart(txt.text(), startPos + 1,
               blockCmntStart, blockCmntEnd, skipQuotedBlkCmntMarks, true);

         if (innerEnd > 0 && nextStart > innerEnd) {
            nextStart = -1;
         }
         String toUncomment;
         if (nextStart != -1) {
            toUncomment = txt.text().substring(startPos, nextStart);
         }
         else {
            if (innerEnd > 0) {
               toUncomment = txt.text().substring(startPos, innerEnd);
            }
            else {
               toUncomment = txt.text().substring(startPos);
            }
         }
         uncommentBlock(toUncomment, startPos);
      }

      private void uncommentBlock(String scn, int pos) {
         isHighlightBlockCmnt = false;
         setTextParams(scn, pos, pos);
         hl.highlight();
         isHighlightBlockCmnt = true;
      }

      private boolean isValid(int pos) {
         return hl.isValid(txt.text(), pos, condition);
      }

      private boolean isInBlock(String blockStart, String blockEnd, int pos) {
         int lastStart = SyntaxUtils.lastBlockStart(txt.text(), pos, blockStart,
               blockEnd, skipQuotedBlkCmntMarks, true);

         int nextEnd = SyntaxUtils.nextBlockEnd(txt.text(), pos, blockStart,
               blockEnd, skipQuotedBlkCmntMarks, true);

         return lastStart != -1 & nextEnd != -1;
      }

      private void setTextParams(String section, int chgPos, int scnStart) {
         this.section = section;
         this.chgPos = chgPos;
         this.scnStart = scnStart;
         isTypeMode = txt.text().length() > section.length();
         isMultiline = LinesFinder.isMultiline(section);
      }

      private SyntaxSearcher() {}
   }
}
