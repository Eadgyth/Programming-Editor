package eg.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

//--Eadgyth---/
import eg.BackgroundTheme;
import eg.utils.ScreenParams;

/**
 * Defines the status bar
 */
public class StatusBar {

   private final JPanel content = new JPanel();
   private final JLabel projectLb = new JLabel();
   private final JLabel languageLb = new JLabel();
   private final JLabel cursorPosLb = new JLabel();
   private final JLabel wordwrapLb = new JLabel();
   
   private BackgroundTheme theme;

   /**
    * Creates a <code>StatusBar</code>
    *
    * @param theme  the BackgroundTheme
    */
   public StatusBar(BackgroundTheme theme) {
      this.theme = theme;
      init();
      setBackground();
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

   //--private--/

   private void init() {
      int lbHeight = 15;
      Dimension width5   = ScreenParams.scaledDimension(5, lbHeight);
      Dimension width20  = ScreenParams.scaledDimension(20, lbHeight);
      Dimension width100 = ScreenParams.scaledDimension(100, lbHeight);
      Dimension width150 = ScreenParams.scaledDimension(150, lbHeight);
      Dimension width200 = ScreenParams.scaledDimension(200, lbHeight);
      JLabel[] lbArr = { languageLb, projectLb, cursorPosLb, wordwrapLb };
      setLbFont(lbArr);
      setLbWidth(languageLb, width100);
      setLbWidth(projectLb, width200);
      setLbWidth(cursorPosLb, width150);
      content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
      content.setBorder(UIComponents.grayMatteBorder(0, 1, 1, 1));
      content.add(Box.createRigidArea(width5));
      content.add(languageLb);
      content.add(Box.createRigidArea(width20));
      content.add(projectLb);
      content.add(Box.createRigidArea(width20));
      content.add(wordwrapLb);
      content.add(Box.createRigidArea(width5));
      content.add(cursorPosLb);
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
}
