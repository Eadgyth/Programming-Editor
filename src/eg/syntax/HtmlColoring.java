package eg.syntax;

public class HtmlColoring implements Colorable {
   
   // incomplete
   final static String[] TAGS = {
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
   final static String[] Attr = {
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
   public void color(Lexer lex) {
      if (!lex.isInBlock(BLOCK_CMNT_START, BLOCK_CMNT_END)) {
         lex.setCharAttrBlack();
         if (!lex.isTypeMode() || lex.isInBlock(BRACKETS[0], BRACKETS[1])) {
            for (String s : Attr) {
               lex.keywordRed(s, true);
            }
            for (String s : TAGS) {
               lex.tag(s);
            }
            lex.quotedLineWise("\"", false);
            lex.quotedLineWise("\'", false);
         }
      }
      lex.blockComments(BLOCK_CMNT_START, BLOCK_CMNT_END);
   }
}
