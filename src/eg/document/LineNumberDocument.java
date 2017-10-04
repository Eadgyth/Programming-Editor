package eg.document;

import java.awt.Color;

import javax.swing.JPanel;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import eg.utils.FileUtils;

/**
 * The document that contains line numbers.<br>
 * Created in {@link FileDocument}
 */
public class LineNumberDocument {
   
   private final static Color GRAY = new Color(170, 170, 170);
   
   private final SimpleAttributeSet lineSet = new SimpleAttributeSet();
   private final StyledDocument lineDoc;
   private final JPanel editAreaPanel;
   private final StringBuilder lineNrBuilder = new StringBuilder();
   
   /**
    * @param lineDoc  the document associated with the area that displays
    * line numbers
    * @param  editAreaPanel  the JPanel that contains the text area and the line
    * number area (used to revaltidate the width of line number area)
    */
   public LineNumberDocument(StyledDocument lineDoc, JPanel editAreaPanel) {
      this.lineDoc = lineDoc;
      this.editAreaPanel = editAreaPanel;
      setLineDocStyle();
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
         lineDoc.insertString(lineDoc.getLength(), lineNrBuilder.toString(),
               lineSet);
         revalidateWidth();
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
         lineDoc.remove(lineDoc.getLength() - length, length);
         revalidateWidth();
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
   
   private void revalidateWidth() {
      editAreaPanel.revalidate();
      editAreaPanel.repaint();
   }
    
   private void setLineDocStyle() {
      StyleConstants.setForeground(lineSet, GRAY);
      StyleConstants.setAlignment(lineSet, StyleConstants.ALIGN_RIGHT);
      StyleConstants.setLineSpacing(lineSet, 0.25f);
      Element el = lineDoc.getParagraphElement(0);
      lineDoc.setParagraphAttributes(0, el.getEndOffset(), lineSet, false);
   }
}
