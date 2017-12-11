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
   
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();
   
   private final JTextPane textArea;
   private final StyledDocument doc;
   
   static {
      StyleConstants.setForeground(SET, Color.BLACK);
      StyleConstants.setBold(SET, false);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }
   
   /**
    * @param textArea  the <code>JTextPane</code> that displays the
    * document
    */
   public TextDocument(JTextPane textArea) {
      this.textArea = textArea;
      doc = textArea.getStyledDocument();
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), SET, false);
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
    * black and plain
    *
    * @return  the <code>SimpleAttributeSet</code>
    */
   public SimpleAttributeSet attrSet() {
      return SET;
   }
   
   /**
    * Gets this text area that displays the document's content
    *
    * @return  the text area
    */
   public JTextPane textArea() {
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
    * Gets the length of this document text
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
    * @param toInsert  the String
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
}
