package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.styledtext.StyledText;
import eg.document.styledtext.Attributes;

/**
 * The syntax highlighting
 */
public class SyntaxHighlighter {

   private final StyledText txt;
   private final SyntaxSearcher searcher;
   private final Attributes attr;
   private Highlighter hl;

   /**
    * @param txt  the {@link StyledText}
    */
   public SyntaxHighlighter(StyledText txt) {
      this.txt = txt;
      attr = txt.attributes();
      searcher = new SyntaxSearcher();
   }

   /**
    * Sets a <code>Highlighter</code>
    *
    * @param hl  the {@link Highlighter}
    */
   public void setHighlighter(Highlighter hl) {
      if (hl == null && this.hl != null) {
         txt.resetAttributes();
      }
      this.hl = hl;
   }

   /**
    * Highlights text elements in the entire text
    */
   public void highlight() {
      searcher.setTextParams(txt.text(), 0, 0);
      hl.highlight(searcher, attr);
   }

   /**
    * Highlights text elements in a section around the position where a
    * a change happened. The section is initially the line that contains
    * <code>chgPos</code>.
    *
    * @param chgPos  the position where a change happened
    */
   public void highlight(int chgPos) {
      int lineStart = LinesFinder.lastNewline(txt.text(), chgPos);
      String line   = LinesFinder.line(txt.text(), lineStart);
      searcher.setTextParams(line, chgPos, lineStart + 1);
      hl.highlight(searcher, attr);
   }

   /**
    * Highlights text elements in a section that may be multiline. The
    * section initially consists of the completed lines that contain
    * <code>change</code> and it begins with the line that contains
    * <code>chgPos</code>.
    *
    * @param change  the change to the text
    * @param chgPos  the position where the change starts
    */
   public void highlight(String change, int chgPos) {
      int linesStart = LinesFinder.lastNewline(txt.text(), chgPos);
      int length     = chgPos - linesStart + change.length();
      String lines   = LinesFinder.lines(txt.text(), linesStart, length);
      searcher.setTextParams(lines, chgPos, linesStart + 1);
      hl.highlight(searcher, attr);
   }

   /**
    * The search and highlighting of text elements
    */
   public class SyntaxSearcher {

      private String section = "";
      private int chgPos;
      private int scnStart;
      private boolean isTypeMode = false;
      private boolean isMultiline = true;
      private boolean isRepairBlock = false;
      private boolean isRepairInnerBlock = false;
      private Highlighter hlSection;
      private int innerStart = 0;
      private int innerEnd = 0;
      private int condition = 0;

      /**
       * Sets a condition for validating a text element. The
       * condition is passed to {@link Highlighter#isValid} when a
       * text element is found.
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
       * statements which may be multiline. The search for the next
       * semicolon relative to the current change position continues
       * until one is found unquoted and {@link Highlighter#isValid}
       * returns true. The section may be further exdended to the
       * region that contains quote marks around the statement.
       */
      public void setStatementSection() {
         if (isTypeMode && !isRepairBlock) {
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
               if (innerEnd > 0) {
                  end = innerEnd;
               }
               else {
                  end = txt.text().length();
               }
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
            section = txt.text().substring(scnStart, end);
         }
      }

