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
import javax.swing.JScrollPane;

import javax.swing.border.EmptyBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//--Eadgyth--/
import eg.FunctionalAction;
import eg.document.EditableDocument;
import eg.ui.UIComponents;
import eg.utils.ScreenParams;

/**
 * The graphical view for the finding and replacing of text
 */
public class Finder implements AddableEditTool {

   private final JPanel content       = new JPanel(new BorderLayout());
   private final JTextField inputTf   = UIComponents.textField();// new JTextField();
   private final JTextField replaceTf = UIComponents.textField();// new JTextField();
   private final JButton searchBt     = new JButton("Find");
   private final JButton replaceBt    = new JButton("Replace");
   private final JButton replaceAllBt = new JButton("Replace all");
   private final JButton closeBt      = new JButton();

   private final TextSearch search = new TextSearch();

   public Finder() {
      initFinderPnl();
      setActions();
   }

   @Override
   public void addClosingAction(FunctionalAction act) {
      closeBt.setAction(act);
   }

   @Override
   public int width() {
      if (content.getWidth() == 0) {
         return ScreenParams.scaledSize(180); //180 is freely chosen
      }
      else {
         return content.getWidth();
      }
   }

   @Override
   public boolean resize() {
      return false;
   }

   @Override
   public Component content() {
      return content;
   }

   @Override
   public void setDocument(EditableDocument edtDoc) {
      search.setDocument(edtDoc);
   }

   /**
    * Does nothing in this class
    */
   @Override
   public void end() {
	  // not used
   }

   //
   //--private--//
   //

   private void initFinderPnl() {
      enableButtons(false);
      content.add(toolbar(), BorderLayout.NORTH);
      content.add(controlsPnl(), BorderLayout.CENTER);
   }

   private JToolBar toolbar() {
      return UIComponents.toolbar(null, null, closeBt);
   }

   private JScrollPane controlsPnl() {
      JPanel pnl = UIComponents.panel();
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
      pnl.add(labelPnl("Replace with:"));
      setSize(replaceTf);
      pnl.add(replaceTf);
      pnl.add(Box.createVerticalStrut(10));
      pnl.add(buttonsPnl(replaceBt, replaceAllBt));
      pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
      inputTf.getDocument().addDocumentListener(docListener);
      javax.swing.JScrollPane scroll = UIComponents.scrollPane();
      scroll.setViewportView(pnl);
      return scroll;
   }

   private JPanel labelPnl(String text) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.setOpaque(false);
      JLabel lb = UIComponents.label(text);
      pnl.add(lb);
      setSize(pnl);
      return pnl;
   }

   private JPanel radioBtPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.setOpaque(false);
      JRadioButton upBt = UIComponents.radioButton("up", false);
      JRadioButton downBt = UIComponents.radioButton("down", true);
      ButtonGroup group = new ButtonGroup();
      group.add(upBt);
      group.add(downBt);
      pnl.add(upBt);
      pnl.add(downBt);
      pnl.setBorder(UIComponents.titledBorderForBkgr("Search direction"));
      setSize(pnl);
      upBt.addItemListener(e ->
         search.setUpwardSearch(e.getStateChange() == ItemEvent.SELECTED));

      return pnl;
   }

   private JPanel checkBoxPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.setOpaque(false);
      JCheckBox cBxWord = UIComponents.checkBox("Only whole words");
      cBxWord.addItemListener(e ->
         search.setRequireWord(e.getStateChange() == ItemEvent.SELECTED));

      JCheckBox cBxCase = UIComponents.checkBox("Case sensitive");
      cBxCase.addItemListener(e ->
         search.setCaseSensitivity(e.getStateChange() == ItemEvent.SELECTED));

      pnl.add(cBxWord);
      pnl.add(cBxCase);
      pnl.setBorder(UIComponents.titledBorderForBkgr("Search options"));
      setSize(pnl);
      return pnl;
   }

   private JPanel buttonsPnl(JButton... bt) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.setOpaque(false);
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
    	 // not used
      }

      @Override
      public void insertUpdate(DocumentEvent documentEvent) {
         enableButtons(true);
      }

      @Override
      public void removeUpdate(DocumentEvent documentEvent) {
         boolean enable = inputTf.getText().length() > 0;
         enableButtons(enable);
      }
   };
}
