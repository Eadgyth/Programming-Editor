package eg.document;

import java.awt.Color;

import javax.swing.JTextPane;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

//--Eadgyth--//
import eg.utils.FileUtils;
import eg.utils.LinesFinder;

/**
 * The line numbering
 */
public class LineNumbers {

   private final static Color GRAY = new Color(170, 170, 170);
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();

   private final StyledDocument doc;
   private final StringBuilder lineNrBuilder = new StringBuilder();

   private int nOld = 1;

   static {
      StyleConstants.setForeground(SET, GRAY);
      StyleConstants.setAlignment(SET, StyleConstants.ALIGN_RIGHT);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }

   /**
    * @param lineNrArea  the <code>JTextPane</code> that displays line
    * numbers
    */
   public LineNumbers(JTextPane lineNrArea) {
      this.doc = lineNrArea.getStyledDocument();
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), SET, false);
      appendLineNumbers(0, nOld);
   }

   /**
    * Updates the line numbers for the specified text
    *
    * @param text  the text
    */
   public void updateLineNumber(String text) {
      int nNew = LinesFinder.lineCount(text);
      if (nNew > nOld) {
         appendLineNumbers(nOld, nNew);
      }
      else if (nNew < nOld) {
         removeLineNumbers(nOld, nNew);
      }
      nOld = nNew;
   }

   //
   //--private--/
   //

   private void appendLineNumbers(int prevLineNr, int lineNr) {
      lineNrBuilder.setLength(0);
      for (int i = prevLineNr + 1; i <= lineNr; i++) {
         lineNrBuilder.append(i).append("\n");
      }
      try {
         doc.insertString(doc.getLength(), lineNrBuilder.toString(), SET);
      }
      catch (BadLocationException e) {
         FileUtils.log(e);
      }
   }

   private void removeLineNumbers(int prevLineNr, int lineNr) {
      int length = 0;
      for (int i = prevLineNr; i > lineNr; i--) {
          length += ((int) Math.log10(i) + 1) + 1;
      }
      try {
         doc.remove(doc.getLength() - length, length);
      }
      catch (BadLocationException e) {
         FileUtils.log(e);
      }
   }
}
