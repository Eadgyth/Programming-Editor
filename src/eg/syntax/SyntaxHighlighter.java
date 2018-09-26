package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.TextDocument;

/**
 * The syntax highlighting in the text contained in
 * <code>TextDocument</code>.<br>
 * Class requires a {@link Highlighter} which calls (selected) methods
 * in this {@link SyntaxHighlighter.SyntaxSearcher}.
 */
public class SyntaxHighlighter {

   private final TextDocument textDoc;
   private final SyntaxSearcher searcher;
   private Highlighter hl;

   /**
    * @param textDoc  the reference to {@link TextDocument}
    */
   public SyntaxHighlighter(TextDocument textDoc) {
      this.textDoc = textDoc;
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
      hl.setSyntaxSearcher(searcher);
      this.hl = hl;
   }

   /**
    * Highlights text elements in the entire text
    *
    * @param text  the entire text
    */
   public void highlightAll(String text) {
      searcher.setTextParams(text, text, 0, 0);
      hl.highlight();
   }

   /**
    * Highlights text elements in the line where a change happened
    *
    * @param text  the entire text
    * @param chgPos  the position where a change happened
    */
   public void highlightLine(String text, int chgPos) {
      int lineStart = LinesFinder.lastNewline(text, chgPos);
      String line = LinesFinder.line(text, lineStart);
      searcher.setTextParams(text, line, chgPos, lineStart + 1);
      hl.highlight();
   }

   /**
    * Highlights text elements in a text change that may be multiline
    *
    * @param text  the entire text
    * @param change  the change to the text
    * @param chgPos  the position where the change happened
    */
   public void highlightMultiline(String text, String change, int chgPos) {
      int linesStart = LinesFinder.lastNewline(text, chgPos);
      String lines = LinesFinder.lines(text, linesStart, change.length());
      searcher.setTextParams(text, lines, chgPos, linesStart + 1);
      hl.highlight();
   }

   /**
    * The search and highlighting of text elements.
    * <p>
    * Class is created in the enclosing class and has no public constructor.
    */
   public class SyntaxSearcher {

      private String text = "";
      private String section = "";
      private int chgPos;
      private int scnPos;
      private int condition = 0;
      private boolean isTypeMode = false;
      private boolean isMultiline = true;
      private boolean isHighlightBlockCmnt = true;
      private boolean allowQuoted = false;
      private int innerStart = 0;
      private int innerEnd = 0;
      
      /**
       * Sets the condition for testing if a found text element is
       * valid. The condition is passed to {@link Highlighter#isValid}.
       *
       * @param condition  a freely chosen integer. Default is 0
       */
      public void setCondition(int condition) {
         this.condition = condition;
      }

      /**
       * Sets the section that ranges from the end of the default section
       * to the next unquoted semicolon. The search for the next semicolon
       * continues so long as {@link Highlighter#isValid} returns false.
       *
       * @param isQuoteMarks  specifies, if true, that the section is
       * extended to the region that contains quote marks around the
       * section
       */
      public void setSemicolonSeparatedSection(boolean isQuoteMarks) {
         if (isTypeMode && isHighlightBlockCmnt) {
            int searchStart = scnPos;
            int searchEnd = scnPos + section.length();
            int semicolon = SyntaxUtils.nextUnquoted(text, ";", searchEnd);
            while (semicolon != -1 && !isValid(semicolon)) {
               semicolon = SyntaxUtils.nextUnquoted(text, ";", semicolon + 1);
            }
            if (semicolon != -1) {
               searchEnd = semicolon;
            }
            else {
               searchEnd = text.length();
            }       
            if (isQuoteMarks) {
               int firstQuoteMark = SyntaxUtils.firstQuoteMark(text, scnPos);
               if (firstQuoteMark != -1) {
                  searchStart = LinesFinder.lastNewline(text, firstQuoteMark) + 1;
               }
               if (searchEnd < text.length()) {         
                  int lastQuoteMark = SyntaxUtils.lastQuoteMark(text, scnPos); 
                  if (lastQuoteMark != 1) {
                     searchEnd = lastQuoteMark;
                  }
               }
            }
            int end = LinesFinder.nextNewline(text, searchEnd);
            scnPos = searchStart;
            if (end != -1) {
               section = text.substring(scnPos, end);
            }
            else {
               section = text.substring(scnPos);
            }
         }
      }

