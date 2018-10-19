package eg.edittools;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

import java.awt.event.ItemEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JLabel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import eg.document.EditableDocument;

//--Eadgyth--/
import eg.Constants;
import eg.utils.UIComponents;

/**
 * The <code>AddableEditTool</code> for finding and replacing
 * text
 */
public class Finder implements AddableEditTool {

   private final JPanel finderPnl     = new JPanel(new BorderLayout());
   private final JTextField inputTf   = new JTextField();
   private final JTextField replaceTf = new JTextField();
   private final JButton searchBt     = new JButton("Find");
   private final JButton replaceBt    = new JButton("Replace");
   private final JButton replaceAllBt = new JButton("Replace all");

   private final TextSearch search = new TextSearch();
   
   public Finder() {
      initFinderPnl();
      setActions();
   }

   @Override
   public void addClosingAction(JButton closeBt) {
      finderPnl.add(toolbar(closeBt), BorderLayout.NORTH);
   }

   @Override
   public Component toolContent() {
      return finderPnl;
   }

   @Override
   public void setEditableDocument(EditableDocument edtDoc) {
      search.setDocument(edtDoc);
   }

   /**
    * Has no effect in this class
    */
   @Override
   public void end() {}

   //
   //--private--//
   //

   private void initFinderPnl() {
      enableButtons(false);
      finderPnl.add(controlsPnl(), BorderLayout.CENTER);
   }

   private JToolBar toolbar(JButton closeBt) {
      JButton[] bts = new JButton[] {
         closeBt
      };
      String[] toolTips = new String[] {
         "Close Finder"
      };
      JToolBar tb = UIComponents.toolBar(bts, toolTips);
      return tb;
   }

   private JPanel controlsPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
      pnl.add(labelPnl("Search for:"));
      setSize(inputTf);
      pnl.add(inputTf);
      pnl.add(Box.createVerticalStrut(10));
      pnl.add(radioBtPnl());
      pnl.add(Box.createVerticalStrut(10));
      pnl.add(checkBoxPnl());
      pnl.add(Box.createVerticalStrut(10));
      pnl.add(buttonsPnl(searchBt));
      pnl.add(Box.createVerticalStrut(20));
      pnl.add(labelPnl("Replace by:"));
      setSize(replaceTf);
      pnl.add(replaceTf);
      pnl.add(Box.createVerticalStrut(10));
      pnl.add(buttonsPnl(replaceBt, replaceAllBt));
      pnl.setBorder(Constants.EMPTY_BORDER_10);
      inputTf.getDocument().addDocumentListener(docListener);
      return pnl;
   }

   private JPanel labelPnl(String text) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel lb = new JLabel(text);
      lb.setFont(Constants.SANSSERIF_BOLD_9);
      lb.setForeground(Constants.GRAY);
      pnl.add(lb);
      setSize(pnl);
      return pnl;
   }

   private JPanel radioBtPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JRadioButton upBt = new JRadioButton("up");
      JRadioButton downBt = new JRadioButton("down", true);
      upBt.setFocusable(false);
      downBt.setFocusable(false);
      ButtonGroup group = new ButtonGroup();
      group.add(upBt);
      group.add(downBt);
      pnl.add(upBt);
      pnl.add(downBt);
      pnl.setBorder(UIComponents.titledBorder("Search direction"));
      setSize(pnl);
      upBt.addItemListener(e ->
         search.setUpwardSearch(e.getStateChange() == ItemEvent.SELECTED));
      return pnl;
   }

   private JPanel checkBoxPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JCheckBox cBx = new JCheckBox("Only whole word");
      cBx.setFocusable(false);
      cBx.addItemListener(e ->
         search.setRequireWord(e.getStateChange() == ItemEvent.SELECTED));
      JCheckBox cBxCase = new JCheckBox("Case sensitive");
      cBxCase.setFocusable(false);
      cBxCase.addItemListener(e ->
         search.setCaseSensitivity(e.getStateChange() == ItemEvent.SELECTED));
      pnl.add(cBx);
      pnl.add(cBxCase);
      pnl.setBorder(UIComponents.titledBorder("Search options"));
      setSize(pnl);
      return pnl;
   }

   private JPanel buttonsPnl(JButton... bt) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
       for (JButton bts : bt) {
           bts.setFocusable(false);
           pnl.add(bts);
       }
      setSize(pnl);
      return pnl;
   }

   private void setSize(Component c) {
      Dimension dim = c.getPreferredSize();
      dim.width = Integer.MAX_VALUE;
      c.setMaximumSize(dim);
   }

   private void setActions() {
      searchBt.addActionListener(e -> search.searchText(
            inputTf.getText()));

      inputTf.addActionListener(e -> search.searchText(
            inputTf.getText()));

      replaceBt.addActionListener(e -> search.replace(
            inputTf.getText(), replaceTf.getText()));

      replaceAllBt.addActionListener(e -> search.replaceAll(
            inputTf.getText().trim(), replaceTf.getText()));
   }
   
   private void enableButtons(boolean b) {
      searchBt.setEnabled(b);
      replaceBt.setEnabled(b);
      replaceAllBt.setEnabled(b);
   }

   DocumentListener docListener = new DocumentListener() {

      @Override
      public void changedUpdate(DocumentEvent documentEvent) {
      }

      @Override
      public void insertUpdate(DocumentEvent documentEvent) {
         boolean enable = inputTf.getText().length() > 0;
         enableButtons(enable);
      }

      @Override
      public void removeUpdate(DocumentEvent documentEvent) {
         boolean enable = inputTf.getText().length() > 0;
         enableButtons(enable);
      }
   };
}
