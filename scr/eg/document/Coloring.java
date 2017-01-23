package eg.document;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--
import eg.Languages;
import eg.utils.Finder;

/*
 * The coloring of syntax / search words
 */
class Coloring {
   
   private final SimpleAttributeSet comSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet keySet    = new SimpleAttributeSet();
   private final SimpleAttributeSet flagSet   = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet     = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet = new SimpleAttributeSet();
   
   private final StyledDocument doc;
   private final SimpleAttributeSet normalSet;
   
   private String[] keywords;
   private boolean isWord = false;
   private String[] operators;
   private boolean isOperators = false;
   private String[] flagged;
   private boolean isFlagged = false;
   private String[] flags;
   private boolean isFlags = false;
   private String lineCmnt = "";
   private boolean isLineCmnt = false;
   private String blockCmntStart = "";
   private String blockCmntEnd = "";
   private boolean isBlockCmnt = false;
   private boolean isStringLit = false;
   private boolean isBrackets = false;
   private boolean isSingleLines = false;
   
   Coloring(StyledDocument doc, SimpleAttributeSet normalSet) {
      this.doc = doc;
      this.normalSet = normalSet;
      setStyles();
   }
   
   void enableSingleLines(boolean isEnabled) {
      isSingleLines = isEnabled;
   }
   
   boolean isBlockCmnt() {
      return isBlockCmnt;
   }
   
   void configColoring(Languages language) {
      switch(language) {
         case JAVA:
            keywords = Syntax.JAVA_KEYWORDS;
            isWord = true;
            flagged = Syntax.JAVA_ANNOTATIONS;
            isFlagged = true;
            isFlags = false;
            isOperators = false;
            lineCmnt = "//";
            isLineCmnt = true;
            blockCmntStart = "/*";
            blockCmntEnd = "*/";
            isBlockCmnt = true;
            isStringLit = true;
            isBrackets = true;
            break;
        case HTML:
            keywords = Syntax.HTML_KEYWORDS;
            isWord = false;
            isFlagged = false;
            isFlags = false;
            isOperators = false;
            isLineCmnt = false;
            blockCmntStart = "<!--";
            blockCmntEnd = "-->";
            isBlockCmnt = true;
            isStringLit = true;
            isBrackets = true;
            break;
        case PERL:
            keywords = Syntax.PERL_KEYWORDS;
            isWord = true;
            isFlagged = false; // variables with $ not added so far
            operators = Syntax.PERL_OP;
            isOperators = true;
            flags = Syntax.PERL_FLAGS;
            isFlags = true;
            lineCmnt = "#";
            isBlockCmnt = false;
            isStringLit = true;
            isBrackets = true;
            break;
      }
   }
   
   void setKeywords(String[] keywords, boolean constrainWord) {
      this.keywords = keywords;
      isWord = constrainWord;
      isFlagged = false;
      isFlags = false;
      isOperators = false;
      isLineCmnt = false;
      isBlockCmnt = false;
      isStringLit = false;
      isBrackets = false;
   }
   
   /**
    * Colors syntax / search terms
    */
   void color(String in, int pos) {   
      String chunk;
      int posStart = pos;
      if (isSingleLines) {
         chunk = Finder.currLine(in, pos);
         posStart = Finder.lastReturn(in, pos) + 1;
      }
      else {
         chunk = in;
      }
      boolean isInBlock = isInBlock(in, pos);
      if (!isBlockCmnt || !isInBlock) {
         doc.setCharacterAttributes(posStart, chunk.length(), normalSet, false);
         if (isFlags) {
             for (String f : flags) {
                 withFlag(chunk, f, flagSet, posStart);
             }
         }
         if (isFlagged) {
             for (String f : flagged) {
                 keys(chunk, f, flagSet, posStart);
             }
         }
         for (String k : keywords) {
              keys(chunk, k, keySet, posStart);
         }
         if (isOperators) {
             for (String o : operators) {
                 operators(chunk, o, keySet, posStart);
             }
         }
         if (isBrackets) {
             for (String b : Syntax.BRACKETS) {
                 operators(chunk, b, brSet, posStart);
             }
         }
         if (isStringLit) {
            if (!isSingleLines) {
               if (chunk.replaceAll("\n", "").length() > 0) {
                  String[] chunkArr = chunk.split("\n");
                  int[] startOfLines = Finder.startOfLines(chunkArr);
                  for (int i = 0; i < chunkArr.length; i++) {
                     stringLiterals(chunkArr[i], startOfLines[i] + posStart);
                  }
               }
            }
            else {
               stringLiterals(chunk, posStart);
            }
         }
         if (isLineCmnt) {
            lineComments(chunk, posStart);
         }
      }
      if (isBlockCmnt) {            
         blockComments(in);
      }     
   }

   private void keys(String in, String query, SimpleAttributeSet set, int pos) {
      int index = 0;
      int nextPos = 0;
      while (index != -1) {
         index = in.indexOf(query, index + nextPos);
         if (index != -1) {
            boolean ok = !isWord || Syntax.isWord(in, query, index);
            if (ok) {
               doc.setCharacterAttributes(index + pos, query.length(),
                     set, false);
            }
         }  
         nextPos = 1; 
      }
   }
   
