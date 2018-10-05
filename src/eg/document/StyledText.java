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
 * The text that is edited.
 * <p>
 * Created in {@link EditableDocument}
 */
public class StyledText {
   
   private final static SimpleAttributeSet SET = new SimpleAttributeSet();
   
   private final JTextPane textArea;
   private final StyledDocument doc;
   
   private String text = "";
   
   static {
      StyleConstants.setForeground(SET, Color.BLACK);
      StyleConstants.setBold(SET, false);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }
   
   /**
    * @param textArea  the reference to the <code>JTextPane</code> that
    * displays the text
    */
   public StyledText(JTextPane textArea) {
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
    * Updates this text
    */
   public void updateText() {
      try {
         text = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
   
   /**
    * Gets this text which is contained in the <code>Document</code>
    * that is displayed in this text area. The text is alsways only
    * the text that is updated by {@link #updateText()}.
    *
    * @return  the text
    */
   public String text() {
      return text;
   }
   
   /**
    * Resets the character attributes in the entire text to black
    * and plain
    */
   public void resetAttributes() {
      setAttributes(0, doc.getLength(), SET);
   }
   
   /**
    * Resets the character attributes in a section of the text to
    * black and plain
    *
    * @param pos  the position where the section start
    * @param length  the length of the section
    */
   public void resetAttributes(int pos, int length) {
      setAttributes(pos, length, SET);
   }
   
   /**
    * Sets character attributes in a section of the text
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param set  the character attributes
    */
   public void setAttributes(int pos, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(pos, length, set, false);
   }
   
   /**
    * Appends a string
    *
    * @param toAppend  the string to append
    */
   public void append(String toAppend) {
      insert(doc.getLength(), toAppend);
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
    * Removes text
    *
    * @param pos  the position where the text to be removed starts
    * @param length  the length of the text to be removed
    */
   public void remove(int pos, int length) {
      try {
         doc.remove(pos, length);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
   
   /**
    * Gets the text area that displays the text
    *
    * @return  the text area
    */
   public JTextPane textArea() {
      return textArea;
   }
}
