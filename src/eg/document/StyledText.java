package eg.document;

import javax.swing.JTextPane;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;


//--Eadgyth--/
import eg.utils.FileUtils;

/**
 * The styled text in a <code>JTextPane</code>
 */
public abstract class StyledText {

   /**
    * The <code>StyledDocument</code> that belongs to this
    * <code>JTextPane</code>
    */
   protected final StyledDocument doc;
  
   private final SimpleAttributeSet normal;
   private final JTextPane textArea;
   
   /**
    * @param textArea  the JTextPane that displays the text
    * @param normal  the SimpleAttributeSet for normal text
    */
   public StyledText(JTextPane textArea, SimpleAttributeSet normal) {
      this.textArea = textArea;
      this.normal = normal;
      doc = textArea.getStyledDocument();
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), normal, false);
   }

   /**
    * Gets this <code>Attributes</code>
    *
    * @return  the Attributes
    */
   public abstract Attributes attributes();

   /**
    * Gets the text in the document
    *
    * @return  the text
    */
   public abstract String text();
   
   /**
    * Inserts a string in the document
    *
    * @param pos  the position where the string is inserted
    * @param s  the string
    */
   public final void insert(int pos, String s) {
      try {
         doc.insertString(pos, s, null);
      }
      catch (BadLocationException e) {
         FileUtils.log(e);
      }
   }

   /**
    * Resets to the character attributes for normal text in the entire
    * in entire text
    */
   public void resetAttributes() {
      setAttributes(0, doc.getLength(), normal);
   }

   /**
    * Resets to the character attributes for normal text in a section of
    * text
    *
    * @param pos  the position where the section start
    * @param length  the length of the section
    */
   public void resetAttributes(int pos, int length) {
      setAttributes(pos, length, normal);
   }

   /**
    * Sets character attributes in a section of the text
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param set  the attributes applied to the section
    */
   public void setAttributes(int pos, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(pos, length, set, false);
   }

   /**
    * Gets the text area that displays the text
    *
    * @return  the text area
    */
   public final JTextPane textArea() {
      return textArea;
   }
}
