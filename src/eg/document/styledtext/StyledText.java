package eg.document.styledtext;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The styling of the text in a <code>StyledDocument</code>
 */
public abstract class StyledText {

   /**
    * The <code>SimpleAttributeSte</code> for normal text */
   protected static final SimpleAttributeSet NORMAL = new SimpleAttributeSet();
   /**
    * The <code>StyledDocument</code> that contains the text */
   protected final StyledDocument doc;

   static {
      StyleConstants.setBold(NORMAL, false);
   }

   /**
    * @param doc  the document that contains the text
    * @param normalText  the color for normal text
    */
   protected StyledText(StyledDocument doc, Color normalText) {
      this.doc = doc;
      StyleConstants.setForeground(NORMAL, normalText);
      doc.setParagraphAttributes(0, doc.getLength(), NORMAL, false);
   }

   /**
    * Returns this <code>Attributes</code>
    *
    * @return  the Attributes
    */
   public abstract Attributes attributes();

   /**
    * Returns the text
    *
    * @return  the text
    */
   public abstract String text();

   /**
    * Resets character attributes to the attributes for normal text
    * in a section of text
    *
    * @param pos  the position where the section start
    * @param length  the length of the section
    */
   public void resetAttributes(int pos, int length) {
      setAttributes(pos, length, NORMAL);
   }

   /**
    * Sets character attributes in a section of the text
    *
    * @param pos  the position where the section starts
    * @param length  the length of the section
    * @param set  the attributes applied to the section; expected
    * to be selected from {@link Attributes}
    */
   public void setAttributes(int pos, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(pos, length, set, false);
   }
}
