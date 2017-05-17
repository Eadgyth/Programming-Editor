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
         lex.quotedLineWise(toColor, posStart, "\"", BRACKETS[0], BRACKETS[1], null);
      }
      lex.blockComments(allText, BLOCK_CMNT_START, BLOCK_CMNT_END);
   }

   private void tag(String toColor, String key, int pos, Lexer lex) {
      int start = 0;
      while (start != -1) {
         start = toColor.toLowerCase().indexOf(key, start);
         if (start != -1) {
            if (isTagStart(toColor, start)
                  && isTagEnd(toColor, key.length(), start)) {
               lex.setCharAttrKeyBlue(start + pos, key.length());
            }
            start += key.length();
         }
      }
   }

   private boolean isTagStart(String text, int pos) {
      boolean isTagStart = false;
      if (pos > 0) {
         char c = text.charAt(pos - 1);
         isTagStart = c == '<';
      }
      if (!isTagStart && pos > 1) {
         char c1 = text.charAt(pos - 2);
         char c2 = text.charAt(pos - 1);
         isTagStart = c2 == '/' && c1 == '<';
      }
      return isTagStart;
   }

   private boolean isTagEnd(String text, int length, int pos) {
      int endPos = pos + length;
      if (text.length() > endPos) {
         char c = text.charAt(endPos);
         return c == '>' || c == ' ';
      }
      else {
         return true;
      }
   }
}
