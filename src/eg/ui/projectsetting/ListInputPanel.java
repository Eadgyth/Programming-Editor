package eg.ui.projectsetting;

import java.io.File;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import javax.swing.border.MatteBorder;

import java.util.ArrayList;
import java.util.List;

//--Eadgyth--/
import eg.utils.ScreenParams;

/**
 * Defines a panel in which a list of strings is displayed in or
 * obtained from a list of text fields
 */
public class ListInputPanel {

   private static final Dimension DIM_TF = ScreenParams.scaledDimension(350, 14);
   private static final Color SEL_TF_YELLOW = new Color(250, 250, 170);

   private final JPanel content = new JPanel(new BorderLayout());
   private final JScrollPane scroll = new JScrollPane(
         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

   private final JButton addBt    = new JButton("add");
   private final JButton removeBt = new JButton("remove");
   private final JButton upBt     = new JButton("up");
   private final JButton downBt   = new JButton("down");
   private final JPanel  inScroll = new JPanel(new FlowLayout(FlowLayout.RIGHT));
   private final JPanel  holder   = new JPanel();

   private final List<JTextField> tfList = new ArrayList<>(5);

   private int tfIndex = 0;

   /**
    * @param label  the label for the panel
    */
   public ListInputPanel(String label) {
      initContent(label);
      setActions();
   }

   /**
    * Gets this <code>JPanel</code> that contains the label, the list
    * of text fields and buttons to modify the list
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Displays the specified list in this list of text fields
    *
    * @param l  the list of strings
    */
   public void displayList(List<String> l) {
      if (l.isEmpty()) {
         return;
      }
      for (int i = 0; i < l.size(); i++) {
         if (i == tfList.size()) {
            addTf(false);
         }
         tfList.get(i).setText(l.get(i));
      }
   }

   /**
    * Assigns the strings in the list of text fields to the specified
    * list object. Empty text fields are skipped.
    *
    * @param l  the list
    */
   public void assignListInput(List<String> l) {
      l.clear();
      for (int i = 0; i < tfList.size(); i++) {
         String in = tfList.get(i).getText().trim();
         if (!in.isEmpty()) {
            l.add(tfList.get(i).getText().trim().replace("/", File.separator));
         }
      }
   }

   /**
    * Sets the focus in the text field selected previously
    */
   public void setLastFocus() {
      if (!tfList.isEmpty()) {
         tfList.get(tfIndex).requestFocusInWindow();
      }
   }

   /**
    * Disables buttons except the "add" button
    */
   public void disableButtons() {
      disableBts();
   }

   //
   //--private--/
   //

   private void addTf(boolean focus) {
      int i = tfList.size();
      tfList.add(new JTextField());
      tfList.get(i).setFont(ScreenParams.scaledFontToPlain(tfList.get(i).getFont(), 8));
      tfList.get(i).setPreferredSize(DIM_TF);
      tfList.get(i).addFocusListener(focusListener);
      holder.add(tfList.get(i));
      holder.revalidate();
      if (focus) {
         tfList.get(i).requestFocusInWindow();
      }
   }

   private void removeTf() {
      holder.remove(tfList.get(tfIndex));
      tfList.remove(tfIndex);
      holder.revalidate();
      holder.repaint();
      if (!tfList.isEmpty()) {
         tfList.get(tfList.size() - 1).requestFocusInWindow();
      }
      else {
         disableBts();
      }
   }

   private void moveDown() {
      String selected = tfList.get(tfIndex).getText();
      String next = tfList.get(tfIndex + 1).getText();
      tfList.get(tfIndex).setText(next);
      tfList.get(tfIndex + 1).setText(selected);
      tfList.get(tfIndex + 1).requestFocusInWindow();
   }

   private void moveUp() {
      String selected = tfList.get(tfIndex).getText();
      String prev = tfList.get(tfIndex - 1).getText();
      tfList.get(tfIndex).setText(prev);
      tfList.get(tfIndex - 1).setText(selected);
      tfList.get(tfIndex - 1).requestFocusInWindow();
   }

   private void disableBts() {
      removeBt.setEnabled(false);
      upBt.setEnabled(false);
      downBt.setEnabled(false);
   }

   private void initContent(String label) {
      holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
      inScroll.add(holder);
      scroll.setViewportView(inScroll);
      scroll.setBorder(null);
      content.add(labelPanel(label), BorderLayout.NORTH);
      content.add(scroll, BorderLayout.CENTER);
      content.add(buttonPanel(), BorderLayout.SOUTH);
      content.setPreferredSize(ScreenParams.scaledDimension(0, 100));
      addTf(false);
   }

   private JPanel labelPanel(String label) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel lb = new JLabel(label);
      lb.setFont(ScreenParams.scaledFontToBold(lb.getFont(), 8));
      pnl.add(lb);
      return pnl;
   }
   
   private JPanel buttonPanel() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.setBorder(new MatteBorder(1, 0, 0, 0, Color.WHITE));
      pnl.add(addBt);
      pnl.add(removeBt);
      pnl.add(upBt);
      pnl.add(downBt);
      return pnl;
   }

   private void setActions() {
      addBt.addActionListener(e -> addTf(true));
      addBt.setMnemonic('A');
      addBt.setFocusable(false);

      removeBt.addActionListener(e -> removeTf());
      removeBt.setMnemonic('R');
      removeBt.setEnabled(false);
      removeBt.setFocusable(false);

      downBt.addActionListener(e -> moveDown());
      downBt.setMnemonic('D');
      downBt.setEnabled(false);
      downBt.setFocusable(false);

      upBt.addActionListener(e -> moveUp());
      upBt.setMnemonic('U');
      upBt.setEnabled(false);
      upBt.setFocusable(false);
   }

   private final FocusListener focusListener = new FocusListener() {

      @Override
      public void focusGained(FocusEvent e) {
         tfIndex = tfList.indexOf(e.getComponent());
         tfList.get(tfIndex).setBackground(SEL_TF_YELLOW);
         removeBt.setEnabled(true);
         upBt.setEnabled(tfIndex > 0);
         downBt.setEnabled(tfIndex < tfList.size() - 1);
      }

      @Override
      public void focusLost(FocusEvent e) {
         int i = tfList.indexOf(e.getComponent());
         if (i != -1) {
            tfList.get(i).setBackground(Color.WHITE);
         }
      }
   };
}
