package eg.syntax;

//--Eadgyth--//
import eg.Languages;
import eg.utils.Finder;

/**
 * The coloring of either chunks of text (which also maybe the entire text)
 * or single lines using a <code>Colorable</code> selected based on the
 * language
 */
public class Coloring {
   
   private final Lexer lex;
   
   /**
    * Creates a <code>Coloring</code>
    *
    * @param lex  the {@link Lexer} object
    */
   public Coloring(Lexer lex) {
      this.lex = lex;
   }
   
   /**
    * Selects a {@link Colorable} based on the language and assigns
    * it to this <code>Lexer</code>
    *
    * @param lang  the language which is one of the constants
    * in {@link Languages} but not PLAIN_TEXT
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
    * text is scanned for block comments). To scan the entire text
    * <code>section</code> is equal to <code>allText</code> or null.
    *
    * @param allText  the entire text of the document
    * @param section  a section of <code>allText</code>. If null
    * <code>allText</code> is used. If it does not start at a line start or
    * does not end at line end the full lines are built
    * @param pos  the pos within the document where a change happened.
    */
   public void colorMultipleLines(String allText, String section, int pos) {
      int posStart = 0;
      if (section == null) {
         section = allText;
      }
      else {
         section = Finder.allLinesAtPos(allText, section, pos);
         System.out.println(section);
         posStart = Finder.lastNewline(allText, pos) + 1;
      }
      lex.setTextToColor(allText, section, pos, posStart);
      lex.color();
   }

   /**
    * Colors the current line where a change happened (the entire text is
    * scanned for block comments though).
    *
    * @param allText  the entire text of the document
    * @param pos  the pos within document where a change happened
    */
   public void colorLine(String allText, int pos) {
      String toColor = Finder.lineAtPos(allText, pos);
      int posStart = Finder.lastNewline(allText, pos) + 1;
      lex.setTextToColor(allText, toColor, pos, posStart);
      lex.color();
   }
}
