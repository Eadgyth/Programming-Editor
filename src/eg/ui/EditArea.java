package eg.ui;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;

import java.awt.print.*;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import javax.swing.event.DocumentListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import java.awt.event.FocusListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

//--Eadgyth--//
import eg.Constants;
import eg.utils.FileUtils;

/**
 * Defines the editor view that contains the text area to edit text and the
 * area that displays line numbers.
 */
public final class EditArea {

   private final static Color NUM_GRAY = new Color(170, 170, 170);
   private final static LineBorder WHITE_BORDER
         = new LineBorder(Color.WHITE, 5);

   private final JPanel textPanel = new JPanel(new BorderLayout());

   private final JTextPane textArea = new JTextPane();
   private final SimpleAttributeSet set = new SimpleAttributeSet();
   private final StyledDocument doc;

   private final JTextPane lineArea = new JTextPane();
   private final SimpleAttributeSet lineSet = new SimpleAttributeSet();
   private final StyledDocument lineDoc;

   private final JPanel disabledWordwrapPnl = new JPanel(new BorderLayout());
   private final JPanel enabledWordwrapPnl = new JPanel();
   private final JScrollPane wrapPnlScoll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane lineNumPnlScroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane noWrapPnlScroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane lineNumAreaScroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_NEVER,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

   private boolean isWordWrap;
   private int scrollPos;

