package eg.syntax;

import eg.Languages;

/**
 * The creation of a <code>Highlighter</code> selected based on the
 * language
 */
public class HighlighterSelector {
   
   /**
    * Creates a <code>Highlighter</code>
    *
    * @param lang  the language which is a constant in
    * {@link Languages}
    * @return  a new {@link Highlighter}, null if no Highlighter is available
    * for the language
    */
   public static Highlighter createHighlighter(Languages lang) {
      Highlighter hl = null;
         switch(lang) {
            case JAVA:
               hl = new JavaHighlighter();
               break;
            case HTML:
               hl = new HTMLHighlighter();
               break;
            case JAVASCRIPT:
               hl = new JavascriptHighlighter();
               break;
            case CSS:
               hl = new CSSHighlighter();
               break;
            case PERL:
               hl = new PerlHighlighter();
               break;
            case R:
               hl = new RHighlighter();
               break;
       }
       return hl;
   }
}
