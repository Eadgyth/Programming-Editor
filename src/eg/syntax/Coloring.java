package eg.syntax;

//--Eadgyth--//
import eg.Languages;
import eg.utils.Finder;

/**
 * The coloring of either chunks of text (which also maybe the entire text)
 * or single lines using a <code>Coloring</code> selected based on the
 * language
 */
public class Coloring {
   
   private final Lexer lex;
   
   /**
    * Creates a <code>Coloring</code>
    *
    * @param lex  the <code>Lexer</code> which a <code>Colorable</code>
    * can assigned to
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
    * Colors a subset of lines or the entire text (in any case the entire
    * text is scanned for block comments).
    *
    * @param allText  the entire text of the document
    * @param section  a section of '{code allText}' which may be multiline.
    * null to color '{code allText}'
    * @param pos  the pos within the entire text where the section starts.
    */
   public void colorSection(String allText, String section, int pos) {
      int posStart = 0;
      if (section == null) {
         section = allText;
      }
      else {
         section = Finder.allLinesAtPos(allText, section, pos);
         posStart = Finder.lastNewline(allText, pos) + 1;
      }
      lex.color(allText, section, pos, posStart);
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
      int posStart = Finder.lastNewline(allText, pos) + 1;      
      lex.color(allText, toColor, pos, posStart);
   }
}
