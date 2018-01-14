package eg.syntax;

/**
 * Syntax highlighting for CSS
 */
public class CSSHighlighter implements Highlighter {

   //
   // Properties
   private final static String[] PROPS = {
      "all",
      "bottom", "box-shadow",
      "caption-side", "clear", "clip", "color", "content", "counter-increment",
      "counter-reset", "cursor",
      "direction", "display",
      "empty-cells",
      "float",
      "height",
      "left", "letter-spacing", "line-height",
      "max-height", "max-width", "min-height", "min-width",
      "opacity", "orphans", "overflow",
      "page-break-after", "page-break-before", "page-break-inside", "position",
      "quotes",
      "right",
      "size",
      "table-layout", "text-align", "text-decoration", "text-indent",
      "text-transform",
      "top", "transform", "transform-origin",
      "unicode-bidi",
      "vertical-align", "visibility",
      "white-space", "widows", "width", "word-spacing",
      "z-index"
   };
   //
   // Property appandages
   private final static String[] BACKGROUND_PROPS = {
      "-attachment", "-clip", "-color",
      "-image", "-origin", "-position",
      "-repreat", "-size"
   };
   private final static String[] BORDER_PROPS = {
      "-bottom", "-bottom-color", "-bottom-left-radius",
      "-bottom-rigth-radius",  "-bottom-style", "-bottom-width",
      "-collapse", "-color", "-image",  "-image-outset",
      "-image-repeat", "-image-slice", "-image-source",
      "-image-width", "-left", "-left-color", "-left-style",
      "-left-width", "-radius", "-right", "-right-color",
      "-right-style", "-right-width", "-spacing", "-style",
      "-top", "-top-color", "-top-left-radius",
      "-top-right-radius", "-top-style", "-top-width",
      "-width"
   };
   private final static String[] TOP_LEFT_RIGHT_BOTTOM_OPT = {
      "-bottom", "-left", "-right", "-top"
   };
   private final static String[] FONT_PROPS = {
      "-family", "-size", "-size-adjust", "-stretch", "-style",
      "-synthesis", "-variant", "-weight"
   }; 
   private final static String[] LIST_PROPS = {
      "-style", "-style-image", "-style-position", "-style-type"
   };  
   private final static String[] MARGIN_PROPS = TOP_LEFT_RIGHT_BOTTOM_OPT;
   private final static String[] OUTLINE_PROPS = {
      "-color", "-style", "-width"
   };
   private final static String[] PADDING_PROPS = TOP_LEFT_RIGHT_BOTTOM_OPT;
   private final static String[] TRANSITION_PROPS = {
      "-delay", "-duration", "-property", "-timing-function"
   };
   //
   // General
   private final static char[] CLASS_START = {'.', '#'};
   private final static char[] CLASS_END = {' ', '{'};
   private final static char[] NON_PROP_START = {'-', '.'};

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      if (!searcher.isInBlockCmnt(SyntaxUtils.BLOCK_CMNT_START,
            SyntaxUtils.BLOCK_CMNT_END)) {

         searcher.setCharAttrBlack();
         searcher.keywords(HTMLHighlighter.TAGS, CLASS_START, false,
               Attributes.BLUE_PLAIN);
               
         searcher.signedVariables(CLASS_START, CLASS_END, false,
               Attributes.BLUE_PLAIN);
         
         searcher.extensibleKeyword("background", BACKGROUND_PROPS,
               NON_PROP_START, true, Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("border", BORDER_PROPS, NON_PROP_START, true,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("font", FONT_PROPS, NON_PROP_START, true,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("list", LIST_PROPS, NON_PROP_START, true,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("margin", MARGIN_PROPS, NON_PROP_START, true,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("outline", OUTLINE_PROPS, NON_PROP_START, true,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("padding", PADDING_PROPS, NON_PROP_START, true,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("transition", TRANSITION_PROPS,
               NON_PROP_START, true, Attributes.RED_PLAIN);
                  
         searcher.keywords(PROPS, NON_PROP_START, true, Attributes.RED_PLAIN);
         searcher.braces();
      }
      searcher.blockComments(SyntaxUtils.BLOCK_CMNT_START,
            SyntaxUtils.BLOCK_CMNT_END);
   }
}
