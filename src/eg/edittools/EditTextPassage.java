package eg.edittools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//--Eadgyth--/
import eg.ui.ToolPanel;
import eg.document.FileDocument;
import eg.utils.Dialogs;
import eg.Constants;
import eg.Preferences;

/**
 * The editing of a text passage in a separate text area
 * This version is a draft
 */
public class EditTextPassage implements AddableEditTool {
   
   private final JPanel editPnl = new JPanel(new BorderLayout());
   private final JTextPane textArea = new JTextPane();
   private final JPanel scrolledPnl = new JPanel(new BorderLayout());
   private final JScrollPane scroll = new JScrollPane(
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   private final JButton setTextBt    = new JButton("Fetch selected text");
   private final JButton insertTextBt = new JButton("Insert");
   private final Preferences prefs = new Preferences();
   private FileDocument fDoc;
   private JTextPane docTextArea;
   
   public EditTextPassage() {
      initPnl();
   }   
   
   @Override
   public void setFileDocument(FileDocument fDoc) {
      this.fDoc  = fDoc;
      this.docTextArea = fDoc.docTextArea();
   }

   @Override
   public void addComponent(ToolPanel toolPnl) {
      toolPnl.addComponent(editPnl, "Edit text passage");
   }
   
   //
   //--private--/
   //
   
   private void setTextFromDoc() {
      String text = docTextArea.getSelectedText();
      textArea.setText(text);  
   }
   
   private void replaceTextInDoc() {
      String text = textArea.getText();
      String sel = docTextArea.getSelectedText();
      int pos = docTextArea.getSelectionStart();
      fDoc.enableCodeEditing(false);
      if (sel != null) {
         fDoc.remove(pos, sel.length());
      }
      EventQueue.invokeLater(() -> {
         fDoc.insert(pos, text);
         fDoc.colorSection(text, pos);
         fDoc.enableCodeEditing(true);
      });
   }

   private void initPnl() {
      textArea.setBorder(Constants.EMPTY_BORDER);
      textArea.setFont(Constants.CONSOLAS_PLAIN_9);
      scrolledPnl.add(textArea, BorderLayout.CENTER);
      scroll.setViewportView(scrolledPnl);
      scroll.getVerticalScrollBar().setUnitIncrement(15);
      scroll.setBorder(null);
      editPnl.add(scroll, BorderLayout.CENTER);
      editPnl.add(buttonPnl(), BorderLayout.SOUTH);
   }
   
   private void setSize(JScrollPane pnl) {
      Dimension dim = pnl.getPreferredSize();
      dim.width = Integer.MAX_VALUE;
      pnl.setMaximumSize(dim);
   }
   
   private JPanel buttonPnl() {
      JPanel pnl = new JPanel(new FlowLayout());
      pnl.add(setTextBt);
      setTextBt.addActionListener(e -> setTextFromDoc());
      pnl.add(insertTextBt);
      insertTextBt.addActionListener(e -> replaceTextInDoc());
      return pnl;
   }
}
