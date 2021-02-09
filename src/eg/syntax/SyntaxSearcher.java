package eg.syntax;

import javax.swing.text.SimpleAttributeSet;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.styledtext.StyledText;
import eg.document.styledtext.Attributes;

/**
 * The search and highlighting of text elements
 */
public class SyntaxSearcher {

   private final StyledText txt;
   private final Attributes attr;

   private final StringMap quotes = new StringMap();
   private final StringMap triQuotes = new StringMap();
   private final StringOperatorMap stringOp = new StringOperatorMap();
   private final StringMap cData = new StringMap();
   private final List<Integer> lineCmnts = new ArrayList<>();

   private Highlighter hl;
   private String section = "";
   private int chgPos;
   private int scnStart;
   private String lCaseText;
   private boolean isTypeMode = false;
   private boolean isRepair = false;
   private boolean isInnerSection = false;
   private boolean quoteInSection;
   private int nTriQuote;
   private int nCData;
   private int nQuote;
   private int nLineCmnt;
   private int nBlockCmnt;
   private int condition = 0;

   /**
    * Creates a <code>SyntaxSearcher</code>. Not meant to be used by
    * a Highlighter.
    *
    * @param txt  the reference to styled text
    */
   public SyntaxSearcher(StyledText txt) {
      this.txt = txt;
      attr = txt.attributes();
   }

   /**
    * Sets a Highlighter. Not meant to be used by a {@link Highlighter}.
    *
    * @param hl  the Highlighter; null resets any highligting to
    * normal text
    */
   public void setHighlighter(Highlighter hl) {
      if (hl == null && this.hl != null) {
         txt.resetAttributes();
      }
      this.hl = hl;
   }

   /**
    * Sets new text parameters for an updated highlighting.
    * Not meant to be used by a {@link Highlighter}.
    *
    * @param section  the line where a change happened or the
    * completed lines that contain a multiline change
    * @param chgPos  the position where a change happened
    * @param scnStart  the position of the section start
    */
   public void setTextParams(String section, int chgPos, int scnStart) {
      this.section = section;
      this.chgPos = chgPos;
      this.scnStart = scnStart;
      isTypeMode = txt.text().length() > section.length();
      lineCmnts.clear();
      triQuotes.reset();
      stringOp.reset();
      quotes.reset();
      cData.reset();
   }

