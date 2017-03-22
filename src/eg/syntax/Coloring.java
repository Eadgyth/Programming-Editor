package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--//
import eg.Languages;
import eg.utils.Finder;

public class Coloring {
   
   private final SimpleAttributeSet keyRedSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet keyBlueSet = new SimpleAttributeSet();
   private final SimpleAttributeSet cmntSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet      = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet normalSet;  
   private final StyledDocument doc;

   private boolean isBlockCmnt = true;
   private boolean isSingleLines;
   private Colorable colorable;
   
   public Coloring(StyledDocument doc, SimpleAttributeSet normalSet) {
      this.doc = doc;
      this.normalSet = normalSet;
      setStyles();
   }
   
   /**
    * Selects a Colorable object based on the language
    * @param lang  the language which is one of the constants
    * in {@link eg.Languages} but not PLAIN_TEXT
    */
   public void selectColorable(Languages lang) {
      switch(lang) {       
         case JAVA:
            colorable = new JavaColoring();
            break;
         case HTML:
            colorable = new HtmlColoring();
            break;
         case PERL:
            colorable = new PerlColoring();
            break;
         default:
            throw new IllegalArgumentException("'lang' is not"
                  + " a coding language");
      }
   }
   
   /**
    * Enables to perform coloring in single lines, where possible
    * @param isEnabled  true to enable to coloring in single lines
    */
   public void enableSingleLines(boolean isEnabled) {
      isSingleLines = isEnabled;
   }
   
   /**
    * Colors text.
    * <p>
    * Coloring is performed in single lines if enabled through
    * {@link #enableSingleLines(boolean)}, otherwise the entire
    * text is scanned.
    * @param in  the text
    * @param pos  the current caret position
    */
   public void color(String in, int pos) {
      if (colorable == null) {
         throw new IllegalStateException("No Colorable is set");
      }
      String chunk;
      int posStart = pos;
      if (isSingleLines) {
         chunk = Finder.currLine(in, pos);
         posStart = Finder.lastReturn(in, pos) + 1;
      }
      else {
         chunk = in;
      }
      colorable.color(in, chunk, posStart, this);
   }
   
   /**
    * (Re-)colors a portion of text in black
    * @param start  the position where the recolored text starts
    * @param length  the length of the text to be recolored
    */
   public void setCharAttrBlack(int start, int length) {
      doc.setCharacterAttributes(start, length, normalSet, false);
   }
   
   /**
    * Colors a portion of text in keyword red
    * @param start  the position where the recolored text starts
    * @param length  the length of the text to be recolored
    */
   public void setCharAttrKeyBlue(int start, int length) {
      doc.setCharacterAttributes(start, length, keyBlueSet, false);
   }
   
   /**
    * Colors keywords in red
    * @param in  the text which may be a single line of the entire text
    * @param key  the keywords
    * @param pos  the start pos of the text within the entire text
    * @param reqWord  if the keywords must be a word
    */
   public void keysRed(String in, String key, int pos, boolean reqWord) {
      words(in, key, keyRedSet, pos, reqWord);
   }
   
   /**
    * Colors keywords in blue
    * @param in  the text which may be a single line of the entire text
    * @param key  the keywords
    * @param pos  the start pos of the text within the entire text
    * @param reqWord  if the keywords must be a word
    */
   public void keysBlue(String in, String key, int pos, boolean reqWord) {
      words(in, key, keyBlueSet, pos, reqWord);
   }
   
   /**
    * Colors brackets in blue and bold
    * @param in  the text which may be a single line of the entire text
    * @param bracket  the bracket
    * @param pos  the start pos of the text within the entire text
    */
   public void brackets(String in, String bracket, int pos) {
      words(in, bracket, brSet, pos, false);
   }
   
