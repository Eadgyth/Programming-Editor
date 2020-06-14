package eg.document.styledtext;

import java.awt.FontMetrics;

import javax.swing.JTextPane;

import javax.swing.event.DocumentListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.utils.FileUtils;

/**
 * The styled text that is edited
 */
public class EditableText extends StyledText {

   private static final BackgroundTheme THEME = BackgroundTheme.givenTheme();
   private static final Attributes ATTR = new Attributes(THEME);
   public static final SimpleAttributeSet SET = new SimpleAttributeSet();

   private final JTextPane textArea;

   private String text = "";

   static {
      StyleConstants.setForeground(SET, THEME.normalText());
      StyleConstants.setBold(SET, false);
		StyleConstants.setLineSpacing(SET, 0.14f);
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
    * Inserts text
    *
    * @param pos  the insert position
    * @param s  the string that contains the text
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
    * @param pos  the start position of the removal
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
    * Sets the tab length
    *
    * @param nSpaces  the length in number of spaces; not 0
    */
   public final void setTabLength(int nSpaces) {
      if (nSpaces == 0) {
         throw new IllegalArgumentException(
               "The number of spaces cannot not be 0");
      }
      FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
      int length = fm.charWidth(' ') * nSpaces;
      TabStop[] ts = new TabStop[20];
      for (int i = 0; i < ts.length; i++) {
         ts[i] = new TabStop((i + 1) * length);
      }
      TabSet tabSet = new TabSet(ts);
      StyleConstants.setTabSet(SET, tabSet);
      doc().setParagraphAttributes(0, doc().getLength(), SET, false);
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
