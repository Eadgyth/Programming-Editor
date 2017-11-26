package eg.edittools;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

import java.awt.event.ItemEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JLabel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import eg.document.FileDocument;

//--Eadgyth--/
import eg.Constants;
import eg.utils.UiComponents;

/**
 * The search and replace of text
 */
public class Finder implements AddableEditTool {

   private final JPanel finderPnl     = new JPanel(new BorderLayout());
   private final JTextField inputTf   = new JTextField();
   private final JTextField replaceTf = new JTextField();
   private final JButton searchBt     = new JButton("Find");
   private final JButton replaceBt    = new JButton("Replace");

   private final TextSearch search = new TextSearch();
   
   @Override
   public void createTool(JButton closeBt) {
      initFinderPnl(closeBt);
      setActions();
   }

   @Override
   public Component toolComponent() {
      return finderPnl;
   }
   
   @Override
   public void setFileDocument(FileDocument fDoc) {
      search.setFileDocument(fDoc);
   }
   
   @Override
   public void end() {
      // nothing
   }

   //
   //--private--/
   //

   private void initFinderPnl(JButton closeBt) {
      finderPnl.add(toolbar(closeBt), BorderLayout.NORTH);
      finderPnl.add(controlsPnl(), BorderLayout.CENTER);
   }
   
   private JToolBar toolbar(JButton closeBt) {
      JButton[] bts = new JButton[] {
         closeBt
      };
      String[] toolTips = new String[] {
         "Close Finder"
      };
      JToolBar tb = UiComponents.lastBtRightToolbar(bts, toolTips);
      return tb;
   }
   
   private JPanel controlsPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
      pnl.add(label("Search for:"));
      setSize(inputTf);
      pnl.add(inputTf);
      pnl.add(checkBoxPnl());
      pnl.add(buttonsPnl(searchBt));
      pnl.add(Box.createVerticalStrut(10));
      pnl.add(label("Replace by:"));
      setSize(replaceTf);
      pnl.add(replaceTf);
      pnl.add(buttonsPnl(replaceBt));
      pnl.setBorder(Constants.EMPTY_BORDER);
      inputTf.getDocument().addDocumentListener(docListen);
      return pnl;
   }  

   private JPanel label(String text) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel lb = new JLabel(text);
      pnl.add(lb);
      setSize(pnl);
      return pnl;
   }

   private JPanel checkBoxPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JCheckBox cBx = new JCheckBox("Select only words");
      cBx.setFocusable(false);
      cBx.addItemListener(e ->
         search.setRequireWord(e.getStateChange() == ItemEvent.SELECTED));
      pnl.add(cBx);
      setSize(pnl);
      return pnl;
   }

   private JPanel buttonsPnl(JButton bt) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      bt.setFocusable(false);
      pnl.add(bt);
      setSize(pnl);
      return pnl;
   }

   private void setSize(Component c) {
      Dimension dim = c.getPreferredSize();
      dim.width = Integer.MAX_VALUE;
      c.setMaximumSize(dim);
   }
   
   private void setActions() {
      searchBt.addActionListener(e -> search.searchText(inputTf.getText()));
      inputTf.addActionListener(e -> search.searchText(inputTf.getText()));
      replaceBt.addActionListener(e -> search.replaceSel(replaceTf.getText()));
   }

   DocumentListener docListen = new DocumentListener() {

      @Override
      public void changedUpdate(DocumentEvent documentEvent) {
      }

      @Override
      public void insertUpdate(DocumentEvent documentEvent) {
         search.resetSearchToStart();
      }

      @Override
      public void removeUpdate(DocumentEvent documentEvent) {
         search.resetSearchToStart();
      }
   };
}