   private void operators(String in, String query, SimpleAttributeSet set, int pos) {
      int index = 0;
      int nextPos = 0;
      while (index != -1) {
         index = in.indexOf(query, index + nextPos);
         if (index != -1) {
            doc.setCharacterAttributes(index + pos, query.length(), set, false);
         }  
         nextPos = 1; 
      }
   }
   
   /* A word that follows a flag (like $) but whose length is unknown */
   private void withFlag(String in, String query,
         SimpleAttributeSet set, int pos) {
      int index = 0;
      int nextPos = 0;
      while (index != -1) {
         index = in.indexOf(query, index + nextPos);
         if (index != -1) {
            if (Syntax.isWordStart(in, index)) {
               int length = Syntax.wordLength(in.substring(index));
               doc.setCharacterAttributes(index + pos, length, set, false);
            }
         }  
         nextPos = 1; 
      }
   }

   private void stringLiterals(String in, int pos) {
      int indStart = 0;
      int indEnd = 0;
      int nextPos = 1;
      while (indStart != -1 && indEnd != -1) {
         indStart = in.indexOf( "\"", indEnd + nextPos );
         if ( indStart != -1 ) {
            indEnd = in.indexOf( "\"", indStart + 1 );
            if ( indEnd != -1 ) {
               int length = indEnd - indStart;
               doc.setCharacterAttributes( indStart + pos, length + 1,
                     strLitSet, false );
            }    
         }
         nextPos = 2;
      }
   }

   private void lineComments(String in, int pos) {
      int lineComInd = 0;
      int nextPos = 0;
      while (lineComInd != -1) {
         lineComInd = in.indexOf(lineCmnt, lineComInd + nextPos );
         if (lineComInd != -1 && !Syntax.isInQuotes( in, lineComInd)) {
            int lineEndInd = in.indexOf("\n", lineComInd + 1);
            int length;
            if (lineEndInd != -1) {
               length = lineEndInd - lineComInd;
            }
            else {
               length = in.length() - lineComInd;
            }
            doc.setCharacterAttributes(lineComInd + pos, length,
                  comSet, false);
         }
         nextPos = 1;
      }
   }
    
   private void blockComments(String in) {
      int indStart = 0;
      int nextPos = 0;
      int indFirstStart = in.indexOf(blockCmntStart, indStart);
      int indFirstEnd = in.indexOf(blockCmntEnd, indStart);
      while (indStart != -1) {
         indStart = in.indexOf(blockCmntStart, indStart + nextPos);
         if (indStart != -1 && !Syntax.isInQuotes(in, indStart)) {       
            int indEnd = in.indexOf(blockCmntEnd, indStart + 1);
            if (indEnd != -1 && !Syntax.isInQuotes(in, indEnd)) {
               int indNextStart = in.substring
                     (indStart + 1, indEnd).indexOf(blockCmntStart, 0);
               if (indNextStart == -1) {
                  int length = indEnd - indStart + blockCmntEnd.length();
                  doc.setCharacterAttributes(indStart, length, comSet, false);
                  if (isSingleLines) {
                     uncommentBlock(in, indEnd + 2);
                     uncommentBlock(in, indStart - 2);
                     if (indFirstStart > indFirstEnd) {
                        uncommentBlock(in, 0);
                     }
                  }
               }
            }
         }                     
         nextPos = 1;
      }
   }

   void uncommentBlock(String in, int pos) {
      /*
       * positions of previous block comment start and next block comment end */
      int indBlockStart = Syntax.indLastBlockStart(in, pos, blockCmntStart,
            blockCmntEnd);
      int indBlockEnd   = Syntax.indNextBlockEnd(in, pos, blockCmntStart,
            blockCmntEnd);
      if (indBlockStart != -1 && indBlockEnd == -1) {
         String toUncomment = in.substring(indBlockStart, pos + 2);
         enableSingleLines(false);
         color(toUncomment, indBlockStart);
         enableSingleLines(true);
      }
      else if (indBlockEnd != -1 && indBlockStart == -1) {
         String toUncomment = in.substring(pos, indBlockEnd + blockCmntEnd.length());
         enableSingleLines(false);
         color(toUncomment, pos);
         enableSingleLines(true);
      }
   }
   
   private boolean isInBlock(String in, int pos) {
      int indBlockStart = Syntax.indLastBlockStart(in, pos, blockCmntStart,
            blockCmntEnd);
      int indBlockEnd   = Syntax.indNextBlockEnd(in, pos, blockCmntStart,
            blockCmntEnd);
      return indBlockStart != -1 & indBlockEnd != -1;
   }
   
   private void setStyles() {
      Color commentGreen = new Color(60, 190, 80);
      StyleConstants.setForeground(comSet, commentGreen);
      StyleConstants.setBold(comSet, false);

      Color keyPink = new Color(230, 0, 110);
      StyleConstants.setForeground(keySet, keyPink);
      StyleConstants.setBold(keySet, false);
      
      Color flagBlue = new Color(60, 60, 250);
      StyleConstants.setForeground(flagSet, flagBlue);
      StyleConstants.setBold(flagSet, false);

      Color bracketBlue = new Color(70, 0, 220);
      StyleConstants.setForeground(brSet, bracketBlue);
      StyleConstants.setBold(brSet, true);

      Color strLitOrange = new Color(230, 140, 50);
      StyleConstants.setForeground(strLitSet, strLitOrange );
      StyleConstants.setBold(strLitSet, false );
   }
}
      