      /**
       * Sets the section that corresponsds to html elements at the position
       * where a change happened
       */
      public void setHtmlSection() {
         if (isTypeMode && isHighlightBlockCmnt) {
            int start = SyntaxUtils.lastBlockStart(text, chgPos, "<", ">", true);
            if (start == -1) {
               start = text.lastIndexOf(">", chgPos) + 1;
               if (start == -1) {
                  start = 0;
               }
            }
            int end;
            if (isMultiline) {
               end = htmlSectionEnd(scnPos + section.length());
            }
            else {
               end = htmlSectionEnd(chgPos + 1);
            }
            scnPos = start;
            section = text.substring(start, end);
         }
      }
      
      /**
       * (Re-)sets the section that is to be highlighted to black and
       * plain.<br>
       * The section is by default the line where a text change happened
       * or a multiline section consisting of full lines in the case a
       * multiline insertion was made.
       */
      public void setSectionBlack() {
         textDoc.setCharAttrBlack(scnPos, section.length());
      }

      /**
       * Searches and highlights keywords.<br>
       * Calls {@link Highlighter#isValid}.
       * 
       * @param keys  the array of keywords
       * @param reqWord  specifies, if true, that keywords must be whole
       * words
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
       * be extended by one of the keywords in <code>extensions</code>.
       * <br>
       * Calls {@link Highlighter#isValid}.
       *
       * @param base  the base keyword
       * @param extensions  the array of strings that may extend the base
       * keyword
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
               int absStart = start + scnPos;
               int endPos = start + base.length();
               boolean ok = SyntaxUtils.isWord(section, start, base.length(),
                        nonWordStart)
                     && isValid(absStart);

               if (ok) {
                  length += extensionLength(extensions, endPos);
                  textDoc.setCharAttr(absStart, length, set);
               }
               start += length;
            }
         }
      }

      /**
       * Searches and highlights variables that start with one of the
       * characters in <code>startChars</code> and end with one of the
       * characters in <code>endChars</code>.<br>
       * Calls {@link Highlighter#isValid}.
       *
       * @param startChars  the array start characters
       * @param endChars  the array of end characters
       * @param isWordStart  specifies, if true, that the variables must
       * not adjoin to a letter or a digit at the start
       * @param set  the SimpleAttributeSet set on the variables
       */
      public void signedVariables(char[] startChars, char[] endChars,
             boolean isWordStart, SimpleAttributeSet set) {

         for (char c : startChars) {
            signedVariable(c, endChars, isWordStart, set);
         }
      }

      /**
       * Searches and highlights opening and closing braces in gray and
       * bold.<br>Calls {@link Highlighter#isValid}..
       */
      public void braces() {
         key("{", false, null, Attributes.GRAY_BOLD);
         key("}", false, null, Attributes.GRAY_BOLD);
      }

      /**
       * Searches and highlights opening and closing brackets in blue and
       * bold.<br>Calls {@link Highlighter#isValid}.
       */
      public void brackets() {
         key("(", false, null, Attributes.BLUE_BOLD);
         key(")", false, null, Attributes.BLUE_BOLD);
      }

      /**
       * Searches and highlights text quoted with single or double
       * quotation marks in the entire text.<br>
       * Calls {@link Highlighter#isValid}.
       *
       * @param set  the SimpleAttributeSet set on quoted text
       */
      public void quoted(SimpleAttributeSet set) {
         quoted(section, scnPos, SyntaxConstants.DOUBLE_QUOTE, set);
         quoted(section, scnPos, SyntaxConstants.SINGLE_QUOTE, set);
      }

      /**
       * Searches and highlights text quoted with single or double
       * quotation marks in which quoted text does not span line breaks.
       * <br>
       * Calls {@link Highlighter#isValid}..
       *
       * @param set  the SimpleAttributeSet set on quoted text
       */
      public void quotedLinewise(SimpleAttributeSet set) {
         if (isMultiline) {
            String[] chunkArr = section.split("\n");
            int sum = 0;
            for (String s : chunkArr) {
               quoted(s, scnPos + sum, SyntaxConstants.DOUBLE_QUOTE, set);
               quoted(s, scnPos + sum, SyntaxConstants.SINGLE_QUOTE, set);
               sum += s.length() + 1;
            }
         }
         else {
            quoted(section, scnPos, SyntaxConstants.DOUBLE_QUOTE, set);
            quoted(section, scnPos, SyntaxConstants.SINGLE_QUOTE, set);
         }
      }

