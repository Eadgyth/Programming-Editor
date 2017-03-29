package eg.syntax;

import eg.utils.Finder;

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
      "@Override", "@Deprecated", "@SuppressWarnings", "@SafeVarargs"
   };

   private final static String lineCmnt = "//";
   private final static String blockCmntStart = "/*";
   private final static String blockCmntEnd = "*/";

   @Override
   public void color(String allText, String toColor, int pos,
         int posStart, Lexer lex) {

      if (!SyntaxUtils.isInBlock(allText, pos, blockCmntStart, blockCmntEnd)) {
         lex.setCharAttrBlack(posStart, toColor.length());
         for (String s : JAVA_ANNOTATIONS) {
            lex.keywordBlue(toColor, s, posStart, false);
         }
         for (String s : JAVA_KEYWORDS) {
            lex.keywordRed(toColor, s, posStart, true);
         }
         for (String s : SyntaxUtils.BRACKETS) {
            lex.bracketBlue(toColor, s, posStart);
         }
         for (String s : SyntaxUtils.CURLY_BRACKETS) {
            lex.bracket(toColor, s, posStart);
         }
         lex.stringLiterals(toColor, posStart, null, null);
         lex.lineComments(toColor, posStart, lineCmnt);
      }
      lex.blockComments(allText, blockCmntStart, blockCmntEnd);
   }
}
