package eg.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import javax.swing.text.DefaultCaret;

import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

//--Eadgyth--//
import eg.Constants;

/**
 * The scolled text area which consist in the text area to edit text, the
 * area that displays line numbers and the font.
 * <p>
 * The usage of the select all, copy, cut, paste key combinations is
 * disabled.
 */
public final class EditArea {

   private final static LineBorder WHITE_BORDER
         = new LineBorder(Color.WHITE, 5); 

   private final JPanel scrolledArea = new JPanel(new BorderLayout());
   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineNumArea = new JTextPane();
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
            
      this.font = font;
      this.fontSize = fontSize;
      scrolledArea.setBorder(Constants.BORDER);
      initTextArea();
      initLineNumbersArea();
      initFont(font, fontSize);
      initScrolledRowArea();
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
      return lineNumArea;
   }
   
   /**
    * Returns the JPanel that holds the area to edit text and, if
    * enabled, the area showing line numbers
    * @return  the JPanel that holds the area to edit text and 
    * the area showing line numbers
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
    * Sets a new font size
    * @param fontSize  the font size
    */
   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineNumArea.setFont(fontNew);
      textArea.setFont(fontNew);
   }

   /**
    * Sets a new font
    * @param font  the String name for the new font
    */
   public void setFont(String font) {
      this.font = font;
      Font fontNew = new Font(font, Font.PLAIN, fontSize);
      lineNumArea.setFont(fontNew);
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
      scrolledArea.add(lineNumAreaScroll, BorderLayout.WEST);
      scrolledArea.add(lineNumPnlScroll, BorderLayout.CENTER);
      setScrollPos(lineNumPnlScroll);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
      isWordWrap = false;
   }
   
   /**
    * Hides line numbers.
    * <p>
    * Invoking this method also annules wordwrap
    */
   public void hideLineNumbers() {
      scrolledArea.remove(lineNumAreaScroll);
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      noWrapPnlScroll.setViewportView(disabledWordwrapPnl);
      scrolledArea.add(noWrapPnlScroll, BorderLayout.CENTER);
      setScrollPos(noWrapPnlScroll);
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
      scrolledArea.remove(lineNumAreaScroll);
      removeCenterComponent();
      wrapPnlScoll.setViewportView(textArea);
      scrolledArea.add(wrapPnlScoll, BorderLayout.CENTER);
      setScrollPos(wrapPnlScoll);
      textArea.requestFocusInWindow();
      scrolledArea.repaint();
      scrolledArea.revalidate();
      isWordWrap = true;
   }
   
   //
   //--private methods
   //

   private void initFont(String font, int fontSize) {
      Font newFont = new Font(font, Font.PLAIN, fontSize);
      textArea.setFont(newFont);
      lineNumArea.setFont(newFont);
   }

   private void initTextArea() {
      textArea.setBorder(WHITE_BORDER);       
   }

   private void initLineNumbersArea() {
      lineNumArea.setBorder(WHITE_BORDER);
      lineNumArea.setEditable(false);
      lineNumArea.setFocusable(false);
      lineNumArea.setBackground(Color.WHITE);
      DefaultCaret caretLine = (DefaultCaret) lineNumArea.getCaret();
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

   private void initScrolledRowArea() {
      lineNumPnlScroll.getVerticalScrollBar().setUnitIncrement(15);
      lineNumPnlScroll.setBorder(null);
      lineNumPnlScroll.setViewportView(disabledWordwrapPnl);
      lineNumAreaScroll.setViewportView(lineNumArea);
      lineNumAreaScroll.setBorder(new MatteBorder(0, 0, 0, 1, Constants.BORDER_GRAY));
      //
      // link scrolling of line number area to text area
      lineNumAreaScroll.getVerticalScrollBar().setModel
            (lineNumPnlScroll.getVerticalScrollBar().getModel());
   }
   
   private void removeCenterComponent() {
      BorderLayout layout = (BorderLayout) scrolledArea.getLayout();
      JScrollPane c = (JScrollPane) layout.getLayoutComponent(BorderLayout.CENTER);
      if (c != null) {
         scrollPos = c.getVerticalScrollBar().getValue();
         scrolledArea.remove(c);
      }
   }
   
   private void setScrollPos(JScrollPane pane) {
      JScrollBar bar = pane.getVerticalScrollBar();
      bar.setValue(scrollPos);
   }

   private void removeKeyActions() {
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
