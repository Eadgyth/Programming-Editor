package eg.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Shape;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;

import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

//--Eadgyth--/
import eg.BackgroundTheme;
import eg.utils.ScreenParams;

/**
 * Defines the panel that contains the area for editing text and
 * the area that displays line numbers.
 * <p>
 * Class uses code written by Stanislav Lapitsky to allow
 * correct line/word wrapping in JTextPane. Two contributions
 * are (and have to be) combined:<br>
 * https://stackoverflow.com/questions/30590031/
 *    jtextpane-line-wrap-behavior
 * <br>
 * https://stackoverflow.com/questions/11000220/
 *    strange-text-wrapping-with-styled-text-in-jtextpane-with-java-7/
 *    14230668#14230668
 */
public final class EditArea {

   private static final BackgroundTheme THEME = BackgroundTheme.givenTheme();
   private static final LineBorder LINE_NR_AREA_BORDER
         = new LineBorder(THEME.background(), 5);
   private static final MatteBorder AREA_BORDER
         = new MatteBorder(5, 5, 0, 0, THEME.background());

   private final JPanel content = UIComponents.grayBorderedPanel();
   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineNrArea = new JTextPane();
   private final JPanel nonWordwrapPnl = new JPanel(new BorderLayout());
   private final JScrollPane nonWordwrapScroll = UIComponents.scrollPane();
   private final JScrollPane wordwrapScroll = UIComponents.scrollPane();
   private final JScrollPane lineNrScroll = new JScrollPane(
         ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

   private boolean isWordwrap;

   /**
    * @param wordwrap  true to enable, false to disable wordwrap
    * @param lineNumbers  true to show, false to hide line numbers.
    * Effectless if wordwrap is true.
    * @param font  the font name
    * @param fontSize  the font size
    */
   public EditArea(boolean wordwrap, boolean lineNumbers,
         String font, int fontSize) {

      this.isWordwrap = wordwrap;
      content.setLayout(new BorderLayout());
      textArea.setEditorKit(new WrapEditorKit());
      initTextArea();
      initLineNrArea();
      initLineNrScrollPane();
      nonWordwrapScroll.setViewportView(nonWordwrapPnl);
      setFont(font, fontSize);
      if (wordwrap) {
         enableWordwrap();
      }
      else {
         disableWordwrap(lineNumbers);
      }
      textArea.addFocusListener(new FocusAdapter() {

         @Override
         public void focusLost(FocusEvent e) {
            textArea.getCaret().setSelectionVisible(true);
         }
      });
      lineNrArea.getStyledDocument().addDocumentListener(lineNrChange);
   }

   /**
    * Returns this <code>JPanel</code> that contains the area for editing
    * text and, if selected, the area that shows line numbers
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Returns this area for editing text
    *
    * @return  the text area
    */
   public JTextPane textArea() {
      return textArea;
   }

   /**
    * Returns this area for showing line numbers
    *
    * @return  the text area
    */
   public JTextPane lineNrArea() {
      return lineNrArea;
   }

   /**
    * Enables wordwrap and hides line numbers
    */
   public void enableWordwrap() {
      enableWordwrapImpl();
   }

   /**
    * Disables wordwrap and shows line numbers if the specified
    * boolean is true
    *
    * @param lineNumbers  true to show, false to hide line numbers
    */
   public void disableWordwrap(boolean lineNumbers) {
      showLineNumbersImpl(lineNumbers);
   }

   /**
    * Returns if wordwrap is enabled
    *
    * @return  true if enabled, false otherwise
    */
   public boolean isWordwrap() {
      return isWordwrap;
   }

   /**
    * Shows or hides line numbers
    *
    * @param b  true to show, false to hide line numnbers
    * @throws IllegalStateException  if b is true and wordwrap is
    * currently enabled
    */
   public void showLineNumbers(boolean b) {
      if (isWordwrap) {
         throw new IllegalStateException("Wordwrap is currently enabled");
      }
      showLineNumbersImpl(b);
   }

   /**
    * Sets the font
    *
    * @param font  the font name
    * @param fontSize  the (unscaled) font size
    */
   public void setFont(String font, int fontSize) {
      Font f = new Font(font, Font.PLAIN, ScreenParams.scaledSize(fontSize));
      lineNrArea.setFont(f);
      textArea.setFont(f);
      revalidate();
   }

   //
   //--private--/
   //

   private void showLineNumbersImpl(boolean show) {
      if (isWordwrap) {
         content.remove(wordwrapScroll);
      }
      if (show) {
         content.add(lineNrScroll, BorderLayout.WEST);
      }
      else {
         content.remove(lineNrScroll);
      }
      nonWordwrapPnl.add(textArea, BorderLayout.CENTER);
      content.add(nonWordwrapScroll, BorderLayout.CENTER);
      isWordwrap = false;
      resetScrollPos(nonWordwrapScroll);
   }

   private void enableWordwrapImpl() {
      content.remove(nonWordwrapScroll);
      content.remove(lineNrScroll);
      wordwrapScroll.setViewportView(textArea);
      content.add(wordwrapScroll, BorderLayout.CENTER);
      isWordwrap = true;
      resetScrollPos(wordwrapScroll);
   }

   private void resetScrollPos(JScrollPane scroll) {
      JScrollBar bar = scroll.getVerticalScrollBar();
      EventQueue.invokeLater(() -> {
         bar.setValue(0);
         revalidate();
      });
   }

   private final DocumentListener lineNrChange = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent de) {
         revalidate();
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
         revalidate();
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
         //not used
      }
   };

   private void revalidate() {
      content.revalidate();
      content.repaint();
   }

   private void initTextArea() {
      textArea.setBorder(AREA_BORDER);
      textArea.setBackground(THEME.background());
      textArea.setCaretColor(THEME.normalText());
      textArea.setSelectionColor(THEME.selectionBackground());
      textArea.setSelectedTextColor(THEME.normalText());
   }

   private void initLineNrArea() {
      lineNrArea.setBorder(LINE_NR_AREA_BORDER);
      lineNrArea.setBackground(THEME.background());
      lineNrArea.setEditable(false);
      lineNrArea.setFocusable(false);
      DefaultCaret caret = (DefaultCaret) lineNrArea.getCaret();
      caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
   }

   private void initLineNrScrollPane() {
      lineNrScroll.setBorder(UIComponents.grayMatteBorder(0, 0, 0, 1));
      lineNrScroll.setViewportView(lineNrArea);
      lineNrScroll.getVerticalScrollBar().setModel(
            nonWordwrapScroll.getVerticalScrollBar().getModel());
   }

   @SuppressWarnings("serial")
   private static class WrapEditorKit extends StyledEditorKit {

      private final ViewFactory vf = new WrapColumnFactory();

      @Override
      public ViewFactory getViewFactory() {
         return vf;
      }
   }

   private static class WrapColumnFactory implements ViewFactory {

      @Override
      public View create(Element elem) {
         String kind = elem.getName();
         if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
               return new WrapLabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
               //
               // Using WrapParagraphView is (for some reason)
               // absolutely necessary for synthax highlighted text
               // because otherwise letters can clash
               return new WrapParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
               return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
               return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
               return new IconView(elem);
            }
         }
         return new LabelView(elem);
      }
   }

   private static class WrapLabelView extends LabelView {

      private boolean isResetBreakSpots = true;

      private WrapLabelView(Element elem) {
         super(elem);
      }

      @Override
      public float getMinimumSpan(int axis) {
         switch (axis) {
         case View.X_AXIS:
            //
            // 0 means that the minimum width of the content is
            // never bigger than the viewport -> no resize
            return 0;
         case View.Y_AXIS:
            return super.getMinimumSpan(axis);
         default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
         }
      }

      @Override
      public View breakView(int axis, int p0, float pos, float len) {
         if (axis == View.X_AXIS) {
            resetBreakSpots();
         }
         return super.breakView(axis, p0, pos, len);
      }

      @Override
      public void preferenceChanged(View child, boolean width, boolean height) {
         if (!isResetBreakSpots) {
            super.preferenceChanged(child, width, height);
         }
      }

      private void resetBreakSpots() {
         isResetBreakSpots = true;
         //
         // removeUpdate in superclass only calls
         // preferenceChanged asking for a pref change
         // in width (width=true; height=false)
         removeUpdate(null, null, null);
         isResetBreakSpots = false;
      }
   }

   private static class WrapParagraphView extends ParagraphView {

      private WrapParagraphView(Element elem) {
         super(elem);
      }

      @Override
      public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
         super.removeUpdate(e, a, f);
         resetBreakSpots();
      }

      @Override
      public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
         super.insertUpdate(e, a, f);
         resetBreakSpots();
      }

		private void resetBreakSpots() {
			for (int i = 0; i < layoutPool.getViewCount(); i++) {
				View view = layoutPool.getView(i);
            if (view instanceof WrapLabelView) {
            	((WrapLabelView) view).resetBreakSpots();
           	}
			}
      }
   }
}
