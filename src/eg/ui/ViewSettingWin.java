package eg.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

//--Eadgyth--/
import eg.utils.ScreenParams;
import eg.utils.SystemParams;

/**
 * The dialog to enter preferences for the display of the main window
 */
public class ViewSettingWin {

   public final static String[] THEME_OPT = {"White", "Dark"};
   public final static String[] ICON_SIZES = {"Small", "Large"};
   public final static String[] LAF_OPT = {"System", "Java default"};

   private final JFrame frame = new JFrame("View preferences");
   private final JCheckBox lineNumbersChBx = new JCheckBox();
   private final JCheckBox toolbarChBx = new JCheckBox();
   private final JCheckBox statusbarChBx = new JCheckBox();
   private final JComboBox<String> backgroundCBx = new JComboBox<>(THEME_OPT);
   private final JComboBox<String> iconSizeCBx = new JComboBox<>(ICON_SIZES);
   private final JComboBox<String> lafCbx = new JComboBox<>(LAF_OPT);
   private final JButton okBt = new JButton("OK");
   private final JButton cancelBt = new JButton("Cancel");

   public ViewSettingWin() {
      initFrame();
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
    * Sets the listener to this ok button
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setOkAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Sets the listener to this cancel button
    *
    * @param al  the <code>ActionListener</code>
    */
   public void setCancelAct(ActionListener al) {
      cancelBt.addActionListener(al);
   }

   /**
    * Sets the listener to the close button of this window
    *
    * @param wa  the <code>WindowAdapter</code>
    */
   public void setDefaultCloseAction(WindowAdapter wa) {
      frame.addWindowListener(wa);
   }

   /**
    * Returns the state of the checkbox for showing/hiding line numbers
    *
    * @return  true if selected, false otherwie
    */
   public boolean isShowLineNumbers() {
      return lineNumbersChBx.isSelected();
   }

   /**
    * Sets the state of the checkbox for showing/hiding line numbers
    *
    * @param  b   true to select, false to deselect
    */
   public void setShowLineNumbers(boolean b) {
      lineNumbersChBx.setSelected(b);
   }

    /**
    * Returns the state of the checkbox for showing/hiding the toolbar
    *
    * @return  true if selected, false otherwie
    */
   public boolean isShowToolbar() {
      return toolbarChBx.isSelected();
   }

   /**
    * Sets the state of the checkbox for showing/hiding the toolbar
    *
    * @param  b  true to select, false to deselect
    */
   public void setShowToolbar(boolean b) {
      toolbarChBx.setSelected(b);
   }

   /**
    * Returns the state of the checkbox for showing/hiding the statusbar
    *
    * @return  true if selectedd, false otherwise
    */
   public boolean isShowStatusbar() {
      return statusbarChBx.isSelected();
   }

   /**
    * Sets the state of the checkbox for showing/hiding the statusbar
    *
    * @param  b  true to select, false to deselect
    */
   public void setShowStatusbar(boolean b) {
      statusbarChBx.setSelected(b);
   }

   /**
    * Returns the index of the combobox selection for the background
    * theme
    *
    * @return  the index
    */
   public int themeIndex() {
      return backgroundCBx.getSelectedIndex();
   }

   /**
    * Sets the combobox selection for the backgound theme
    *
    * @param index  the index to select
    */
   public void setTheme(int index) {
      backgroundCBx.setSelectedIndex(index);
   }

   /**
    * Sets the combobox selection for the backgound theme
    *
    * @param item  the item to select
    */
   public void setTheme(String item) {
      backgroundCBx.setSelectedItem(item);
   }

   /**
    * Returns the index of the combobox selection for the icon size
    *
    * @return  the index
    */
   public int iconSizeIndex() {
      return iconSizeCBx.getSelectedIndex();
   }

   /**
    * Sets the combobox selection for the icon size
    *
    * @param index  the index to select
    */
   public void setIconSize(int index) {
      iconSizeCBx.setSelectedIndex(index);
   }

  /**
    * Sets the combobox selection for the icon size
    *
    * @param item  the item to select
    */
   public void setIconSize(String item) {
      iconSizeCBx.setSelectedItem(item);
   }

   /**
    * Returns the index of combobox selection for the look and feel
    *
    * @return  the index
    */
   public int lafIndex() {
      return lafCbx.getSelectedIndex();
   }

   /**
    * Sets the combobox selection for the LaF
    *
    * @param index  the index to select
    */
   public void setLaf(int index) {
      lafCbx.setSelectedIndex(index);
   }

  /**
    * Sets the combobox selection for the LaF
    *
    * @param item  the item to select
    */
   public void setLaf(String item) {
      lafCbx.setSelectedItem(item);
   }

   //
   //--private--/
   //

   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setResizable(false);
      frame.setContentPane(combinedPnl());
      frame.getRootPane().setDefaultButton(okBt);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
   }

