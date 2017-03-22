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
   public void color(String in, String chunk, int pos, int posStart, Coloring col) {
      if (!col.isInBlock(in, pos, blockCmntStart, blockCmntEnd)) {
         col.setCharAttrBlack(posStart, chunk.length());
         for (String s : JAVA_ANNOTATIONS) {
            col.keysBlue(chunk, s, posStart, false);
         }
         for (String s : JAVA_KEYWORDS) {
            col.keysRed(chunk, s, posStart, true);
         }
         for (String s : SyntaxUtils.BRACKETS) {
            col.brackets(chunk, s, posStart);
         }
         col.stringLiterals(chunk, posStart, null, null);
         col.lineComments(chunk, posStart, lineCmnt);
      }
      col.blockComments(in, blockCmntStart, blockCmntEnd);
   }
}
