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

import eg.utils.ScreenParams;

/**
 * The dialog for setting the font and font size
 */
public class FontSettingWin {

   private final static String[] FONT_SIZES = {
     "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
   };

   private final JFrame frame = new JFrame("Font");
   private final JComboBox<String> selectFont;
   private final JComboBox<String> selectSize;
   private final JButton okBt = new JButton("OK");
   private final JButton cancelBt = new JButton("Cancel");
   private final String[] fonts;

   private String font;
   private int size;

   /**
    * @param  font  the font name that is initially set selected
    * @param  size  the font size that is initiallay set selected
    */
   public FontSettingWin(String font, int size) {
      this.font = font;
      this.size = size;
      fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();

      selectFont = new JComboBox<>(fonts);
      selectSize = new JComboBox<>(FONT_SIZES);
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
    * Adds an action listener to this ok button
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
      font = fonts[selectFont.getSelectedIndex()];
      return font;
   }

   /**
    * Returns the font size selection
    *
    * @return  the font size
    */
   public int size() {
      String sizeStr = FONT_SIZES[selectSize.getSelectedIndex()];
      size = Integer.parseInt(sizeStr);
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
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
      twoComboBx.add(Box.createRigidArea(ScreenParams.scaledDimension(10, 0)));
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
      comboBox.setFont(ScreenParams.scaledFontToPlain(comboBox.getFont(), 8));
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
