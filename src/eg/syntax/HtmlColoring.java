package eg.syntax;

import eg.utils.Finder;

public class HtmlColoring implements Colorable {
   
   final static String[] HTML_TAGS = {
      "a", "area", "applet",
      "b", "br", "body", "button",
      "div",
      "em",
      "head", "html", "hr",
      "h1", "h2", "h3", "h4", "h5", "h6",
      "i", "img",
      "li",
      "meta",
      "object", "ol",
      "p",
      "script", "style",
      "table",  "textarea", "title",
      "ul",
   };
   
   private final static String[] BRACKETS = { "<", ">" };
   
   private final String blockCmntStart = "<!--";
   private final String blockCmntEnd = "-->";

   @Override
   public void color(String in, String chunk, int pos, int posStart, Coloring col) {
      if (!col.isInBlock(in, pos, blockCmntStart, blockCmntEnd)) {
         col.setCharAttrBlack(posStart, chunk.length());
         for (String b : BRACKETS) {
            col.brackets(chunk, b, posStart);
         }
         for (String s : HTML_TAGS) {
            keyInTag(chunk, s, posStart, col);
         }
         col.stringLiterals(chunk, posStart, BRACKETS[0], BRACKETS[1]);
      }    
      col.blockComments(in, blockCmntStart, blockCmntEnd);
   }

   private void keyInTag(String in, String key, int pos, Coloring col) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(key, start + jump);
         if (start != -1) {
            int tagStartOffset = tagStartOffset(in, start);
            if (tagStartOffset != -1
                  && isTagEnd(in, key.length(), start)) {
               int startOffset = start - tagStartOffset;
               int length = key.length() + tagStartOffset;
               col.setCharAttrKeyBlue(startOffset + pos, length);
            }
         }  
         jump = 1; 
      }
   }

   private int tagStartOffset(String in, int pos) {
      int offset = -1;
      if (pos > 0) {
         char c = in.charAt(pos - 1);
         if (c == '<') {
            offset = 0;
         }
      }
      if (offset == -1 && pos > 1) {
         char c1 = in.charAt(pos - 2);
         char c2 = in.charAt(pos - 1);
         if (c2 == '/' && c1 == '<') {
            offset = 1;
         }
      }
      return offset;
   }

   private boolean isTagEnd(String in, int length, int pos) {
      int endPos = pos + length;
      String end = "";   
      if (in.length() > endPos) {
         char c = in.charAt(endPos);
         return c == '>' || c == ' ';
      }
      else {
         return true;
      }
   }
}
