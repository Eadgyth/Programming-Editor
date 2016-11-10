package eg.document;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.EventQueue;
import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;

import javax.swing.text.DefaultCaret;

import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

//--Eadgyth--//
import eg.Preferences;
import eg.Constants;

/**
 * The scolled text area which consist in the text area,
 * the line numbers (which can be shown or hidden) and the font
 * (which can be changed)
 */
class EditArea {

   private final static Preferences prefs = new Preferences();

   private final JPanel scrolledArea = new JPanel(new BorderLayout());

   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineNumbers = new JTextPane();
   private final JPanel disabledWordwrapArea
         = new JPanel(new BorderLayout());
   private final JScrollPane scrollRowArea = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane scrollSimpleArea = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private JScrollPane scrollLines;

   private String font;
   private int fontSize;

   /**
    * @param withLineNumbers  true to display line numbers in this
    * scrolled text area line numbers
    */
   EditArea(boolean withLineNumbers) {
      prefs.readPrefs();
      
      scrolledArea.setBorder(new LineBorder(Constants.BORDER_GRAY, 2));
      initTextArea();
      initLineNumbersArea();
      initFont();
      initScrolledRowArea();
      initScrollSimpleArea();
      disabledWordwrapArea.add(textArea, BorderLayout.CENTER);
      if (withLineNumbers) {
         showLineNumbers();
      }
      else {
         hideLineNumbers();
      }
      removeCopyPasteKeys();
   }
   
   /**
    * @return  the JTextPane in which text is edited
    */   
   JTextPane textArea() {
      return textArea;
   }
   
   /**
    * @return  the JTextPane that shows line numbers
    */   
   JTextPane lineNumbers() {
      return lineNumbers;
   }
   
   JPanel scrolledArea() {
      return scrolledArea;
   }
   
    /**
    * @return  the font of this text area
    * 
    */
   String getFont() {
      return font;
   }
   
   /**
    * @return  the font size of this text area
    */
   int getFontSize() {
      return fontSize;
   }
   
   /**
    * Sets the font size and stores the new size in prefs
    */
   void setFontSize(int fontSize) {
      this.fontSize = fontSize;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineNumbers.setFont(fontNew);
      textArea.setFont(fontNew );
      prefs.storePrefs("fontSize", String.valueOf(fontSize));
   }

   /**
    * Sets the font and stores the new font in prefs
    */
   void setFont(String font) {
      this.font = font;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineNumbers.setFont(fontNew);
      textArea.setFont(fontNew);
      prefs.storePrefs("font", font);
   }
   
   void showLineNumbers() {
      scrolledArea.remove(scrollSimpleArea);
      scrollRowArea.setViewportView(disabledWordwrapArea);
      scrolledArea.add(scrollLines, BorderLayout.WEST);
      scrolledArea.add(scrollRowArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
   }
   
   void hideLineNumbers() {
      scrolledArea.remove(scrollRowArea);
      scrolledArea.remove(scrollLines);
      scrollSimpleArea.setViewportView(disabledWordwrapArea);
      scrolledArea.add(scrollSimpleArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
   }
   
   private void initFont() {
      font = prefs.prop.getProperty("font");
      fontSize = Integer.parseInt(prefs.prop.getProperty("fontSize"));
      Font newFont = new Font(font, Font.PLAIN, fontSize);
      textArea.setFont(newFont);
      lineNumbers.setFont(newFont);
   }

   private void initTextArea() {
      textArea.setBorder(new LineBorder(Color.white, 5));       
   }

   private void initLineNumbersArea() {
      lineNumbers.setBorder(new LineBorder(Color.WHITE, 5));
      lineNumbers.setEditable(false);
      lineNumbers.setFocusable(false);
      lineNumbers.setBackground(Color.WHITE);
      DefaultCaret caretLine = (DefaultCaret) lineNumbers.getCaret();
      caretLine.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
   }
   
   private void initScrollSimpleArea() {
      scrollSimpleArea.getVerticalScrollBar().setUnitIncrement(15);
      scrollSimpleArea.setBorder(null);
      scrollSimpleArea.setViewportView(disabledWordwrapArea);
   }

   private void initScrolledRowArea() {
      scrollRowArea.getVerticalScrollBar().setUnitIncrement(15);
      scrollRowArea.setBorder(null);
      scrollRowArea.setViewportView(disabledWordwrapArea);

      scrollLines = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollLines.setViewportView(lineNumbers);
      scrollLines.setBorder(new MatteBorder(0, 0, 0, 1, Constants.BORDER_GRAY));

      /* link line numbers pane to input pane scrolling */
      scrollLines.getVerticalScrollBar().setModel
            (scrollRowArea.getVerticalScrollBar().getModel());
   }

   /* Remove keys by binding the strokes to an invalid action name */
   private void removeCopyPasteKeys() {
      KeyStroke ksCopy = KeyStroke.getKeyStroke("control pressed C");
      textArea.getInputMap().put(ksCopy, "dummy");
      KeyStroke ksPaste = KeyStroke.getKeyStroke("control pressed V");
      textArea.getInputMap().put(ksPaste, "dummy");
   }
}