   private JPanel combinedPnl() {
      JPanel pnl = new JPanel(new BorderLayout());
      pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      JPanel innerPnl = new JPanel(new GridLayout(1, 2));
      innerPnl.add(windowSettingsPnl());
      innerPnl.add(appearanceSettingsPnl());
      pnl.add(innerPnl, BorderLayout.CENTER);
      pnl.add(buttonsPanel(), BorderLayout.SOUTH);
      return pnl;
   }

   private JPanel windowSettingsPnl() {
      JPanel pnl = new JPanel(new GridLayout(3, 1));
      pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      pnl.add(lineNumberSettingPnl());
      pnl.add(toolbarSettingPnl());
      pnl.add(statusbarSettingPnl());
      JPanel ctrPnl = new JPanel();
      ctrPnl.add(pnl);
      return ctrPnl;
   }

   private JPanel appearanceSettingsPnl() {
      JPanel pnl = new JPanel();
      pnl.setBorder(UIComponents.titledBorder("Appearance"));
      JPanel innerPnl = new JPanel(new GridLayout(4, 1));
      JLabel lb = new JLabel("Selections take effect only after restart");
      lb.setFont(Fonts.SANSSERIF_PLAIN_8);
      innerPnl.add(backgroundSettingPnl());
      innerPnl.add(iconSizeSettingPnl());
      innerPnl.add(lafSettingPnl());
      innerPnl.add(lb);
      pnl.add(innerPnl);
      return pnl;
   }

   private JPanel lineNumberSettingPnl() {
      return checkBxPnl(lineNumbersChBx,
            "Show line numbers when wordwrap is disabled");
   }

   private JPanel toolbarSettingPnl() {
      return checkBxPnl(toolbarChBx, "Show toolbar");
   }

   private JPanel statusbarSettingPnl() {
      return checkBxPnl(statusbarChBx, "Show status bar");
   }

   private JPanel backgroundSettingPnl() {
      return comboBxPnl(backgroundCBx, "Background:");
   }

   private JPanel iconSizeSettingPnl() {
      return comboBxPnl(iconSizeCBx, "Size of icons:");
   }

   private JPanel lafSettingPnl() {
      return comboBxPnl(lafCbx, "Look & feel:");
   }

   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JLabel lb = new JLabel(title);
      lb.setFont(Fonts.SANSSERIF_BOLD_8);
      lb.setPreferredSize(ScreenParams.scaledDimension(200, 0));
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      JPanel holdCheckBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdCheckBox.add(checkBox);
      pnl.add(holdCheckBox);
      pnl.add(lb);
      return pnl;
   }

   private JPanel comboBxPnl(JComboBox<String> comboBox, String title) {
      JLabel lb = new JLabel(title);
      lb.setFont(Fonts.SANSSERIF_BOLD_8);
      lb.setPreferredSize(ScreenParams.scaledDimension(75, 0));
      comboBox.setFont(Fonts.SANSSERIF_PLAIN_9);
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      pnl.add(lb);
      JPanel holdComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdComboBox.add(comboBox);
      pnl.add(holdComboBox);
      comboBox.setEnabled(SystemParams.eadgythDataDirExists());
      return pnl;
   }

   private JPanel buttonsPanel() {
      JPanel pnl = new JPanel(new FlowLayout());
      int topMargin = ScreenParams.scaledSize(10);
      pnl.setBorder(BorderFactory.createEmptyBorder(topMargin, 0, 0, 0));
      pnl.add(okBt);
      pnl.add(cancelBt);
      return pnl;
   }
}
