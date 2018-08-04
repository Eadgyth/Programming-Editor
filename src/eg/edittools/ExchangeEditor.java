package eg.edittools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

//--Eadgyth--/
import eg.Constants;
import eg.Languages;
import eg.Edit;
import eg.FunctionalAction;
import eg.ui.EditArea;
import eg.ui.IconFiles;
import eg.document.EditableDocument;
import eg.document.EditingStateReadable;
import eg.utils.UIComponents;

/**
 * The <code>AddableEditTool</code> for the editing and viewing of text
 * in a separate text area
 */
public class ExchangeEditor implements AddableEditTool {

   private final JPanel exchPnl     = new JPanel(new BorderLayout());
   private final JButton loadBt     = new JButton("Load file...");
   private final JButton copyFromBt = new JButton("Copy from ");
   private final JButton copyToBt   = new JButton("Copy to");
   private final JButton undoBt     = new JButton();
   private final JButton redoBt     = new JButton();
   private final JButton cutBt      = new JButton();
   private final JButton copyBt     = new JButton();
   private final JButton pasteBt    = new JButton();
   private final JButton indentBt   = new JButton();
   private final JButton outdentBt  = new JButton();
   private final JButton clearBt    = new JButton(IconFiles.CLEAR_ICON);

   private final JPanel editAreaPnl;
   private final TextExchange exch;
   private final Edit edit = new Edit();

   public ExchangeEditor() {
      EditArea ea = new EditArea(false, false, "Consolas", 8);
      editAreaPnl = ea.editAreaPnl();
      EditableDocument ed = new EditableDocument(ea, Languages.NORMAL_TEXT);
      ed.setEditingStateReadable(editReadable);
      exch = new TextExchange(ed);
      edit.setDocument(ed);
      initExchangePnl();
   }

   @Override
   public void addClosingAction(JButton closeBt) {
       exchPnl.add(closingToolbar(closeBt), BorderLayout.NORTH);
   }

   @Override
   public Component toolComponent() {
      exch.setBackupText();
      return exchPnl;
   }

   @Override
   public void setEditableDocument(EditableDocument edtDoc) {
      exch.setSourceDocument(edtDoc);
   }

   /**
    * Saves the content in the exchange editor to the file
    * 'exchangeContent.txt' in the program folder
    */
   @Override
   public void end() {
      exch.save();
   }

   //
   //--private--/
   //

   private void initExchangePnl() {
      setBtnActions();
      enableUndoRedo(false, false);
      enableCutCopy(false);
      editAreaPnl.setBorder(Constants.MATTE_TOP_BOTTOM);
      exchPnl.add(editAreaPnl, BorderLayout.CENTER);
      JPanel pnl = new JPanel(new BorderLayout());
      pnl.add(editToolbar(), BorderLayout.CENTER);
      pnl.add(controlsPnl(), BorderLayout.SOUTH);
      exchPnl.add(pnl, BorderLayout.SOUTH);
   }

   private JToolBar closingToolbar(JButton closeBt) {
      JButton[] bts = new JButton[] {
         closeBt
      };
      String[] toolTips = new String[] {
         "Close the exchange editor"
      };
      JToolBar tb = UIComponents.lastBtRightToolbar(bts, toolTips);
      Dimension dim = new Dimension(0, Constants.BAR_HEIGHT);
      tb.setPreferredSize(dim);
      return tb;
   }
   
   private JToolBar editToolbar() {
      JButton[] bts = new JButton[] {
         undoBt, redoBt, cutBt, copyBt, pasteBt,
         indentBt, outdentBt, clearBt
      };
      String[] toolTips = new String[] {
         "Undo (Ctrl+Z)", "Redo (Ctrl+Y)",
         "Cut selection (Ctrl+X)",
         "Copy (Ctrl+C)", "Paste (Ctrl+P)",
         "Increase indentation(Ctrl+M)", "Reduce indentation (Ctrl+L)",
         "Clear the text area",
      };
      JToolBar tb = UIComponents.toolbar(bts, toolTips);
      return tb;
   }       

