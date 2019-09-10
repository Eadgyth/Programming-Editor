package eg.syntax;

import eg.document.styledtext.Attributes;

/**
 * Syntax highlighting for PHP
 */
public class PHPHighlighter implements Highlighter {

   private final static String[] PHP_KEYWORDS = {
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

   private final static char[] END_OF_VAR = {
      ' ', '\n', '{', '}', '?', '!', '[', ']', ';', '.', ':', '\\', '#',
      '&', '|', '=', '+', '-', '*', '/'
   };
   
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
         s.signedVariable('$', END_OF_VAR, null, attr.bluePlain);
         s.keywordsIgnoreCase(PHP_KEYWORDS, true, null, attr.redPlain);
         s.quote();
         s.lineComments(SyntaxConstants.DOUBLE_SLASH);
         s.lineComments(SyntaxConstants.HASH);
      }
      s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
            SyntaxUtils.BLOCK_QUOTED);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      return true;
   }
}
