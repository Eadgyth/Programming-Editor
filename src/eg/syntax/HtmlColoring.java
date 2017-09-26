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
      "i", "img", "input",
      "li",
      "meta",
      "object", "ol",
      "p",
      "s", "script", "span", "style",
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
      "scr", "source", "style",
      "title",  "type",
      "valign", "value",
      "width"
   };

   private final static String BLOCK_CMNT_START = "<!--";
   private final static String BLOCK_CMNT_END = "-->";

   @Override
   public void color(Coloring col) {
      if (!col.isInBlock(BLOCK_CMNT_START, BLOCK_CMNT_END)
             & !col.isInBlock(SyntaxUtils.BLOCK_CMNT_START,
                              SyntaxUtils.BLOCK_CMNT_END)) {

         col.setCharAttrBlack();
         col.htmlTags(TAGS);
         col.htmlKeywords(ATTRIBUTES, "<", ">");
         col.htmlKeywords(JavascriptColoring.JS_KEYWORDS, "<script>", "</script>");
         col.quotedTextHtml();
         col.lineCommentsJavascriptInHtml();
      }
      col.blockComments(BLOCK_CMNT_START, BLOCK_CMNT_END);
      col.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
