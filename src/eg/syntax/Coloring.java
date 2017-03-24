package eg.syntax;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.Color;

//--Eadgyth--//
import eg.Languages;
import eg.utils.Finder;

/**
 * The coloring of text using a selected {@code Colorable}.
 */
public class Coloring {
   
   private final SimpleAttributeSet keyRedSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet keyBlueSet = new SimpleAttributeSet();
   private final SimpleAttributeSet cmntSet    = new SimpleAttributeSet();
   private final SimpleAttributeSet brSet      = new SimpleAttributeSet();
   private final SimpleAttributeSet strLitSet  = new SimpleAttributeSet();
   private final SimpleAttributeSet normalSet;  
   private final StyledDocument doc;

   private boolean isBlockCmnt = true;
   private boolean isCurrLine;
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
            throw new IllegalArgumentException("No Colorable"
                  + " is defined for " + lang);
      }
   }
   
   /**
    * Enables to perform coloring only in the current line where
    * changes happen
    * @param isEnabled  true to enable to coloring in single lines
    */
   public void enableCurrentLine(boolean isEnabled) {
      isCurrLine = isEnabled;
   }
   
   /**
    * Colors text.
    * <p>
    * Coloring is performed in single lines if enabled through
    * {@link #enableCurrentLine(boolean)}, otherwise the entire text
    * is scanned. However, block comments are always colored using
    * the entire text.
    * <p>
    * Calls {@link Colorable #color(String,String,int,int,this)}
    *
    * @param allText  the entire text of the document
    * @param pos  the pos within the entire text where a change happened
    */
   public void color(String allText, int pos) {
      if (colorable == null) {
         throw new IllegalStateException("No Colorable is selected");
      }
      String toColor;
      int posStart = pos;
      if (isCurrLine) {
         toColor = Finder.currLine(allText, pos);
         posStart = Finder.lastReturn(allText, pos) + 1;
      }
      else {
         toColor = allText;
      }
      colorable.color(allText, toColor, pos, posStart, this);
   }
   
   /**
    * (Re-)colors a portion of text in black
    *
    * @param start  the position where the recolored text starts
    * @param length  the length of the text to be recolored
    */
   public void setCharAttrBlack(int start, int length) {
      doc.setCharacterAttributes(start, length, normalSet, false);
   }
   
   /**
    * Colors a portion of text in keyword blue
    *
    * @param start  the position where the recolored text starts
    * @param length  the length of the text to be recolored
    */
   public void setCharAttrKeyBlue(int start, int length) {
      doc.setCharacterAttributes(start, length, keyBlueSet, false);
   }
   
   /**
    * Searches and colors in red a keyword
    *
    * @param toColor  the text which may a portion from the entire text
    * @param key  the keyword
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param reqWord  if the keyword must be a word
    */
   public void keywordRed(String toColor, String key, int pos, boolean reqWord) {
      string(toColor, key, keyRedSet, pos, reqWord);
   }
   
   /**
    * Searches and colors in blue a keyword
    *
    * @param toColor  the text which may a portion from the entire text
    * @param key  the keyword
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param reqWord  if the keyword must be a word
    */
   public void keywordBlue(String toColor, String key, int pos, boolean reqWord) {
      string(toColor, key, keyBlueSet, pos, reqWord);
   }
   
   /**
    * Searches a bracket and shows it in blue and bold
    *
    * @param toColor  the text which may a portion from the entire text
    * @param bracket  the bracket
    * @param pos  the start position of '{@code toColor}' within the entire text
    */
   public void brackets(String toColor, String bracket, int pos) {
      string(toColor, bracket, brSet, pos, false);
   }
   
   /**
    * Colors string literals in brown
    *
    * @param toColor  the text which may a portion from the entire text
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param blockStart  the String that represents the start of a text block
    * where the String literal must be found in. Null to ignore any ocurrence
    * in a block
    * @param blockEnd  the String that represents the end of a text block
    * where the String literal must be found in. Not null if {@code blockStart}
    * is not null
    */
   public void stringLiterals(String toColor, int pos, String blockStart,
         String blockEnd) {

      if (!isCurrLine) {
         //
         // split because string literals are not colored across lines
         if (toColor.replaceAll("\n", "").length() > 0) {
            String[] chunkArr = toColor.split("\n");
            int[] startOfLines = Finder.startOfLines(chunkArr);
            for (int i = 0; i < chunkArr.length; i++) {
               stringLitLine(chunkArr[i], startOfLines[i] + pos,
                      blockStart, blockEnd);
            }
         }
      }
      else {
         stringLitLine(toColor, pos, blockStart, blockEnd);
      }
   }

   /**
    * Searches and colors commented lines in green
    *
    * @param toColor  the text which may a portion from the entire text
    * @param pos  the start position of '{@code toColor}' within the entire text
    * @param lineCmnt  the String that equals the start of a line comment
    */
   public void lineComments(String toColor, int pos, String lineCmnt) {
      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(lineCmnt, start);
         if (start != -1) {
            if (!SyntaxUtils.isInQuotes(toColor, start, lineCmnt)) {
               int lineEnd = toColor.indexOf("\n", start + 1);
               int length;
               if (lineEnd != -1) {
                  length = lineEnd - start;
               }
               else {
                  length = toColor.length() - start;
               }
               doc.setCharacterAttributes(start + pos, length,
                     cmntSet, false);
            }
            start += 1;
         }
      }
   }
   
   /**
    * Searches and colors block comments in green
    *
    * @param allText  the entire text
    * @param blockStart  the String that represents the start signal for
    * a block
    * @param blockEnd  the String that represents the end signal for a
    * bloack
    */
    public void blockComments(String allText, String blockStart,
          String blockEnd) {

      if (!isBlockCmnt) {
         return;
      }
      //
      // in case the very first block start is removed
      uncommentFirstBlock(allText, blockStart, blockEnd);
      //
      // search for block starts
      int start = 0;
      while (start != -1) {
         start = allText.indexOf(blockStart, start);
         if (start != -1) {
            if (!SyntaxUtils.isInQuotes(allText, start, blockStart)) {
               int end = SyntaxUtils.indNextBlockEnd(allText, start + 1,
                     blockStart, blockEnd);
               if (end != -1) {
                  int length = end - start + blockEnd.length();
                  doc.setCharacterAttributes(start, length, cmntSet, false);
                  //
                  // in case a block start is removed
                  uncommentBlock(allText, end + blockEnd.length(),
                         blockStart, blockEnd);
               }
               else {
                  colSectionExBlock(allText.substring(start), start);
               }
            } 
            start += 1;
         }
      }
   }
   
   //
   //--private methods--
   //
   
   private void string(String toColor, String str, SimpleAttributeSet set,
         int pos, boolean reqWord) {

      int start = 0;
      while (start != -1) {
         start = toColor.indexOf(str, start);
         if (start != -1) {
            boolean ok = !reqWord || SyntaxUtils.isWord(toColor, str, start);
            if (ok) {
               doc.setCharacterAttributes(start + pos, str.length(),
                     set, false);
            }
            start += str.length(); 
         }  
      }
   }
   
   private void stringLitLine(String line, int pos, String blockStart,
         String blockEnd) {

      int start = 0;
      int end = 0;
      while (start != -1 && end != -1) {
         start = line.indexOf("\"", start);
         if (start != -1 ) {
            end = line.indexOf("\"", start + 1);
            if (end != -1 ) {
               int length = end - start;
               boolean ok = blockStart == null
                     || SyntaxUtils.isInBlock(line, start, blockStart, blockEnd);
               if (ok) {
                  doc.setCharacterAttributes(start + pos, length + 1,
                        strLitSet, false );
               }
               start += length + 1;
            } 
         }
      }
   }

   private void uncommentBlock(String allText, int pos, String blockStart,
         String blockEnd) {
      
      if (isCurrLine) {
         int lastStart = SyntaxUtils.indLastBlockStart(allText, pos, blockStart,
               blockEnd);
         int nextEnd   = SyntaxUtils.indNextBlockEnd(allText, pos, blockStart,
               blockEnd);
         if (nextEnd != -1 && lastStart == -1) {
            String toUncomment = allText.substring(pos, nextEnd + blockEnd.length());
            colSectionExBlock(toUncomment, pos);
         }
      }
   }
   
   private void uncommentFirstBlock(String allText, String blockStart,
         String blockEnd ) {
            
      if (isCurrLine) {
         int firstEnd = allText.indexOf(blockEnd, 0);
         if (firstEnd != -1
               && !SyntaxUtils.isInQuotes(allText, firstEnd, blockStart)) {
            int firstStart = allText.lastIndexOf(blockStart, firstEnd);
            if (firstStart == -1) {
               colSectionExBlock(allText.substring(0, firstEnd + 2), 0);
            } 
         }
      }
   }

   private void colSectionExBlock(String section, int pos) {     
      if (isCurrLine) {
         enableCurrentLine(false);
         isBlockCmnt = false;
         color(section, pos);
         enableCurrentLine(true);
         isBlockCmnt = true;
      }
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
      
