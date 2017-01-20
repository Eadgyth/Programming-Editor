package eg.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
 * The scolled text area which consist in the text area to edit
 * text, the area that displays line numbers and the font.
 * <p>
 * This class can change its font and the font size, can show or hide
 * the area that shows line numbers and can enable/disable wordwrap.
 * <p>
 * The usage of the copy, cut, paste keys is disabled.
 */
public class EditArea {

   private final static Preferences PREFS = new Preferences();
   private static Constants c;

   private final JPanel scrolledArea = new JPanel(new BorderLayout());
   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineArea = new JTextPane();

   private final JPanel disabledWordwrapArea
         = new JPanel(new BorderLayout());
   private final JPanel enabledWordwrapArea = new JPanel();

   private final JScrollPane scrollWrapArea = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
   private boolean isWordWrap;

   public EditArea() {
      PREFS.readPrefs(); 
      scrolledArea.setBorder(c.LOW_ETCHED);
      initTextArea();
      initLineNumbersArea();
      initFont();
      initScrolledRowArea();
      initScrollSimpleArea();
      intitScrollWrapArea();
      boolean isLineNumbers =
            c.SHOW.equals(PREFS.getProperty(c.LINE_NUM_PREFS));
      if ("enabled".equals(PREFS.getProperty("wordWrap"))) {
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
      removeKeyActions();
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
    * @return  the JPanel that holds the area to edit text and 
    * the area showing line numbers in a scroll pane
    */
   public JPanel scrolledArea() {
      return scrolledArea;
   }
   
   /**
    * If wordwrap is enabled
    * @return  if wordwrap is enabled
    */
   public boolean isWordWrap() {
      return isWordWrap;
   }
   
   /**
    * Sets the font size and stores the new size in prefs
    * @param fontSize  the font size
    */
   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew );
      PREFS.storePrefs(c.FONT_SIZE_PREFS, String.valueOf(fontSize));
   }

   /**
    * Sets the font and stores the new font in prefs
    * @param font  the String name of the new font
    */
   public void setFont(String font) {
      this.font = font;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew);
      PREFS.storePrefs(c.FONT_PREFS, font);
   }
   
   /**
    * Shows the area that displays line numbers.
    * <p>
    * Invoking this mathod also annules wordwrap
    */
   public void showLineNumbers() {
      removeCenterComponent();
      disabledWordwrapArea.add(textArea, BorderLayout.CENTER);
      scrollRowArea.setViewportView(disabledWordwrapArea);
      scrolledArea.add(scrollLines, BorderLayout.WEST);
      scrolledArea.add(scrollRowArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
      isWordWrap = false;
   }
   
   /**
    * Hides the area that displays line numbers.
    * <p>
    * Invoking this mathod also annules wordwrap
    */
   public void hideLineNumbers() {
      scrolledArea.remove(scrollLines);
      removeCenterComponent();
      disabledWordwrapArea.add(textArea, BorderLayout.CENTER);
      scrollSimpleArea.setViewportView(disabledWordwrapArea);
      scrolledArea.add(scrollSimpleArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
      isWordWrap = false;
   }
   
   /**
    * Enables wordwrap.
    * <p>
    * Invoking this method also hides the area that displays
    * line numbers
    */
   public void enableWordWrap() {
      scrolledArea.remove(scrollLines);
      removeCenterComponent();
      scrollWrapArea.setViewportView(textArea);
      scrolledArea.add(scrollWrapArea, BorderLayout.CENTER);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
      isWordWrap = true;
   }
   
   //
   //--private methods
   //

   private void initFont() {
      font = PREFS.getProperty("font");
      fontSize = Integer.parseInt(PREFS.getProperty("fontSize"));
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
   
   private void intitScrollWrapArea() {
      scrollWrapArea.getVerticalScrollBar().setUnitIncrement(15);
      scrollWrapArea.setBorder(null);
      scrollWrapArea.setViewportView(enabledWordwrapArea);
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
      scrollLines.setBorder(new MatteBorder(0, 0, 0, 1, c.BORDER_GRAY));
      //
      // link scrolling of row number area to text area
      scrollLines.getVerticalScrollBar().setModel
            (scrollRowArea.getVerticalScrollBar().getModel());
   }
   
   private void removeCenterComponent() {
      BorderLayout layout = (BorderLayout) scrolledArea.getLayout();
      Component c = layout.getLayoutComponent(BorderLayout.CENTER);
      if (c != null) {
         scrolledArea.remove(c);
      }
   }

   private void removeKeyActions() {
      KeyStroke ksCut = KeyStroke.getKeyStroke("control pressed X");
      textArea.getInputMap().put(ksCut, "dummy");
      KeyStroke ksCopy = KeyStroke.getKeyStroke("control pressed C");
      textArea.getInputMap().put(ksCopy, "dummy");
      KeyStroke ksPaste = KeyStroke.getKeyStroke("control pressed V");
      textArea.getInputMap().put(ksPaste, "dummy");
   }
}
