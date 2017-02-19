package eg.ui;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;

import java.awt.print.*;

import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

//--Eadgyth--//
import eg.Constants;
import eg.utils.FileUtils;

/**
 * Defines the editor view that contains the text area to edit text and the 
 * area that displays line numbers.
 * <p>
 * The text area ({@code JTextPane}) is associated with a {@code StyledDocument}.
 */
public final class EditArea {

   private final static Color NUM_GRAY = new Color(100, 100, 100);
   private final static LineBorder WHITE_BORDER
         = new LineBorder(Color.WHITE, 5);

   private final JPanel textPanel = new JPanel(new BorderLayout());

   private final JTextPane textArea = new JTextPane();
   private final SimpleAttributeSet normalSet = new SimpleAttributeSet();
   private final StyledDocument doc;

   private final JTextPane lineArea = new JTextPane();
   private final SimpleAttributeSet lineSet = new SimpleAttributeSet();
   private final StyledDocument lineDoc;

   private final JPanel disabledWordwrapPnl
         = new JPanel(new BorderLayout());
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

   private String font;
   private int fontSize;
   private boolean isWordWrap;
   private int scrollPos;

   /**
    * @param isWordWrap  true to enable wordwrap
    * @param isLineNumbers  true to show line numbering. Is effectless if
    * {@code isWordWrap} is true
    * @param font  the String name for the initial font
    * @param fontSize  the size of the initial font
    */
   public EditArea(boolean isWordWrap, boolean isLineNumbers,
         String font, int fontSize) {
      
      this.doc = textArea.getStyledDocument();
      setDocStyle();
      this.lineDoc = lineArea.getStyledDocument();
      setLineDocStyle();
            
      this.font = font;
      this.fontSize = fontSize;
      textPanel.setBorder(Constants.BORDER);
      initTextArea();
      initLineNrArea();
      initFont(font, fontSize);
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
   }
   
   /**
    * Returns the JPanel that holds the area to edit text and, if
    * selected, the area showing line numbers
    * @return  the JPanel that holds the area to edit text and, if
    * selected the area showing line numbers
    */
   public JPanel textPanel() {
      return textPanel;
   }
   
   /**
    * Returns this text area in which text is edited
    * @return  this text area
    */
   public JTextPane textArea() {
      return textArea;
   }
   
   /**
    * Associates this text area with a new {@code DefaultStyledDocument}
    * with no attributes
    */
   public void setBlankDoc() {
      Document blank = new DefaultStyledDocument();
      textArea.setDocument(blank);
   }
   
   /**
    * Associates this text area with this {@code StyledDocument}
    */
   public void setDoc() {
      textArea.setDocument(doc);
   }
   
   /**
    * Returns the text in this {@code StyledDocument}
    * @return  the text in this {@code StyledDocument}
    */
   public String getDocText() {
      String in = null;
      try {
         in = doc.getText(0, doc.getLength());
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
      return in;
   }
   
   /**
    * Returns the {@code StyledDocument} associated with this
    * text area
    * @return  the {@code StyledDocument} associated with this
    * text area
    */
   public StyledDocument getDoc() {
      return doc;
   }
   
   /**
    * Returns this {@code SimpleAttributeSet} which represents the
    * "normal" text appearance in the text area
    * @return  this {@code SimpleAttributeSet} for this text area
    */
   public SimpleAttributeSet getNormalSet() {
      return normalSet;
   }
   
   /**
    * If wordwrap is enabled
    * @return  if wordwrap is enabled
    */
   public boolean isWordWrap() {
      return isWordWrap;
   }
   
   /**
    * (Re-)colors the entire text in the default color
    */
   public void allTextToBlack() {
      doc.setCharacterAttributes(0, getDocText().length(), normalSet, false);
   }
   
   /**
    * (Re-)colors the text starting at the specified position and
    * spanning the specified length in the default color
    * @param length  the length of text that is colored in the
    * default color
    * @param pos  the position where the text to color starts
    */
   public void textToBlack(int length, int pos) {
      doc.setCharacterAttributes(pos, length, normalSet, false);
   }
   
   /**
    * Inserts text in this document
    * @param pos  the position where new text is inserted
    * @param toInsert  the String that contains the text to insert
    */
   public void insertStr(int pos, String toInsert) {
      try {
         doc.insertString(pos, toInsert, normalSet);
      }
      catch (BadLocationException e) {
         FileUtils.logStack(e);
      }
   }
   
   /**
    * Removes text from this document
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
    * @param lineNr  the number to be added
    */
   public void appendRowNumber(int lineNr) {
      try {
         lineDoc.insertString(lineDoc.getLength(),
               Integer.toString(lineNr) + "\n", lineSet);
      }
      catch(BadLocationException e) {
         FileUtils.logStack(e);
      }
      //revalidateLineAreaWidth(lineNr);
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
    * Adapts the withs of the area showing line numbers as the number
    * of digits of the highest line number changes
    * @param nLines  the number of lines
    */
   public void revalidateLineAreaWidth(int nLines) {
      textPanel.revalidate();
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
    * Sets a new font size
    * @param fontSize  the font size
    */
   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew);
   }

   /**
    * Sets a new font
    * @param font  the String name for the new font
    */
   public void setFont(String font) {
      this.font = font;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew);
   }      
   
   /**
    * Shows line numbers.
    * <p>
    * Invoking this method also annules wordwrap
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
    * Hides line numbers.
    * <p>
    * Invoking this method also annules wordwrap
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
    * Enables wordwrap.
    * <p>
    * Invoking this method also hides the area that displays
    * line numbers
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

   private void initFont(String font, int fontSize) {
      Font newFont = new Font(font, Font.PLAIN, fontSize);
      textArea.setFont(newFont);
      lineArea.setFont(newFont);
   }

   private void initTextArea() {
      textArea.setBorder(WHITE_BORDER);       
   }

   private void initLineNrArea() {
      lineArea.setBorder(WHITE_BORDER);
      lineArea.setEditable(false);
      lineArea.setFocusable(false);
      lineArea.setBackground(Color.WHITE);
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
      lineNumAreaScroll.setBorder(new MatteBorder(0, 0, 0, 1, Constants.BORDER_GRAY));
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
      StyleConstants.setForeground(normalSet, Color.BLACK);
      StyleConstants.setLineSpacing(normalSet, 0.2f);
      StyleConstants.setBold(normalSet, false);
      Element el = doc.getParagraphElement(0);
      doc.setParagraphAttributes(0, el.getEndOffset(), normalSet, false);
   }  
   
   private void setLineDocStyle() {
      StyleConstants.setForeground(lineSet, NUM_GRAY);
      StyleConstants.setAlignment(lineSet, StyleConstants.ALIGN_RIGHT);     
      StyleConstants.setLineSpacing(lineSet, 0.2f);
      Element el = lineDoc.getParagraphElement(0);
      lineDoc.setParagraphAttributes(0, el.getEndOffset(), lineSet, false);
   }
}
