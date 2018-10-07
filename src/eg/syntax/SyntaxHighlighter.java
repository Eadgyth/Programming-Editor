package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.StyledText;

/**
 * The syntax highlighting of the text contained in
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
    * Highlights text elements in a section around the position
    * where a change happened. The section is initially the line
    * that contains <code>chgPos</code>.
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
    * Highlights text elements in a section that may be multiline.
    * The section initially consists of the completed lines that
    * contain <code>change</code> and begins with the line that
    * contains <code>chgPos</code>.
    *
    * @param change  the change to the text
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
    * The search and highlighting of text elements.
    * <p>
    * The highlighting may take place in sections while text is
    * edited. The section is initially defined as described for
    * the <code>highlight</code> methods in the enclosing
    * {@link SyntaxHighlighter}. Highlighting during editing text
    * requires to once reset the attributes by {@link #resetAttributes}.
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
       * Sets the condition for additionally validating a found text
       * element. The condition is passed to {@link Highlighter#isValid}.
       *
       * @param condition  a freely chosen integer. Default is 0
       */
      public void setCondition(int condition) {
         this.condition = condition;
      }

     /**
      * Sets the section that is simply the entire text
      */
      public void setEntireText() {
         scnStart = 0;
         section = txt.text();
      }

      /**
       * Sets the section that takes into account semicolon separated
       * statements which may be multiline. The section may be further
       * exdended to the region that contains quote marks around the
       * statement.<br>
       * The search for the next semicolon, relative to the end of the
       * initial section, continues so long as it is found in quotes
       * and {@link Highlighter#isValid} returns false.
       */
      public void setStatementSection() {
         if (isTypeMode && isHighlightBlockCmnt) {
            int start = scnStart;
            int end = start + section.length();
            if (scnStart > 0 && txt.text().charAt(scnStart - 1) == '\n') {
               start = LinesFinder.lastNewline(txt.text(), scnStart - 1) + 1;
            }
            int sep = SyntaxUtils.nextUnquoted(txt.text(), ";", end);
            while (sep != -1 && !isValid(sep, 0)) {
               sep = SyntaxUtils.nextUnquoted(txt.text(), ";", sep + 1);
            }
            if (sep != -1) {
               end = sep;
            }
            else {
               end = txt.text().length();
            }
            int firstQuote = SyntaxUtils.firstQuoteMark(txt.text(), start);
            if (firstQuote != -1) {
               start = LinesFinder.lastNewline(txt.text(), firstQuote) + 1;
            }
            if (end < txt.text().length()) {
               int lastQuote = SyntaxUtils.lastQuoteMark(txt.text(), end);
               if (lastQuote != -1) {
                  end = lastQuote;
               }
            }
            end = LinesFinder.nextNewline(txt.text(), end);
            scnStart = start;
            if (end != -1) {
               section = txt.text().substring(scnStart, end);
            }
            else {
               section = txt.text().substring(scnStart);
            }
         }
      }

      /**
       * Sets the section that takes into account markup language tags
       */
      public void setMarkupSection() {
         if (isTypeMode && isHighlightBlockCmnt) {
            int start = txt.text().lastIndexOf("<", chgPos);
            if (start == -1) {
               start = 0;
            }
            int end;
            if (isMultiline) {
               end = markupTagEnd(scnStart + section.length() + 1);
            }
            else {
               end = markupTagEnd(chgPos + 1);
            }
            scnStart = start;
            section = txt.text().substring(start, end);
         }
      }

      /**
       * Resets the attributes in the section that is to be highlighted
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
       * @param nonWordStart  the array of characters that must not precede
       * the keyword. Can be null and is ignored if reqWord is false
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
       * @param extensions  the array of keywords that may extend base
       * @param nonWordStart  the array of characters that must not precede
       * the keyword. Can be null
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
                     && isValid(absStart, base.length());

               if (ok) {
                  length += extensionLength(extensions, endPos);
                  txt.setAttributes(absStart, length, set);
               }
               start += length;
            }
         }
      }

      /**
       * Searches and highlights variables that one of the specified signs
       * precede.<br>(requires that {@link Highlighter#isValid} returns
       * true)
       *
       * @param signs  the signs for the start of the variables
       * @param endMarks  the marks for the end of the variables
       * @param successors  the characters that disable endMarks if they
       * directly follow a sign
       * @param set  the SimpleAttributeSet set on the variables
       */
      public void signedVariables(char[] signs, char[] endMarks, char[] successors,
            SimpleAttributeSet set) {

         for (char c : signs) {
            signedVariable(c, endMarks, successors, set);
         }
      }
      
      /**
       * Searches and highlights variables that the specified sign
       * precedes.<br>(requires that {@link Highlighter#isValid} returns
       * true)
       *
       * @param sign  the sign for the start of the variable
       * @param endMarks  the marks for the end of the variable
       * @param successors  the characters that disable endMarks if they
       * directly follow the sign
       * @param set  the SimpleAttributeSet set on the variables
       */
      public void signedVariable(char sign, char[] endMarks, char[] successors,
            SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(sign, start);
            if (start != -1) {
               int absStart = start + scnStart;
               int length = SyntaxUtils.sectionLength(section, start, endMarks,
                     successors);

               if (isValid(absStart, length)) {
                  txt.setAttributes(absStart, length, set);
                  start += length + 1;
               }
               else {
                  start++;
               }
            }
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
       * Searches and highlights quoted text in single or double
       * quotation marks in orange.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       */
      public void quote() {
         quote(section, scnStart, Attributes.ORANGE_PLAIN);
      }

      /**
       * Searches and highlights quoted text in single or double
       * quotation marks in orange whereby a quotation must be found
       * inside a line.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       */
      public void quoteInLine() {
         if (isMultiline) {
            String[] chunkArr = section.split("\n");
            int sum = 0;
            for (String s : chunkArr) {
               quote(s, scnStart + sum, Attributes.ORANGE_PLAIN);
               sum += s.length() + 1;
            }
         }
         else {
            quote(section, scnStart, Attributes.ORANGE_PLAIN);
         }
      }

      /**
       * Searches and highlights markup elements. Tag names are shown
       * in blue and bold, attributes in red and attribute values, if
       * quoted, in purple.
       *
       * @param tags  the array of html tag names; null for XML
       * @param attributes  the array of html attributes; null for XML
       */
      public void markupElements(String[] tags,  String[] attributes) {
         if (tags == null) {
            xmlElements();
         }
         else {
            String scnLowerCase = section.toLowerCase();
            String textLowerCase = txt.text().toLowerCase();
            for (String s : tags) {
               htmlElement(s, attributes, scnLowerCase, textLowerCase);
            }
         }
      }

      /**
       * Searches sections in an html document where text elements are
       * highlighted with a temporary <code>Highlighter</code>
       *
       * @param startTag  the start tag
       * @param endTag  the end tag
       * @param hlSection  the {@link Highlighter}
       */
      public void embeddedHtmlSections(String startTag, String endTag,
            Highlighter hlSection) {

         
         Highlighter hlCurr = hl;
         setHighlighter(hlSection);
         int start = 0;
         while (start != -1) {
            start = txt.text().toLowerCase().indexOf(startTag, start);            
            int length = 0;    
            if (start != -1) {
               boolean isInCmnt = isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
                  SyntaxConstants.HTML_BLOCK_CMNT_END, start);
                  
               if (!isInCmnt) {
                  int end = SyntaxUtils.nextBlockEnd(txt.text().toLowerCase(),
                        start + 1, startTag, endTag, false, false);
   
                  if (end != -1) {
                     int startTagEnd = SyntaxUtils.nextBlockEnd(txt.text(),
                           start + 1, "<", ">", false, false);
   
                     if (startTagEnd != -1) {
                        innerStart = startTagEnd + 1;
                        innerEnd = end;
                        String innerScn = txt.text().substring(innerStart, end);
                        setTextParams(innerScn, chgPos, innerStart);
                        hl.highlight();
                        length = innerScn.length();
                     }
                  }
               }
               start += length + 1;
            }
         }
         innerStart = 0;
         innerEnd = 0;
         setHighlighter(hlCurr);
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
                     && isValid(absStart, lineCmntStart.length())) {

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
       * Specifies that block comment marks that are quoted are skipped.
       * Skipping takes place only if the quotation is found within a line.
       */
      public void setSkipQuotedBlkCmntMarks() {
         skipQuotedBlkCmntMarks = true;
      }

      /**
       * Returns if the position where a change happened is found inside
       * a block
       *
       * @param blockStart  the block comment start
       * @param blockEnd  the block comment end
       * @return  the boolean value that is true if inside a block
       * comment; false regardless of the position if the text change
       * is multiline
       */
      public boolean isInBlock(String blockStart, String blockEnd) {
         if (isMultiline) {
            return false;
         }
         else {
            return isInBlock(blockStart, blockEnd, chgPos);
         }
      }

      /**
       * Searches and highlights a block in green
       *
       * @param blockStart  the block start
       * @param blockEnd  the block end
       */
      public void block(String blockStart, String blockEnd) {
         if (!isHighlightBlockCmnt) {
            return;
         }
         removedBlockStart(innerStart, blockStart, blockEnd);
         int start = innerStart;
         while (start != -1) {
            start = txt.text().indexOf(blockStart, start);
            int length = 0;
            if (innerEnd > 0 && start >= innerEnd - blockStart.length()) {
               start = -1;
            }
            if (start != -1) {
               if (!skipQuotedBlkCmntMarks
                     || !SyntaxUtils.isQuotedInLine(txt.text(), start)) {

                  int end = SyntaxUtils.nextBlockEnd(txt.text(), start + 1,
                        blockStart, blockEnd, skipQuotedBlkCmntMarks, true);

                  if (innerEnd > 0 && end >= innerEnd - blockEnd.length()) {
                     end = -1;
                  }
                  if (end != -1) {
                     length = end - start + blockEnd.length();
                     txt.setAttributes(start, length, Attributes.GREEN_PLAIN);
                     removedBlockStart(end + blockEnd.length(), blockStart,
                           blockEnd);
                  }
                  else {
                     removedBlockEnd(start, blockStart, blockEnd);
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
                     && isValid(absStart, key.length());

               if (ok) {
                  txt.setAttributes(absStart, key.length(), set);
               }
               start += key.length();
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

      private void htmlElement(String tagName, String[] attributes,
            String scnLowerCase, String textLowerCase) {

         int start = 0;
         while (start != -1) {
            start = scnLowerCase.indexOf(tagName, start);
            if (start != -1) {
               int absStart = start + scnStart;
               int tagEnd = start + tagName.length();
               int length = tagName.length();
               boolean isStartTag = start > 0
                    && '<' == section.charAt(start - 1)
                    && SyntaxUtils.isWordEnd(section, tagEnd);

               if (isStartTag) {
                  tagEnd = markupTagEnd(absStart);
                  String tag = textLowerCase.substring(absStart, tagEnd);
                  length = tag.length();
                  for (String s : attributes) {
                      htmlAttributes(s, tag, absStart);
                  }
                  quote(tag, absStart, Attributes.PURPLE_PLAIN);
               }
               boolean isEndTag = !isStartTag
                     && start > 1
                     && '/' == section.charAt(start - 1)
                     && '<' == section.charAt(start - 2)
                     && SyntaxUtils.isWordEnd(section, tagEnd);

               if (isStartTag || isEndTag) {
                  txt.setAttributes(absStart, tagName.length(),
                        Attributes.BLUE_PLAIN);
               }
               start += length;
            }
         }
      }

      private void xmlElements() {
         int start = 0;
         while (start != -1) {
            start = section.indexOf('<', start);
            if (start != -1) {
               boolean isEndTag = section.length() > start + 1
                     && section.charAt(start + 1) == '/';

               int search = start;
               if (isEndTag) {
                  search++;
               }
               int length = SyntaxUtils.sectionLength(section, search,
                        SyntaxConstants.XML_TAG_END_CHARS, null);

               boolean isTagName = false;
               if (section.length() > search + 1) {
                  char test = section.charAt(search + 1);
                  isTagName = Character.isLetter(test) || test == '_';
               }
               if (isTagName) {
                  int nameEnd = search + length;
                  if (!isEndTag && isMarkupAttrStart(nameEnd)) {
                     int absStart = start + scnStart;
                     int tagEnd = markupTagEnd(absStart + 1);
                     String tag = txt.text().substring(absStart, tagEnd);
                     for (char c : SyntaxConstants.XML_ATTR_START_CHARS) {
                        xmlAttributes(c, SyntaxConstants.XML_ATTR_END_CHARS,
                              tag, absStart);
                     }
                     quote(tag, absStart, Attributes.PURPLE_PLAIN);
                  }
                  int colorStart = search + scnStart + 1;
                  txt.setAttributes(colorStart, length - 1, Attributes.BLUE_PLAIN);
               }
               start += length + 1;
            }
         }
      }

      private int markupTagEnd(int pos) {
         int end = SyntaxUtils.nextBlockEnd(txt.text(), pos, "<", ">", false, false);
         if (end == -1) {
            end = txt.text().indexOf("</", pos);
            if (end == -1) {
               end = txt.text().length();
            }
         }
         return end;
      }

      private boolean isMarkupAttrStart(int pos) {
         return section.length() > pos
               && (' ' == section.charAt(pos)
               || '\n' == section.charAt(pos));
      }

      private void htmlAttributes(String keyword, String tag, int tagStart) {
         int start = 0;
         while (start != -1) {
            start = tag.indexOf(keyword, start);
            if (start != -1) {
               boolean ok = SyntaxUtils.isWord(tag, start, keyword.length(), null);
               if (ok) {
                  txt.setAttributes(start + tagStart, keyword.length(),
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
               length = SyntaxUtils.sectionLength(xmlEl, start, endChars, null);
               txt.setAttributes(absStart + 1, length - 1, Attributes.RED_PLAIN);
               start += length;
            }
         }
      }

      private void quote(String scn, int scnPos, SimpleAttributeSet set) {
         quote(scn, scnPos, SyntaxConstants.DOUBLE_QUOTE, set);
         quote(scn, scnPos, SyntaxConstants.SINGLE_QUOTE, set);
      }

      private void quote(String scn, int scnPos, char quoteMark,
            SimpleAttributeSet set) {

         final boolean isSingleQuote = quoteMark == SyntaxConstants.SINGLE_QUOTE;
         int start = 0;
         int end = 0;
         while (start != -1 && end != -1) {
            start = SyntaxUtils.nextNonEscaped(scn, quoteMark, start);
            int absStart = start + scnPos;
            if (start != -1) {
               int length = 0;
               boolean ok = isQuotation(isSingleQuote, scn, start, scnPos);
               end = SyntaxUtils.nextNonEscaped(scn, quoteMark, start + 1);
               if (end != -1) {
                  ok = ok && isQuotation(isSingleQuote, scn, end, scnPos);
                  if (ok) {
                     length = end - start + 1;
                     txt.setAttributes(absStart, length, set);
                  }
               }
               start += length + 1;
            }
         }
      }

      private boolean isQuotation(boolean isSingleQuote, String scn, int pos,
            int scnPos) {

         return (!isSingleQuote
               || !SyntaxUtils.isQuoted(scn, pos, SyntaxConstants.DOUBLE_QUOTE))
               && isValid(pos + scnPos, 0);
      }

      private void removedBlockStart(int cmntEnd, String blockStart,
            String blockEnd) {

         if (!isTypeMode) {
            return;
         }
         int nextEnd = SyntaxUtils.nextBlockEnd(txt.text(), cmntEnd,
               blockStart, blockEnd, skipQuotedBlkCmntMarks, true);

         if (innerEnd > 0 && nextEnd > innerEnd) {
             nextEnd = -1;
         }
         if (nextEnd != -1) {
            int lastStart = SyntaxUtils.lastBlockStart(txt.text(), cmntEnd,
                  blockStart, blockEnd, skipQuotedBlkCmntMarks, true);

            int lineStart;
            if (lastStart == -1) {
               lineStart = cmntEnd;
            }
            else {
               lineStart = LinesFinder.lastNewline(txt.text(), nextEnd);
            }
            int end = nextEnd + blockEnd.length();
            String toRepair = txt.text().substring(lineStart, end);
            repairCancelledBlock(toRepair, lineStart);
         }
      }

      private void removedBlockEnd(int startPos, String blockStart,
            String blockEnd) {

         if (!isTypeMode) {
            return;
         }
         int nextStart = SyntaxUtils.nextBlockStart(txt.text(), startPos + 1,
               blockStart, blockEnd, skipQuotedBlkCmntMarks, true);

         if (innerEnd > 0 && nextStart > innerEnd) {
            nextStart = -1;
         }
         String toRepair;
         if (nextStart != -1) {
            toRepair = txt.text().substring(startPos, nextStart);
         }
         else {
            if (innerEnd > 0) {
               toRepair = txt.text().substring(startPos, innerEnd);
            }
            else {
               toRepair = txt.text().substring(startPos);
            }
         }
         repairCancelledBlock(toRepair, startPos);
      }

      private void repairCancelledBlock(String toRepair, int pos) {
         isHighlightBlockCmnt = false;
         setTextParams(toRepair, pos, pos);
         hl.highlight();
         isHighlightBlockCmnt = true;
      }

      private boolean isInBlock(String blockStart, String blockEnd, int pos) {
         int lastStart = SyntaxUtils.lastBlockStart(txt.text(), pos, blockStart,
               blockEnd, skipQuotedBlkCmntMarks, true);

         int nextEnd = -1;
         if (lastStart != -1) {
            nextEnd = SyntaxUtils.nextBlockEnd(txt.text(), pos, blockStart,
               blockEnd, skipQuotedBlkCmntMarks, true);
         }

         return lastStart != -1 & nextEnd != -1;
      }

      private boolean isValid(int pos, int length) {
         return hl.isValid(txt.text(), pos, length, condition);
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