   private JPanel controlsPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
      pnl.add(setTextToolbar());
      pnl.add(setLangBox());
      return pnl;
   }
   
   private JToolBar setTextToolbar() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      JButton[] bts = new JButton[] {
         loadBt, copyFromBt, copyToBt
      };
      String[] toolTips = new String[] {
         "Load file content",
         "Copy text from the document in main editor",
         "Copy text to the document in main editor",
      };
      JToolBar tb = UIComponents.toolbar(bts, toolTips);
      return tb;
   }

   private JPanel setLangBox() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
      String[] opt = new String[Languages.values().length];
      for (int i = 0; i < opt.length; i++) {
         opt[i] = Languages.values()[i].display();
      }
      JComboBox<String> cb = new JComboBox<>(opt);
      cb.addItemListener(ie -> {
         if (ie.getStateChange() == ItemEvent.SELECTED) {
            exch.changeCodeEditing(Languages.values()[cb.getSelectedIndex()]);
         }
      });
      cb.setFocusable(false);
      pnl.add(cb);
      return pnl;
   }
   
   private void enableUndoRedo(boolean isUndo, boolean isRedo) {
      undoBt.setEnabled(isUndo);
      redoBt.setEnabled(isRedo);
   }

   private void enableCutCopy(boolean b) {
      cutBt.setEnabled(b);
      copyBt.setEnabled(b);
   }

   private void setBtnActions() {
      copyFromBt.addActionListener(e -> exch.copyTextFromSource());
      copyToBt.addActionListener(e -> exch.copyTextToSource());

      undoBt.setAction(new FunctionalAction("", IconFiles.UNDO_ICON,
            e -> edit.undo()));
      setKeyBinding(undoBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_Z, ActionEvent.CTRL_MASK), "Z_pressed");

      redoBt.setAction(new FunctionalAction("", IconFiles.REDO_ICON,
            e -> edit.redo()));
      setKeyBinding(redoBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_Y, ActionEvent.CTRL_MASK), "Y_pressed");

      cutBt.setAction(new FunctionalAction("", IconFiles.CUT_ICON,
            e -> edit.cut()));
      setKeyBinding(cutBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_X, ActionEvent.CTRL_MASK), "X_pressed");

      copyBt.setAction(new FunctionalAction("", IconFiles.COPY_ICON,
            e -> edit.setClipboard()));
      setKeyBinding(copyBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_C, ActionEvent.CTRL_MASK), "C_pressed");

      pasteBt.setAction(new FunctionalAction(null, IconFiles.PASTE_ICON,
            e -> edit.pasteText()));
      setKeyBinding(pasteBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_V, ActionEvent.CTRL_MASK), "V_pressed");

      indentBt.setAction(new FunctionalAction(null, IconFiles.INDENT_ICON,
            e -> edit.indent()));
      setKeyBinding(indentBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_M, ActionEvent.CTRL_MASK), "M_pressed");

      outdentBt.setAction(new FunctionalAction(null, IconFiles.OUTDENT_ICON,
            e -> edit.outdent()));
      setKeyBinding(outdentBt, KeyStroke.getKeyStroke(
            KeyEvent.VK_L, ActionEvent.CTRL_MASK), "L_pressed");

      clearBt.addActionListener(e -> exch.clear());
      
      loadBt.addActionListener(e -> exch.loadFile());
   }

   private void setKeyBinding(JButton bt, KeyStroke ks, String key) {
      int isInput = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
      exchPnl.getInputMap(isInput).put(ks, key);
      exchPnl.getActionMap().put(key, bt.getAction());
   }

   private final EditingStateReadable editReadable = new EditingStateReadable() {

      @Override
      public void setInChangeState(boolean isChange) {}

      @Override
      public void setUndoableState(boolean canUndo, boolean canRedo) {
         enableUndoRedo(canUndo, canRedo);
      }

      @Override
      public void setSelectionState(boolean isSelection) {
         enableCutCopy(isSelection);
      }

      @Override
      public void setCursorPosition(int line, int col) {}
   };
}