      /**
       * Searches and highlights line comments in green.<br>
       * Calls {@link Highlighter#isValid}.
       *
       * @param lineCmntStart  the string that marks the start of a line
       * comment
       */
      public void lineComments(String lineCmntStart) {
         int start = 0;
         while (start != -1) {
            int length = 0;
            start = section.indexOf(lineCmntStart, start);
            if (start != -1) {
               int absStart = start + scnPos;
               if (!isPositionInQuotes(start) && isValid(absStart)) {
                  int lineEnd = section.indexOf("\n", start);
                  if (lineEnd != -1) {
                     length = lineEnd - start;
                  }
                  else {
                     length = section.length() - start;
                  }
                  textDoc.setCharAttr(absStart, length, Attributes.GREEN_PLAIN);
               }
               start += length + 1;
            }
         }
      }

      /**
       * Sets the boolean that specifies that block commment marks may
       * be surrounded by quotation marks
       *
       * @param allow  the boolean value; true to allow
       */
      public void blkCmntMarksQuoted(boolean allow) {
         allowQuoted = allow;
      }

      /**
       * Searches and highlights block comments in green
       *
       * @param blockCmntStart  the string that marks the start of a
       * comment
       * @param blockCmntEnd  the string that marks the end of a comment
       */
      public void blockComments(String blockCmntStart, String blockCmntEnd) {
         if (!isHighlightBlockCmnt) {
            return;
         }
         removedBlockCmntStart(innerStart, blockCmntStart, blockCmntEnd);
         int start = innerStart;
         while (start != -1) {
            start = text.indexOf(blockCmntStart, start);
            int length = 0;
            if (innerEnd > 0 && start >= innerEnd - blockCmntStart.length()) {
               start = -1;
            }
            if (start != -1) {
               if (allowQuoted || !SyntaxUtils.isBorderedByQuotes(text, start,
                    blockCmntStart.length())) {

                  int end = SyntaxUtils.nextBlockEnd(text, start + 1,
                        blockCmntStart, blockCmntEnd, allowQuoted);

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

      /**
       * Searches and highlights html elements. Tag names are shown in
       * blue and bold, attributes in red and attribute values, if in
       * quotes, in purple.
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
       * Searches sections in an html document where text elements are
       * highlighted with a temporary <code>Highlighter</code> (for CSS
       * or Javascript)
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
       * Returns the boolean that, if true, indicates that the position
       * where a change happened is found inside a block comment. Returns
       * false regardless of the position if the text change is multiline
       *
       * @param blockCmntStart  the string that marks the start of a
       * comment
       * @param blockCmntEnd  the string that marks the end of a comment
       * @return  the boolean value
       */
      public boolean isInBlockCmnt(String blockCmntStart, String blockCmntEnd) {
         if (isMultiline) {
            return false;
         }
         else {
            return isInBlock(blockCmntStart, blockCmntEnd, chgPos);
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
               int absStart = start + scnPos;
               boolean ok = (!reqWord || SyntaxUtils.isWord(section, start,
                     key.length(), nonWordStart))
                     && isValid(absStart);

               if (ok) {
                  textDoc.setCharAttr(absStart, key.length(), set);
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
            int length;
            if (start != -1) {
               int absStart = start + scnPos;
               boolean ok = (!isWordStart
                        || SyntaxUtils.isWordStart(section, start, null))
                     && isValid(absStart);

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
               boolean isStartTag = start > 0 && '<' == section.charAt(start - 1);
               int endPos = start + tag.length();
               if (isStartTag) {
                  boolean applyAttributes = section.length() > endPos
                        && (' ' == section.charAt(endPos)
                           || '\n' == section.charAt(endPos));

                  if (applyAttributes) {
                     int elStart = start + scnPos;
                     String htmlEl = text.substring(elStart, htmlSectionEnd(elStart));
                     for (String s : attributes) {
                         htmlAttribute(s, htmlEl, elStart);
                     }
                     quoted(htmlEl, elStart, SyntaxConstants.DOUBLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                     quoted(htmlEl, elStart, SyntaxConstants.SINGLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                  }
               }
               boolean isEndTag = !isStartTag && start > 1
                     && '/' == section.charAt(start - 1)
                     && '<' == section.charAt(start - 2);

               boolean ok = (isStartTag || isEndTag)
                     && SyntaxUtils.isWordEnd(section, endPos);

               if (ok) {
                  textDoc.setCharAttr(start + scnPos, tag.length(),
                        Attributes.BLUE_BOLD);
               }
               start += tag.length();
            }
         }
      }

      private int htmlSectionEnd(int pos) {
         int end = SyntaxUtils.nextBlockEnd(text, pos, "<", ">", true);
         if (end == -1) {
            end = text.indexOf("<", pos);
            if (end == -1) {
               end = text.length();
            }
         }
         return end;
      }

      private void htmlAttribute(String keyword, String htmlEl, int elStart) {
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
                  textDoc.setCharAttr(absStart, keyword.length(),
                        Attributes.RED_PLAIN);
               }
               start += keyword.length();
            }
         }
      }

      private void embeddedHtmlSections(String startTag, String endTag) {
         int start = 0;
         while (start != -1) {
            start = text.toLowerCase().indexOf(startTag, start);
            if (start != -1) {
               int length = 0;
               int end = SyntaxUtils.nextBlockEnd(text.toLowerCase(), start + 1,
                      startTag, endTag, false);

               if (end != -1) {
                  innerStart = SyntaxUtils.nextBlockEnd(text, start + 1, "<", ">", false);
                  if (innerStart != -1) {
                     innerEnd = end;
                     String innerScn = text.substring(innerStart, end);
                     setTextParams(text, innerScn, chgPos, innerStart);
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
               boolean ok
                     = (!isSingleQuote
                        || !SyntaxUtils.isInQuotes(
                           scn, start, SyntaxConstants.DOUBLE_QUOTE))
                     && isValid(absStart);

               end = SyntaxUtils.nextNonEscaped(scn, quoteMark, start + 1);
               if (end != -1) {
                  ok = ok && ((!isSingleQuote || !SyntaxUtils.isInQuotes(
                           scn, end, SyntaxConstants.DOUBLE_QUOTE))
                        && isValid(end + scnStart));

                  if (ok) {
                     length = end - start + 1;
                     textDoc.setCharAttr(absStart, length, set);
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
         int nextEnd = SyntaxUtils.nextBlockEnd(text, endPos, blockCmntStart,
               blockCmntEnd, allowQuoted);

         int lastStart = SyntaxUtils.lastBlockStart(text, endPos, blockCmntStart,
               blockCmntEnd, allowQuoted);

         if (innerEnd > 0 && nextEnd > innerEnd) {
             nextEnd = -1;
         }
         if (nextEnd != -1) {
            int lineStart;
            if (lastStart == -1) {
               lineStart = endPos;
            }
            else {
               lineStart = LinesFinder.lastNewline(text, nextEnd);
            }
            int end = nextEnd + blockCmntEnd.length();
            String toUncomment = text.substring(lineStart, end);
            uncommentBlock(toUncomment, lineStart);
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
               int currLineEnd = LinesFinder.nextNewline(text, chgPos);
               toUncomment = text.substring(startPos, currLineEnd);
            }
         }
         uncommentBlock(toUncomment, startPos);
      }

      private void uncommentBlock(String scn, int pos) {
         isHighlightBlockCmnt = false;
         setTextParams(text, scn, pos, pos);
         hl.highlight();
         isHighlightBlockCmnt = true;
      }

      private boolean isValid(int pos) {
         return hl.isValid(text, pos, condition);
      }

      private boolean isInBlock(String blockStart, String blockEnd, int pos) {

         int lastStart = SyntaxUtils.lastBlockStart(text, pos, blockStart,
               blockEnd, allowQuoted);

         int nextEnd = SyntaxUtils.nextBlockEnd(text, pos, blockStart, blockEnd,
               allowQuoted);

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
         return SyntaxUtils.isInQuotes(line, relStart, SyntaxConstants.DOUBLE_QUOTE)
            || SyntaxUtils.isInQuotes(line, relStart, SyntaxConstants.SINGLE_QUOTE);
      }

      private void setTextParams(String text, String section, int chgPos, int scnPos) {
         this.text = text;
         this.section = section;
         this.chgPos = chgPos;
         this.scnPos = scnPos;
         isTypeMode = text.length() > section.length();
         isMultiline = LinesFinder.isMultiline(section);
      }

      private SyntaxSearcher() {}
   }
}
