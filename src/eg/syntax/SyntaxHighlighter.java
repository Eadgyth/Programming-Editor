package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

import java.util.List;
import java.util.ArrayList;

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
    * Highlights text elements in a section around the position
    * where a change happened. The section is initially the line
    * that contains <code>chgPos</code> or the two lines around
    * this position if <code>isNewline</code> is true.
    *
    * @param chgPos  the position where a change happened
    * @param isNewline  if the change is a newline character
    */
   public void highlight(int chgPos, boolean isNewline) {
      int lineStart = LinesFinder.lastNewline(txt.text(), chgPos);
      String scn;
      if (!isNewline) {
         scn = LinesFinder.line(txt.text(), lineStart);
      }
      else {
         int lineEnd = LinesFinder.nextNewline(txt.text(), chgPos + 1);
         scn = txt.text().substring(lineStart + 1, lineEnd);
      }
      searcher.setTextParams(scn, chgPos, lineStart + 1);
      hl.highlight(searcher, attr);
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
      String scn = LinesFinder.lines(txt.text(), linesStart, length);
      searcher.setTextParams(scn, chgPos, linesStart + 1);
      hl.highlight(searcher, attr);
   }

   /**
    * The search and highlighting of text elements
    */
   public class SyntaxSearcher {

      private final List<Integer> lineCmntStarts = new ArrayList<>();

      private String section = "";
      private int chgPos;
      private int scnStart;
      private boolean isTypeMode = false;
      private boolean isMultiline = true;
      private boolean isRepairBlock = false;
      private boolean isRepairInnerBlock = false;
      private boolean isQuoteInSection;
      private int nDoubleQuote;
      private int nSingleQuote;
      private int nTriDoubleQuote;
      private int nTriSingleQuote;
      private int innerStart = 0;
      private int innerEnd = 0;
      private int condition = 0;

      /**
       * Sets a condition for validating found text elements. The
       * condition is passed to {@link Highlighter#isValid} by this
       * search methods (where indicated) if a text element is
       * found.
       *
       * @param condition  a freely chosen integer. Default is 0
       */
      public void setCondition(int condition) {
         this.condition = condition;
      }

      /**
       * Sets the section to update that comprises semicolon
       * separated statements which may be multiline.
       * Calls {@link Highlighter#isValid}: If the change position
       * is in an invalid region for limiting the update to a
       * semicolon separated region the section ranges from the
       * nearest semicolon that is found in a valid region before
       * the change to the text end
       */
      public void setStatementSection() {
         if (!isTypeMode || isRepairBlock) {
            return;
         }
         int start = SyntaxUtils.lastUnquoted(txt.text(), ";", scnStart);
         int end = txt.text().length();
         boolean extend = !isValid(chgPos, 0);
         if (extend) {
            while (start != -1 && (scnStart - start < 1 || !isValid(start, 0))) {
               start = SyntaxUtils.lastUnquoted(txt.text(), ";", start - 1);
            }
         }
         else {
            int next = SyntaxUtils.nextUnquoted(txt.text(), ";", scnStart);
            end = next != -1 ? next : end;
         }
         start = start != -1 ? start + 1 : 0;
         end = LinesFinder.nextNewline(txt.text(), end);
         scnStart = start;
         section = txt.text().substring(scnStart, end);
      }

      /**
       * Sets the section to update that takes into account
       * markup language tags
       */
      public void setMarkupSection() {
         if (!isTypeMode || isRepairBlock) {
            return;
         }
         int start = txt.text().lastIndexOf('<', chgPos);
         if (start == -1) {
            start = 0;
         }
         int end = markupTagEnd(scnStart + section.length() + 1);
         scnStart = start;
         section = txt.text().substring(start, end);
      }

      /**
       * Resets the attributes in the section that is defined
       * to be updated after a text change. By default this
       * section is the line where a change happened.
       *
       * @see #setMarkupSection()
       * @see #setStatementSection()
       */
      public void resetAttributes() {
         txt.resetAttributes(scnStart, section.length());
      }

      /**
       * Searches and highlights keywords.
       * Calls {@link Highlighter#isValid}.
       *
       * @param keys  the array of keywords
       * @param reqWord  true to require that keywords are whole
       * words, false otherwise
       * @param nonWordStart  the array of characters that must
       * not precede the keyword. Can be null and ignored if
       * reqWord is false
       * @param set  the SimpleAttributeSet set on the keywords
       */
      public void keywords(String[] keys, boolean reqWord, char[] nonWordStart,
            SimpleAttributeSet set) {

         for (String s : keys) {
            key(s, reqWord, nonWordStart, set);
         }
      }

      /**
       * Searches and highlights keywords without case sensitivity.
       * Calls {@link Highlighter#isValid}.
       *
       * @param keys  the array of keywords
       * @param reqWord  true to require that keywords are whole words
       * @param nonWordStart  the array of characters that must not precede
       * the keyword. Can be null and is ignored if reqWord is false
       * @param set  the SimpleAttributeSet set on the keywords
       */
      public void keywordsIgnoreCase(String[] keys, boolean reqWord,
            char[] nonWordStart, SimpleAttributeSet set) {

         String scn = section;
         section = section.toLowerCase();
         for (String s : keys) {
            key(s, reqWord, nonWordStart, set);
         }
         section = scn;
      }

      /**
       * Searches and highlights an extensible keyword.
       * Calls {@link Highlighter#isValid}.
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
       * Searches and highlights variables that one of the
       * specified signs precedes.
       * Calls {@link Highlighter#isValid}.
       *
       * @param signs  the signs for the start of the variables
       * @param endMarks  the marks for the end of the variables
       * @param successors  the characters that disable endMarks
       * if they directly follow a sign
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
       * precedes.
       * Calls {@link Highlighter#isValid}.
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
       * bold.
       * Calls {@link Highlighter#isValid}.
       */
      public void braces() {
         key("{", false, null, attr.bracketsBold);
         key("}", false, null, attr.bracketsBold);
      }

      /**
       * Searches and highlights opening and closing brackets in blue and
       * bold.
       * Calls {@link Highlighter#isValid}.
       */
      public void brackets() {
         key("(", false, null, attr.bracketsBold);
         key(")", false, null, attr.bracketsBold);
      }

      /**
       * Searches and highlights quoted text in single or double
       * quotation marks in orange.
       * Calls {@link Highlighter#isValid}.
       */
      public void quote() {
         isQuoteInSection = false;
         quote(txt.text(), 0, attr.orangePlain);
      }

      /**
       * Searches and highlights quoted text in single or double
       * quotation marks in orange whereby a quotation must be found
       * inside a line.
       * Calls {@link Highlighter#isValid}.
       */
      public void quoteInLine() {
         isQuoteInSection = true;
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
         isQuoteInSection = true;
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

               int offset = isEndTag ? start + 2 : start + 1;
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
            removedBlockStart(0, startTag, endTag, SyntaxUtils.IGNORE_QUOTED);
         }
         String t = txt.text().toLowerCase();
         int start = 0;
         while (start != -1) {
            start = t.indexOf(startTag, start);
            int length = 0;
            if (start != -1) {
               boolean isInCmnt = SyntaxUtils.isInBlock(
                     txt.text(),
                     SyntaxConstants.HTML_BLOCK_CMNT_START,
                     SyntaxConstants.HTML_BLOCK_CMNT_END, start,
                     SyntaxUtils.IGNORE_QUOTED);

               if (!isInCmnt) {
                  int searchStart = start + 1;
                  int end = SyntaxUtils.nextBlockEnd(t, searchStart, startTag,
                       endTag, SyntaxUtils.IGNORE_QUOTED);

                  if (end != -1) {
                     int startTagEnd;
                     if (reqClosingBracket) {
                        startTagEnd = SyntaxUtils.nextBlockEnd(t, searchStart,
                              "<", ">", SyntaxUtils.IGNORE_QUOTED);

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
                              endTag, SyntaxUtils.IGNORE_QUOTED);
                     }
                  }
                  else if (!isRepairBlock) {
                     removedBlockEnd(start, startTag, endTag,
                           SyntaxUtils.IGNORE_QUOTED);
                  }
               }
               start += length + 1;
            }
         }
      }

      /**
       * Searches and highlights text blocks surrounded by the specified
       * delimiter in orange.
       * Calls {@link Highlighter#isValid}.
       *
       * @param del  the delimiter
       */
      public void textBlock(String del) {
         int count = 0;
         int start = 0;
         while (start != -1) {
            start = txt.text().indexOf(del, start);
            if (start != -1) {
               int length = skippedLineCmntLength(start);
               if (length == 0) {
                  count++;
                  length = del.length();
                  int next = txt.text().indexOf(del, start + del.length());
                  if (next != -1) {
                     count++;
                     int l = next - start + del.length();
                     if (isValid(start, l)) {
                        length = l;
                        txt.setAttributes(start, length, attr.orangePlain);
                     }
                  }
               }
               start += length;
            }
         }
         textBlockChange(del, count);
      }

      /**
       * Searches and highlights line comments in green.
       * Calls {@link Highlighter#isValid}.
       *
       * @param lineCmntStart  the line comment start
       * @param quoteOpt  the option to define if matches in quotes
       * are skipped: one of {@link SyntaxUtils#IGNORE_QUOTED},
       * {@link SyntaxUtils#BLOCK_QUOTED} and {@link SyntaxUtils#LINE_QUOTED}
       */
      public void lineComments(String lineCmntStart, int quoteOpt) {
         int start = 0;
         while (start != -1) {
            int length = 0;
            start = section.indexOf(lineCmntStart, start);
            if (start != -1) {
               int absStart = start + scnStart;
               if (!SyntaxUtils.isQuoted(section, start, quoteOpt)
                     && isValid(absStart, lineCmntStart.length())) {

                  lineCmntStarts.add(absStart);
                  int lineEnd = section.indexOf('\n', start);
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
       * are skipped: one of {@link SyntaxUtils#IGNORE_QUOTED},
       * {@link SyntaxUtils#BLOCK_QUOTED} and {@link SyntaxUtils#LINE_QUOTED}
       * @return  true if inside a block comment; false otherwise. Also false
       * regardless of the position if the text change is multiline
       */
      public boolean isInBlock(String blockStart, String blockEnd,
            int quoteOpt) {

         if (isMultiline) {
            return false;
         }
         else {
            return SyntaxUtils.isInBlock(txt.text(), blockStart, blockEnd, chgPos,
                  quoteOpt);
         }
      }

      /**
       * Searches and highlights a block in green
       *
       * @param blockStart  the block start
       * @param blockEnd  the block end
       * @param quoteOpt  the option to define if matches in quotes
       * are skipped: IGNORE_QUOTED, BLOCK_QUOTED, LINE_QUOTED
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
               if (quoteOpt == SyntaxUtils.IGNORE_QUOTED
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

            if (found && s.length() > length) {
               length = s.length();
            }
         }
         return length;
      }

      private int markupTagEnd(int pos) {
         int end = SyntaxUtils.nextBlockEnd(txt.text(), pos, "<", ">",
               SyntaxUtils.IGNORE_QUOTED);

         if (end == -1) {
            end = txt.text().indexOf('<', pos);
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

         final char altQuoteMark = quoteMark == SyntaxConstants.DOUBLE_QUOTE ?
               SyntaxConstants.SINGLE_QUOTE : SyntaxConstants.DOUBLE_QUOTE;

         int count = 0;
         int start = 0;
         while (start != -1) {
            start = scn.indexOf(quoteMark, start);
            int absStart = start + scnPos;
            if (start != -1) {
               int length = skippedLineCmntLength(absStart);
               if (length == 0) {
                  boolean ok = !SyntaxUtils.isQuoted(scn, start, altQuoteMark)
                        && isValid(start + scnPos, 1);

                  if (ok) {
                     count++;
                  }
                  int end = SyntaxUtils.nextNonEscaped(scn, quoteMark, start + 1);
                  length = 1;
                  if (end != -1) {
                     ok = ok && isValid(end + scnPos, 1);
                     if (ok) {
                        count++;
                        length = end - start + 1;
                        txt.setAttributes(absStart, length, set);
                     }
                  }
               }
               start += length;
            }
         }
         if (!isQuoteInSection) {
            quoteChange(Character.toString(quoteMark), count);
         }
      }

      private int skippedLineCmntLength(int pos) {
         int length = 0;
         int nextNewline = LinesFinder.nextNewline(txt.text(), pos);
         int lastNewline = LinesFinder.lastNewline(txt.text(), pos);
         for (int i : lineCmntStarts) {
            if (i > lastNewline && i < pos) {
               length = nextNewline - pos;
               break;
            }
         }
         return length;
      }

      private void quoteChange(String quoteMark, int count) {
         if (isRepairBlock || !isTypeMode) {
            return;
         }
         boolean b = false;
         switch(quoteMark) {
            case SyntaxConstants.DOUBLE_QUOTE_STR:
               b = nDoubleQuote != count;
               nDoubleQuote = count;
               break;

            case SyntaxConstants.SINGLE_QUOTE_STR:
               b = nSingleQuote != count;
               nSingleQuote = count;
               break;
            default:
               break;
         }
         if (b) {
            repair(txt.text(), 0);
         }
      }

      private void textBlockChange(String del, int count) {
         if (isRepairBlock || !isTypeMode) {
            return;
         }
         boolean b = false;
         switch(del) {
            case SyntaxConstants.TRI_DOUBLE_QUOTE:
               b = nTriDoubleQuote != count;
               nTriDoubleQuote = count;
               break;

            case SyntaxConstants.TRI_SINGLE_QUOTE:
               b = nTriSingleQuote != count;
               nTriSingleQuote = count;
               break;
            default:
               break;
         }
         if (b) {
            repair(txt.text(), 0);
         }
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
            repair(toRepair, lineStart);
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
         repair(toRepair, startPos);
      }

      private void repair(String toRepair, int pos) {
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
         lineCmntStarts.clear();
      }

      private SyntaxSearcher() {}
   }
}
