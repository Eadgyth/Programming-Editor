package eg.document;

import java.awt.Color;

import javax.swing.JTextPane;

import javax.swing.event.DocumentListener;

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
    * document that is edited
    */
   public TextDocument(JTextPane textArea) {
      this.textArea = textArea;
      doc = textArea.getStyledDocument();
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), SET, false);
   }
   
   /**
    * Adds a <code>DocumentListener</code>
    *
    * @param dl  the <code>DocumentListener</code>
    */
   public void addDocumentListener(DocumentListener dl) {
      doc.addDocumentListener(dl);
   }
   
   /**
    * (Re-)sets the characters in the section that starts at the specified
    * position and spans the specified length to black and plain
    *
    * @param pos  the position
    * @param length  the length
    */
   public void setCharAttrBlack(int pos, int length) {
      setCharAttr(pos, length, SET);
   }

   /**
    * (Re-)sets all characters to black and plain
    */
   public void setAllCharAttrBlack() {
      setCharAttr(0, doc.getLength(), SET);
   }
   
   /**
    * Sets the character attributes in the section that starts at the specified
    * position and spans the specified length
    *
    * @param pos  the position
    * @param length  the length
    * @param set  the character attributes
    */
   public void setCharAttr(int pos, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(pos, length, set, false);
   }
   
   /**
    * Gets this text area that displays the document
    *
    * @return  the text area
    */
   public JTextPane textArea() {
      return textArea;
   }
   
   /**
    * Gets the text in this document
    *
    * @return  the text
    */
   public String docText() {
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
   public int doclength() {
      return doc.getLength();
   }

   /**
    * Inserts a string
    *
    * @param pos  the position where the string is inserted
    * @param toInsert  the string
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
