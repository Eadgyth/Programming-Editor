package eg.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;

//--Eadgyth--//
import eg.Preferences;
import eg.Constants;

/**
 * The frame that contains components to control the display of the
 * main window
 */
public class ViewSettingWin {

   public final static String[] LAF_OPT = {"System", "Java default"};
   public final static String[] ICON_SIZES = {"16 x 16", "22 x 22"};

   private final JFrame frame = new JFrame("View settings");
   private final Preferences prefs = new Preferences();
   
   private final JCheckBox checkLineNumbers       = new JCheckBox();
   private final JCheckBox checkToolbar           = new JCheckBox();
   private final JCheckBox checkStatusbar         = new JCheckBox();
   private final JComboBox<String> selectLaf      = new JComboBox<>(LAF_OPT);
   private final JComboBox<String> selectIconSize = new JComboBox<>(ICON_SIZES);
   private final JButton   okBt                   = new JButton("OK");
   
   public ViewSettingWin() {
      prefs.readPrefs();
      selectLaf.setSelectedItem(prefs.getProperty("LaF"));
      selectIconSize.setSelectedItem(prefs.getProperty("iconSize"));
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
    * Returns if showing the toolbar is selected in the corresponding
    * checkbox
    *
    * @return if showing the toolbar is selected
    */ 
   public boolean isShowToolbar() {
      return checkToolbar.isSelected();
   }
   
   /**
    * Returns if showing line numbers is selected in the corresponding
    * checkbox
    *
    * @return if showing line numbers is selected
    */ 
   public boolean isShowLineNumbers() {
      return checkLineNumbers.isSelected();
   }
   
   /**
    * Returns if showing the status bar is selected
    *
    * @return if showing the staus bar is selected
    */ 
   public boolean isShowStatusbar() {
      return checkStatusbar.isSelected();
   }
   
   /**
    * Returns the index of combobox selection for the icon size
    *
    * @return  the index of combobox selection for the icon size
    */ 
   public int selectedIconSize() {
      return selectIconSize.getSelectedIndex();
   }
   
   /**
    * Returns the index of combobox selection for the look and feel
    *
    * @return  the index of combobox selection for the look and feel
    */ 
   public int selectedLaf() {
      return selectLaf.getSelectedIndex();
   }
   
   //
   //--private methods
   //
   
   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(allPanels());
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON.getImage());
   }
   
   private JPanel allPanels() {
      JPanel allPanels = new JPanel(new GridLayout(6, 1));
      allPanels.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      allPanels.add(setLineNumberPanel());
      allPanels.add(setToolbarPanel());
      allPanels.add(setStatusBarPanel());
      allPanels.add(setIconSizePnl());
      allPanels.add(setLafPnl());
      allPanels.add(buttonsPanel());
      frame.getRootPane().setDefaultButton(okBt);
      
      return allPanels;
   }
   
   private JPanel setToolbarPanel() {      
      if ("show".equals(prefs.getProperty("toolbar"))) {
         checkToolbar.setSelected(true);
      }
      else {
         checkToolbar.setSelected(false);
      }    
      return checkBxPnl(checkToolbar, "Show toolbar:");
   }
   
   private JPanel setLineNumberPanel() {      
      if ("show".equals(prefs.getProperty("lineNumbers"))) {
         checkLineNumbers.setSelected(true);
      }
      else {
         checkLineNumbers.setSelected(false);
      }
      return checkBxPnl(checkLineNumbers,
            "Show line numbers when wordwrap is disabled:");
   }
   
   private JPanel setStatusBarPanel() {      
      if ("show".equals(prefs.getProperty("statusbar"))) {
         checkStatusbar.setSelected(true);
      }
      else {
         checkStatusbar.setSelected(false);
      }      
      return checkBxPnl(checkStatusbar, "Show status bar:");
   }
   
   private JPanel setIconSizePnl() {
      return comboBxPnl(selectIconSize, "Size of icons (needs restarting Eadgyth):");
   }
   
   private JPanel setLafPnl() {
      return comboBxPnl(selectLaf, "Look & feel (needs restarting Eadgyth):");
   }

   private JPanel checkBxPnl(JCheckBox checkBox, String title) {
      JLabel label = new JLabel(title);
      label.setFont(Constants.SANSSERIF_BOLD_9);
      JPanel holdCheckBx = new JPanel(new FlowLayout(FlowLayout.LEFT));
      
      JPanel checkBxPnl = new JPanel(); 
      checkBxPnl.setLayout(new BoxLayout(checkBxPnl, BoxLayout.LINE_AXIS));
      checkBox.setHorizontalTextPosition(JCheckBox.LEFT);     
      checkBxPnl.add(label);
      checkBxPnl.add(Box.createHorizontalGlue());
      checkBxPnl.add(checkBox);
      return checkBxPnl;
   }
   
   private JPanel comboBxPnl(JComboBox comboBox, String title) {      
      JLabel lb = new JLabel(title);
      lb.setFont(Constants.SANSSERIF_BOLD_9);
      comboBox.setFont(Constants.SANSSERIF_PLAIN_9);

      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      pnl.add(lb);
      pnl.add(Box.createHorizontalGlue());
      JPanel holdComboBx = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      holdComboBx.add(comboBox);
      pnl.add(holdComboBx);
      return pnl;
   }
   
   private JPanel buttonsPanel() {
      JPanel buttonsPanel = new JPanel(new FlowLayout());   
      buttonsPanel.add(okBt);
      return buttonsPanel;
   }
}