      /**
       * Sets the section that takes into account markup language tags
       */
      public void setMarkupSection() {
         if (isTypeMode && !isRepairBlock) {
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
       * Searches and highlights keywords without case sensitivity.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       *
       * @param keys  the array of keywords
       * @param reqWord  true to require that keywords are whole words
       * @param nonWordStart  the array of characters that must not precede
       * the keyword. Can be null and is ignored if reqWord is false
       * @param set  the SimpleAttributeSet set on the keywords
       */
      public void keywordsIgnoreCase(String[] keys, boolean reqWord, char[] nonWordStart,
            SimpleAttributeSet set) {

         String scn = section;
         section = section.toLowerCase();
         for (String s : keys) {
            key(s, reqWord, nonWordStart, set);
         }
         section = scn;
      }

      /**
       * Searches and highlights an extensible base keyword.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       *
       * @param base  the base keyword
       * @param extensions  the array of keywords that may extend base
       * @param nonWordStart  the array of characters that must not precede
       * the keyword in addition to digits and letters. Can be null
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
               length += extensionLength(extensions, endPos);
               boolean ok = SyntaxUtils.isWord(section, start, length,
                     nonWordStart)
                     && isValid(absStart, base.length());

               if (ok) {
                  txt.setAttributes(absStart, length, set);
               }
               start += length;
            }
         }
      }

      /**
       * Searches and highlights variables that one of the specified signs
       * precedes.<br>(requires that {@link Highlighter#isValid} returns
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
       * Searches and highlights a variable that the specified sign
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
       * Searches and highlights opening and closing braces in blue and
       * bold.<br>(requires that {@link Highlighter#isValid} returns true)
       */
      public void braces() {
         key("{", false, null, attr.bracketsBold);
         key("}", false, null, attr.bracketsBold);
      }

      /**
       * Searches and highlights opening and closing brackets in blue and
       * bold.<br>(requires that {@link Highlighter#isValid} returns true)
       */
      public void brackets() {
         key("(", false, null, attr.bracketsBold);
         key(")", false, null, attr.bracketsBold);
      }

      /**
       * Searches and highlights quoted text in single or double
       * quotation marks in orange.<br>
       * (requires that {@link Highlighter#isValid} returns true)
       */
      public void quote() {
         quote(section, scnStart, attr.orangePlain);
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
               quote(s, scnStart + sum, attr.orangePlain);
               sum += s.length() + 1;
            }
         }
         else {
            quote(section, scnStart, attr.orangePlain);
         }
      }

      /**
       * Searches and highlights markup elements. Tag names are shown in
       * blue, attributes in red and quoted attribute values in purple.
       *
       * @param html  true for html, false for xml
       */
      public void markup(boolean html) {
         String scn = section;
         String t = txt.text();
         if (html) {
            scn = section.toLowerCase();
            t = txt.text().toLowerCase();
         }
         int start = 0;
         while (start != -1) {
            start = scn.indexOf('<', start);
            if (start != -1) {
               int length = 0;
               boolean isEndTag = scn.length() > start + 1
                     && scn.charAt(start + 1) == '/';

               int offset = start + 1;
               if (isEndTag) {
                  offset++;
               }
               boolean isTagName = false;
               if (section.length() > offset) {
                  char test = section.charAt(offset);
                  isTagName = Character.isLetter(test) || (!html & test == '_');
               }
               if (isTagName) {
                  int nameLength;
                  if (html) {
                    nameLength = SyntaxUtils.sectionLength(scn, offset,
                           SyntaxConstants.HTML_TAGS);
                  }
                  else {
                     nameLength = SyntaxUtils.sectionLength(scn, offset,
                           SyntaxConstants.RESERVED_XML_CHARS, null);
                  }
                  length = nameLength;
                  int endPos = offset + length;
                  if (SyntaxUtils.isWordEnd(scn, endPos)) {
                     if (!isEndTag) {
                        int absStart = start + scnStart;
                        int tagEnd = markupTagEnd(absStart + 1);
                        String tag = t.substring(absStart, tagEnd);
                        if (html) {
                           for (String s : SyntaxConstants.HTML_ATTR) {
                               htmlAttributes(s, tag, absStart);
                           }
                        }
                        else {
                           xmlAttributes(tag, absStart, nameLength);
                        }
                        quote(tag, absStart, attr.purplePlain);
                     }
                     int colorStart = offset + scnStart;
                     txt.setAttributes(colorStart, nameLength, attr.bluePlain);
                  }
               }
               start += length + 1;
            }
         }
      }