   /**
    * Modifies the section to update for markup text elements
    */
   public void setMarkupSection() {
      if (!isTypeMode || isRepair) {
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
    * Expands the section to update to a block ranging from the
    * last to the next <code>mark</code>. Any occurence of the
    * mark in quotes is ignored, however,
    *
    * @param mark  the mark
    */
   public void setBlockSection(String mark) {
      if (!isTypeMode || isRepair) {
         return;
      }
      setBlockSection(mark, true);
   }

   /**
    * Resets the attributes in the section to update
    */
   public void resetAttributes() {
      txt.resetAttributes(scnStart, section.length());
   }

   /**
    * Sets a condition for validating found text elements. The
    * condition is passed to {@link Highlighter#isValid} by this
    * search methods (where indicated) if a text element is found.
    *
    * @param condition  a freely chosen integer. Default is 0 at the
    * beginning of an updated highlighting
    */
   public void setCondition(int condition) {
      this.condition = condition;
   }

   /**
    * Searches text blocks in triple quotes and highlights in orange.
    * Calls {@link Highlighter#isValid} for opening text block marks
    *
    * @param inclSingleQuotes  true to include triple quotes typed
    * with single quotes; false for double quotes only
    */
   public void tripleQuoteTextBlocks(boolean inclSingleQuotes) {
      int i = -1;
      int count = 0;
      int start = 0;
      boolean change = false;
      while (start != -1 && ! change) {
         int iDouble = txt.text().indexOf(SyntaxConstants.TRI_DOUBLE_QUOTE, start);
         String mark = SyntaxConstants.TRI_DOUBLE_QUOTE;
         boolean isDouble = true;
         if (inclSingleQuotes) {
            int iSingle = txt.text().indexOf(SyntaxConstants.TRI_SINGLE_QUOTE, start);
            isDouble = SyntaxUtils.firstOccurence(iDouble, iSingle);
            start = isDouble ? iDouble : iSingle;
            mark = isDouble ? mark : SyntaxConstants.TRI_SINGLE_QUOTE;
         }
         else {
            start = iDouble;
         }
         if (start != -1) {
            int len = 2;
            int lc = hl.behindLineCmntMark(txt.text(), start);
            int bc = hl.inBlockCmntMarks(txt.text(), start);
            boolean inCmnt = (lc != -1 && !inQuotes(lc)) || (bc != -1 && !inQuotes(bc));
            if (!inCmnt && isValid(start)) {
               count++;
               int end = txt.text().indexOf(mark, start + 3);
               if (end != -1) {
                  i++;
                  count++;
                  len = end - start + 3;
                  triQuotes.add(start, end);
                  change = !isRepair && !inclSingleQuotes
                        &&triQuotes.quoteMarkChange(i, isDouble);

                  txt.setAttributes(start, len, attr.orangePlain);
               }
            }
            start += len;
         }
      }
      if (nTriQuote != count || change) {
         repair(txt.text(), 0);
      }
      nTriQuote = count;
   }

   /**
    * Searches heredocs. Method should be called before
    * {@link #quote(boolean)}
    *
    * @param hds  the reference to HeredocSearch
    */
   public void mapHeredocs(HeredocSearch hds) {
      String text = isInnerSection ? section : txt.text();
      int start = isInnerSection ? scnStart : 0;
      if (stringOp.addHeredocs(hds, text, start, isRepair)) {
         repair(txt.text(), 0);
      }
   }

   /**
    * Searches quote operators. Method should be called before
    * {@link #quote(boolean)}
    *
    * @param qos  the reference to QuoteOperatorSearch
    */
   public void mapQuoteOperators(QuoteOperatorSearch qos) {
      if (stringOp.addQuoteOperators(qos, txt.text(), isRepair)) {
         repair(txt.text(), 0);
      }
   }

   /**
    * Searches quoted text in single and double quotes and highlights
    * in orange. Calls {@link Highlighter#isValid} for opening quotes.
    *
    * @param singleLine  true for quotations within lines only, false
    * to allow multiline quotations
    */
   public void quote(boolean singleLine) {
      quoteInSection = singleLine;
      if (quoteInSection) {
         if (LinesFinder.isMultiline(section)) {
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
      else {
         if (isInnerSection) {
            quote(section, scnStart, attr.orangePlain);
         }
         else {
            quote(txt.text(), 0, attr.orangePlain);
         }
      }
   }

   /**
    * Searches line comments and highlights in green.
    * Calls {@link Highlighter#isValid}.
    *
    * @param marks  the possible marks for a line comment start
    */
   public void lineComments(String[] marks) {
      String scn = isInnerSection ? section : txt.text();
      int count = 0;
      for (String mark : marks) {
         int start = 0;
         while (start != -1) {
            int len = mark.length();
            start = scn.indexOf(mark, start);
            if (start != -1) {
               count++;
               int absStart = isInnerSection ? start + scnStart : start;
               if (!inString(absStart, false) && isValid(absStart)) {
                  lineCmnts.add(absStart);
                  int lineEnd = scn.indexOf('\n', start);
                  len = lineEnd != -1 ? lineEnd - start : scn.length() - start;
                  if (scnStart <= absStart && absStart <= scnStart + section.length()) {
                     txt.setAttributes(absStart, len, attr.greenPlain);
                  }
               }
               start += len;
            }
         }
      }
      if (!quoteInSection && nLineCmnt != count) {
         repair(txt.text(), 0);
      }
      if (!isInnerSection) {
         nLineCmnt = count;
      }
   }

   /**
    * Searches and highlights keywords.
    * Calls {@link Highlighter#isValid}
    *
    * @param keys  the array of keywords
    * @param nonStart  the characters that must not precede a
    * keyword. Can be null.
    * @param set  the SimpleAttributeSet set on the keywords
    */
   public void keywords(String[] keys, char[] nonStart, SimpleAttributeSet set) {
      for (String s : keys) {
         key(s, true, nonStart, set);
      }
   }

   /**
    * Searches and highlights keywords without case sensitivity.
    * Calls {@link Highlighter#isValid}.
    *
    * @param keys  the array of keywords
    * @param nonStart  the characters that must not precede a
    * keyword. Can be null.
    * @param set  the SimpleAttributeSet set on the keywords
    */
   public void keywordsIgnoreCase(String[] keys, char[] nonStart,
         SimpleAttributeSet set) {

      String scn = section;
      section = section.toLowerCase();
      for (String s : keys) {
         key(s, true, nonStart, set);
      }
      section = scn;
   }

   /**
    * Searches and highlights an extensible keyword.
    * Calls {@link Highlighter#isValid}.
    *
    * @param base  the base keyword
    * @param extensions  the array of keywords that may extend base
    * @param nonStart  the characters that must not precede the
    * keyword. Can be null and is ignored if word is false
    * @param set  the SimpleAttributeSet set on the keywords
    */
   public void extensibleKeyword(String base, String[] extensions, char[] nonStart,
         SimpleAttributeSet set) {

      int start = 0;
      while (start != -1) {
         start = section.indexOf(base, start);
         int len = base.length();
         if (start != -1) {
            int absStart = start + scnStart;
            len += extensionLength(extensions, start + base.length());
            if (SyntaxUtils.isWord(section, start, len, nonStart) && isValid(absStart)) {
               txt.setAttributes(absStart, len, set);
            }
            start += len;
         }
      }
   }

   /**
    * Searches and highlights variables that starts with a character
    * in the specified signs. Calls {@link Highlighter#isValid}.
    *
    * @param signs  the start signs
    * @param endMarks  the marks for the end of the vatiable
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
    * Searches opening and closing braces and highlights in bold.
    * Calls {@link Highlighter#isValid}.
    */
   public void braces() {
      key("{", false, null, attr.bracketsBold);
      key("}", false, null, attr.bracketsBold);
   }

   /**
    * Searches opening and closing brackets and highlights in bold
    * Calls {@link Highlighter#isValid}.
    */
   public void brackets() {
      key("(", false, null, attr.bracketsBold);
      key(")", false, null, attr.bracketsBold);
   }

   /**
    * Searches blocks comments and highlights in green
    *
    * @param blockStart  the block start
    * @param blockEnd  the block end
    * @param ignoreQuotes  true to ignore if the block start is quoted
    */
   public void blockComments(String blockStart, String blockEnd,
         boolean ignoreQuotes) {

      setBlockSection(blockEnd, ignoreQuotes);
      int count = 0;
      int start = 0;
      while (start != -1) {
         start = nextBlockCmntStart(blockStart, start, ignoreQuotes);
         int len = 1;
         if (start != -1) {
            count++;
            int searchStart = start + blockStart.length();
            int end = section.indexOf(blockEnd, searchStart);
            int nextStart = nextBlockCmntStart(blockStart, searchStart, ignoreQuotes);
            if (end > nextStart && nextStart != -1) {
               count++;
            }
            if (end != -1) {
               count++;
               int nextEnd = section.indexOf(blockEnd, end + blockEnd.length());
               if (nextEnd != -1 && (nextStart == -1 || nextEnd < nextStart)) {
                  count++;
               }
               len = end - start + blockEnd.length();
               txt.setAttributes(start + scnStart, len, attr.greenPlain);
            }
            start += len;
         }
      }
      if (nBlockCmnt != count) {
         repair(section, scnStart);
      }
      if (!isInnerSection) {
         nBlockCmnt = count;
      }
   }

   /**
    * Searches and highlights markup elements. Tag names are blue,
    * attributes red and quoted attribute values purple. Block
    * comments are highlighted in green and CDATA blocks are displayed
    * as normal text.
    *
    * @param html  true for html, false for xml
    */
   public void markup(boolean html) {
      quoteInSection = true;
      String scn = html ? section.toLowerCase() : section;
      lCaseText = html ? txt.text().toLowerCase() : txt.text();
      int start = 0;
      while (start != -1) {
         start = scn.indexOf('<', start);
         if (start != -1) {
            int len = 0;
            boolean isEndTag = scn.length() > start + 1 && scn.charAt(start + 1) == '/';
            int offset = isEndTag ? start + 2 : start + 1;
            boolean isTagStart = false;
            if (section.length() > offset) {
               char test = section.charAt(offset);
               isTagStart = Character.isLetter(test) || (test == '_' && !html);
            }
            if (isTagStart) {
               len = xmlKeywordLength(scn, offset, html);
               if (SyntaxUtils.isWordEnd(scn, offset + len)) {
                  if (!isEndTag) {
                     int absStart = start + scnStart;
                     int tagEnd = markupTagEnd(absStart + 1);
                     String tag = lCaseText.substring(absStart, tagEnd);
                     quote(tag, absStart, attr.purplePlain);
                     if (html) {
                        htmlAttributes(tag, absStart);
                     }
                     else {
                        xmlAttributes(tag, absStart, len);
                     }
                  }
                  int colorStart = offset + scnStart;
                  txt.setAttributes(colorStart, len, attr.bluePlain);
               }
            }
            start += len + 1;
         }
      }
      cDataTextBlock(SyntaxConstants.CDATA_BLOCK_START,
            SyntaxConstants.CDATA_BLOCK_END);
      blockComments(SyntaxConstants.HTML_BLOCK_CMNT_START,
            SyntaxConstants.HTML_BLOCK_CMNT_END, true);
   }

   /**
    * Searches and highlights sections embedded in HTML
    *
    * @param startTag  the start tag without closing bracket
    * @param endTag  the end tag
    * @param reqClosingBracket  true if the start tag needs a
    * closing bracket; false otherwise
    * @param hlSection  the Highlighter for the section
    */
   public void innerSections(String startTag, String endTag,
         boolean reqClosingBracket, Highlighter hlSection) {

      int start = 0;
      while (start != -1) {
         start = lCaseText.indexOf(startTag, start);
         int len = 0;
         if (start != -1) {
            if (-1 == SyntaxUtils.inBlock(txt.text(),
                  SyntaxConstants.HTML_BLOCK_CMNT_START,
                  SyntaxConstants.HTML_BLOCK_CMNT_END, start)) {

               int searchStart = start + 1;
               int end = SyntaxUtils.nextBlockEnd(txt.text(), searchStart, startTag,
                     endTag);

               if (end != -1) {
                  int startTagEnd = reqClosingBracket ?
                        1 + SyntaxUtils.nextBlockEnd(txt.text(), searchStart, "<", ">")
                        : start + startTag.length();

                  if (startTagEnd != 0) {
                     isInnerSection = true;
                     String scn = txt.text().substring(startTagEnd, end);
                     setTextParams(scn, chgPos, startTagEnd);
                     Highlighter hlCurr = hl;
                     hl = hlSection;
                     hl.highlight(this, attr);
                     hl = hlCurr;
                     len = scn.length();
                  }
                  isInnerSection = false;
               }
            }
            start += len + 1;
         }
      }
   }

   //
   //--private--/
   //

   private void key(String key, boolean word, char[] nonStart, SimpleAttributeSet set) {
      int start = 0;
      while (start != -1) {
         start = section.indexOf(key, start);
         if (start != -1) {
            int absStart = start + scnStart;
            if ((!word || SyntaxUtils.isWord(section, start, key.length(), nonStart))
                  && isValid(absStart) && !inString(absStart, false)
                  && !inLineCmnt(absStart)) {

               txt.setAttributes(absStart, key.length(), set);
            }
            start += key.length();
         }
      }
   }

   private void signedVariable(char sign, char[] endMarks, char[] successors,
         SimpleAttributeSet set) {

      int start = 0;
      while (start != -1) {
         start = section.indexOf(sign, start);
         if (start != -1) {
            int len = 1;
            int absStart = start + scnStart;
            if (isValid(absStart) && !inLineCmnt(absStart)
                  && !inString(absStart, false)) {

               len = SyntaxUtils.sectionLength(section, start, endMarks,
                  successors);

               txt.setAttributes(absStart, len, set);
            }
            start += len;
         }
      }
   }

   private int extensionLength(String[] extensions, int extStart) {
      int len = 0;
      for (String s : extensions) {
         boolean found = extStart == section.indexOf(s, extStart)
               && SyntaxUtils.isWordEnd(section, extStart + s.length());

         if (found && s.length() > len) {
            len = s.length();
         }
      }
      return len;
   }

   private int markupTagEnd(int pos) {
      int end = SyntaxUtils.nextBlockEnd(txt.text(), pos, "<", ">");
      if (end == -1) {
         end = txt.text().indexOf('<', pos);
         if (end == -1) {
            end = txt.text().length();
         }
      }
      return end;
   }

   private int xmlKeywordLength(String section, int start, boolean html) {
      return html ?
            SyntaxUtils.wordLength(section, start, SyntaxConstants.HTML_TAGS)
            : SyntaxUtils.sectionLength(section, start,
                  SyntaxConstants.RES_XML_CHARS, null);
   }

   private void htmlAttributes(String tag, int tagStart) {
      for (String s : SyntaxConstants.HTML_ATTR) {
         int start = 0;
         while (start != -1) {
            start = tag.indexOf(s, start);
            if (start != -1) {
               if (SyntaxUtils.isWord(tag, start, s.length(), null)) {
                  txt.setAttributes(start + tagStart, s.length(), attr.redPlain);
               }
               start += s.length();
            }
         }
      }
   }

   private void xmlAttributes(String tag, int tagStart, int pos) {
      int offset = pos;
      int i = offset;
      while (i < tag.length()) {
         if (!SyntaxUtils.isQuoted(tag, i)
               && !SyntaxUtils.isCharEqualTo(tag, i, SyntaxConstants.RES_XML_CHARS)) {

            txt.setAttributes(i + tagStart, 1, attr.redPlain);
         }
         i++;
      }
   }

   private void cDataTextBlock(String startTag, String endTag) {
      int count = 0;
      int start = 0;
      while (start != -1) {
         start = txt.text().indexOf(startTag, start);
         int len = 1;
         if (start != -1) {
            count++;
            int tagEnd = start + startTag.length();
            int end = txt.text().indexOf(endTag, tagEnd);
            int nextStart = txt.text().indexOf(startTag, tagEnd);
            if (end > nextStart && nextStart != -1) {
               count++;
            }
            if (end != -1) {
               int nextEnd = txt.text().indexOf(endTag, end + endTag.length());
               if (nextEnd != -1 && (nextStart == -1 || nextEnd < nextStart)) {
                  count++;
               }
               len = end - start + endTag.length();
               count++;
               cData.add(start, end);
               txt.resetAttributes(start, len);
            }
            start += len;
         }
      }
      if (nCData != count) {
         repair(txt.text(), 0);
      }
      nCData = count;
   }

   private void quote(String scn, int scnPos, SimpleAttributeSet set) {
      int i = -1;
      int count = 0;
      int check = -1;
      int start = 0;
      boolean change = false;
      while (start != -1 && !change) {
         int iDouble = scn.indexOf(SyntaxConstants.DOUBLE_QUOTE, start);
         int iSingle = scn.indexOf(SyntaxConstants.SINGLE_QUOTE, start);
         boolean isDouble = SyntaxUtils.firstOccurence(iDouble, iSingle);
         start = isDouble ? iDouble : iSingle;
         char mark = isDouble ? SyntaxConstants.DOUBLE_QUOTE
               : SyntaxConstants.SINGLE_QUOTE;

         int absStart = start + scnPos;
         if (start != -1) {
            int len = 1;
            if (quotable(check, absStart) && isValid(absStart)) {
               count++;
               int end = SyntaxUtils.nextNotEscaped(scn, mark, start + 1);
               if (end != -1) {
                  i++;
                  count++;
                  int absEnd = end + scnPos;
                  len = absEnd - absStart + 1;
                  quotes.add(absStart, absEnd);
                  change = isTypeMode && !isInnerSection
                           && !isRepair && quotes.quoteMarkChange(i, isDouble);

                  check = quotableStart(absEnd);
                  int scnEnd = scnStart + section.length();
                  if (quoteInSection || isRepair
                        || ((absStart >= scnStart && scnEnd > absStart)
                        || (scnStart > absStart && scnEnd < absEnd)
                        || (absEnd >= scnStart && absEnd <= scnEnd))) {

                      txt.setAttributes(absStart, len, set);
                  }
               }
            }
            start += len;
         }
      }
      if (!quoteInSection && (nQuote != count || change)) {
         repair(txt.text(), 0);
      }
      nQuote = count;
   }

   private boolean quotable(int prevMark, int pos) {
      int nextMark = quotableStart(pos);
      return -1 == nextMark || (nextMark != -1 && prevMark == nextMark);
   }

   private int quotableStart(int pos) {
      String text = isInnerSection ? section : txt.text();
      int diff = isInnerSection ? scnStart : 0;
      int i = hl.inBlockCmntMarks(text, pos - diff);
      if (i != -1) {
         i += diff;
      }
      if (i == -1) {
         i = stringOp.inEitherString(pos);
      }
      if (i != -1) {
         int lc = hl.behindLineCmntMark(text, i - diff);
         i = (lc == -1 || inQuotes(lc + diff )) ? i : -1;
      }
      if (i == -1) {
         int lc = hl.behindLineCmntMark(text, pos - diff);
         i = (lc != -1 && -1 == stringOp.inQuoteOperator(lc)) ? lc + diff : -1;
      }
      if (i != -1 && quoteInSection) {
         i = inQuotes(i) ? -1 : i;
      }
      return i;
   }

   private void setBlockSection(String blockEnd, boolean ignoreQuotes) {
      if (!isTypeMode || isInnerSection || isRepair) {
         return;
      }
      int start = 0;
      int end = txt.text().length();
      int lastEnd = lastBlockCmntEnd(blockEnd, scnStart, ignoreQuotes);
      if (lastEnd != -1) {
         if (lastEnd == scnStart) {
            lastEnd = lastBlockCmntEnd(blockEnd, lastEnd - 1, ignoreQuotes);
         }
         if (lastEnd != -1) {
            start = lastEnd + blockEnd.length();
         }
      }
      int nextEnd = txt.text().indexOf(blockEnd, scnStart + section.length());
      if (nextEnd != -1) {
         end = LinesFinder.nextNewline(txt.text(), nextEnd);
      }
      scnStart = start;
      section = txt.text().substring(start, end);
   }

   private int nextBlockCmntStart(String blockStart, int pos, boolean ignoreQuotes) {
      int i = section.indexOf(blockStart, pos);
      int absPos = i + scnStart;
      while (i != -1 && (inString(absPos, ignoreQuotes) || inLineCmnt(absPos))) {
         i = section.indexOf(blockStart, i + 1);
         absPos = i + scnStart;
      }
      return i;
   }

   private int lastBlockCmntEnd(String blockEnd, int pos, boolean ignoreQuotes) {
      int i = txt.text().lastIndexOf(blockEnd, pos);
      while (i != -1 && (inString(i, ignoreQuotes) || inLineCmnt(i))) {
         i = txt.text().lastIndexOf(blockEnd, i - blockEnd.length());
      }
      return i;
   }

   private boolean inString(int pos, boolean ignoreQuotes) {
      boolean b = !ignoreQuotes && inQuotes(pos);
      if (!b) {
         if (!quoteInSection) {
            int inStrOp = stringOp.inEitherString(pos);
            b = (inStrOp != -1 && !inQuotesOrLineCmnt(inStrOp));
         }
         else {
            int inTriQuote = triQuotes.inString(pos);
            b = (inTriQuote != -1 && !inLineCmnt(inTriQuote))
                  || -1 != cData.inString(pos); // both is n/a
         }
      }
      return b;
   }

   private boolean inQuotesOrLineCmnt(int pos) {
      return inQuotes(pos) || inLineCmnt(pos);
   }

   private boolean inQuotes(int pos) {
      return quoteInSection ?
          SyntaxUtils.isQuotedInLine(txt.text(), pos)
          : -1 != quotes.inString(pos);
   }

   private boolean inLineCmnt(int pos) {
      int lastNewline = LinesFinder.lastNewline(txt.text(), pos);
      for (int i : lineCmnts) {
         if (i > lastNewline && pos > i) {
            return true;
         }
      }
      return false;
   }

   private void repair(String toRepair, int pos) {
      if (isRepair || !isTypeMode || isInnerSection) {
         return;
      }
      isRepair = true;
      setTextParams(toRepair, pos, pos);
      hl.highlight(this, attr);
      isRepair = false;
   }

   private boolean isValid(int pos) {
      return hl.isValid(txt.text(), pos, condition);
   }
}
