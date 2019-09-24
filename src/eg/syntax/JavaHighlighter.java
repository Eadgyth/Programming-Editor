package eg.syntax;

import eg.document.styledtext.Attributes;

//--Eadgyth--/
import eg.utils.SystemParams;

/**
 * Syntax highlighting for Java
 */
public class JavaHighlighter implements Highlighter {

   private final static String[] JAVA_KEYWORDS = {
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
     "requires", "return",
     "strictfp", "switch", "synchronized", "short", "static", "super",
     "String",
     "this", "throw", "throws", "transient", "true", "try",
     "void", "volatile",
     "while"
   };

   private final static String[] JAVA_ANNOTATIONS = {
      "@Override", "@Deprecated", "@SuppressWarnings", "@SafeVarargs",
      "@FunctionalInterface"
   };

   private final int IGNORE_COND = 0;
   private final static int TEXT_BLOCK_COND = 1;
   private final static int VALID_TEXT_BLOCK_COND = 2;

   @Override
   public void highlight(SyntaxHighlighter.SyntaxSearcher s, Attributes attr) {
      if (!s.isInBlock(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
            SyntaxUtils.LINE_QUOTED)) {

         s.setCondition(IGNORE_COND);
         if (SystemParams.IS_JAVA_13) {
            s.setExtendedBlockSection(SyntaxConstants.TRI_DOUBLE_QUOTE, "");
         }
         s.resetAttributes();
         s.keywords(JAVA_ANNOTATIONS, true, null, attr.bluePlain);
         s.keywords(JAVA_KEYWORDS, true, null, attr.redPlain);
         s.brackets();
         s.braces();
         if (SystemParams.IS_JAVA_13) {
            s.setCondition(TEXT_BLOCK_COND);
         }
         s.quoteInLine();
         s.lineComments(SyntaxConstants.DOUBLE_SLASH, SyntaxUtils.LINE_QUOTED);
         if (SystemParams.IS_JAVA_13) {
            s.setCondition(VALID_TEXT_BLOCK_COND);
            s.textBlock(SyntaxConstants.TRI_DOUBLE_QUOTE);
         }
      }
      s.block(SyntaxConstants.SLASH_STAR, SyntaxConstants.STAR_SLASH,
           SyntaxUtils.LINE_QUOTED);
   }

   @Override
   public boolean isValid(String text, int pos, int length, int condition) {
      switch (condition) {
         case IGNORE_COND:
            return true;

         case TEXT_BLOCK_COND:
            return !SyntaxUtils.isInTextBlock(
                    text, SyntaxConstants.TRI_DOUBLE_QUOTE, pos, SyntaxConstants.HASH);

         case VALID_TEXT_BLOCK_COND:
            if (text.length() > pos + 3) {
               int nextNonSpace = SyntaxUtils.nextNonSpace(text, pos + 3);
               boolean isStart = text.charAt(nextNonSpace) == '\n';
               boolean isStartInBlockCnmt =
                       SyntaxUtils.isInBlock(
                               text,
                               SyntaxConstants.SLASH_STAR,
                               SyntaxConstants.STAR_SLASH, pos,
                               SyntaxUtils.IGNORE_QUOTED);

               boolean isEndInBlockCmnt =
                       SyntaxUtils.isInBlock(
                               text,
                               SyntaxConstants.SLASH_STAR,
                               SyntaxConstants.STAR_SLASH,
                               pos + length,
                               SyntaxUtils.IGNORE_QUOTED);

               boolean isBlockCmnt = isStartInBlockCnmt || isEndInBlockCmnt;
               return isStart && !isBlockCmnt;
            }
            else {
               return false;
            }

         default:
            break;
      }
      return false;
   }
}
