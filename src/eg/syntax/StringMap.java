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
   // to remember after reset
   private final List<Boolean> types = new ArrayList<>();
   private int prevSize = 0;
   private int prevNTested = 0;
   private int prevNChecked = 0;

   /**
    * Clears the string map for a renewed mapping
    */
   public void reset() {
      if (types.size() > starts.size()) {
         for (int i = types.size() - 1; i >= starts.size(); i--) {
            types.remove(i);
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
    * Returns if the number of strings or one of the specified
    * paramters has changed after the previous reset
    *
    * @param nTested  the nummber of quote marks tested for validity
    * as opening quote marks
    * @param nChecked  the nummber of valid opening quote marks
    * (regardless if it equals the actual number of strings)
    * @return  if a quote number change occurred
    */
   public boolean sizeChange(int nTested, int nChecked) {
      boolean b = prevNTested != nTested || prevNChecked != nChecked
            || starts.size() != prevSize;

      prevNTested = nTested;
      prevNChecked = nChecked;
      prevSize = starts.size();
      return b;
   }

   /**
    * Returns if the type of quote mark, i.e. double quote versus
    * single quote, has changed at the ith string.
    *
    * @param type  true for (consistently) one type of quote mark,
    * false for the other
    * @param i  the index
    * @return  true if the mark changed; false otherwise
    */
   public boolean quoteMarkChange(int i, boolean type) {
      boolean b = markChangeImpl(i, type);
      if (i < types.size()) {
         types.set(i, type);
      }
      else if (i == types.size()) {
         types.add(type);
      }
      return b;
   }

   /**
    * Returns the current number of strings
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
      if ((lastStart != -1 && lastStart > lastEnd)
            && (nextEnd != -1 && (nextStart == -1 || nextEnd < nextStart))) {

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
      if (types.isEmpty() || i >= types.size()) {
         return false;
      }
      return types.get(i) != doubleQuote;
   }
}
