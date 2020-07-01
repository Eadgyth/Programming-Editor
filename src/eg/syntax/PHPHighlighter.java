package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for PHP
 */
public class PHPHighlighter implements Highlighter {

   private static final String[] PHP_KEYWORDS = {
      "abstract", "and", "as",
      "break",
      "callable", "case", "catch", "class", "clone", "const", "continue",
      "declare", "default", "do",
      "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach",
      "endif", "endswith", "endwhile", "eval", "exit", "extends",
      "final", "finally", "for", "foreach", "function",
      "global", "goto",
      "if", "implements", "include", "include__once", "instanceof", "insteadof",
      "interface",
      "namespace", "new",
      "or",
      "print", "private", "protected", "public",
      "require", "require_once", "return",
      "static", "switch",
      "throw", "trait", "try",
      "use",
      "var",
      "while",
      "xor",
      "yield",
   };

   private static final char[] END_OF_VAR = {
      ' ', '\n', '{', '}', '?', '!', '[', ']', ';', '.', ':', '\\', '#',
      '&', '|', '=', '+', '-', '*', '/', '!', '"', '\'', '§', '%', '$', 
   };
   
   private static final int VAR_COND = 1;

   private final boolean mixed;

   /**
    * @param mixed  true to use <code>PHPHighlighter</code>
    * in <code>HTMLHighlighter</code>; false for pure PHP
    */
   public PHPHighlighter(boolean mixed) {
      this.mixed = mixed;
   }

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH, SyntaxUtils.BLOCK_QUOTED)) {

         if (!mixed) {
            s.setStatementSection();
         }
         s.resetAttributes();
         s.setCondition(VAR_COND);
         s.signedVariable('$', END_OF_VAR, null, attr.bluePlain);
         s.setCondition(0);
         s.keywordsIgnoreCase(PHP_KEYWORDS, true, null, attr.redPlain);
         s.lineComments(SyntaxConstants.DOUBLE_SLASH, SyntaxUtils.BLOCK_QUOTED);
         s.lineComments(SyntaxConstants.HASH, SyntaxUtils.BLOCK_QUOTED);
         s.quote();
      }
      s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
            SyntaxUtils.BLOCK_QUOTED);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      if (condition == VAR_COND && text.length() - 1 > pos) {
         char second = text.charAt(pos + 1);
         return second == '_' || Character.isLetter(second);
      }
      return true;
   }
}
