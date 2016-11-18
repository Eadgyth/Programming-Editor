package eg.ui;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class EditArea {

   private final static Preferences PREFS = new Preferences();
   private final JPanel scrolledArea = new JPanel(new BorderLayout());

   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineArea = new JTextPane();

   private final JPanel disabledWordwrapArea
         = new JPanel(new BorderLayout());
   private final JScrollPane scrollRowArea = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane scrollSimpleArea = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane scrollLines = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_NEVER,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

   private String font;
   private int fontSize;

   public EditArea() {
      PREFS.readPrefs();   
      scrolledArea.setBorder(new LineBorder(Constants.BORDER_GRAY, 2));
      initTextArea();
      initLineNumbersArea();
      initFont();
      initScrolledRowArea();
      initScrollSimpleArea();
      disabledWordwrapArea.add(textArea, BorderLayout.CENTER);
      boolean withLineNumbers =
            Constants.SHOW.equals(PREFS.prop.getProperty("lineNumbers"));
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
   public JTextPane textArea() {
      return textArea;
   }
   
   /**
    * @return  the JTextPane that shows line numbers
    */   
   public JTextPane lineArea() {
      return lineArea;
   }
   
   /**
    * @return  the panel that contains this text area in the scroll pane and
    * and the area that shows line numbers
    */
   public JPanel scrolledArea() {
      return scrolledArea;
   }
   
   /**
    * @return  the font of this text area
    * 
    */
   public String getFont() {
      return font;
   }
   
   /**
    * @return  the font size of this text area
    */
   public int getFontSize() {
      return fontSize;
   }
   
   /**
    * Sets the font size and stores the new size in prefs
    */
   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew );
      PREFS.storePrefs("fontSize", String.valueOf(fontSize));
   }

   /**
    * Sets the font and stores the new font in prefs
    */
   public void setFont(String font) {
      this.font = font;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew);
      PREFS.storePrefs("font", font);
   }
   
   public void showLineNumbers() {
      scrolledArea.remove(scrollSimpleArea);
      scrollRowArea.setViewportView(disabledWordwrapArea);
      scrolledArea.add(scrollLines, BorderLayout.WEST);
      scrolledArea.add(scrollRowArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
   }
   
   public void hideLineNumbers() {
      scrolledArea.remove(scrollRowArea);
      scrolledArea.remove(scrollLines);
      scrollSimpleArea.setViewportView(disabledWordwrapArea);
      scrolledArea.add(scrollSimpleArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
   }
   
   private void initFont() {
      font = PREFS.prop.getProperty("font");
      fontSize = Integer.parseInt(PREFS.prop.getProperty("fontSize"));
      Font newFont = new Font(font, Font.PLAIN, fontSize);
      textArea.setFont(newFont);
      lineArea.setFont(newFont);
   }

   private void initTextArea() {
      textArea.setBorder(new LineBorder(Color.white, 5));       
   }

   private void initLineNumbersArea() {
      lineArea.setBorder(new LineBorder(Color.WHITE, 5));
      lineArea.setEditable(false);
      lineArea.setFocusable(false);
      lineArea.setBackground(Color.WHITE);
      DefaultCaret caretLine = (DefaultCaret) lineArea.getCaret();
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
      scrollLines.setViewportView(lineArea);
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