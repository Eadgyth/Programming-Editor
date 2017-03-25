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
   public void color(String allText, String toColor, int pos,
         int posStart, Coloring col) {

      if (!SyntaxUtils.isInBlock(allText, pos, blockCmntStart, blockCmntEnd)) {
         col.setCharAttrBlack(posStart, toColor.length());
         for (String s : BRACKETS) {
            col.bracketBlue(toColor, s, posStart);
         }
         for (String s : HTML_TAGS) {
            tag(toColor, s, posStart, col);
         }
         col.stringLiterals(toColor, posStart, BRACKETS[0], BRACKETS[1]);
      }    
      col.blockComments(allText, blockCmntStart, blockCmntEnd);
   }

   private void tag(String toColor, String key, int pos, Coloring col) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(key, start);
         if (start != -1) {
            int tagStartOffset = tagStartOffset(toColor, start);
            if (tagStartOffset != -1
                  && isTagEnd(toColor, key.length(), start)) {
               int startOffset = start - tagStartOffset;
               int length = key.length() + tagStartOffset;
               col.setCharAttrKeyBlue(startOffset + pos, length);
            }
            start += key.length(); 
         }
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
