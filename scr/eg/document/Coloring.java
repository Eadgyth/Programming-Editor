package eg.document;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--
import eg.Languages;
import eg.utils.Finder;

class Coloring {
   
   private final SimpleAttributeSet comSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet keySet    = new SimpleAttributeSet();
   private final SimpleAttributeSet flagSet   = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet     = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet = new SimpleAttributeSet();   
   private final StyledDocument doc;
   private final SimpleAttributeSet normalSet;
   
   private String[] keywords;
   private boolean contrainWord = false;
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
            contrainWord = true;
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
            contrainWord = false;
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
            contrainWord = true;
            isFlagged = false;
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
      this.contrainWord = constrainWord;
      isFlagged = false;
      isFlags = false;
      isOperators = false;
      isLineCmnt = false;
      isBlockCmnt = false;
      isStringLit = false;
      isBrackets = false;
   }

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

   private void keys(String in, String key, SimpleAttributeSet set, int pos) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(key, start + jump);
         if (start != -1) {
            boolean ok = !contrainWord || Syntax.isWord(in, key, start);
            if (ok) {
               doc.setCharacterAttributes(start + pos, key.length(),
                     set, false);
            }
         }  
         jump = 1; 
      }
   }
   
   private void operators(String in, String opr, SimpleAttributeSet set, int pos) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(opr, start + jump);
         if (start != -1) {
            doc.setCharacterAttributes(start + pos, opr.length(), set, false);
         }  
         jump = 1; 
      }
   }
   
   /* A word that follows a flag (like $) but whose length is unknown */
   private void withFlag(String in, String flag, SimpleAttributeSet set, int pos) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(flag, start + jump);
         if (start != -1 && Syntax.isWordStart(in, start)) {
            int length = Syntax.wordLength(in.substring(start));
            doc.setCharacterAttributes(start + pos, length, set, false);
         }  
         jump = 1; 
      }
   }

   private void stringLiterals(String in, int pos) {
      int start = 0;
      int end = 0;
      int jump = 0;
      while (start != -1 && end != -1) {
         start = in.indexOf("\"", end + jump);
         if (start != -1 ) {
            end = in.indexOf("\"", start + 1);
            if (end != -1 ) {
               int length = end - start;
               doc.setCharacterAttributes(start + pos, length + 1,
                     strLitSet, false );
            }    
         }
         jump = 1;
      }
   }

   private void lineComments(String in, int pos) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(lineCmnt, start + jump);
         if (start != -1 && !Syntax.isInQuotes(in, start, lineCmnt.length())) {
            int lineEnd = in.indexOf("\n", start + 1);
            int length;
            if (lineEnd != -1) {
               length = lineEnd - start;
            }
            else {
               length = in.length() - start;
            }
            doc.setCharacterAttributes(start + pos, length,
                  comSet, false);
         }
         jump = 1;
      }
   }
   
   /* colors block comments but also recolors when uncommented */
   private void blockComments(String in) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(blockCmntStart, start + jump);
         if (start != -1 && !Syntax.isInQuotes(in, start, blockCmntStart.length())) {
            int end = Syntax.indNextBlockEnd(in, start + 1, blockCmntStart,
                  blockCmntEnd);
            if (end != -1) {
               int length = end - start + blockCmntEnd.length();
               doc.setCharacterAttributes(start, length, comSet, false);
               if (isSingleLines) {
                  uncommentBlock(in, end + 2);
                  uncommentBlock(in, start - 2);
               }
            }
            else {
               if (isSingleLines) {
                  colSectionExBlock(in.substring(start), start);
               } 
            }
         }
         jump = 1;
      }
      if (isSingleLines) {
         int firstEnd = in.indexOf(blockCmntEnd, 0);
         if (firstEnd != -1 ) {
            int firstStart = in.lastIndexOf(blockCmntStart, firstEnd);
            if (firstStart == -1) {
               colSectionExBlock(in.substring(0, firstEnd + 2), 0);
            } 
         }
      }
   }

   private void uncommentBlock(String in, int pos) {
      int lastStart = Syntax.indLastBlockStart(in, pos, blockCmntStart,
            blockCmntEnd);
      int nextEnd   = Syntax.indNextBlockEnd(in, pos, blockCmntStart,
            blockCmntEnd);
      if (lastStart != -1 && nextEnd == -1) {
         String toUncomment = in.substring(lastStart, pos + 2);
         colSectionExBlock(toUncomment, lastStart);
      }
      else if (nextEnd != -1 && lastStart == -1) {
         String toUncomment = in.substring(pos, nextEnd + blockCmntEnd.length());
         colSectionExBlock(toUncomment, pos);
      }
   }
   
   /* Colors the specifies String but skips block comments */
   private void colSectionExBlock(String section, int pos) {
      enableSingleLines(false);
      isBlockCmnt = false;
      color(section, pos);
      enableSingleLines(true);
      isBlockCmnt = true;
   }
   
   private boolean isInBlock(String in, int pos) {
      int lastStart = Syntax.indLastBlockStart(in, pos, blockCmntStart,
            blockCmntEnd);
      int nextEnd   = Syntax.indNextBlockEnd(in, pos, blockCmntStart,
            blockCmntEnd);
      return lastStart != -1 & nextEnd != -1;
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
      
