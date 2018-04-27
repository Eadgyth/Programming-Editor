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
   // Property extensions
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
   private final static String[] TOP_LEFT_RIGHT_BOTTOM = {
      "-bottom", "-left", "-right", "-top"
   };
   private final static String[] FONT_PROPS = {
      "-family", "-size", "-size-adjust", "-stretch", "-style",
      "-synthesis", "-variant", "-weight"
   }; 
   private final static String[] LIST_PROPS = {
      "-style", "-style-image", "-style-position", "-style-type"
   };  
   private final static String[] MARGIN_PROPS = TOP_LEFT_RIGHT_BOTTOM;
   private final static String[] OUTLINE_PROPS = {
      "-color", "-style", "-width"
   };
   private final static String[] PADDING_PROPS = TOP_LEFT_RIGHT_BOTTOM;
   private final static String[] TRANSITION_PROPS = {
      "-delay", "-duration", "-property", "-timing-function"
   };
   //
   // General
   private final static char[] CLASS_START = {'.', '#'};
   private final static char[] CLASS_END = {' ', '{'};
   private final static char[] NON_PROP_START = {'-', '.'};
   private final static int IGNORE_OPT = 0;
   private final static int OPEN_BRACE_AHEAD = 1;
   private final static int NO_OPEN_BRACE_AHEAD = 2;

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher searcher) {
      if (!searcher.isInBlockCmnt(SyntaxUtils.BLOCK_CMNT_START,
            SyntaxUtils.BLOCK_CMNT_END)) {

         searcher.setSectionBlack();
         searcher.setOption(NO_OPEN_BRACE_AHEAD);
         searcher.keywords(HTMLHighlighter.TAGS, CLASS_START,
               Attributes.BLUE_PLAIN);
               
         searcher.signedVariables(CLASS_START, CLASS_END,
               Attributes.BLUE_PLAIN);

         searcher.setOption(OPEN_BRACE_AHEAD);
         searcher.extensibleKeyword("background", BACKGROUND_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("border", BORDER_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("font", FONT_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("list", LIST_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("margin", MARGIN_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("outline", OUTLINE_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("padding", PADDING_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
               
         searcher.extensibleKeyword("transition", TRANSITION_PROPS, NON_PROP_START,
               Attributes.RED_PLAIN);
                  
         searcher.keywords(PROPS, NON_PROP_START, Attributes.RED_PLAIN);
         
         searcher.setOption(IGNORE_OPT);
         searcher.braces();
      }
      searcher.blockComments(SyntaxUtils.BLOCK_CMNT_START,
            SyntaxUtils.BLOCK_CMNT_END);
   }
   
   @Override
   public boolean isEnabled(String text, int pos, int option) {
      if (option == IGNORE_OPT) {
         return true;
      }    
      int lastOpenBrace
            = SyntaxUtils.lastBlockStart(text, pos, "{", "}");

      return (option == OPEN_BRACE_AHEAD && lastOpenBrace != -1)
            || (option == NO_OPEN_BRACE_AHEAD && lastOpenBrace == -1);
   }
}
