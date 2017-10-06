package eg.document;

import java.awt.Color;

import javax.swing.JPanel;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

//--Eadgyth--//
import eg.utils.FileUtils;
import eg.ui.LineNrWidthAdaptable;

/**
 * The document that contains line numbers.<br>
 * Created in {@link FileDocument}
 */
public class LineNumberDocument {
   
   private final static Color GRAY = new Color(170, 170, 170);
   
   private final SimpleAttributeSet set = new SimpleAttributeSet();
   private final StyledDocument doc;
   private final LineNrWidthAdaptable lineNrWidth;
   private final StringBuilder lineNrBuilder = new StringBuilder();

   /**
    * @param doc  the document associated with the area that displays
    * line numbers
    * @param  lineNrWidth  the reference to the {@link LineNrWidthAdaptable}
    */
   public LineNumberDocument(StyledDocument doc,
         LineNrWidthAdaptable lineNrWidth) {

      this.doc = doc;
      this.lineNrWidth = lineNrWidth;
      setDocStyle();
   }
   
   /**
    * Appends line numbers
    *
    * @param prevLineNr  the previous number of lines
    * @param lineNr  the new number of lines
    */
   public void appendLineNumbers(int prevLineNr, int lineNr) {
      lineNrBuilder.setLength(0);
      for (int i = prevLineNr + 1; i <= lineNr; i++) {
         lineNrBuilder.append(Integer.toString(i));
         lineNrBuilder.append("\n");
      }
      try {
         doc.insertString(doc.getLength(), lineNrBuilder.toString(), set);
         lineNrWidth.adaptLineNrWidth(prevLineNr, lineNr);
      }
      catch(BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Removes line numbers
    *
    * @param prevLineNr  the previous number of lines
    * @param lineNr  the new number of lines
    */
   public void removeLineNumbers(int prevLineNr, int lineNr) {
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
    
   private void setDocStyle() {
      StyleConstants.setForeground(set, GRAY);
      StyleConstants.setAlignment(set, StyleConstants.ALIGN_RIGHT);
      StyleConstants.setLineSpacing(set, 0.25f);
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), set, false);
   }
}
