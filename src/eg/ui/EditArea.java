package eg.ui;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.print.*;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import java.text.MessageFormat;

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
import eg.utils.FileUtils;
import eg.utils.ScreenParams;

/**
 * The editor view that defines a JPanel that contains the text area for editing
 * text and the area that displays line numbers
 */
public final class EditArea {

   private final static LineBorder WHITE_BORDER
         = new LineBorder(Color.WHITE, 5);

   private final JPanel editAreaPnl = new JPanel(new BorderLayout());
   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineNrArea = new JTextPane();

   private final JPanel disabledWordwrapPnl = new JPanel(new BorderLayout());
   private final JPanel enabledWordwrapPnl = new JPanel();

   private final JScrollPane wordwrapScoll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane noWordwrapScroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane linkedLineNrScroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JScrollPane lineNrScroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_NEVER,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

   private boolean isWordwrap;
   private int scrollPos;

   /**
    * @param isWordwrap  true to enable, false to disable wordwrap
    * @param isLineNumbers  true to show line numbering. Is effectless if
    * wordwrap is enabled
    * @param font  the name of the initial font
    * @param fontSize  the size of the initial font
    */
   public EditArea(boolean isWordwrap, boolean isLineNumbers,
         String font, int fontSize) {

      removeShortCuts();
      initEditAreaPnl();
      initTextArea();
      initLineNrArea();
      initLinkedLineNrScrolling();
      initWordwrapScrolling();
      initNoWordwrapScrolling();
      setFont(font, fontSize);
      if (isWordwrap) {
         enableWordwrap();
      }
      else {
         disableWordwrap(isLineNumbers);
      }

      textArea.addFocusListener(new FocusAdapter() {

         @Override
         public void focusLost(FocusEvent e) {
            textArea.getCaret().setSelectionVisible(true);
         }
      });
   }

   /**
    * Gets this <code>JPanel</code> which contains the area for editing
    * text and the area that shows line numbers
    *
    * @return  this edit area panel
    */
   public JPanel editAreaPnl() {
      return editAreaPnl;
   }

   /**
    * Gets this text area for editing text
    *
    * @return  this area for editing text
    */
   public JTextPane textArea() {
      return textArea;
   }
   
   /**
    * Gets this text area for showing line numbers
    *
    * @return  this area for line numbers
    */
   public JTextPane lineNrArea() {
      return lineNrArea;
   }

   /**
    * Gets this implemented method in <code>LineNrWidthAdaptable</code>
    *
    * @return  the implemented method
    */
   public LineNrWidthAdaptable lineNrWidth() {
      return (i, j) -> adaptLineNrWidth(i, j);
   }

   /**
    * Returns the boolean that indicates if wordwrap is enabled in this
    * text area
    *
    * @return  the boolean
    */
   public boolean isWordwrap() {
      return isWordwrap;
   }

   /**
    * Enables wordwrap. Invoking this method also hides the area that
    * displays line numbers
    */
   public void enableWordwrap() {
      editAreaPnl.remove(lineNrScroll);
      removeCenterComponent();
      wordwrapScoll.setViewportView(textArea);
      editAreaPnl.add(wordwrapScoll, BorderLayout.CENTER);
      setScrollPos(wordwrapScoll);
      textArea.requestFocusInWindow();
      revalidate();
      isWordwrap = true;
   }
   
   /**
    * Disables wordwrap and makes the area to show line numbers visible
    * if the specified boolean is true
    *
    * @param b  the boolean value
    */
   public void disableWordwrap(boolean b) {
      if (b) {
         showLineNumbers();
      }
      else {
         hideLineNumbers();
      }
   }
   
   /**
    * Sets the font in this area for editing text and this area that displays
    * line numbers
    *
    * @param font  the name of the font
    * @param fontSize  the font size
    */
   public void setFont(String font, int fontSize) {
      Font fontNew = new Font(font, Font.PLAIN, ScreenParams.scaledSize(fontSize));
      lineNrArea.setFont(fontNew);
      textArea.setFont(fontNew);
      revalidate();
   }

