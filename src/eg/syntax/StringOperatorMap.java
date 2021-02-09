package eg.syntax;

import java.util.List;
import java.util.ArrayList;

//--Eadgyth--/
import eg.utils.LinesFinder;

/**
 * The mapping of strings defined by operators, i.e. here doc and
 * 'q operator'
 */
public class StringOperatorMap {

   private final StringMap heredocs = new StringMap();
   private final StringMap quoteOprs = new StringMap();
   //
   // to remember after a renewed mapping
   private final List<Integer> hLengths = new ArrayList<>();
   private final List<Integer> qLengths = new ArrayList<>();
   //
   // counting starts and ends during a renewed mapping
   private int nHeredoc;
   private int nQuoteOp;

   /**
    * Clears the string map for a renewed mapping
    */
   public void reset() {
      if (hLengths.size() > heredocs.size()) {
         for (int i = heredocs.size(); i < hLengths.size(); i++) {
            hLengths.remove(i);
         }
      }
      if (qLengths.size() > quoteOprs.size()) {
         for (int i = quoteOprs.size(); i < qLengths.size(); i++) {
            qLengths.remove(i);
         }
      }
      heredocs.reset();
      quoteOprs.reset();
   }

   /**
    * Searches and adds all string starts and ends defined by here docs
    *
    * @param hds  the HeredocSearch
    * @param text  the text
    * @param scnStart  the start of text which is only larger than
    * 0 if the text is an inner section
    * @param repairMode  true to indicate that the search takes place
    * in repairMode
    * @return  true if a change (number of here docs, length change
    * in a here doc) occured that requires a renewed highlighting;
    * false otherwise
    */
   public boolean addHeredocs(HeredocSearch hds, String text, int scnStart,
         boolean repairMode) {

      int n = nHeredoc;
      int count = 0;
      int iLen = -1;
      boolean lengthChange = false;
      int start = 0;
      while (start != -1 && !lengthChange) {
         start = hds.nextHeredoc(text, start);
         int len = 2;
         if (start != -1) {
            int lineEnd = LinesFinder.nextNewline(text, start);
            String tag = hds.heredocTag(text, start, lineEnd);
            if (!tag.isEmpty()) {
               count++;
               int end = text.indexOf(tag, lineEnd);
               while (end != -1 && !hds.validHeredocEnd(text, end, tag.length())) {
                  end = text.indexOf(tag, end + 1);
               }
               if (end != -1) {
                  count++;
                  iLen++;
                  int absEnd = end + scnStart;
                  int absTextStart = lineEnd + scnStart + 1;
                  heredocs.add(absTextStart, absEnd);
                  len = end - lineEnd;
                  if (scnStart == 0) { // for inner section entire text
                     if (!repairMode) { //    is updated anyway
                        lengthChange = lengthChange(iLen, len, hLengths);
                     }
                     setLength(iLen, len, hLengths);
                  }
               }
            }
            start += len;
         }
      }
      nHeredoc = count;
      return n != count || lengthChange;
   }

   /**
    * Searches and adds all string starts and ends defined by quote
    * operators
    *
    * @param qos  the QuoteOperatorSearch
    * @param text  the text
    * @param repairMode  true to indicate that the search takes place
    * in repairMode
    * @return  true if a change (number of quotes, length
    * change in a quote) occured that requires a renewed highlighting;
    * false otherwise
    */
   public boolean addQuoteOperators(QuoteOperatorSearch qos, String text,
         boolean repairMode) {

      int n = nQuoteOp;
      int count = 0;
      int iLen = -1;
      boolean lengthChange = false;
      int start = 0;
      while (start != -1 && !lengthChange) {
         start = qos.nextQuoteKeyword(text, start);
         int keyLength = 1;
         int len = 1;
         if (start != -1) {
            keyLength = qos.quoteKeywordLength(text, start);
            if (keyLength != 0) {
               count++;
               int qStart = start + keyLength;
               len = qos.quoteLength(text, qStart);
               if (len != 0) {
                  count++;
                  iLen++;
                  quoteOprs.add(qStart, qStart + len);
                  if (!repairMode) {
                     lengthChange = lengthChange(iLen, len, qLengths);
                  }
                  setLength(iLen, len, qLengths);
               }
            }
            start += len + keyLength;
         }
      }
      nQuoteOp = count;
      return n != count || lengthChange;
   }

   /**
    * Returns if the specified position is inside a string
    * defined by either here doc or quote operator
    *
    * @param pos  the position
    * @return  the start position of the string; -1 if not in
    * a string
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
    * Returns if the specified position is inside a string
    * defined by a quote operator only
    *
    * @param pos  the posiition
    * @return  the start position of the string; -1 if not in
    * a string
    */
   public int inQuoteOperator(int pos) {
      return quoteOprs.inString(pos);
   }

   //
   //--private--/
   //

   //
   // This can fail when keeping a key pressed!!!
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
