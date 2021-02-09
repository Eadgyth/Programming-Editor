package eg.syntax;

import eg.document.styledtext.Attributes;

//--Eadgyth--/
import eg.utils.LinesFinder;

/**
 * Syntax highlighting for PHP
 */
public class PHPHighlighter implements Highlighter, HeredocSearch {

   private static final String[] PHP_KEYWORDS = {
      "abstract", "and", "array", "as",
      "break", "bool",
      "callable", "case", "catch", "class", "clone", "const", "continue",
      "declare", "default", "do",
      "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach",
      "endif", "endswith", "endwhile", "eval", "exit", "extends",
      "false", "final", "finally", "float", "for", "foreach", "function",
      "global", "goto",
      "if", "implements", "include", "include__once", "instanceof", "insteadof",
      "int", "interface", "iterable",
      "namespace", "new", "null",
      "object", "or",
      "print", "private", "protected", "public",
      "require", "require_once", "return",
      "static", "string", "switch",
      "throw", "trait", "try", "true",
      "use",
      "var", "void",
      "while",
      "xor",
      "yield",
   };

   private static final char[] END_OF_VAR = {
      ' ', '\n', '{', '}', '?', '!', '[', ']', ';', '.', ':', '\\', '#',
      '&', '|', '=', '+', '-', '*', '/', '!', '"', '\'', '§', '%', '$',
   };

   private static final char[] START_OF_VAR = {
      '$'
   };

   private static final String[] LINE_CMNT_MARKS = {
      SyntaxConstants.HASH, SyntaxConstants.DOUBLE_SLASH
   };

   private static final String HEREDOC_SYMBOL = "<<<";

   private static final int VAR_COND = 1;

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      s.setCondition(VAR_COND);
      s.setCondition(0);
      s.mapHeredocs(this);
      s.quote(false);
      s.lineComments(LINE_CMNT_MARKS);
      s.keywordsIgnoreCase(PHP_KEYWORDS, START_OF_VAR, attr.redPlain);
      s.signedVariables(START_OF_VAR, END_OF_VAR, null, attr.bluePlain);
      s.blockComments(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH, false);
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      if (condition == VAR_COND && text.length() - 1 > pos) {
         char second = text.charAt(pos + 1);
         return second == '_' || Character.isLetter(second);
      }
      return true;
   }

   @Override
   public int behindLineCmntMark(String text, int pos) {
      int res = -1;
      int slash = SyntaxUtils.behindMark(text, SyntaxConstants.DOUBLE_SLASH, pos);
      int hash = SyntaxUtils.behindMark(text, SyntaxConstants.HASH, pos);
      if (slash > hash) {
         res = slash;
      }
      else if (hash > slash) {
         res = hash;
      }
      return res;
   }

   @Override
   public int inBlockCmntMarks(String text, int pos) {
      return SyntaxUtils.inBlock(text, SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH, pos);
   }

   @Override
   public int nextHeredoc(String text, int start) {
      return text.indexOf(HEREDOC_SYMBOL, start);
   }

   @Override
   public String heredocTag(String text, int pos, int lineEnd) {
      int start = pos + HEREDOC_SYMBOL.length();
      if (start == lineEnd) {
         return "";
      }
      start = SyntaxUtils.nextNonSpace(text, start, true);
      boolean quoted = false;
      char s = text.charAt(start);
      if (s == '\'' || s == '\"') {
         start++;
         quoted = true;
      }
      int len = 0;
      for (int i = start; i < lineEnd; i++) {
         char c = text.charAt(i);
         if (i == start) {
            if (Character.isLetter(c) || c == '_') {
               len++;
            }
         }
         else {
            if (SyntaxUtils.isLetterOrDigit(c) || c == '_') {
               len++;
            }
            else if (i > start && (c == '\'' || c == '\"')) {
               break;
            }
            else {
               quoted = false;
               break;
            }
         }
      }
      int allowedLen = quoted ? lineEnd - start - 1 : lineEnd - start;
      return allowedLen == len ? text.substring(start, start + len) : "";
   }

   @Override
   public boolean validHeredocEnd(String text, int end, int tagLength) {
      int lineStart = LinesFinder.lastNewline(text, end);
      int idStart = SyntaxUtils.nextNonSpace(text, lineStart + 1, true);
      boolean b = false;
      if (end == idStart) {
         int idEnd = idStart + tagLength;
         b = text.length() == idEnd;
         if (text.length() > idEnd) {
            char c = text.charAt(idEnd);
            if (text.length() - 1 == idEnd) {
               b = c == '\n' || c == ';';
            }
            else if (text.length() - 2 >= idEnd) {
               b = c == '\n'
               || (c == ';' && text.charAt(idEnd + 1) == '\n');
            }
         }
      }
      return b;
   }
}
