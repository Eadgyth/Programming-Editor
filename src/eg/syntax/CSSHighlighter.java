package eg.syntax;

/**
 * Syntax highlighting for CSS
 */
public class CSSHighlighter implements Highlighter {

   // complete ?
   private final static String[] PROPERTIES = {
      "all",
      "background", "background-attachment", "background-clip", "background-color",
      "background-image", "background-origin", "background-position",
      "background-repreat", "background-size",
      "border", "border-bottom", "border-bottom-color", "border-bottom-left-radius",
      "border-bottom-rigth-radius",  "border-bottom-style", "border-bottom-width",
      "border-collapse", "border-color", "border-image",  "border-image-outset",
      "border-image-repeat", "border-image-slice", "border-image-source",
      "border-image-width", "border-left", "border-left-color", "border-left-style",
      "border-left-width", "border-radius", "border-right", "border-right-color",
      "border-right-style", "border-right-width", "border-spacing", "border-style",
      "border-top", "border-top-color", "border-top-left-radius",
      "border-top-right-radius", "border-top-style", "border-top-width",
      "border-width", "bottom", "box-shadow",
      "caption-side", "clear", "clip", "color", "content", "counter-increment",
      "counter-reset", "cursor",
      "direction", "display",
      "empty-cells",
      "float", "font", "font-family", "font-size", "font-size-adjust",
      "font-stretch", "font-style", "font-synthesis", "font-variant", "font-weight",
      "height",
      "left", "letter-spacing", "line-height", "list-style", "list-style-image",
      "list-style-position", "list-style-type",
      "margin", "margin-bottom", "margin-left", "margin-right", "margin-top",
      "max-height", "max-width", "min-height", "min-width",
      "opacity", "orphans", "outline", "outline-color", "outline-style",
      "outline-width", "overflow",
      "padding", "padding-bottom", "padding-left", "padding-right", "padding-top",
      "page-break-after", "page-break-before", "page-break-inside", "position",
      "quotes",
      "right",
      "size",
      "table-layout", "text-align", "text-decoration", "text-indent",
      "text-transform",
      "top", "transform", "transform-origin", "transition", "transition-delay",
      "transition-duration", "transition-property", "transition-timing-function",
      "unicode-bidi",
      "vertical-align", "visibility",
      "white-space", "widows", "width", "word-spacing",
      "z-index"
   };

   private final static char[] CLASS_START = {
      '.', '#'
   };

   private final static char[] CLASS_END = {
      ' ', '{'
   };

   private final static char[] NON_PROP_START = {
      '-', '.'
   };

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      if (!searcher.isInBlock(SyntaxUtils.BLOCK_CMNT_START,
            SyntaxUtils.BLOCK_CMNT_END)) {

         searcher.setCharAttrBlack();
         searcher.keywords(HTMLHighlighter.TAGS, CLASS_START, false,
               Attributes.BLUE_PLAIN);
               
         searcher.signedVariables(CLASS_START, CLASS_END, false,
               Attributes.BLUE_PLAIN);
               
         searcher.keywords(PROPERTIES, NON_PROP_START, true, Attributes.RED_PLAIN);
         searcher.bracesGray();
      }
      searcher.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
