package eg.ui;

import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import eg.utils.ScreenParams;

/**
 * The dialog for setting the font and font size
 */
public class FontSettingWin {

   private static final String[] FONTS =
         GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();

   private final JFrame frame = new JFrame("Font");
   private final JComboBox<String> selectFont;
   private final JComboBox<Integer> selectSize;
   private final JButton okBt = new JButton("OK");
   private final JButton cancelBt = new JButton("Cancel");
   private final Integer[] fontSizes;

   private String font;
   private int size;

   /**
    * Creates a <code>FontSettingsWin</code>
    *
    * @param fontSizes  the array of selectable (unscaled) font sizes
    * @param  font  the font name that is initially set selected
    * @param  size  the (unscaled) font size that is initiallay
    * set selected
    */
   public FontSettingWin(Integer[] fontSizes, String font, int size) {
      this.fontSizes = fontSizes;
      this.font = font;
      this.size = size;
      selectFont = new JComboBox<>(FONTS);
      selectSize = new JComboBox<>(fontSizes);
      initFrame(font, size);
      cancelBt.addActionListener(e -> undoSettings());
      frame.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent we) {
            undoSettings();
         }
      });
   }

   /**
    * Sets this frame visible or invisible
    *
    * @param b  the boolean value that is true to set visible, false
    * set invisible
    */
   public void setVisible(boolean b) {
      frame.setVisible(b);
   }

   /**
    * Registers the listener on this ok button
    *
    * @param al  the {@code ActionListener}
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Returns the font selection
    *
    * @return  the font
    */
   public String font() {
      font = FONTS[selectFont.getSelectedIndex()];
      return font;
   }

   /**
    * Returns the font size selection
    *
    * @return  the font size
    */
   public int size() {
      size = fontSizes[selectSize.getSelectedIndex()];
      return size;
   }

   //
   //--private--/
   //

   private void undoSettings() {
      selectFont.setSelectedItem(font);
      selectSize.setSelectedItem(String.valueOf(size));
      setVisible(false);
   }

   private void initFrame(String font, int size) {
      frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combinedPnl(font, size));
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
   }

   private JPanel combinedPnl(String font, int size) {
      JPanel twoComboBx = new JPanel();
      twoComboBx.setLayout(new BoxLayout(twoComboBx, BoxLayout.LINE_AXIS));
      twoComboBx.add(fontPnl(font));
      twoComboBx.add(Box.createRigidArea(ScreenParams.scaledDimension(10, 0)));
      twoComboBx.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
      selectSize.setSelectedItem(size);
      return comboBoxPnl(selectSize, "Size:   ");
   }

   private JPanel comboBoxPnl(JComboBox<?> comboBox, String title) {
      comboBox.setFocusable(false);
      JLabel titleLb = new JLabel(title);
      titleLb.setFont(ScreenParams.scaledFontToBold(titleLb.getFont(), 9));
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      pnl.add(titleLb);
      pnl.add(comboBox);
      return pnl;
   }

   private JPanel buttonPnl() {
      JPanel pnl = new JPanel(new FlowLayout());
      pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      pnl.add(okBt);
      pnl.add(cancelBt);
      return pnl;
   }
}
