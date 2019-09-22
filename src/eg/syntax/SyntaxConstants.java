package eg.syntax;

/**
 * Constants for strings and characters used for searching text elements
 */
public class SyntaxConstants {

   /**
    * The single quote character */
   public final static char SINGLE_QUOTE = '\'';

   /**
    * The double quote character */
   public  final static char DOUBLE_QUOTE = '\"';

   /**
    * The single quote*/
   public final static String SINGLE_QUOTE_STR = "\'";

   /**
    * The double quote */
   public  final static String DOUBLE_QUOTE_STR = "\"";

   /**
    * The triple double quotes*/
   public final static String TRI_DOUBLE_QUOTE = "\"\"\"";

   /**
    * The triple single quotes*/
   public final static String TRI_SINGLE_QUOTE = "\'\'\'";

   /**
    * The slash-star block comment start */
   public final static String SLASH_STAR = "/*";

   /**
    * The star-slash block comment end */
   public final static String STAR_SLASH = "*/";

   /**
    * The double slash line comment start */
   public final static String DOUBLE_SLASH = "//";

   /**
    * The hash sign line comment start */
   public final static String HASH = "#";

   /**
    * The markup block comment start */
   public final static String HTML_BLOCK_CMNT_START = "<!--";

   /**
    * The markup block comment end */
   public final static String HTML_BLOCK_CMNT_END = "-->";

   /**
    * The markup CDATA block start */
   public final static String CDATA_BLOCK_START = "<![CDATA[";

   /**
    * The markup CDATA block end */
   public final static String CDATA_BLOCK_END = "]]>";

   /**
    * The characters that end an XML tag */
   public final static char[] XML_TAG_ENDS = {
      '<', '>'
   };
   
   /**
    * The characters that are reserved in XML */
   public final static char[] RESERVED_XML_CHARS = {
      '>', '<', '/', ' ', '\n',  '&', '\"', '\'', '%', '=', '+', ';', ',', '\\'
   };

   /**
    * The HTML tag names
    */
   public final static String[] HTML_TAGS = {
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
   /**
    * The html attribute keywords */
   public final static String[] HTML_ATTR = {
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
}
