package eg.syntax;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.utils.LinesFinder;
import eg.document.styledtext.StyledText;

/**
 * The mapping of strings defined by operators, i.e., heredocs and
 * those named 'quote operator'
 */
public class StringOperatorMap {

   private final StringMap heredocs = new StringMap();
   private final StringMap quoteOprs = new StringMap();
   private final StyledText txt;
   //
   // to remember after reset
   private final List<Integer> hLengths = new ArrayList<>();
   private final List<Integer> qLengths = new ArrayList<>();

   /**
    * @param txt  the reference to StyledText
    */
   public StringOperatorMap(StyledText txt) {
      this.txt = txt;
   }

   /**
    * Clears the string map for a renewed mapping
    */
   public void reset() {
      if (hLengths.size() > heredocs.size()) {
         for (int i = hLengths.size() - 1; i >= heredocs.size(); i--) {
            hLengths.remove(i);
         }
      }
      if (qLengths.size() > quoteOprs.size()) {
         for (int i = qLengths.size() - 1; i >= quoteOprs.size(); i--) {
            qLengths.remove(i);
         }
      }
      heredocs.reset();
      quoteOprs.reset();
   }

   /**
    * Adds all string starts and ends defined by heredocs
    *
    * @param hds  the HeredocSearch
    * @param text  the text
    * @param scnStart  the start of text which is only larger than
    * 0 if the text is an inner section
    * @param repairMode  true to indicate that the search takes
    * place in repair mode which means that the entire text is
    * updated
    * @return  true if a change happened that requires repair
    * of the entire text; false otherwise
    */
   public boolean addHeredocs(HeredocSearch hds, String text, int scnStart,
         boolean repairMode) {

      int nTested = 0;
      int nChecked = 0;
      int iLen = -1;
      boolean lengthChange = false;
      int start = 0;
      while (start != -1 && !lengthChange) {
         start = hds.nextHeredoc(text, start);
         int len = 2;
         if (start != -1) {
            nTested++;
            int lineEnd = LinesFinder.nextNewline(text, start);
            String tag = hds.heredocTag(text, start, lineEnd);
            if (!tag.isEmpty()) {
               nChecked++;
               int end = text.indexOf(tag, lineEnd);
               while (end != -1 && !hds.validHeredocEnd(text, end, tag.length())) {
                  end = text.indexOf(tag, end + 1);
               }
               if (end != -1) {
                  iLen++;
                  int absEnd = end + scnStart;
                  int absStart = start + scnStart;
                  heredocs.add(absStart, absEnd);
                  len = end - lineEnd;
                  //
                  // for inner section (scnStart > 0) the entire
                  // section is updated anyway
                  if (scnStart == 0) {
                     if (!repairMode) {
                        lengthChange = lengthChange(iLen, len, hLengths);
                     }
                     setLength(iLen, len, hLengths);
                  }
               }
            }
            start += len;
         }
      }
      return heredocs.sizeChange(nTested, nChecked) || lengthChange;
   }

   /**
    * Adds all string starts and ends defined by 'quote operators'
    *
    * @param qos  the QuoteOperatorSearch
    * @param text  the text
    * @param repairMode  true to indicate that the search takes
    * place in repair mode which means that the entire text is
    * updated
    * @return  true if a change happened that requires repair
    * of the entire text; false otherwise
    */
   public boolean addQuoteOperators(QuoteOperatorSearch qos, String text,
         boolean repairMode) {

      return addQuoteOperators(qos, text, repairMode, false);
   }

   /**
    * Adds all string starts and ends defined by 'quote operators'
    *
    * @param qos  the QuoteOperatorSearch
    * @param text  the text
    * @param repairMode  true to indicate that the search takes
    * place in repair mode which means that the entire text is
    * updated
    * @param highlight  true to highlight the quote section including
    * identifier and end delimiter in orange; false otherwise
    * @return  true if a change happened that requires repair
    * of the entire text; false otherwise
    */
   public boolean addQuoteOperators(QuoteOperatorSearch qos, String text,
            boolean repairMode, boolean highlight) {

      int nTested = 0;
      int nChecked = 0;
      boolean lengthChange = false;
      int iLen = -1;
      int start = 0;
      while (start != -1 && !lengthChange) {
         start = qos.nextQuoteOperator(text, start);
         int step = 1;
         if (start != -1) {
            nTested++;
            int keyLength = qos.quoteIdentifierLength(text, start);
            if (keyLength != 0) {
               step += keyLength;
               nChecked++;
               int qStart = start + keyLength;
               int len = qos.quoteLength(text, qStart);
               if (len != 0) {
                  step += len;
                  iLen++;
                  quoteOprs.add(qStart, qStart + len);
                  if (!repairMode) {
                     lengthChange = lengthChange(iLen, len, qLengths);
                  }
                  setLength(iLen, len, qLengths);
                  if (highlight) {
                     txt.setAttributes(start, len + keyLength,
                           txt.attributes().orangePlain);
                  }
               }
            }
            start += step;
         }
      }
      return quoteOprs.sizeChange(nTested, nChecked) || lengthChange;
   }

   /**
    * Returns if the specified position is inside a string defined
    * by either heredoc or quote operator
    *
    * @param pos  the position
    * @return  the start position of the string; -1 if not in a
    * string
    */
   public int inEitherString(int pos) {
      int q = quoteOprs.inString(pos);
      int h = heredocs.inString(pos);
      int res = -1;
      if (q != -1 && h == -1) {
         res = -1 == heredocs.inString(q) ? q : -1;
      }
      else if (h != -1 && q == -1) {
         res = -1 == quoteOprs.inString(h) ? h : -1;
      }
      else if (h != -1 && q != -1) {
         res = q < h ? q : h;
      }
      return res;
   }

   /**
    * Returns if the specified position is inside a string defined by
    * a quote operator only
    *
    * @param pos  the posiition
    * @return  the start position of the string; -1 if not in
    * a string
    */
   public int inQuoteOperator(int pos) {
      return quoteOprs.inString(pos);
   }

   /**
    * Retuns if no strings defined by quote operators were added
    *
    * @return true if the list of strings defined by quote operators
    * is empty; false otherwise
    */
   public boolean isQuoteOperatorEmpty() {
      return quoteOprs.size() == 0;
   }

   //
   //--private--/
   //

   //
   // The two following methods are called consecutively which fails
   // when keeping a key pressed in a large document
   //
   private boolean lengthChange(int i, int length, List<Integer> l) {
      boolean b = true;
      if (l.isEmpty() || i >= l.size()) {
         b = false;
      }
      if (b) {
         int len = l.get(i);
         b = (length - len) * (length - len) >= 4;
      }
      return b;
   }

   private void setLength(int i, int length, List<Integer> l) {
      if (i < l.size()) {
         l.set(i, length);
      }
      else if (i == l.size()) {
         l.add(length);
      }
   }
}
