package eg.syntax;

/**
 * Syntax coloring for Java
 */
public class JavaColoring implements Colorable {

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
     "return",
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

   @Override
   public void color(SyntaxSearch search) {
     if (!search.isInBlock(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END)) {
         search.setCharAttrBlack();
         search.keywordsBlue(JAVA_ANNOTATIONS, false);
         search.keywordsRed(JAVA_KEYWORDS, true);
         search.bracketsBlue();
         search.bracesGray();
         search.quotedText();
         search.lineComments(SyntaxUtils.LINE_CMNT, '\0');   
     }
     search.blockComments(SyntaxUtils.BLOCK_CMNT_START, SyntaxUtils.BLOCK_CMNT_END);
   }
}