      /**
       * Searches sections in an html document where text elements are
       * highlighted with a temporary <code>Highlighter</code>
       *
       * @param startTag  the start tag without closing bracket
       * @param endTag  the end tag
       * @param reqClosingBracket  true if the start tag needs a closing
       * bracket; false otherwise
       * @param hlSection  the {@link Highlighter}
       */
      public void innerHtmlSections(String startTag, String endTag,
            boolean reqClosingBracket, Highlighter hlSection) {

         if (isRepairInnerBlock) {
            return;
         }
         if (!isRepairBlock) {
            removedBlockStart(0, startTag, endTag, SyntaxUtils.INCLUDE_QUOTED);
         }
         String t = txt.text().toLowerCase();
         int start = 0;
         while (start != -1) {
            start = t.indexOf(startTag, start);
            int length = 0;
            if (start != -1) {
               boolean isInCmnt = isInBlock(SyntaxConstants.HTML_BLOCK_CMNT_START,
                     SyntaxConstants.HTML_BLOCK_CMNT_END, start,
                     SyntaxUtils.INCLUDE_QUOTED);

               if (!isInCmnt) {
                  int searchStart = start + 1;
                  int end = SyntaxUtils.nextBlockEnd(t, searchStart, startTag,
                       endTag, SyntaxUtils.INCLUDE_QUOTED);

                  if (end != -1) {
                     int startTagEnd;
                     if (reqClosingBracket) {
                        startTagEnd = SyntaxUtils.nextBlockEnd(t, searchStart,
                              "<", ">", SyntaxUtils.INCLUDE_QUOTED);

                        if (startTagEnd != -1) {
                           startTagEnd++;
                        }
                     }
                     else {
                        startTagEnd = start + startTag.length();
                     }
                     if (startTagEnd != -1) {
                        innerStart = startTagEnd;
                        innerEnd = end;
                        String innerScn = t.substring(innerStart, end);
                        setTextParams(innerScn, chgPos, innerStart);
                        Highlighter curr = hl;
                        hl = hlSection;
                        hl.highlight(this, attr);
                        hl = curr;
                        length = innerScn.length();
                     }
                     innerStart = 0;
                     innerEnd = 0;
                     if (!isRepairBlock) {
                        removedBlockStart(end + endTag.length(), startTag,
                              endTag, SyntaxUtils.INCLUDE_QUOTED);
                     }
                  }
                  else {
                     if (!isRepairBlock) {
                        removedBlockEnd(start, startTag, endTag,
                              SyntaxUtils.INCLUDE_QUOTED);
                     }
                  }
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
                     && isValid(absStart, lineCmntStart.length())) {

                  int lineEnd = section.indexOf("\n", start);
                  if (lineEnd != -1) {
                     length = lineEnd - start;
                  }
                  else {
                     length = section.length() - start;
                  }
                  txt.setAttributes(absStart, length, attr.greenPlain);
               }
               start += length + 1;
            }
         }
      }

      /**
       * Returns if the position where a change happened is found inside
       * a block
       *
       * @param blockStart  the block comment start
       * @param blockEnd  the block comment end
       * @param quoteOpt  the option to define if matches in quotes
       * are skipped: INCLUDE_QUOTED, SKIP_QUOTED, SKIP_LINE_QUOTED
       * in {@link SyntaxUtils}
       * @return  true if inside a block comment; false otherwise. Also false
       * regardless of the position if the text change is multiline
       */
      public boolean isInBlock(String blockStart, String blockEnd,
            int quoteOpt) {

         if (isMultiline) {
            return false;
         }
         else {
            return isInBlock(blockStart, blockEnd, chgPos, quoteOpt);
         }
      }

