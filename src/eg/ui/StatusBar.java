package eg.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

//--Eadgyth---/
import eg.BackgroundTheme;
import eg.utils.ScreenParams;

/**
 * Defines the status bar
 */
public class StatusBar {

   private static final Icon ERROR_ICON = IconFiles.ERROR_ICON;

   private final JPanel content = new JPanel();
   private final JLabel projectLb = new JLabel();
   private final JLabel languageLb = new JLabel();
   private final JLabel cursorPosLb = new JLabel();
   private final JLabel wordwrapLb = new JLabel();
   private final JLabel encodingLb = new JLabel();

   private final BackgroundTheme theme;

   /**
    * Creates a <code>StatusBar</code>
    *
    * @param theme  the BackgroundTheme
    */
   public StatusBar(BackgroundTheme theme) {
      this.theme = theme;
      init();
      setBackground();
      encodingLb.addMouseListener(encodingLbListener);
   }

   /**
    * Gets this <code>JPanel</code> which represents the status bar
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Displays the language
    *
    * @param lang  the language
    */
   public void displayLanguage(String lang) {
      languageLb.setText("Language: " + lang);
   }

   /**
    * Displays the cursor position
    *
    * @param lineNr  the line number
    * @param colNr  the column number
    */
   public void displayCursorPosition(int lineNr, int colNr) {
      cursorPosLb.setText("Line " + lineNr + "  Col " + colNr);
   }

   /**
    * Displays if wordwrap is switched on
    *
    * @param b  true if wordwrap is switched on, false otherwise
    */
   public void displayWordwrapState(boolean b) {
      if (b) {
         cursorPosLb.setForeground(Color.GRAY);
         wordwrapLb.setText("Word-wrap ");
      }
      else {
         if (theme == null) {
            cursorPosLb.setForeground(Color.BLACK);
         }
         else {
            cursorPosLb.setForeground(theme.normalText());
         }
         wordwrapLb.setText("");
      }
   }

   /**
    * Displays the project name
    *
    * @param projName  the name
    */
   public void displayProjectName(String projName) {
      projectLb.setText("Current project: " + projName);
   }

   /**
    * Displays the charset with optional info.
    *
    * If <code>charsetInfo</code> contains "error, an error
    * icon and a tooltip text is added.
    *
    * @param charsetInfo  the display for the necoding
    */
   public void displayCharset(String charsetInfo) {
      if (charsetInfo.contains("error")) {
         encodingLb.setIcon(ERROR_ICON);
         encodingLb.setToolTipText(UTF8_WARN);
      }
      else {
         encodingLb.setIcon(null);
         encodingLb.setToolTipText(null);
      }
      encodingLb.setText(charsetInfo);
      encodingLb.revalidate();
      encodingLb.repaint();
   }

   //--private--/

   private void init() {
      int lbHeight = 15;
      Dimension width5   = ScreenParams.scaledDimension(5, lbHeight);
      Dimension width100 = ScreenParams.scaledDimension(100, lbHeight);
      Dimension width200 = ScreenParams.scaledDimension(200, lbHeight);
      JLabel[] lbArr = { languageLb, projectLb, cursorPosLb, wordwrapLb, encodingLb };
      setLbFont(lbArr);
      setLbWidth(languageLb, width100);
      setLbWidth(projectLb, width200);
      setLbWidth(cursorPosLb, width100);
      content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
      content.setBorder(UIComponents.grayMatteBorder(0, 1, 1, 1));
      content.add(Box.createRigidArea(width5));
      content.add(languageLb);
      content.add(projectLb);
      content.add(wordwrapLb);
      content.add(cursorPosLb);
      content.add(encodingLb);
      projectLb.setText("Current project: none");
   }

   private void setBackground() {
      if (!theme.isDark()) {
         return;
      }
      content.setBackground(theme.lightBackground());
      projectLb.setForeground(theme.normalText());
      languageLb.setForeground(theme.normalText());
      wordwrapLb.setForeground(theme.normalText());
      encodingLb.setForeground(theme.normalText());
   }

   private void setLbFont(JLabel[] lb) {
      Font f = lb[0].getFont();
      for (JLabel l : lb) {
         l.setFont(ScreenParams.scaledFontToPlain(f, 8));
      }
   }

   private void setLbWidth(JLabel lb, Dimension dim) {
      lb.setPreferredSize(dim);
      lb.setMinimumSize(dim);
      lb.setMaximumSize(dim);
   }

   private final MouseListener encodingLbListener = new MouseAdapter() {

      private final int defaultInitial = ToolTipManager
            .sharedInstance().getInitialDelay();

      private final int defaultDismiss = ToolTipManager
            .sharedInstance().getDismissDelay();

      @Override
      public void mouseEntered(MouseEvent e) {
         ToolTipManager.sharedInstance().setInitialDelay(0);
         ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
         ToolTipManager.sharedInstance().mouseMoved(e);
      }

      @Override
         public void mouseExited(MouseEvent e) {
         ToolTipManager.sharedInstance().setInitialDelay(defaultInitial);
         ToolTipManager.sharedInstance().setDismissDelay(defaultDismiss);
      }
   };

   private static final String UTF8_WARN =
      "<html>" +
      "<b>File is not UTF-8 \u2013 the file's encoding could not be determined.</b><br>" +
      "<ul>" +
      "<li>Invalid UTF-8 sequences are displayed as replacements (e.g. appearing like \uFFFD).</li>" +
      "<li>To convert to UTF-8, please close the file and try to open with a fallback encoding<br>" +
      "selected in the File menu first.</li>" +
      "</ul>" +
      "</html>";
}
