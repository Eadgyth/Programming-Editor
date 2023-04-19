package eg.syntax;

import eg.document.styledtext.Attributes;

//--Eadgyth--/
import eg.utils.SystemParams;

/**
 * Syntax highlighting for Java
 */
public class JavaHighlighter implements Highlighter {

   private static final String[] JAVA_KEYWORDS = {
     "abstract", "assert",
     "break", "boolean", "Boolean", "byte",
     "catch", "case","const","continue", "class", "char",
     "default", "do", "double",
     "else", "enum", "extends",
     "false", "finally", "final", "float", "for",
     "if", "implements", "import", "instanceof", "int", "interface",
     "long",
     "native", "new", "null",
     "package", "private", "protected", "public",
     "return",
     "strictfp", "switch", "synchronized", "short", "static", "super",
     "String",
     "this", "throw", "throws", "transient", "true", "try",
     "void", "volatile",
     "while"
   };
   
   private static final String[] JAVA_9_PLUS_KEYWORDS = {
      "exports", "module", "requires"
   };
      
   private static final String[] JAVA_10_PLUS_KEYWORDS = {
      "var"
   };

   private static final String[] JAVA_ANNOTATIONS = {
      "@Override", "@Deprecated", "@SuppressWarnings", "@SafeVarargs",
      "@FunctionalInterface"
   };

   private static final String[] LINE_CMNT_MARK = {
      SyntaxConstants.DOUBLE_SLASH
   };

   private static	final int IGNORE_COND = 0;
   private static final int VALID_TEXT_BLOCK_COND = 1;

   @Override
   public void highlight(SyntaxSearcher s, Attributes attr) {
      s.resetAttributes();
      if (SystemParams.IS_JAVA_13_OR_HIGHER) {
         s.setCondition(VALID_TEXT_BLOCK_COND);
         s.tripleQuoteTextBlocks(false);
         s.setCondition(IGNORE_COND);
      }
      s.quote(true);
      s.lineComments(LINE_CMNT_MARK);
      s.brackets();
      s.braces();
      s.keywords(JAVA_ANNOTATIONS, null, attr.bluePlain);
      s.keywords(JAVA_KEYWORDS, null, attr.redPlain);
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         s.keywords(JAVA_9_PLUS_KEYWORDS, null, attr.redPlain);
      }
      if (SystemParams.IS_JAVA_10_OR_HIGHER) {
         s.keywords(JAVA_10_PLUS_KEYWORDS, null, attr.redPlain);
      }
      s.blockComments(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH, false);
   }

   @Override
   public boolean isValid(String text, int pos, int condition) {
      if (condition == VALID_TEXT_BLOCK_COND && text.length() > pos + 3) {
         int nextNonSpace = SyntaxUtils.nextNonSpace(text, pos + 3, true);
         return text.charAt(nextNonSpace) == '\n';
      }
      return true;
   }

   @Override
   public int behindLineCmntMark(String text, int pos) {
      return SyntaxUtils.behindMark(text, SyntaxConstants.DOUBLE_SLASH, pos);
   }

   @Override
   public int inBlockCmntMarks(String text, int pos) {
      return SyntaxUtils.inBlock(text, SyntaxConstants.SLASH_STAR,
            SyntaxConstants.STAR_SLASH, pos);
   }
}
