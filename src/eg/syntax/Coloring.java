package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

//--Eadgyth--//
import eg.Languages;

/**
 * Maintains a <code>SyntaxSearch</code> for Coloring
 */
public class Coloring {
   
   private final SyntaxSearch search;
   
   public Coloring(StyledDocument doc, SimpleAttributeSet set) {
      search = new SyntaxSearch(doc, set);
   } 
   
   /**
    * Selects a {@link Colorable} based on the language and assigns
    * it to this <code>SyntaxSearch</code>. If <code>lang</code> is
    * not a coding language no Colorable is set and the text is
    * (re-)colored in black
    *
    * @param lang  a language in {@link Languages}
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
            search.setAllCharAttrBlack();
      }
      search.setColorable(colorable);
   }
   
   /**
    * Colors text.
    *
    * @param text  the entire text
    * @param toColor  the part of <code>text</code> this is
    * colored
    * @param pos  the position where a change happened
    * @param posStart  the position where <code>toColor</code>
    * starts
    */
   public void color(String text, String toColor, int pos,
         int posStart) {
       
       search.setTextParams(text, toColor, pos, posStart);
    }   
}
