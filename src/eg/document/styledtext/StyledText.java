package eg.document.styledtext;

import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

/**
 * The styling of the text contained in a <code>StyledDocument</code>
 */
public abstract class StyledText {

   private final StyledDocument doc;
   private final SimpleAttributeSet normal;

   /**
    * @param doc  the document that contains the text
    * @param normal  the SimpleAttributeSet for normal text style
    */
   protected StyledText(StyledDocument doc, SimpleAttributeSet normal) {
      this.doc = doc;
      this.normal = normal;
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), normal, false);
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
    * in the entire text
    */
   public void resetAttributes() {
      setAttributes(0, doc.getLength(), normal);
   }

   /**
    * Resets character attributes to the attributes for normal text
    * in a section of text
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
    * @param set  the attributes applied to the section; expected
    * to be selected from {@link Attributes}
    */
   public void setAttributes(int pos, int length, SimpleAttributeSet set) {
      doc.setCharacterAttributes(pos, length, set, false);
   }

   /**
    * Gets this document
    *
    * @return this document
    */
   protected final StyledDocument doc() {
      return doc;
   }
}
