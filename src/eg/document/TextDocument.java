package eg.document;

import java.awt.Color;

import javax.swing.JTextPane;

import javax.swing.event.CaretListener;

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
    * @return  this text area that display the document
    */
   public JTextPane docTextArea() {
      return textArea;
   }
   
   /**
    * Returns the text in this document
    *
    * @return  the text in this <code>StyledDocument</code>
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
    * @return  the document length
    */
   public int length() {
      return doc.getLength();
   }

   /**
    * Inserts text in this document
    *
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      try {
         doc.insertString(pos, toInsert, null);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Removes text from this document
    *
    * @param start  the position where text to be removed starts
    * @param length  the length of the text to be removed
    */
   public void removeStr(int start, int length) {
      try {
         doc.remove(start, length);
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
