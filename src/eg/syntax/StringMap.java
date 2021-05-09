package eg.syntax;

import java.util.List;
import java.util.ArrayList;

/**
 * The mapping of strings
 */
public class StringMap {

   private final List<Integer> starts = new ArrayList<>();
   private final List<Integer> ends = new ArrayList<>();
   //
   // to remember after a new mapping
   private final List<Boolean> marks = new ArrayList<>();

   /**
    * Clears the string map for a renewed mapping
    */
   public void reset() {
      if (marks.size() > starts.size()) {
         for (int i = marks.size() - 1; i >= starts.size(); i--) {
            marks.remove(i);
         }
      }
      starts.clear();
      ends.clear();
   }

   /**
    * Adds the start and end positions of a string
    *
    * @param start  the start
    * @param end  the end
    */
   public void add(int start, int end) {
      addImpl(start, end);
   }

   /**
    * Returns if the type of quote mark, i.e. double quotes versus
    * single quotes, has changed at the ith string.
    *
    * @param type  true for (consistently) one type of quote mark,
    * false for the other
    * @param i  the index
    * @return  true if the mark changed; false otherwise
    */
   public boolean quoteMarkChange(int i, boolean type) {
      boolean b = markChangeImpl(i, type);
      if (i < marks.size()) {
         marks.set(i, type);
      }
      else if (i == marks.size()) {
         marks.add(type);
      }
      return b;
   }

   /**
    * Returns the the current number of strings
    *
    * @return  the size
    */
   public int size() {
      return starts.size();
   }

   /**
    * Returns if the specified position is inside a string
    *
    * @param pos  the position
    * @return  the start position of the string; -1 if not in a
    * string
    */
   public int inString(int pos) {
      if (starts.isEmpty()) {
         return -1;
      }
      int lastStart = -1;
      int nextStart = -1;
      for (int i : starts) {
         if (i > pos) {
            nextStart = i;
            break;
         }
         lastStart = i;
      }
      int nextEnd = -1;
      int lastEnd = -1;
      for (int i : ends) {
         if (i > pos) {
            nextEnd = i;
            break;
         }
         lastEnd = i;
      }
      if ((lastStart != -1 & lastStart > lastEnd)
            && (nextEnd != -1 & (nextStart == -1 || nextEnd < nextStart))) {

         return lastStart;
      }
      else {
         return -1;
      }
   }

   //
   //--private--/
   //

   private void addImpl(int start, int end) {
      starts.add(start);
      ends.add(end);
   }

   private boolean markChangeImpl(int i, boolean doubleQuote) {
      if (marks.isEmpty() || i >= marks.size()) {
         return false;
      }
      return marks.get(i) != doubleQuote;
   }
}
