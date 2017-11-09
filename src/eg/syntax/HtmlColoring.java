package eg.syntax;

/**
 * Syntax coloring for Html
 */
public class HtmlColoring implements Colorable {
   
   // incomplete
   private final static String[] TAGS = {
      "a", "area", "applet",
      "b", "blockquote", "body", "bold", "button", "br",
      "code", "col",
      "data", "div",
      "em",
      "font", "form",
      "head", "html", "hr",
      "h1", "h2", "h3", "h4", "h5", "h6",
      "i", "img", "input",
      "label", "li",
      "meta",
      "object", "ol", "output",
      "p",
      "s", "script", "span", "style",
      "table", "textarea", "title",
      "ul",
   };

   // incomplete
   private final static String[] ATTRIBUTES = {
      "align",
      "bgcolor",
      "class",  "cols", "content",
      "for",
      "height", "href",
      "id",
      "name",
      "onclick",
      "rows",
      "scr", "source", "style",
      "title",  "type",
      "valign", "value",
      "width"
   };

   private final static String BLOCK_CMNT_START = "<!--";
   private final static String BLOCK_CMNT_END = "-->";
   private final JavascriptColoring jsCol = new JavascriptColoring();

   @Override
   public void color(SyntaxSearch search) {
      if (!search.isInBlock(BLOCK_CMNT_START, BLOCK_CMNT_END)) {
         search.setCharAttrBlack();
         search.htmlTags(TAGS, ATTRIBUTES);
         search.javascriptInHtml(jsCol);
      }
      search.blockComments(BLOCK_CMNT_START, BLOCK_CMNT_END);
   }
}
