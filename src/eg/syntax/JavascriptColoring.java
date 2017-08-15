package eg.syntax;

public class JavascriptColoring implements Colorable {
   
   // incomplete
   private final static String[] KEYWORDS = {
      "else",
      "if",
      "function",
      "var"
   };

   @Override
   public void color(Lexer lex) {
      if (!lex.isInBlock(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END)) {
         lex.setCharAttrBlack();
         lex.keywordsRed(KEYWORDS, true);
         lex.quotedLineWise();
      }
      lex.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
