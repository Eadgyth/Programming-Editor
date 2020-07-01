package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for CSS
 */
public class CSSHighlighter implements Highlighter {

   private static final String[] PROPS = {
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
   private static final String[] BACKGROUND_PROPS = {
      "-attachment", "-clip", "-color",
      "-image", "-origin", "-position",
      "-repreat", "-size"
   };
   private static final String[] BORDER_PROPS = {
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
   private static final String[] TOP_LEFT_RIGHT_BOTTOM = {
      "-bottom", "-left", "-right", "-top"
   };
   private static final String[] FONT_PROPS = {
      "-family", "-size", "-size-adjust", "-stretch", "-style",
      "-synthesis", "-variant", "-weight"
   };
   private static final String[] LIST_PROPS = {
      "-style", "-style-image", "-style-position", "-style-type"
   };
   private static final String[] MARGIN_PROPS = TOP_LEFT_RIGHT_BOTTOM;
   private static final String[] OUTLINE_PROPS = {
      "-color", "-style", "-width"
   };
   private static final String[] PADDING_PROPS = TOP_LEFT_RIGHT_BOTTOM;
   private static final String[] TRANSITION_PROPS = {
      "-delay", "-duration", "-property", "-timing-function"
   };

   private static final char[] CLASS_START = {'.', '#'};
   private static final char[] CLASS_END = {' ', '{', ')'};
   private static final char[] NON_PROP_START = {'-', '.'};
   private static final int IGNORE_COND = 0;
   private static final int OPEN_BRACE_AHEAD_COND = 1;
   private static final int NO_OPEN_BRACE_AHEAD_COND = 2;

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
            SyntaxUtils.IGNORE_QUOTED)) {

         s.resetAttributes();

         s.setCondition(NO_OPEN_BRACE_AHEAD_COND);
         s.keywords(SyntaxConstants.HTML_TAGS, true, CLASS_START,
               attr.bluePlain);

         s.signedVariables(CLASS_START, CLASS_END, null, attr.bluePlain);

         s.setCondition(OPEN_BRACE_AHEAD_COND);
         s.extensibleKeyword("background", BACKGROUND_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("border", BORDER_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("font", FONT_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("list", LIST_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("margin", MARGIN_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("outline", OUTLINE_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("padding", PADDING_PROPS, NON_PROP_START,
               attr.redPlain);

         s.extensibleKeyword("transition", TRANSITION_PROPS, NON_PROP_START,
               attr.redPlain);

         s.keywords(PROPS, true, NON_PROP_START, attr.redPlain);

         s.quoteInLine();
         s.setCondition(IGNORE_COND);
         s.braces();
      }
      s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
            SyntaxUtils.IGNORE_QUOTED);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      if (condition == IGNORE_COND) {
         return true;
      }
      int lastOpenBrace
            = SyntaxUtils.lastBlockStart(text, pos, "{", "}",
                  SyntaxUtils.IGNORE_QUOTED);

      return (condition == OPEN_BRACE_AHEAD_COND && lastOpenBrace != -1)
            || (condition == NO_OPEN_BRACE_AHEAD_COND && lastOpenBrace == -1);
   }
}
