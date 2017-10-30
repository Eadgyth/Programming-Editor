package eg.edittools;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

//--Eadgyth--/
import eg.ui.ToolPanel;
import eg.document.FileDocument;
import eg.utils.Dialogs;
import eg.Constants;

/**
 * The search and replace of text
 */
public class Finder implements AddableEditTool {

   private final JTextField inputTf     = new JTextField();
   private final JTextField replInputTf = new JTextField();
   private final JButton searchBt       = new JButton("Find");
   private final JButton replBt         = new JButton("Replace");

   private JPanel mainPnl = null;
   private boolean constrainWord = false;
   private int index = -1;

   private FileDocument fDoc;
   private JTextPane textArea;

   public Finder() {
      if (mainPnl == null) {
         setupMainPnl();
      }
   }

   @Override
   public void setFileDocument(FileDocument fDoc) {
      this.fDoc  = fDoc;
      this.textArea = fDoc.docTextArea();
   }

   @Override
   public void addComponent(ToolPanel toolPnl) {
      toolPnl.addComponent(mainPnl, "Find");
   }

   //
   //--private--/
   //

   private void searchText() {
      int caret = textArea.getCaretPosition();
      textArea.setSelectionStart(caret);
      textArea.setSelectionEnd(caret);
      String toSearch = inputTf.getText();;
      String content = fDoc.getText();
      boolean notFound = false;
      int ind = 0;
      int nextStep = 0;
      if (index > -1) {
         nextStep = 1;
      }
      ind = nextIndex(content, toSearch, index + nextStep);
      /*
       * go back to start if last match is reached */
      if (ind == -1 & index > -1) {
         index = 0;
         nextStep = 0;
         ind = nextIndex(content, toSearch, index + nextStep);
      }
      if (ind != -1) {
         index = ind;
         fDoc.requestFocus();
         textArea.select(index, index + toSearch.length());
      }
      else {
         Dialogs.infoMessage(toSearch + " could not be found", null);
         fDoc.requestFocus();
         index = -1;
      }
   }

   private int nextIndex(String content, String toSearch, int pos) {
      if (constrainWord) {
         return findWordIndex(content, toSearch, pos);
      }
      else {
         return content.indexOf(toSearch, pos);
      }
   }

   private int findWordIndex(String content, String toSearch, int pos) {
      int result = -1;
      int ind = 0;
      int nextStep = 0;
      while (ind != -1) {
         ind = content.indexOf(toSearch, pos + ind + nextStep);
         if (ind != -1 & isWord(content, toSearch, ind)) {
            result = ind;
            ind = -1;
         }
         nextStep = 1;
      }
      return result;
   }

   private boolean isWord(String content, String toSearch, int pos) {
      return eg.syntax.SyntaxUtils.isWord(content, pos, toSearch.length());
   }

   private void replaceSel() {
      String replaceBy = replInputTf.getText();
      if (index != -1 && textArea.getSelectedText() != null) {
         textArea.replaceSelection(replaceBy);
      }
   }

   private void setupMainPnl() {
      mainPnl = new JPanel();
      mainPnl.setLayout(new BoxLayout(mainPnl, BoxLayout.PAGE_AXIS));
      mainPnl.add(label("Search for:"));
      setSize(inputTf);
      mainPnl.add(inputTf);
      mainPnl.add(constrainWordBxPnl());
      mainPnl.add(buttonsPnl(searchBt));
      mainPnl.add(Box.createVerticalStrut(10));
      mainPnl.add(label("Replace by:"));
      setSize(replInputTf);
      mainPnl.add(replInputTf);
      mainPnl.add(buttonsPnl(replBt));
      mainPnl.setBorder(Constants.EMPTY_BORDER);
      inputTf.getDocument().addDocumentListener(docListen);
      searchBt.addActionListener(e -> searchText());
      replBt.addActionListener(e -> replaceSel());
   }

   private JPanel label(String text) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel lb = new JLabel(text);
      pnl.add(lb);
      setSize(pnl);
      return pnl;
   }

   private JPanel constrainWordBxPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JCheckBox cBx = new JCheckBox("Select only words");
      cBx.addItemListener(e -> {
         constrainWord = e.getStateChange() == ItemEvent.SELECTED
               ? true : false;
      });
      pnl.add(cBx);
      setSize(pnl);
      return pnl;
   }

   private JPanel buttonsPnl(JButton bt) {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pnl.add(bt);
      setSize(pnl);
      return pnl;
   }

   private void setSize(Component c) {
      Dimension dim = c.getPreferredSize();
      dim.width = Integer.MAX_VALUE;
      c.setMaximumSize(dim);
   }

   DocumentListener docListen = new DocumentListener() {
      public void changedUpdate(DocumentEvent documentEvent) {
      }
      public void insertUpdate(DocumentEvent documentEvent) {
         index = -1;
      }
      public void removeUpdate(DocumentEvent documentEvent) {
         index = -1;
      }
   };
}
