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
   
   /**
    * Colors a section of text or the entire text (in any case the entire
    * text is scanned for block comments).
    *
    * @param allText  the entire text of the document
    * @param section  a section of '{code allText}' which may be multiline.
    * null to color '{code allText}'
    * @param posStart  the pos within the entire text where the section to
    * be colored starts. Set to 0 if '{code section}' is null.
    */
   public void colorSection(String allText, String section, int posStart) {
      int length = 0;
      if (section == null) {
         length = allText.length();
         section = allText;
         posStart = 0;
         //int pos = posStart;
      }
      else {
         length = section.length();
         section = Finder.allLinesAtPos(allText, section, posStart);
         posStart = Finder.lastReturn(allText, posStart) + 1;
      }
      lex.color(allText, section, posStart, posStart);
   }

   /**
    * Colors the current line where a change happened (the entire text is
    * scanned for block comments).
    *
    * @param allText  the entire text of the document
    * @param pos  the pos within the entire text where a change happened
    */
   public void colorLine(String allText, int pos) {
      String toColor = Finder.lineAtPos(allText, pos);
      int posStart = Finder.lastReturn(allText, pos) + 1;
      lex.color(allText, toColor, pos, posStart);
   }
}
