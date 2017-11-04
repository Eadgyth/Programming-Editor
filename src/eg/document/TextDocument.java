package eg.document;

import java.awt.Color;

import javax.swing.JTextPane;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import eg.utils.FileUtils;

/**
 * The text document that is edited.<br>
 * Created in {@link FileDocument}
 */
public class TextDocument {
   
   private final JTextPane textArea;
   private final SimpleAttributeSet set = new SimpleAttributeSet();
   private final StyledDocument doc;
   
   /**
    * @param textArea  the <code>JTextPane</code> that displays this
    * document
    */
   public TextDocument(JTextPane textArea) {
      this.textArea = textArea;
      doc = textArea.getStyledDocument();
      setStyle();
   }
   
   /**
    * Gets this <code>StyledDocument</code>
    *
    * @return  this <code>StyledDocument</code>
    */
   public StyledDocument doc() {
      return doc;
   }
   
   /**
    * Gets this <code>SimpleAttributeSet</code> which has the attributes
    * black and not bold 
    *
    * @return  this <code>SimpleAttributeSet</code>
    */
   public SimpleAttributeSet attrSet() {
      return set;
   }
   
   /**
    * Gets this text area that displays the document
    *
    * @return  the text area
    */
   public JTextPane docTextArea() {
      return textArea;
   }
   
   /**
    * Gets the text in this documument
    *
    * @return  the text
    */
   public String getText() {
      String text = null;
      try {
         text = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
      return text;
   }
   
   /**
    * Gets the length of this document
    *
    * @return  the length
    */
   public int length() {
      return doc.getLength();
   }

   /**
    * Inserts the string <code>toInsert</code> at the specified
    * position
    *
    * @param pos  the position
    * @param toInsert  the String to insert
    */
   public void insert(int pos, String toInsert) {
      try {
         doc.insertString(pos, toInsert, null);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Removes text of the specified length at the specified
    * position
    *
    * @param pos  the position
    * @param length  the length
    */
   public void remove(int pos, int length) {
      try {
         doc.remove(pos, length);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
   
   private void setStyle() {
      StyleConstants.setForeground(set, Color.BLACK);
      StyleConstants.setBold(set, false);
      StyleConstants.setLineSpacing(set, 0.25f);
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), set, false);
   }
}
