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
   public void color(Lexer lex) {

      if (!lex.isInBlock(BLOCK_CMNT_START, BLOCK_CMNT_END)) {

         lex.setCharAttrBlack();
         for (String s : JAVA_ANNOTATIONS) {
            lex.keywordBlue(s, false);
         }
         for (String s : JAVA_KEYWORDS) {
            lex.keywordRed(s, true);
         }
         for (String s : SyntaxUtils.BRACKETS) {
            lex.bracketBlue(s);
         }
         for (String s : SyntaxUtils.CURLY_BRACKETS) {
            lex.bracket(s);
         }
         lex.quotedLineWise("\'", null, null, "\\");
         lex.quotedLineWise("\"", null, null, "\\");
         lex.lineComments(LINE_CMNT);   
      }
      lex.blockComments(BLOCK_CMNT_START, BLOCK_CMNT_END);
   }
}
