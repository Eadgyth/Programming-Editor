package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--//
import eg.Languages;
import eg.utils.Finder;

/**
 * The coloring of text using a selected {@code Colorable}.
 */
public class Coloring {
   
   private final Lexer lex;
   
   /**
    * Creates a Coloring
    *
    * @param lex  the {@link Lexer}
    */
   public Coloring(Lexer lex) {
      this.lex = lex;
   }
   
   /**
    * Selects a {@link Colorable} based on the language
    *
    * @param lang  the language which is one of the constants
    * in {@link eg.Languages} but not PLAIN_TEXT
    */
   public void selectColorable(Languages lang) {
      Colorable colorable = null;
      switch(lang) {       
         case JAVA:
            colorable = new JavaColoring();
            break;
         case HTML:
            colorable = new HtmlColoring();
            break;
         case PERL:
            colorable = new PerlColoring();
            break;
         default:
            throw new IllegalArgumentException("No Colorable"
                  + " is defined for " + lang);
      }
      lex.setColorable(colorable);
   }
}
