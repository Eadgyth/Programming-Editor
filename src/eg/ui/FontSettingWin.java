package eg.ui;

import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;

//--Eadgyth--/
import eg.Constants;

/**
 * A dialog for setting the font and font size
 */
public class FontSettingWin {

   private final static String[] FONT_SIZES = {
     "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
   };

   private final JFrame frame = new JFrame("Font");
   private final JComboBox<String> selectFont;
   private final JComboBox<String> selectSize;
   private final JButton okBt = new JButton("OK");
   private final String[] fonts;

   /**
    * @param  font  the font name that is initially set selected
    * @param  size  the font size that is initiallay set selected
    */
   public FontSettingWin(String font, int size) {
      fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
            
      selectFont = new JComboBox<>(fonts);
      selectSize = new JComboBox<>(FONT_SIZES);
      initFrame(font, size);
   }

   /**
    * Sets the boolean that specified if this frame is made visible
    * or invisible
    *
    * @param b  the boolean value
    */
   public void makeVisible(boolean b) {
      frame.setVisible(b);
   }

   /**
    * Adds an action listener to this ok button
    *
    * @param al  the {@code ActionListener}
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Returns the font selection in the corresponding combobox
    *
    * @return the selected font
    */
   public String fontComboBxRes() {
      String font = fonts[selectFont.getSelectedIndex()];
      return font;
   }

   /**
    * Returns the font size selection in the corresponding combobox
    *
    * @return the selected font
    */
   public int sizeComboBxRes() {
      String size = FONT_SIZES[selectSize.getSelectedIndex()];
      return Integer.parseInt(size);
   }

   //
   //--private--/
   //

   private void initFrame(String font, int size) {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combinedPnl(font, size));
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
   }

   private JPanel combinedPnl(String font, int size) {
      JPanel twoComboBx = new JPanel();
      twoComboBx.setLayout(new BoxLayout(twoComboBx, BoxLayout.LINE_AXIS));
      twoComboBx.add(fontPnl(font));
      twoComboBx.add(Box.createRigidArea(eg.utils.ScreenParams.scaledDimension(10, 0)));
      twoComboBx.add(sizePnl(size));

      JPanel combined = new JPanel();
      combined.setLayout(new BoxLayout(combined, BoxLayout.Y_AXIS));
      combined.add(twoComboBx);
      combined.add(buttonPnl());
      combined.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      return combined;
   }

   private JPanel fontPnl(String font) {
      selectFont.setSelectedItem(font);
      return comboBoxPnl(selectFont, "Font:   ");
   }

   private JPanel sizePnl(int size) {
      selectSize.setSelectedItem(String.valueOf(size));
      return comboBoxPnl(selectSize, "Size:   ");
   }

   private JPanel comboBoxPnl(JComboBox<String> comboBox, String title) {
      comboBox.setFocusable(false);
      comboBox.setFont(Constants.VERDANA_PLAIN_8);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(Constants.SANSSERIF_BOLD_9);
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
