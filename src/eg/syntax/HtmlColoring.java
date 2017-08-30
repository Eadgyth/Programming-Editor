package eg.syntax;

public class HtmlColoring implements Colorable {
   
   // incomplete
   private final static String[] TAGS = {
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
      "script", "span", "style",
      "table",  "textarea", "title",
      "ul",
   };

   // incomplete
   private final static String[] ATTRIBUTES = {
      "align",
      "bgcolor",
      "class",  "cols", "content",
      "height", "href",
      "id",
      "onclick",
      "rows",
      "style",
      "title",  "type",
      "valign", "value",
      "width"
   };

   private final static String BLOCK_CMNT_START = "<!--";
   private final static String BLOCK_CMNT_END = "-->";

   @Override
   public void color(Lexer lex) {
      if (!lex.isInBlock(BLOCK_CMNT_START, BLOCK_CMNT_END)
             & !lex.isInBlock(SyntaxUtils.BLOCK_CMNT_START,
                              SyntaxUtils.BLOCK_CMNT_END)) {

         lex.setCharAttrBlack();
         lex.htmlTags(TAGS);
         lex.htmlKeywords(ATTRIBUTES, "<", ">");
         lex.htmlKeywords(JavascriptColoring.JS_KEYWORDS, "<script>", "</script>");
         lex.quotedTextHtml();
         lex.lineCommentsJavascriptInHtml();
      }
      lex.blockComments(BLOCK_CMNT_START, BLOCK_CMNT_END);
      lex.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
