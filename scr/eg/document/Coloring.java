package eg.document;

import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;

import java.awt.Color;

//--Eadgyth--
import eg.utils.Finder;

/*
 * The coloring of syntax / search words
 */
class Coloring {
   
   private final SimpleAttributeSet comSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet keySet    = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet     = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet = new SimpleAttributeSet();
   
   private final StyledDocument doc;
   private final SimpleAttributeSet normalSet;
   
   private String[] keywords;
   private String lineCmnt;
   private boolean isLineCmnt = false;
   private String blockCmntStart;
   private String blockCmntEnd;
   private boolean isBlockCmnt = false;
   private boolean isStringLit = false;
   private boolean isBrackets = false;
   private boolean isWord = false;
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
   
   void configColoring(String[] keywords, String lineCmnt, String blockCmntStart,
            String blockCmntEnd, boolean isStringLit, boolean isBrackets,
            boolean isWord) {
      this.keywords = keywords;
      this.lineCmnt = lineCmnt;
      isLineCmnt = lineCmnt.length() > 0;
      this.blockCmntStart = blockCmntStart;
      this.blockCmntEnd = blockCmntEnd;
      isBlockCmnt = blockCmntStart.length() > 0;
      this.isStringLit = isStringLit;
      this.isBrackets = isBrackets;
      this.isWord = isWord;
   }
   
   /**
    * Colors synthax / search terms
    */
   void color(String in, int pos) {    
      String chunk;
      if (isSingleLines) {
         chunk = Finder.currLine(in, pos);
         pos = in.lastIndexOf("\n", pos) + 1;
      }
      else {
         chunk = in;
      }
      /*
       * positions of previous block comment start and next block comment end */
      int indBlockStart = Finder.indLastBlockStart(in, pos, blockCmntStart,
            blockCmntEnd);
      int indBlockEnd   = Finder.indNextBlockEnd(in, pos, blockCmntStart,
            blockCmntEnd);
      /*
       * if cursor is not inside a block comment or using block comments 
       * is disabled */
      if ((indBlockStart == -1 || indBlockEnd == -1) || !isBlockCmnt) {
         doc.setCharacterAttributes(pos, chunk.length(), normalSet, false);
         for (int i = 0; i < keywords.length; i++) {
            keys(chunk, keywords[i], keySet, pos);
         }
         if (isBrackets) {
            for (int i = 0; i < Keywords.BRACKETS.length; i++) {
               brackets(chunk, Keywords.BRACKETS[i], pos);
            }
         }
         if (isStringLit) {
            if (!isSingleLines) {
               if (chunk.replaceAll("\n", "").length() > 0) {
                  String[] chunkArr = chunk.split("\n");
                  int[] startOfLines = Finder.startOfLines(chunkArr);
                  for (int i = 0; i < chunkArr.length; i++) {
                     stringLiterals(chunkArr[i], startOfLines[i] + pos);
                  }
               }
            }
            else {
               stringLiterals(chunk, pos);
            }
         }
         if (isLineCmnt) {
            lineComments(chunk, pos);
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
            boolean ok = !isWord || Finder.isWord(in, query, index);
            if (ok) {
               doc.setCharacterAttributes(index + pos, query.length(),
                     set, false);
            }
         }  
         nextPos = 1; 
      }
   }

   private void brackets(String in, String query, int pos) {
      int index = 0;
      int nextPos = 0;

      while (index != -1) {
         index = in.indexOf(query, index + nextPos);
         if (index != -1) {
            doc.setCharacterAttributes(index + pos, 1, brSet, false);
         }
         nextPos = 1;
      }
   }

   private void stringLiterals(String in, int pos) {
      int indStart = 0;
      int indEnd = 0;
      int nextPos = 1;
      while ( indStart != -1 && indEnd != -1 ) {
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
         if (lineComInd != -1 && !Finder.isInQuotes( in, lineComInd)) {
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

      while (indStart != -1) {
         indStart = in.indexOf(blockCmntStart, indStart + nextPos);
         if (indStart != -1 && !Finder.isInQuotes(in, indStart)) {       
            int indEnd = in.indexOf(blockCmntEnd, indStart + 1);
            if (indEnd != -1 && !Finder.isInQuotes(in, indEnd)) {
               int indNextStart = in.substring
                     (indStart + 1, indEnd).indexOf(blockCmntStart, 0);

               if (indNextStart == -1) {
                  int length = indEnd - indStart + blockCmntEnd.length();
                  doc.setCharacterAttributes(indStart, length, comSet, false);
                  /*
                   * maybe part of an existing block is outcommented */
                  if (isSingleLines) {
                     uncommentBlock(in, indEnd + 2);
                     uncommentBlock(in, indStart - 2);
                  }
               }
            }
         }         
         nextPos = 1;
      }
   }

   void uncommentBlock(String in, int pos) {

      // positions of previous block comment start and next block comment end
      int indBlockStart = Finder.indLastBlockStart(in, pos, blockCmntStart,
            blockCmntEnd);
      int indBlockEnd   = Finder.indNextBlockEnd(in, pos, blockCmntStart,
            blockCmntEnd);

      if (indBlockStart != -1 && indBlockEnd == -1) {
         String toUncomment = in.substring(indBlockStart, pos);
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
   
   private void setStyles() {
      Color commentGreen = new Color(60, 190, 80);
      StyleConstants.setForeground(comSet, commentGreen);
      StyleConstants.setBold(comSet, false);

      Color keyPink = new Color(230, 0, 110);
      StyleConstants.setForeground(keySet, keyPink);
      StyleConstants.setBold(keySet, false);

      Color bracketBlue = new Color(70, 0, 220);
      StyleConstants.setForeground(brSet, bracketBlue);
      StyleConstants.setBold(brSet, true);

      Color strLitOrange = new Color(230, 140, 50);
      StyleConstants.setForeground(strLitSet, strLitOrange );
      StyleConstants.setBold(strLitSet, false );
   }
}
      