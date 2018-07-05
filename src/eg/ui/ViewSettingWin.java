package eg.ui;

import java.awt.Dimension;
import java.awt.BorderLayout;
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
import javax.swing.BorderFactory;

//--Eadgyth--/
import eg.Preferences;
import eg.Constants;

/**
 * The frame that contains components to enter preferences for the display of the
 * main window
 */
public class ViewSettingWin {

   public final static String[] LAF_OPT = {"System", "Java default"};
   public final static String[] ICON_SIZES = {"16 x 16", "22 x 22"};
   
   private final static Dimension LABEL_DIM
         = eg.utils.ScreenParams.scaledDimension(230, 0);

   private final JFrame frame = new JFrame("View preferences");
   private final Preferences prefs = Preferences.readProgramPrefs();
   
   private final JCheckBox checkLineNumbers       = new JCheckBox();
   private final JCheckBox checkToolbar           = new JCheckBox();
   private final JCheckBox checkStatusbar         = new JCheckBox();
   private final JComboBox<String> selectLaf      = new JComboBox<>(LAF_OPT);
   private final JComboBox<String> selectIconSize = new JComboBox<>(ICON_SIZES);
   private final JButton   okBt                   = new JButton("OK");
   
   public ViewSettingWin() {
      String laf = prefs.getProperty("LaF");
      if (laf.length() > 0) {
         selectLaf.setSelectedItem(laf);
      }
      else {
         selectLaf.setSelectedItem(LAF_OPT[1]);
      }
      selectIconSize.setSelectedItem(prefs.getProperty("iconSize"));
      initFrame();
   }
   
   /**
    * Sets the specified boolean that specifies if this frame is made
    * visible or invisible
    *
    * @param b the boolean value
    */
   public void makeVisible(boolean b) {
      frame.setVisible(b);
   }
   
   /**
    * Sets the listener to this ok button
    *
    * @param al  the {@code ActionListener}
    */
   public void setOkAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Returns the boolean that indicates if the checkbox for
    * showing the toolbar is ticked
    *
    * @return  the boolean value
    */ 
   public boolean isShowToolbar() {
      return checkToolbar.isSelected();
   }
   
   /**
    * Returns the boolean that indicates if the checkbox for
    * showing line numbers is ticked
    *
    * @return the boolean value
    */ 
   public boolean isShowLineNumbers() {
      return checkLineNumbers.isSelected();
   }
   
   /**
    * Returns the boolean that indicates if the checkbox for
    * showing the statusbar is ticked
    *
    * @return the boolean value
    */ 
   public boolean isShowStatusbar() {
      return checkStatusbar.isSelected();
   }
   
   /**
    * Returns the index of combobox selection for the icon size
    *
    * @return  the index
    */ 
   public int selectedIconSize() {
      return selectIconSize.getSelectedIndex();
   }
   
   /**
    * Returns the index of combobox selection for the look and feel
    *
    * @return  the index
    */ 
   public int selectedLaf() {
      return selectLaf.getSelectedIndex();
   }
   
   //
   //--private--/
   //
   
   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(allPanels());
      frame.getRootPane().setDefaultButton(okBt);
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
   }
   
   private JPanel allPanels() {
      JPanel pnl = new JPanel(new BorderLayout());
      pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      JPanel selectionsPnl = new JPanel(new GridLayout(5, 1));
      selectionsPnl.add(setLineNumberPanel());
      selectionsPnl.add(setToolbarPanel());
      selectionsPnl.add(setStatusBarPanel());
      selectionsPnl.add(setIconSizePnl());
      selectionsPnl.add(setLafPnl());
      selectionsPnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      pnl.add(selectionsPnl, BorderLayout.CENTER);
      pnl.add(buttonsPanel(), BorderLayout.SOUTH);
      return pnl;
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
      JLabel lb = new JLabel(title);
      lb.setFont(Constants.SANSSERIF_BOLD_8);
      lb.setPreferredSize(LABEL_DIM);
      JPanel pnl = new JPanel(); 
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));     
      pnl.add(lb);
      JPanel holdCheckBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdCheckBox.add(checkBox);
      pnl.add(holdCheckBox);
      return pnl;
   }
   
   private JPanel comboBxPnl(JComboBox<String> comboBox, String title) {
      JLabel lb = new JLabel(title);
      lb.setFont(Constants.SANSSERIF_BOLD_8);
      lb.setPreferredSize(LABEL_DIM);
      comboBox.setFont(Constants.SANSSERIF_PLAIN_9);
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      pnl.add(lb);
      JPanel holdComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdComboBox.add(comboBox);
      pnl.add(holdComboBox);
      return pnl;
   }
   
   private JPanel buttonsPanel() {
      JPanel pnl = new JPanel(new FlowLayout());   
      pnl.add(okBt);
      return pnl;
   }
}
