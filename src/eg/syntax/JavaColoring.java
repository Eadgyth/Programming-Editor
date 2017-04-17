package eg.syntax;

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

   private final static String LINE_CMNT = "//";
   private final static String BLOCK_CMNT_START = "/*";
   private final static String BLOCK_CMNT_END = "*/";

   @Override
   public void color(String allText, String toColor, int pos,
         int posStart, Lexer lex) {

      if (!SyntaxUtils.isInBlock(allText, pos, BLOCK_CMNT_START,
              BLOCK_CMNT_END)) {
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
         lex.quoted(toColor, posStart, "\'", null, null);
         lex.quoted(toColor, posStart, "\"", null, null);
         lex.lineComments(toColor, posStart, LINE_CMNT);
      }
      lex.blockComments(allText, BLOCK_CMNT_START, BLOCK_CMNT_END);
   }
}
