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

import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;

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

   private final static LineBorder WHITE_BORDER
         = new LineBorder(Color.WHITE, 5);

   private final JPanel editAreaPanel = new JPanel(new BorderLayout());
   private final JTextPane textArea   = new JTextPane();   
   private final JTextPane lineArea   = new JTextPane();

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

   private boolean isWordwrap;
   private int scrollPos;

   /**
    * @param isWordwrap  true to enable wordwrap
    * @param isLineNumbers  true to show line numbering. Is effectless if
    * wordwrap is enabled
    * @param font  the name of the initial font
    * @param fontSize  the size of the initial font
    */
   public EditArea(boolean isWordwrap, boolean isLineNumbers,
         String font, int fontSize) {

      removeShortCuts();
      editAreaPanel.setBorder(Constants.DARK_BORDER);
      initTextArea();
      initLineNrArea();
      setFont(font, fontSize);
      initScrollLineNrArea();
      initScrollSimpleArea();
      intitScrollWrapArea();
      if (isWordwrap) {
         enableWordwrap();
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
    * Returns this <code>JTextPane</code> in which text is edited
    *
    * @return  this <code>JTextPane</code>
    */
   public JTextPane textArea() {
      return textArea;
   }
   
   /**
    * Returns the {@code StyledDocument} associated with this
    * area that shows line numbers
    *
    * @return  the {@code StyledDocument} associated with this
    * area that shows line numbers
    */
   public StyledDocument lineDoc() {
      return lineArea.getStyledDocument();
   }

   /**
    * Returns the JPanel that holds the area to edit text and
    * the area showing line numbers
    *
    * @return  the JPanel that holds the area to edit text and
    * the area showing line numbers
    */
   public JPanel editAreaPanel() {
      return editAreaPanel;
   }

   /**
    * Returns if wordwrap is enabled
    *
    * @return  if wordwrap is enabled
    */
   public boolean isWordwrap() {
      return isWordwrap;
   }

    /**
    * Sets a new font
    *
    * @param font  the name of the font
    * @param fontSize  the font size
    */
   public void setFont(String font, int fontSize) {
      Font fontNew = new Font(font, Font.PLAIN,
            (int) (fontSize * eg.Constants.SCREEN_RES_RATIO));
      lineArea.setFont(fontNew);
      textArea.setFont(fontNew);
      revalidateEditAreaPanel();
   }

   /**
    * Shows line numbers. Invoking this method also annules wordwrap
    */
   public void showLineNumbers() {
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      lineNumPnlScroll.setViewportView(disabledWordwrapPnl);
      editAreaPanel.add(lineNumAreaScroll, BorderLayout.WEST);
      editAreaPanel.add(lineNumPnlScroll, BorderLayout.CENTER);
      setScrollPos(lineNumPnlScroll);
      textArea.requestFocusInWindow();
      revalidateEditAreaPanel();
      isWordwrap = false;
   }

   /**
    * Hides line numbers. Invoking this method also annules wordwrap
    */
   public void hideLineNumbers() {
      editAreaPanel.remove(lineNumAreaScroll);
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      noWrapPnlScroll.setViewportView(disabledWordwrapPnl);
      editAreaPanel.add(noWrapPnlScroll, BorderLayout.CENTER);
      setScrollPos(noWrapPnlScroll);
      textArea.requestFocusInWindow();
      revalidateEditAreaPanel();
      isWordwrap = false;
   }

   /**
    * Enables wordwrap. Invoking this method also hides the area
    * that displays line numbers
    */
   public void enableWordwrap() {
      editAreaPanel.remove(lineNumAreaScroll);
      removeCenterComponent();
      wrapPnlScoll.setViewportView(textArea);
      editAreaPanel.add(wrapPnlScoll, BorderLayout.CENTER);
      setScrollPos(wrapPnlScoll);
      textArea.requestFocusInWindow();
      revalidateEditAreaPanel();
      isWordwrap = true;
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

   //
   //--private methods--//
   //
   
   private void revalidateEditAreaPanel() {
      editAreaPanel.revalidate();
      editAreaPanel.repaint();
   }

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
      BorderLayout layout = (BorderLayout) editAreaPanel.getLayout();
      JScrollPane c = (JScrollPane) layout.getLayoutComponent(BorderLayout.CENTER);
      if (c != null) {
         scrollPos = c.getVerticalScrollBar().getValue();
         editAreaPanel.remove(c);
      }
   }

   private void setScrollPos(JScrollPane pane) {
      JScrollBar bar = pane.getVerticalScrollBar();
      bar.setValue(scrollPos);
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
