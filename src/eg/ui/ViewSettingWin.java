package eg.ui;

import java.awt.Dimension;
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
import eg.Constants;

/**
 * The dialog to enter preferences for the display of the main window.
 */
public class ViewSettingWin {

   public final static String[] LAF_OPT = {"System", "Java default"};
   public final static String[] ICON_SIZES = {"Small", "Large"};
   
   private final static Dimension LABEL_DIM
         = eg.utils.ScreenParams.scaledDimension(230, 0);

   private final JFrame frame = new JFrame("View preferences");
   
   private final JCheckBox checkLineNumbers       = new JCheckBox();
   private final JCheckBox checkToolbar           = new JCheckBox();
   private final JCheckBox checkStatusbar         = new JCheckBox();
   private final JComboBox<String> selectLaf      = new JComboBox<>(LAF_OPT);
   private final JComboBox<String> selectIconSize = new JComboBox<>(ICON_SIZES);
   private final JButton   okBt                   = new JButton("OK");
   private final JButton   cancelBt               = new JButton("Cancel");
   
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
    * Sets the listener for the close button of this frame
    *
    * @param wa  the <code>WindowAdapter</code>
    */
   public void setDefaultCloseAction(WindowAdapter wa) {
      frame.addWindowListener(wa);
   }
   
   /**
    * Returns the state of the checkbox for showing/hiding line numbers
    *
    * @return  the boolean value that is true if selected, false
    * otherwie
    */ 
   public boolean isShowLineNumbers() {
      return checkLineNumbers.isSelected();
   }
   
   /**
    * Sets the state of the checkbox for showing/hiding line numbers
    *
    * @param  b  the boolean value that is true to select, false to
    * deselect
    */
   public void setShowLineNumbers(boolean b) {
      checkLineNumbers.setSelected(b);
   }      
   
    /**
    * Returns the state of the checkbox for showing/hiding the toolbar
    *
    * @return  the boolean value; true if selected, false otherwie
    */ 
   public boolean isShowToolbar() {
      return checkToolbar.isSelected();
   }
   
   /**
    * Sets the state of the checkbox for showing/hiding the toolbar
    *
    * @param  b  the boolean value that is true to select, false to deselect
    */
   public void setShowToolbar(boolean b) {
      checkToolbar.setSelected(b);
   }
   
   /**
    * Returns the state of the checkbox for showing/hiding the statusbar
    *
    * @return  the boolean value that is true if selectedd, false otherwise
    */ 
   public boolean isShowStatusbar() {
      return checkStatusbar.isSelected();
   }
   
   /**
    * Sets the state of the checkbox for showing/hiding the statusbar
    *
    * @param  b  the boolean value that is true to select, false to deselect
    */
   public void setShowStatusbar(boolean b) {
      checkStatusbar.setSelected(b);
   }
   
   /**
    * Returns the index of the combobox selection for the icon size
    *
    * @return  the index
    */ 
   public int iconSizeIndex() {
      return selectIconSize.getSelectedIndex();
   }
   
   /**
    * Sets the combobox selection for the icon size
    *
    * @param index  the index of the item to select
    */
   public void setIconSize(int index) {
      selectIconSize.setSelectedIndex(index);
   }
   
  /**
    * Sets the combobox selection for the icon size
    *
    * @param item  the item to select
    */
   public void setIconSize(String item) {
      selectIconSize.setSelectedItem(item);
   }
   
   /**
    * Returns the index of combobox selection for the look and feel
    *
    * @return  the index
    */ 
   public int lafIndex() {
      return selectLaf.getSelectedIndex();
   }
   
   /**
    * Sets the combobox selection for the LaF
    *
    * @param index  the index of the item to select
    */
   public void setLaf(int index) {
      selectLaf.setSelectedIndex(index);
   }
   
  /**
    * Sets the combobox selection for the LaF
    *
    * @param item  the item to select
    */
   public void setLaf(String item) {
      selectLaf.setSelectedItem(item);
   }  
   
   //
   //--private--/
   //
   
   private void initFrame() {
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
   
   private JPanel setLineNumberPanel() {
      return checkBxPnl(checkLineNumbers,
            "Show line numbers when wordwrap is disabled:");
   }
   
   private JPanel setToolbarPanel() { 
      return checkBxPnl(checkToolbar, "Show toolbar:");
   }
   
   private JPanel setStatusBarPanel() {
      return checkBxPnl(checkStatusbar, "Show status bar:");
   }
   
   private JPanel setIconSizePnl() {
      return comboBxPnl(selectIconSize, "Size of icons (requires restart):");
   }
   
   private JPanel setLafPnl() {
      return comboBxPnl(selectLaf, "Look & feel (requires restart):");
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
      pnl.add(cancelBt);
      return pnl;
   }
}
