package eg.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import javax.swing.border.LineBorder;


//--Eadgyth--/
import eg.BackgroundTheme;
import eg.FunctionalAction;
import eg.utils.ScreenParams;

/**
 * Defines the panel which contains the text area that functions as the
 * console and a toolbar for actions to run commands.
 */
public class ConsolePanel {

   private final JPanel content = UIComponents.grayBorderedPanel();
   private final JButton closeBt = new JButton();
   
   private BackgroundTheme theme;
   
   /**
    * Creates a <code>ConsolePanel</code>
    *
    * @param theme  the BackgroundTheme
    */
   public ConsolePanel(BackgroundTheme theme) {
      this.theme = theme;
   }
   
   /**
    * Initializes the console content
    *
    * @param area  the text area
    * @param bts  the array of buttons added to the toolbar
    * @param tooltips  the tooltips
    */
   public void initContent(JTextArea area, JButton[] bts, String[] tooltips) {
      content.setLayout(new BorderLayout());
      JToolBar toolbar = UIComponents.toolbar(bts, tooltips, closeBt);
      content.add(toolbar, BorderLayout.NORTH);
      JScrollPane scroll = UIComponents.scrollPane();
      scroll.setViewportView(area);
      content.add(scroll, BorderLayout.CENTER);

      area.setFont(ScreenParams.SANSSERIF_PLAIN_8);
      area.setBackground(theme.background());
      area.setForeground(theme.normalText());
      area.setSelectionColor(theme.selectionBackground());
      area.setSelectedTextColor(theme.normalText());
      area.setBorder(new LineBorder(theme.background(), 5));
      area.setCaretColor(theme.normalText());
   }

   /**
    * Gets this JPanel which contains the text area and the toolbar
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Sets the action for closing the console panel to this
    * closing button
    *
    * @param act  the action
    */
   public void setClosingAct(FunctionalAction act) {
      closeBt.setAction(act);
   }
}
