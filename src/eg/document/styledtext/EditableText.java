package eg.document.styledtext;

import javax.swing.JTextPane;

import javax.swing.event.DocumentListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.utils.FileUtils;

/**
 * The styled text that is edited
 */
public class EditableText extends StyledText {
   
   private static final BackgroundTheme THEME = BackgroundTheme.givenTheme();
   private static final Attributes ATTR = new Attributes(THEME);
   private static final SimpleAttributeSet SET = new SimpleAttributeSet();

   private final JTextPane textArea;
   
   private String text = "";
   
   static {
      StyleConstants.setForeground(SET, THEME.normalText());
      StyleConstants.setBold(SET, false);
      StyleConstants.setLineSpacing(SET, 0.25f);
   }
   
   /**
    * @param textArea  the JTextPane that displays the text
    */
   public EditableText(JTextPane textArea) {
      super(textArea.getStyledDocument(), SET);
      this.textArea = textArea;
   }
   
   @Override
   public final Attributes attributes() {
      return ATTR;
   }
   
   /**
    * {@inheritDoc}.
    * This is always only the copy of the document text which is updated
    * by {@link #updateTextCopy}
    */
   @Override
   public final String text() {
      return text;
   }

   /**
    * Updates this copy of the text contained in the document
    */
   public final void updateTextCopy() {
      try {
         text = doc().getText(0, doc().getLength());
      }
      catch (BadLocationException e) {
         FileUtils.log(e);
      }
   }
   
   /**
    * Appends a string
    *
    * @param s  the string to append
    */
   public final void append(String s) {
      insert(doc().getLength(), s);
   }
   
   /**
    * Inserts a string
    *
    * @param pos  the position where the string is inserted
    * @param s  the string
    */
   public final void insert(int pos, String s) {
      try {
         doc().insertString(pos, s, null);
      }
      catch (BadLocationException e) {
         FileUtils.log(e);
      }
   }

   /**
    * Removes text
    *
    * @param pos  the position where the removed text starts
    * @param length  the length of the removed text
    */
   public final void remove(int pos, int length) {
      try {
         doc().remove(pos, length);
      }
      catch (BadLocationException e) {
         FileUtils.log(e);
      }
   }
   
   /**
    * Adds a <code>DocumentListener</code>
    *
    * @param dl  the DocumentListener
    */
   public final void addDocumentListener(DocumentListener dl) {
      doc().addDocumentListener(dl);
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
