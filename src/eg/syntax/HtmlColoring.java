package eg.syntax;

public class HtmlColoring implements Colorable {
   
   // incomplete
   final static String[] HTML_TAGS = {
      "a", "area", "applet",
      "b", "blockquote", "body", "bold", "button", "br",
      "code", "col",
      "data", "div",
      "em",
      "font",
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
   
   // incomplete
   final static String[] HTML_Attr = {
      "align",
      "bgcolor",
      "class",
      "height", "href",
      "id",
      "style",
      "title",
      "valign", "value",
      "width"
   };
   
   private final static String[] BRACKETS = { "<", ">" }; 
   private final static String BLOCK_CMNT_START = "<!--";
   private final static String BLOCK_CMNT_END = "-->";

   @Override
   public void color(String allText, String toColor, int pos,
         int posStart, Lexer lex) {

      if (!SyntaxUtils.isInBlock(allText, pos, BLOCK_CMNT_START,
              BLOCK_CMNT_END)) {
         lex.setCharAttrBlack(posStart, toColor.length());
         for (String s : HTML_Attr) {
            lex.keywordRed(toColor, s, posStart, true);
         }
         for (String s : HTML_TAGS) {
            tag(toColor, s, posStart, lex);
         }
         lex.quoted(toColor, posStart, "\"", BRACKETS[0], BRACKETS[1]);
      }    
      lex.blockComments(allText, BLOCK_CMNT_START, BLOCK_CMNT_END);
   }

   private void tag(String toColor, String key, int pos, Lexer lex) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(key, start);
         if (start != -1) {
            int tagStartOffset = tagStartOffset(toColor, start);
            if (tagStartOffset != -1
                  && isTagEnd(toColor, key.length(), start)) {
               int startOffset = start - tagStartOffset;
               int length = key.length() + tagStartOffset;
               lex.setCharAttrKeyBlue(startOffset + pos, length);
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
            offset = 0;
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