   /**
    * @param isWordWrap  true to enable wordwrap
    * @param isLineNumbers  true to show line numbering. Is effectless if
    * wordwrap is enabled
    * @param font  the name of the initial font
    * @param fontSize  the size of the initial font
    */
   public EditArea(boolean isWordWrap, boolean isLineNumbers,
         String font, int fontSize) {

      doc = textArea.getStyledDocument();
      setDocStyle();
      lineDoc = lineArea.getStyledDocument();
      setLineDocStyle();
      removeShortCuts();
      textPanel.setBorder(Constants.DARK_BORDER);
      initTextArea();
      initLineNrArea();
      setFont(font, fontSize);
      initScrollLineNrArea();
      initScrollSimpleArea();
      intitScrollWrapArea();
      if (isWordWrap) {
         enableWordWrap();
      }
      else {
         if (isLineNumbers) {
            showLineNumbers();
         }
         else {
            hideLineNumbers();
         }
      }
      
      textArea.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent fe) {
            textArea.getCaret().setSelectionVisible(true);
         }
      });
   }

   /**
    * Returns the JPanel that holds the area to edit text and
    * the area showing line numbers
    *
    * @return  the JPanel that holds the area to edit text and
    * the area showing line numbers
    */
   public JPanel textPanel() {
      return textPanel;
   }

   /**
    * Returns this <code>JTextPane</code> in which text is edited
    *
    * @return  this <code>JTextPane</code>
    */
   public JTextPane textArea() {
      return textArea;
   }

   /**
    * Returns the {@code StyledDocument} associated with this
    * text area
    *
    * @return  the {@code StyledDocument} associated with this
    * text area
    */
   public StyledDocument getDoc() {
      return doc;
   }
   
   public SimpleAttributeSet getAttrSet() {
      return set;
   }

   /**
    * @return  if wordwrap is enabled
    */
   public boolean isWordWrap() {
      return isWordWrap;
   }

   /**
    * Returns the text in this document
    *
    * @return  the entire text in this <code>StyledDocument</code>
    */
   public String getDocText() {
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

   /**
    * Adds a line number
    *
    * @param lineNr  the number to be added
    */
   public void appendLineNumber(int lineNr) {
      try {
         lineDoc.insertString(lineDoc.getLength(),
               Integer.toString(lineNr) + "\n", lineSet);
      }
      catch(BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Removes all line numbers
    */
   public void removeAllLineNumbers() {
      try {
         lineDoc.remove(0, lineDoc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }

   /**
    * Adapts the withs of the area showing line numbers
    */
   public void revalidateLineAreaWidth() {
      textPanel.revalidate();
      textPanel.repaint();
   }

   /**
    * Prints this document to a printer
    */
   public void print() {
      try {
         textArea.print();
      } catch (PrinterException e) {
         FileUtils.logStack(e);
      }
   }

    /**
    * Sets a new font
    *
    * @param font  the name of the font
    * @param fontSize  the font size
    */
   public void setFont(String font, int fontSize) {
      Font fontNew = new Font(font, Font.PLAIN, (int) (fontSize * eg.Constants.SCREEN_RES_RATIO));
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew);
   }

   /**
    * Shows line numbers. Invoking this method also annules wordwrap
    */
   public void showLineNumbers() {
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      lineNumPnlScroll.setViewportView(disabledWordwrapPnl);
      textPanel.add(lineNumAreaScroll, BorderLayout.WEST);
      textPanel.add(lineNumPnlScroll, BorderLayout.CENTER);
      setScrollPos(lineNumPnlScroll);
      textArea.requestFocusInWindow();
      textPanel.repaint();
      textPanel.revalidate();
      isWordWrap = false;
   }

   /**
    * Hides line numbers. Invoking this method also annules wordwrap
    */
   public void hideLineNumbers() {
      textPanel.remove(lineNumAreaScroll);
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      noWrapPnlScroll.setViewportView(disabledWordwrapPnl);
      textPanel.add(noWrapPnlScroll, BorderLayout.CENTER);
      setScrollPos(noWrapPnlScroll);
      textArea.requestFocusInWindow();
      textPanel.repaint();
      textPanel.revalidate();
      isWordWrap = false;
   }

   /**
    * Enables wordwrap. Invoking this method also hides the area
    * that displays line numbers
    */
   public void enableWordWrap() {
      textPanel.remove(lineNumAreaScroll);
      removeCenterComponent();
      wrapPnlScoll.setViewportView(textArea);
      textPanel.add(wrapPnlScoll, BorderLayout.CENTER);
      setScrollPos(wrapPnlScoll);
      textArea.requestFocusInWindow();
      textPanel.repaint();
      textPanel.revalidate();
      isWordWrap = true;
   }

   //
   //--private methods
   //

   private void initTextArea() {
      textArea.setBorder(WHITE_BORDER);
   }

   private void initLineNrArea() {
      lineArea.setBorder(WHITE_BORDER);
      lineArea.setEditable(false);
      lineArea.setFocusable(false);
      DefaultCaret caretLine = (DefaultCaret) lineArea.getCaret();
      caretLine.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
   }

   private void intitScrollWrapArea() {
      wrapPnlScoll.getVerticalScrollBar().setUnitIncrement(15);
      wrapPnlScoll.setBorder(null);
      wrapPnlScoll.setViewportView(enabledWordwrapPnl);
   }

   private void initScrollSimpleArea() {
      noWrapPnlScroll.getVerticalScrollBar().setUnitIncrement(15);
      noWrapPnlScroll.setBorder(null);
      noWrapPnlScroll.setViewportView(disabledWordwrapPnl);
   }

   private void initScrollLineNrArea() {
      lineNumPnlScroll.getVerticalScrollBar().setUnitIncrement(15);
      lineNumPnlScroll.setBorder(null);
      lineNumPnlScroll.setViewportView(disabledWordwrapPnl);
      lineNumAreaScroll.setViewportView(lineArea);
      lineNumAreaScroll.setBorder(null);
      lineNumAreaScroll.setBorder(new MatteBorder(0, 0, 0, 1,
            Constants.BORDER_DARK_GRAY));
      //
      // link scrolling of line number area to text area
      lineNumAreaScroll.getVerticalScrollBar().setModel
            (lineNumPnlScroll.getVerticalScrollBar().getModel());
   }

   private void removeCenterComponent() {
      BorderLayout layout = (BorderLayout) textPanel.getLayout();
      JScrollPane c = (JScrollPane) layout.getLayoutComponent(BorderLayout.CENTER);
      if (c != null) {
         scrollPos = c.getVerticalScrollBar().getValue();
         textPanel.remove(c);
      }
   }

   private void setScrollPos(JScrollPane pane) {
      JScrollBar bar = pane.getVerticalScrollBar();
      bar.setValue(scrollPos);
   }

   private void setDocStyle() {
      StyleConstants.setForeground(set, Color.BLACK);
      StyleConstants.setBold(set, false);
      StyleConstants.setLineSpacing(set, 0.25f);
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), set, false);
   }

   private void setLineDocStyle() {
      StyleConstants.setForeground(lineSet, NUM_GRAY);
      StyleConstants.setAlignment(lineSet, StyleConstants.ALIGN_RIGHT);
      StyleConstants.setLineSpacing(lineSet, 0.25f);
      Element el = lineDoc.getParagraphElement(0);
      lineDoc.setParagraphAttributes(0, el.getEndOffset(), lineSet, false);
   }

   private void removeShortCuts() {
      KeyStroke ksSelAll = KeyStroke.getKeyStroke("control pressed a");
      textArea.getInputMap().put(ksSelAll, "dummy");
      KeyStroke ksCut = KeyStroke.getKeyStroke("control pressed X");
      textArea.getInputMap().put(ksCut, "dummy");
      KeyStroke ksCopy = KeyStroke.getKeyStroke("control pressed C");
      textArea.getInputMap().put(ksCopy, "dummy");
      KeyStroke ksPaste = KeyStroke.getKeyStroke("control pressed V");
      textArea.getInputMap().put(ksPaste, "dummy");
   }
}