      /**
       * Searches and highlights a block in green
       *
       * @param blockStart  the block start
       * @param blockEnd  the block end
       * @param quoteOpt  the option to define if matches in quotes
       * are skipped: INCLUDE_QUOTED, SKIP_QUOTED, SKIP_LINE_QUOTED
       * in {@link SyntaxUtils}
       */
      public void block(String blockStart, String blockEnd, int quoteOpt) {
         if (isRepairBlock || isRepairInnerBlock) {
            return;
         }
         removedBlockStart(innerStart, blockStart, blockEnd, quoteOpt);
         int start = innerStart;
         while (start != -1) {
            start = txt.text().indexOf(blockStart, start);
            int length = 1;
            if (innerEnd > 0 && start >= innerEnd - blockStart.length()) {
               start = -1;
            }
            if (start != -1) {
               boolean quoteInLine;
               if (quoteOpt == SyntaxUtils.INCLUDE_QUOTED
                     || !SyntaxUtils.isQuoted(txt.text(), start, quoteOpt)) {

                  int end = SyntaxUtils.nextBlockEnd(txt.text(), start + 1,
                        blockStart, blockEnd, quoteOpt);

                  if (innerEnd > 0 && end >= innerEnd - blockEnd.length()) {
                     end = -1;
                  }
                  if (end != -1) {
                     length = end - start + blockEnd.length();
                     txt.setAttributes(start, length, attr.greenPlain);
                     removedBlockStart(end + blockEnd.length(), blockStart,
                           blockEnd, quoteOpt);
                  }
                  else {
                     removedBlockEnd(start, blockStart, blockEnd, quoteOpt);
                  }
               }
               start += length;
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

      private int markupTagEnd(int pos) {
         int end = SyntaxUtils.nextBlockEnd(txt.text(), pos, "<", ">",
               SyntaxUtils.INCLUDE_QUOTED);

         if (end == -1) {
            end = txt.text().indexOf("<", pos);
            if (end == -1) {
               end = txt.text().length();
            }
         }
         return end;
      }

      private void htmlAttributes(String keyword, String tag, int tagStart) {
         int start = 0;
         while (start != -1) {
            start = tag.indexOf(keyword, start);
            if (start != -1) {
               boolean ok = SyntaxUtils.isWord(tag, start, keyword.length(), null);
               if (ok) {
                  txt.setAttributes(start + tagStart, keyword.length(),
                       attr.redPlain);
               }
               start += keyword.length();
            }
         }
      }

      private void xmlAttributes(String tag, int tagStart, int pos) {
         int offset = pos;
         int length = SyntaxUtils.sectionLength(tag, offset,
               SyntaxConstants.XML_TAG_ENDS, null);

         int i = offset;
         while (i < tag.length()) {
            if (!SyntaxUtils.isQuoted(tag, i)
                  && !SyntaxUtils.isCharEqualTo(tag,
                        SyntaxConstants.RESERVED_XML_CHARS, i)) {

               txt.setAttributes(i + tagStart, 1, attr.redPlain);
            }
            i++;
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

      private boolean isInBlock(String blockStart, String blockEnd, int pos,
            int quoteOpt) {

         int lastStart = SyntaxUtils.lastBlockStart(txt.text(), pos, blockStart,
               blockEnd, quoteOpt);

         int nextEnd = -1;
         if (lastStart != -1) {
            nextEnd = SyntaxUtils.nextBlockEnd(txt.text(), pos, blockStart,
               blockEnd, quoteOpt);
         }
         return (lastStart != -1 & nextEnd != -1) && nextEnd != lastStart;
      }

      private void removedBlockStart(int lastEnd, String blockStart,
            String blockEnd, int quoteOpt) {

         if (!isTypeMode) {
            return;
         }
         int nextEnd = SyntaxUtils.nextBlockEnd(txt.text(), lastEnd,
               blockStart, blockEnd, quoteOpt);

         if (innerEnd > 0 && nextEnd > innerEnd) {
             nextEnd = -1;
         }
         if (nextEnd != -1 && nextEnd != lastEnd) {
            int lastStart = SyntaxUtils.lastBlockStart(txt.text(), lastEnd,
                  blockStart, blockEnd, quoteOpt);

            int lineStart;
            if (lastStart == -1) {
               lineStart = lastEnd;
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
            String blockEnd, int quoteOpt) {

         if (!isTypeMode) {
            return;
         }
         int nextStart = SyntaxUtils.nextBlockStart(txt.text(), startPos + 1,
               blockStart, blockEnd, quoteOpt);

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
         if (innerStart > 0) {
            isRepairInnerBlock = true;
         }
         else {
            isRepairBlock = true;
         }
         setTextParams(toRepair, pos, pos);
         hl.highlight(this, attr);
         isRepairInnerBlock = false;
         isRepairBlock = false;
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
