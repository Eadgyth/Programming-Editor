package eg.syntax;

//--Eadgyth--//
import eg.Languages;

/**
 * Sets a <code>Colorable</code> in <code>Coloring</code> class
 * depending on the language
 */
public class LanguageSetter {
   
   private final Coloring col;
   
   /**
    * Creates a <code>LanguageSetter</code>
    *
    * @param col  a {@link Coloring} object
    */
   public LanguageSetter(Coloring col) {
      this.col = col;
   }
   
   /**
    * Selects a {@link Colorable} based on the language and assigns
    * it to this <code>Coloring</code>
    *
    * @param lang  the language which is one of the constants
    * in {@link Languages} but not PLAIN_TEXT
    */
   public void setColorable(Languages lang) {
      Colorable colorable = null;
      switch(lang) {       
         case JAVA:
            colorable = new JavaColoring();
            break;
         case HTML:
            colorable = new HtmlColoring();
            break;
         case JAVASCRIPT:
            colorable = new JavascriptColoring();
            break;
         case PERL:
            colorable = new PerlColoring();
            break;
         default:
            throw new IllegalArgumentException("No Colorable"
                  + " is defined for " + lang);
      }
      col.setColorable(colorable);
   }
}
