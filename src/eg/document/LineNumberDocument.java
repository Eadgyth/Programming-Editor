package eg.document;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

//--Eadgyth--//
import eg.utils.FileUtils;
import eg.utils.LinesFinder;
import eg.ui.LineNrWidthAdaptable;

/**
 * The document that contains line numbers.
 * <p>Created in {@link FileDocument}
 */
public class LineNumberDocument {

   private final static Color GRAY = new Color(170, 170, 170);
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();

   private final StyledDocument doc;
   private final LineNrWidthAdaptable lineNrWidth;
   private final StringBuilder lineNrBuilder = new StringBuilder();

   private int nOld = 1;

   static {
      StyleConstants.setForeground(SET, GRAY);
      StyleConstants.setAlignment(SET, StyleConstants.ALIGN_RIGHT);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }

   /**
    * @param doc  the document associated with the area that displays
    * line numbers
    * @param  lineNrWidth  the reference to {@link LineNrWidthAdaptable}
    */
   public LineNumberDocument(StyledDocument doc, LineNrWidthAdaptable lineNrWidth) {
      this.doc = doc;
      this.lineNrWidth = lineNrWidth;
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), SET, false);
      appendLineNumbers(0, nOld);
   }

   /**
    * Updates the line numbers
    *
    * @param text  the text of the document
    */
   public void updateLineNumber(String text) {
      int nNew = LinesFinder.lineNumber(text);
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
         lineNrBuilder.append(Integer.toString(i));
         lineNrBuilder.append("\n");
      }
      try {
         doc.insertString(doc.getLength(), lineNrBuilder.toString(), SET);
         lineNrWidth.adaptLineNrWidth(prevLineNr, lineNr);
      }
      catch(BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   private void removeLineNumbers(int prevLineNr, int lineNr) {
      int length = 0;
      for (int i = prevLineNr; i > lineNr; i--) {
          length += (Integer.toString(i).length() + 1);
      }
      try {
         doc.remove(doc.getLength() - length, length);
         lineNrWidth.adaptLineNrWidth(prevLineNr, lineNr);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
}
