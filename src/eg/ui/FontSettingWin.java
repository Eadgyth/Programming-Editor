package eg.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;

//--Eadgyth--//
import eg.Constants;
import eg.Preferences;

/**
 * A frame that contains combo boxes to change the font and font size
 */
public class FontSettingWin {

   private final static String[] FONTS = {
      "Arial", "Consolas", "Courier", "Courier New", "Lucida Console",
      "Lucida Sans Typewriter Regular", "Verdana"
   };

   private final static String[] FONT_SIZES = {
      "10", "11", "12", "13", "14", "15", "16"
   };

   private final JFrame frame = new JFrame("Font");
   private final JComboBox<String> selectFont = new JComboBox<>(FONTS);
   private final JComboBox<String> selectSize = new JComboBox<>(FONT_SIZES);
   private final JButton okBt = new JButton("OK");

   private final Preferences prefs = new Preferences();

   public FontSettingWin() {
      prefs.readPrefs();
      initFrame();
   }

   /**
    * Makes this frame visible/unvisible
    *
    * @param isVisible  true/false to make this frame visible/unvisible
    */
   public void makeVisible(boolean isVisible) {
      frame.setVisible(isVisible);
   }
   
   /**
    * Adds an action handler to this ok button
    *
    * @param al  the {@code ActionListener}
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Returns the font selection in the corresponding combobox and
    * and stores the selection to the preferences file
    *
    * @return the selected font
    */
   public String fontComboBxRes() {
      String font = FONTS[selectFont.getSelectedIndex()];
      prefs.storePrefs("font", font);
      return font;
   }

   /**
    * Returns the font size selection in the corresponding combobox and
    * and stores the selection to the preferences file
    *
    * @return the selected font
    */
   public int sizeComboBxRes() {
      String size = FONT_SIZES[selectSize.getSelectedIndex()];
      prefs.storePrefs("fontSize", size);
      return Integer.parseInt(size);
   }

   //
   //--private methods
   //

   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combinedPnl());
      frame.setSize(400, 120);
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
   }

   private JPanel combinedPnl() {
      JPanel twoComboBx = new JPanel();
      twoComboBx.setLayout(new BoxLayout(twoComboBx, BoxLayout.LINE_AXIS));
      twoComboBx.add(fontPnl());
      twoComboBx.add(Box.createHorizontalGlue());
      twoComboBx.add(sizePnl());
      twoComboBx.setPreferredSize(new Dimension(380, 20));

      JPanel combined = new JPanel(new FlowLayout());
      combined.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      combined.add(twoComboBx);
      combined.add(buttonPnl());
      return combined;
   }

   private JPanel fontPnl() {
      selectFont.setSelectedItem(prefs.getProperty("font"));
      return comboBoxPnl(selectFont, "Font:   ");     
   }

   private JPanel sizePnl() {
      selectSize.setSelectedItem(prefs.getProperty("fontSize"));
      return comboBoxPnl(selectSize, "Size:   ");     
   }

   private JPanel comboBoxPnl(JComboBox<String> comboBox, String title) {
      comboBox.setFocusable(false);
      comboBox.setFont(Constants.VERDANA_PLAIN_11);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(Constants.SANSSERIF_BOLD_12);
      JPanel comboBoxPnl = new JPanel();
      comboBoxPnl.setLayout(new BoxLayout(comboBoxPnl, BoxLayout.LINE_AXIS));
      comboBoxPnl.add(titleLb);
      comboBoxPnl.add(comboBox);      
      return comboBoxPnl;
   }

    private JPanel buttonPnl() {
      JPanel buttonsPanel = new JPanel(new FlowLayout());
      buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));   
      buttonsPanel.add(okBt);
      return buttonsPanel;
   }
}
