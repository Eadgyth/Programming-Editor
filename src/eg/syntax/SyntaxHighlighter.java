package eg.syntax;

import java.awt.EventQueue;

import javax.swing.text.SimpleAttributeSet;

//--Eadgyth--//
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
    * Highlights text elements in the line where a change happened
    *
    * @param text  the entire text in the document
    * @param chgPos  the position where a change happened
    */
   public void highlightLine(String text, int chgPos) {
      int lineStart = LinesFinder.lastNewline(text, chgPos);
      int lineEnd = LinesFinder.nextNewline(text, chgPos);
      String line = LinesFinder.line(text, lineStart, lineEnd);
      EventQueue.invokeLater(() -> {
         searcher.setTextParams(text, line, chgPos, lineStart + 1);
         hl.highlight(searcher);
      });
   }

   /**
    * Highlights text elements after a text change that may be multiline
    * has been made
    *
    * @param text  the entire text in the document
    * @param change  the change to the text. The full lines that contain
    * the change are scanned
    * @param chgPos  the position where a change happened. 0 if the entire
    * text is scanned
    */
   public void highlightMultiline(String text, String change, int chgPos) {
      String lines = LinesFinder.allLinesAtPos(text, change, chgPos);
      int linesStart = LinesFinder.lastNewline(text, chgPos) + 1;
      searcher.setTextParams(text, lines, chgPos, linesStart);
      hl.highlight(searcher);
   }

   /**
    * The search of text elements and the setting of character attributes in
    * <code>TextDocument</code>.<br>
    * Class is created in the enclosing class and has no public constructor.
    */
   public class SyntaxSearcher {

      private final TextDocument textDoc;

      private String text = "";
      private String section = "";
      private int chgPos;
      private int scnPos;
      private boolean isTypeMode = false;
      private boolean isMultiline = true;
      private boolean isHighlightBlockCmnt = true;
      private int innerStart = 0;
      private int innerEnd = 0;
      private int option = 0;

      /**
       * Sets the section of text for highlighting html elements
       */
      public void setHtmlSection() {
         if (isTypeMode && isHighlightBlockCmnt) {
            String innerScn;
            int start = SyntaxUtils.lastBlockStart(text, chgPos, "<", ">");
            if (start == -1) {
               start = text.lastIndexOf(">", chgPos) + 1;
               if (start == -1) {
                  start = 0;
               }
            }
            int origEnd = scnPos + section.length();
            int end = SyntaxUtils.nextBlockEnd(text, origEnd, "<", ">");
            if (end == -1) {
               end = text.indexOf("<", origEnd);
               if (end == -1) {
                  end = text.length();
               }
            }
            innerScn = text.substring(start, end);
            //System.out.println("html section\n" + innerScn);
            setTextParams(text, innerScn, chgPos, start);
         }
      }

      /**
       * Sets the section of text that is to be highlighted to
       * black and plain
       */
      public void setCharAttrBlack() {
         textDoc.setCharAttrBlack(scnPos, section.length());
      }

      /**
       * Sets the option for testing if highlighting a found text element is
       * enabled.
       * <p>
       * The value is passed to {@link Highlighter#isEnabled(String,int,int)}
       * which is called by all search methods when a text element ist found
       * except {@link #blockComments(String,String)},
       * {@link #htmlElements(String[],String[])} and
       * {@link #embeddedHtmlSection(String,String)}
       *
       * @param option  the option
       */
      public void setOption(int option) {
         this.option = option;
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
      public void keywords(String[] keys, boolean reqWord,
            SimpleAttributeSet set) {

         for (String s : keys) {
            key(s, reqWord, set);
         }
      }

      /**
       * Highlights keywords
       *
       * @param keys  the array of keywords
       * @param nonWordStart  the array of characters that the keyword
       * must not be preceded with, in addition to digits and letters. Can
       * be null
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void keywords(String[] keys, char[] nonWordStart,
            SimpleAttributeSet set) {

         for (String s : keys) {
            key(s, nonWordStart, set);
         }
      }

      /**
       * Highlights an extensible keyword
       *
       * @param keyBase  the base keyword
       * @param keyExtensions  the array of strings that may extend the keyword
       * @param nonWordStart  the array of characters that the keyword
       * must not be preceded with, in addition to digits and letters. Can
       * be null
       * @param set  the <code>SimpleAttributeSet</code> set on the
       * keywords
       */
      public void extensibleKeyword(String keyBase, String[] keyExtensions,
            char[] nonWordStart, SimpleAttributeSet set) {

         key(keyBase, keyExtensions, nonWordStart, set);
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
       * Highlights braces in gray and bold
       */
      public void braces() {
         key("{", false, Attributes.GRAY_BOLD);
         key("}", false, Attributes.GRAY_BOLD);
      }

      /**
       * Highlights brackets in blue and bold
       */
      public void brackets() {
         key("(", false, Attributes.BLUE_BOLD);
         key(")", false, Attributes.BLUE_BOLD);
      }

      /**
       * Highlights text quoted with single or double quotation marks
       * in orange in which quoted text does not span line breaks
       *
       * @param set  the <code>SimpleAttributeSet</code> set on
       * the variables
       */
      public void quotedTextInLines(SimpleAttributeSet set) {
         if (isMultiline) {
            String[] chunkArr = section.split("\n");
            int sum = 0;
            for (String s : chunkArr) {
               quoted(s, scnPos + sum, SyntaxUtils.DOUBLE_QUOTE, set);
               quoted(s, scnPos + sum, SyntaxUtils.SINGLE_QUOTE, set);
               sum += s.length() + 1;
            }
         }
         else {
            quoted(section, scnPos, SyntaxUtils.DOUBLE_QUOTE, set);
            quoted(section, scnPos, SyntaxUtils.SINGLE_QUOTE, set);
         }
      }

      /**
       * Highlights line comments in green
       *
       * @param lineCmntStart  the string that marks the start of a line
       * comment
       */
      public void lineComments(String lineCmntStart) {
         lineCommentsImpl(lineCmntStart);
      }

      /**
       * Highlights block comments in green
       *
       * @param blockCmntStart  the string that marks the start of a comment
       * @param blockCmntEnd  the string that marks the end of a comment
       */
      public void blockComments(String blockCmntStart, String blockCmntEnd) {
         blockCommentsImpl(blockCmntStart, blockCmntEnd);
      }

       /**
       * Highlights html tags with attributes. Tags are shown in blue
       * and bold, attributes in red and attribute values in purple
       *
       * @param tags  the array of html tags
       * @param attributes  the array of html attributes
       */
      public void htmlElements(String[] tags,  String[] attributes) {
         for (String s : tags) {
            htmlElement(s, attributes);
         }
      }

      /**
       * Highlights sections in a html documents with a temporary
       * <code>Highlighter</code> for CSS or Javascript.
       *
       * @param startTag  the start tag
       * @param endTag  the end tag
       * @param hlSection  the {@link Highlighter} for embedded sections
       */
      public void embeddedHtmlSection(String startTag, String endTag,
            Highlighter hlSection) {

         Highlighter hlTemp = hl;
         hl = hlSection;
         embeddedHtmlSection(startTag, endTag);
         hl = hlTemp;
      }

      /**
       * Returns the boolean that, if true, indicates that the position
       * where a change happened is found inside a block comment. Returns
       * false regardless of the position if this section to be highlighted
       * is multiline
       *
       * @param blockCmntStart  the string that marks the start of a comment
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

      private void key(String key, boolean reqWord, SimpleAttributeSet set) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(key, start);
            if (start != -1) {
               int absStart = start + scnPos;
               boolean ok = (!reqWord
                     || SyntaxUtils.isWord(section, start, key.length(), null))
                     && isEnabled(absStart);

               if (ok) {
                  textDoc.setCharAttr(absStart, key.length(), set);
               }
               start += key.length();
            }
         }
      }

      private void key(String key, char[] nonWordStart, SimpleAttributeSet set) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(key, start);
            if (start != -1) {
               int absStart = start + scnPos;
               boolean ok = SyntaxUtils.isWord(section, start, key.length(),
                     nonWordStart)
                     && isEnabled(absStart);

               if (ok) {
                  textDoc.setCharAttr(absStart, key.length(), set);
               }
               start += key.length();
            }
         }
      }

      private void key(String keyBase, String[] keyExtensions, char[] nonWordStart,
            SimpleAttributeSet set) {

         int start = 0;
         while (start != -1) {
            start = section.indexOf(keyBase, start);
            int length = keyBase.length();
            if (start != -1) {
               int absStart = start + scnPos;
               boolean ok = SyntaxUtils.isWord(section, start, keyBase.length(),
                     nonWordStart)
                     && isEnabled(absStart);

               if (ok) {
                  length += keyExtensionLength(keyExtensions,
                        start + keyBase.length(), set);

                  textDoc.setCharAttr(absStart, length, set);
               }
               start += length;
            }
         }
      }

      private int keyExtensionLength(String[] keyExtensions, int extStart,
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
               int absStart = start + scnPos;
               if (SyntaxUtils.isWordStart(section, start, null)
                     && isEnabled(absStart)) {

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
                     for (String s : attributes) {
                         htmlAttribute(s);
                     }
                     quoted(section, scnPos, SyntaxUtils.DOUBLE_QUOTE,
                           Attributes.PURPLE_PLAIN);
                     quoted(section, scnPos, SyntaxUtils.SINGLE_QUOTE,
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

      private void htmlAttribute(String keyword) {
         int start = 0;
         while (start != -1) {
            start = section.indexOf(keyword, start);
            if (start != -1) {
               int absStart = start + scnPos;
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

      private void embeddedHtmlSection(String startTag, String endTag) {
         int start = 0;
         while (start != -1) {
            start = text.toLowerCase().indexOf(startTag, start);
            if (start != -1) {
               int length = 0;
               int end = SyntaxUtils.nextBlockEnd(text.toLowerCase(), start + 1,
                      startTag, endTag);

               if (end != -1) {
                  innerStart = SyntaxUtils.nextBlockEnd(text,
                        start + 1, "<", ">");

                  if (innerStart != -1) {
                     innerEnd = end;
                     String innerScn = null;
                     int scnStart = innerStart + 1;

                     if (isTypeMode) {
                        if (chgPos > start && chgPos < end) {
                           if (chgPos > innerStart
                                 && chgPos < end + endTag.length() - 1) {

                              if (innerStart > scnPos) {
                                 innerScn = text.substring(scnStart,
                                       scnPos + section.length());
                              }
                              else {
                                 innerScn = section;
                                 scnStart = scnPos;
                              }
                           }
                           else {
                              innerScn = text.substring(scnStart, end);
                           }
                        }
                     }
                     if (!isTypeMode || !isHighlightBlockCmnt) {
                        innerScn = text.substring(scnStart, end);
                     }
                     if (innerScn != null) {
                        //System.out.println("inner section\n" + innerScn);
                        setTextParams(text, innerScn, chgPos, scnStart);
                        hl.highlight(this);
                        length = innerScn.length();
                     }
                  }
               }
               start += length + 1;
            }
         }
         innerStart = 0;
         innerEnd = 0;
      }

      private void quoted(String scn, int lineStart, char quoteMark,
            SimpleAttributeSet set) {

         final boolean isSingleQuote = quoteMark == SyntaxUtils.SINGLE_QUOTE;
         int start = 0;
         int end = 0;
         while (start != -1 && end != -1) {
            start = SyntaxUtils.nextNonEscaped(scn, quoteMark, start);
            int absStart = start + lineStart;
            if (start != -1) {
               int length = 0;
               boolean ok = (!isSingleQuote || !SyntaxUtils.isInQuotes(
                     scn, start, SyntaxUtils.DOUBLE_QUOTE))
                     && isEnabled(absStart);

               end = SyntaxUtils.nextNonEscaped(scn, quoteMark, start + 1);
               if (end != -1) {
                  ok = (!isSingleQuote || !SyntaxUtils.isInQuotes(
                        section, end, SyntaxUtils.DOUBLE_QUOTE))
                        && isEnabled(absStart);

                  if (ok) {
                     length = end - start + 1;
                     textDoc.setCharAttr(absStart, length, set);
                  }
               }
               start += length + 1;
            }
         }
      }

      private void lineCommentsImpl(String lineCmntStart) {
         int start = 0;
         while (start != -1) {
            int length = 0;
            start = section.indexOf(lineCmntStart, start);
            if (start != -1) {
               int absStart = start + scnPos;
               if (!isPositionInQuotes(start) && isEnabled(absStart)) {
                  int lineEnd = section.indexOf("\n", start + 1);
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

         if (nextEnd != -1) {
            if (innerEnd > 0 && nextEnd > innerEnd) {
                nextEnd = -1;
            }
            if (nextEnd != -1) {
               String toUncomment = text.substring(endPos,
                     nextEnd + blockCmntEnd.length());

               uncommentBlock(toUncomment, endPos);
            }
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

      private void uncommentBlock(String scn, int pos) {
         if (isTypeMode) {
            //System.out.println("uncomment\n" + scn);
            isHighlightBlockCmnt = false;
            setTextParams(text, scn, pos, pos);
            hl.highlight(this);
            isHighlightBlockCmnt = true;
         }
      }

      private boolean isEnabled(int pos) {
         return hl.isEnabled(text, pos, option);
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

      private void setTextParams(String text, String section, int chgPos,
            int scnPos) {

         this.text = text;
         this.section = section;
         this.chgPos = chgPos;
         this.scnPos = scnPos;
         isTypeMode = text.length() > section.length();
         isMultiline = LinesFinder.isMultiline(section);
      }

      private SyntaxSearcher(TextDocument textDoc) {
         this.textDoc = textDoc;
      }
   }
}
