package eg.ui;

import java.awt.FlowLayout;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.WindowConstants;

//--Eadgyth--/
import eg.utils.ScreenParams;

/**
 * The dialog for setting the indentation
 */
public class IndentSettingWin {

   /**
    * The array of possible numbers of spaces from 1 to 8
    */
   public static final String[] N_SPACES = {
      "1", "2", "3", "4", "5", "6", "7", "8"
   };

   private final JFrame frame = new JFrame("Indentation");
   private final JComboBox<String> selectSpacesCBx;
   private final JRadioButton indentSpacesBt = new JRadioButton("Spaces");
   private final JRadioButton indentTabsBt = new JRadioButton("Tabs");
   private final JButton okBt = new JButton("OK");
   private final JButton cancelBt = new JButton("Cancel");

   private int indentLength;
   private boolean indentTab;

   /**
    * @param indentLength  the inital selection for the
    * indent length
    * @param indentTab  the initial selection for the options
    * to use tabs or spaces: true for tabs, false for spaces
    *
    */
   public IndentSettingWin(int indentLength, boolean indentTab) {
      this.indentLength = indentLength;
      this.indentTab = indentTab;
      selectSpacesCBx = new JComboBox<>(N_SPACES);
      initFrame();
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
    * @param b  true to set visible, false set invisible
    */
   public void setVisible(boolean b) {
      frame.setVisible(b);
   }

   /**
    * Registers the listener on this ok button
    *
    * @param al  the ActionListener
    */
   public void okAct(ActionListener al) {
      okBt.addActionListener(al);
   }

   /**
    * Returns the selection for the number of spaces
    *
    * @return  the number
    */
   public int indentLength() {
      return selectSpacesCBx.getSelectedIndex() + 1;
   }

   /**
    * Returns if it is selected to indent tabs
    *
    * @return  true if selected, false otherwie
    */
   public boolean indentTab() {
      return indentTabsBt.isSelected();
   }

   /**
    * Updates the selections for the indentation mode
    *
    * @param indentLength  the selection for the indent
    * indent length
    * @param indentTab  the selection for the options to use
    * tabs or spaces: true for tabs, false for spaces
    */
   public void update(int indentLength, boolean indentTab) {
      this.indentLength = indentLength;
      this.indentTab = indentTab;
      selectSpacesCBx.setSelectedIndex(indentLength - 1);
      indentTabsBt.setSelected(indentTab);
      indentSpacesBt.setSelected(!indentTab);
   }

   //
   //--private--/
   //

   private void undoSettings() {
      selectSpacesCBx.setSelectedIndex(indentLength - 1);
      indentTabsBt.setSelected(indentTab);
      setVisible(false);
   }

   private void initFrame() {
      frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      frame.setResizable(false);
      frame.setLocation(550, 100);
      frame.setContentPane(combinedPnl());
      frame.getRootPane().setDefaultButton(okBt);
      frame.pack();
      frame.setVisible(false);
      frame.setAlwaysOnTop(true);
      frame.setIconImage(IconFiles.EADGYTH_ICON_16.getImage());
   }

   private JPanel combinedPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));
      pnl.add(comboBoxPnl());
      pnl.add(Box.createRigidArea(ScreenParams.scaledDimension(20, 0)));
      pnl.add(buttonGroupPnl());
      pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      JPanel combined = new JPanel(new BorderLayout());
      combined.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      combined.add(pnl, BorderLayout.CENTER);
      combined.add(buttonPnl(), BorderLayout.SOUTH);
      return combined;
   }

   private JPanel comboBoxPnl() {
      selectSpacesCBx.setSelectedIndex(indentLength - 1);
      JLabel titleLb = new JLabel("Number of spaces:  ");
      titleLb.setFont(ScreenParams.scaledFontToBold(titleLb.getFont(), 8));
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.add(titleLb);
      pnl.add(selectSpacesCBx);
      pnl.setBorder(UIComponents.titledBorder("Indent length"));
      return pnl;
   }

   private JPanel buttonGroupPnl() {
      indentSpacesBt.setFont(
            ScreenParams.scaledFontToBold(indentSpacesBt.getFont(), 8));

      indentTabsBt.setFont(
            ScreenParams.scaledFontToBold(indentTabsBt.getFont(), 8));

      ButtonGroup indentBtGroup = new ButtonGroup();
      indentBtGroup.add(indentSpacesBt);
      indentBtGroup.add(indentTabsBt);
      indentTabsBt.setSelected(indentTab);
      indentSpacesBt.setSelected(!indentTab);

      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      JPanel holder1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holder1.add(indentSpacesBt);
      pnl.add(holder1);
      JPanel holder2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holder2.add(indentTabsBt);
      pnl.add(holder2);
      pnl.setBorder(UIComponents.titledBorder("Indent mode"));
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