   /**
    * Colors words that follow a flag (like $) but whose lengths is unknown
    * in blue
    * @param in  the text which may be a single line of the entire text
    * @param  flag  the start signal for the word
    * @param pos  the start pos of the text within the entire text
    */
   public void withFlag(String in, String flag, int pos) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(flag, start + jump);
         if (start != -1 && SyntaxUtils.isWordStart(in, start)) {
            int length = SyntaxUtils.wordLength(in.substring(start));
            doc.setCharacterAttributes(start + pos, length, keyBlueSet, false);
         }  
         jump = 1; 
      }
   }
   
   /**
    * Colors String literals
    * @param in  the text which may be a single line of the entire text
    * @param pos  the start pos of the text within the entire text
    * @param blockStart  the String that represents the start of a text block
    * where the String literal must be found in. Null to ignore any ocurrence
    * in a block
    * @param blockEnd  the String that represents the end of a text block
    * where the String literal must be found in. Not null if {@code blockStart}
    * is not null
    */
   public void stringLiterals(String in, int pos, String blockStart,
         String blockEnd) {   
     if (!isSingleLines) {
         if (in.replaceAll("\n", "").length() > 0) {
            String[] chunkArr = in.split("\n");
            int[] startOfLines = Finder.startOfLines(chunkArr);
            for (int i = 0; i < chunkArr.length; i++) {
               stringLitChunk(chunkArr[i], startOfLines[i] + pos,
                      blockStart, blockEnd);
            }
         }
      }
      else {
         stringLitChunk(in, pos, blockStart, blockEnd);
      }
   }

   /**
    * Colors line comments
    * @param in  the text which may be a single line from the entire text
    * @param pos  the start position of the text within the entire text
    * @param lineCmnt  the String that represents the start of line comment
    */
   public void lineComments(String in, int pos, String lineCmnt) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(lineCmnt, start + jump);
         if (start != -1 && !SyntaxUtils.isInQuotes(in, start, lineCmnt.length())) {
            int lineEnd = in.indexOf("\n", start + 1);
            int length;
            if (lineEnd != -1) {
               length = lineEnd - start;
            }
            else {
               length = in.length() - start;
            }
            doc.setCharacterAttributes(start + pos, length,
                  cmntSet, false);
         }
         jump = 1;
      }
   }
   
   /**
    * Colors block comments but also recolors the text when a block is
    * uncommented
    * @param in  the entire text
    * @param blockCmntStart  the String that represents the start signal for
    * a block
    * @param blockCmntEnd  the String that represents the end signal for a
    * bloack
    */
   public void blockComments(String in, String blockCmntStart, String blockCmntEnd) {    
      if (!isBlockCmnt) {
         return;
      }

      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(blockCmntStart, start + jump);
         if (start != -1 && !SyntaxUtils.isInQuotes(in, start, blockCmntStart.length())) {
            int end = SyntaxUtils.indNextBlockEnd(in, start + 1, blockCmntStart,
                  blockCmntEnd);
            if (end != -1) {
               int length = end - start + blockCmntEnd.length();
               if (isSingleLines) {
                  uncommentBlock(in, end + blockCmntEnd.length(),
                        blockCmntStart, blockCmntEnd);
                  uncommentBlock(in, start + blockCmntStart.length(),
                        blockCmntStart, blockCmntEnd);
               }
               doc.setCharacterAttributes(start, length, cmntSet, false);
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
   
   /**
    * Returns if the specified pos is found in a certain block of text
    * @param in  the entire text
    * @param pos  the position that may be found in a block of text
    * @param blockStart  the String that defines the blocj start
    * @param blockEnd  the String that defines the block end
    * @return  if the specified pos is found in a certain block of text 
    */
   public boolean isInBlock(String in, int pos, String blockStart,
         String blockEnd) {

      int lastStart = SyntaxUtils.indLastBlockStart(in, pos, blockStart,
            blockEnd);
      int nextEnd   = SyntaxUtils.indNextBlockEnd(in, pos, blockStart,
            blockEnd);
      return lastStart != -1 & nextEnd != -1;
   }
   
   //
   //--private methods--
   //
   
   private void words(String in, String key, SimpleAttributeSet set,
         int pos, boolean reqWord) {
      int start = 0;
      int jump = 0;
      while (start != -1) {
         start = in.indexOf(key, start + jump);
         if (start != -1) {
            boolean ok = !reqWord || SyntaxUtils.isWord(in, key, start);
            if (ok) {
               doc.setCharacterAttributes(start + pos, key.length(),
                     set, false);
            }
         }  
         jump = 1; 
      }
   }
   
   private void stringLitChunk(String in, int pos, String blockStart,
         String blockEnd) {

      int start = 0;
      int end = 0;
      int jump = 0;
      while (start != -1 && end != -1) {
         start = in.indexOf("\"", end + jump);
         if (start != -1 ) {
            end = in.indexOf("\"", start + 1);
            if (end != -1 ) {
               int length = end - start;
               if (blockStart == null || 
                  isInBlock(in, start, blockStart, blockEnd)) {
                     doc.setCharacterAttributes(start + pos, length + 1,
                           strLitSet, false );
               }
            }    
         }
         jump = 1;
      }
   }

   private void uncommentBlock(String in, int pos, String blockStart,
         String blockEnd) {

      int lastStart = SyntaxUtils.indLastBlockStart(in, pos, blockStart,
            blockEnd);
      int nextEnd   = SyntaxUtils.indNextBlockEnd(in, pos, blockStart,
            blockEnd);
      if (lastStart != -1 && nextEnd == -1) {
         String toUncomment = in.substring(lastStart, pos + blockStart.length());
         colSectionExBlock(toUncomment, lastStart);
      }
      else if (nextEnd != -1 && lastStart == -1) {
         String toUncomment = in.substring(pos, nextEnd + blockEnd.length());
         colSectionExBlock(toUncomment, pos);
      }
   }

   private void colSectionExBlock(String section, int pos) {
      enableSingleLines(false);
      isBlockCmnt = false;
      color(section, pos);
      enableSingleLines(true);
      isBlockCmnt = true;
   }
   
   private void setStyles() {
      Color commentGreen = new Color(80, 190, 80);
      StyleConstants.setForeground(cmntSet, commentGreen);
      StyleConstants.setBold(cmntSet, false);

      Color keyRed = new Color(230, 0, 90);
      StyleConstants.setForeground(keyRedSet, keyRed);
      StyleConstants.setBold(keyRedSet, false);
      
      Color keyBlue = new Color(60, 60, 250);
      StyleConstants.setForeground(keyBlueSet, keyBlue);
      StyleConstants.setBold(keyBlueSet, false);

      Color bracketBlue = new Color(60, 60, 255);
      StyleConstants.setForeground(brSet, bracketBlue);
      StyleConstants.setBold(brSet, true);

      Color strLitOrange = new Color(230, 140, 50);
      StyleConstants.setForeground(strLitSet, strLitOrange );
      StyleConstants.setBold(strLitSet, false );
   }
}
      
