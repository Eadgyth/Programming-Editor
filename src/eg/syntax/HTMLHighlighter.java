package eg.syntax;

/**
 * Syntax highlighting for Html
 */
public class HTMLHighlighter implements Highlighter {

   /**
    * The array of HTML tags
    */
   public final static String[] TAGS = {
      "a", "abbr", "address", "applet", "area", "article", "aside", "audio",
      "b", "base", "bdi", "bdo", "blockquote", "bold", "body", "br", "button",
      "canvas", "caption", "code", "col", "colgroup", "command",
      "data", "details", "datalist", "dd", "del", "dfn", "div", "dl", "dt",
      "em", "embed",
      "fieldset", "figure", "figcaption", "form", "footer", "frame", "frameset",
      "head", "header", "html", "hr",
      "h1", "h2", "h3", "h4", "h5", "h6",
      "i", "iframe", "img", "input", "ins",
      "kbd", "keygen",
      "label", "legend", "li", "link",
      "main", "map", "mark", "math", "menu", "menuitem", "meta", "meter",
      "nav", "noframes", "noscript",
      "object", "ol", "optgroup", "option", "output",
      "p", "param", "pre", "progress",
      "rp", "rt", "ruby",
      "s", "samp", "script", "section", "select", "small", "source", "span",
      "strong", "style", "sub", "summary", "sup", "svg",
      "table", "tbody", "textarea", "tfoot", "thead", "time", "title", "td",
      "th", "tr", "track",
      "ul",
      "video",
      "wbr",
   };
   private final static String[] ATTRIBUTES = {
      "accesskey", "abbr", "accept", "accept-charset", "action", "align", "alt",
      "archive", "axis",
      "bgcolor", "border",
      "callpadding", "callspacing", "char", "charset", "checked", "cite", "class",
      "classid", "code", "codebase", "codetag", "compact", "content", "coords",
      "cols", "colspan",
      "data", "datetime", "declare", "defer", "dir", "disabled",
      "enctype",
      "for", "frame", "frameborder",
      "headers", "height", "href", "hreflang", "hspace", "http-equiv",
      "id", "ismap",
      "label", "longdesc",
      "marginheight", "marginwidth", "maxlength", "media", "method", "multiple",
      "name", "nohref", "noresize", "noshade",
      "onclick", "onload",
      "profile",
      "readonly", "rel", "rev", "rows", "rowspan", "rules",
      "scheme", "scope", "scrolling", "selected", "size", "shape", "src", "source",
      "standby", "start", "style", "summary",
      "tabindex", "target", "title",  "type",
      "usemap",
      "valign", "value", "valuetype", "vspace",
      "width",
      "xml:lang", "xml:space", "xmlns",
   };
   private final static String BLOCK_CMNT_START = "<!--";
   private final static String BLOCK_CMNT_END = "-->";

   private final JavascriptHighlighter js = new JavascriptHighlighter();
   private final CSSHighlighter css = new CSSHighlighter();
   
   private SyntaxHighlighter.SyntaxSearcher searcher;
   
   @Override
   public void setSyntaxSearcher(SyntaxHighlighter.SyntaxSearcher searcher) {
      this.searcher = searcher;
      searcher.blkCmntMarksQuoted(true);
   }

   @Override
   public void highlight() {
      if (!searcher.isInBlockCmnt(BLOCK_CMNT_START, BLOCK_CMNT_END)) {
         searcher.setHtmlSection();
         searcher.setSectionBlack();
         searcher.htmlElements(TAGS, ATTRIBUTES);
         searcher.embeddedHtmlSections("<script", "</script>", js);      
         searcher.embeddedHtmlSections("<style", "</style>", css);   
      }
      searcher.blockComments(BLOCK_CMNT_START, BLOCK_CMNT_END);
   }
   
   @Override
   public boolean isValid(String text, int pos, int condition) {
      return true;
   }
}