   /**
    * Prints the text content in this text area to a printer
    */
   public void print() {
      try {
         MessageFormat footerFormat = new MessageFormat("Page {0}");
         textArea.print(null, footerFormat, true, null, null, false);
      } catch (PrinterException e) {
         FileUtils.logStack(e);
      }
   }

   //
   //--private--
   //

   private void showLineNumbers() {
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      linkedLineNrScroll.setViewportView(disabledWordwrapPnl);
      editAreaPnl.add(lineNrScroll, BorderLayout.WEST);
      editAreaPnl.add(linkedLineNrScroll, BorderLayout.CENTER);
      setScrollPos(linkedLineNrScroll);
      textArea.requestFocusInWindow();
      revalidate();
      isWordwrap = false;
   }
   
   private void hideLineNumbers() {
      editAreaPnl.remove(lineNrScroll);
      removeCenterComponent();
      disabledWordwrapPnl.add(textArea, BorderLayout.CENTER);
      noWordwrapScroll.setViewportView(disabledWordwrapPnl);
      editAreaPnl.add(noWordwrapScroll, BorderLayout.CENTER);
      setScrollPos(noWordwrapScroll);
      textArea.requestFocusInWindow();
      revalidate();
      isWordwrap = false;
   }
   
   private void adaptLineNrWidth(int prevLineNr, int lineNr) {
      if ((int) Math.log10(prevLineNr) - (int) Math.log10(lineNr) != 0) {
         revalidate();
      }
   }

   private void revalidate() {
      editAreaPnl.revalidate();
      editAreaPnl.repaint();
   }
   
   private void removeCenterComponent() {
      BorderLayout layout = (BorderLayout) editAreaPnl.getLayout();
      JScrollPane c = (JScrollPane) layout.getLayoutComponent(BorderLayout.CENTER);
      if (c != null) {
         scrollPos = c.getVerticalScrollBar().getValue();
         editAreaPnl.remove(c);
      }
   }

   private void setScrollPos(JScrollPane pane) {
      JScrollBar bar = pane.getVerticalScrollBar();
      bar.setValue(scrollPos);
   }

   private void initEditAreaPnl() {
      editAreaPnl.setBorder(Constants.GRAY_BORDER);
   }

   private void initTextArea() {
      textArea.setBorder(WHITE_BORDER);
   }

   private void initLineNrArea() {
      lineNrArea.setBorder(WHITE_BORDER);
      lineNrArea.setEditable(false);
      lineNrArea.setFocusable(false);
      DefaultCaret caret = (DefaultCaret) lineNrArea.getCaret();
      caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
   }

   private void initWordwrapScrolling() {
      wordwrapScoll.getVerticalScrollBar().setUnitIncrement(15);
      wordwrapScoll.setBorder(null);
      wordwrapScoll.setViewportView(enabledWordwrapPnl);
   }

   private void initNoWordwrapScrolling() {
      noWordwrapScroll.getVerticalScrollBar().setUnitIncrement(15);
      noWordwrapScroll.setBorder(null);
      noWordwrapScroll.setViewportView(disabledWordwrapPnl);
   }

   private void initLinkedLineNrScrolling() {
      linkedLineNrScroll.getVerticalScrollBar().setUnitIncrement(15);
      linkedLineNrScroll.setBorder(null);
      linkedLineNrScroll.setViewportView(disabledWordwrapPnl);
      lineNrScroll.setViewportView(lineNrArea);
      lineNrScroll.setBorder(null);
      lineNrScroll.setBorder(new MatteBorder(0, 0, 0, 1, Constants.GRAY));
      lineNrScroll.getVerticalScrollBar().setModel
            (linkedLineNrScroll.getVerticalScrollBar().getModel());
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
