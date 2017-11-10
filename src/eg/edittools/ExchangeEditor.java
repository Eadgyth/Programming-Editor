package eg.edittools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

//--Eadgyth--/
import eg.Constants;
import eg.Languages;
import eg.Edit;
import eg.FunctionalAction;
import eg.ui.EditArea;
import eg.ui.IconFiles;
import eg.document.FileDocument;
import eg.utils.UiComponents;

/**
 * The editing of text taken from a file document in a separate text area.
 * The font is fixed (Consolas)
 */
public class ExchangeEditor implements AddableEditTool {

   private final JPanel exchPnl       = new JPanel(new BorderLayout());
   private final JButton setTextBt    = new JButton("Fetch");
   private final JButton insertTextBt = new JButton("Insert");
   private final JButton takeLangBt   = new JButton("Adopt code editing");
   private final JButton undoBt       = new JButton();
   private final JButton redoBt       = new JButton();
   private final JButton cutBt        = new JButton();
   private final JButton copyBt       = new JButton();
   private final JButton pasteBt      = new JButton();
   private final JButton indentBt     = new JButton();
   private final JButton outdentBt    = new JButton();
   private final JButton clearBt      = new JButton(IconFiles.CLEAR_ICON);

   private final JPanel editAreaPnl;
   private final TextExchange exch;
   private final Edit edit = new Edit();

   public ExchangeEditor() {
      EditArea ea = new EditArea(false, false, "Consolas", 9);
      editAreaPnl = ea.editAreaPnl();
      FileDocument fd = new FileDocument(ea, Languages.NORMAL_TEXT);
      exch = new TextExchange(fd);
      fd.setUndoableChangeListener(e ->
             enableUndoRedo(e.canUndo(), e.canRedo()));
      fd.setTextSelectionListener(e ->
             enableCutCopy(e.isSelection()));
      edit.setFileDocument(fd);
   }

   @Override
   public void createToolPanel(JButton closeBt) {
      initExchangePnl(closeBt);
   }

   @Override
   public Component toolComponent() {
      return exchPnl;
   }

   @Override
   public void setFileDocument(FileDocument fDoc) {
      exch.setSourceDocument(fDoc);
   }

   @Override
   public void end() {
      exch.save();
   }

   //
   //--private--/
   //

   private void enableUndoRedo(boolean isUndo, boolean isRedo) {
      undoBt.setEnabled(isUndo);
      redoBt.setEnabled(isRedo);
   }

   private void enableCutCopy(boolean b) {
      cutBt.setEnabled(b);
      copyBt.setEnabled(b);
   }

   private void initExchangePnl(JButton closeBt) {
      editAreaPnl.setBorder(Constants.MATTE_TOP);
      exchPnl.add(toolbar(closeBt), BorderLayout.NORTH);
      exchPnl.add(editAreaPnl, BorderLayout.CENTER);
      exchPnl.add(controlsPnl(), BorderLayout.SOUTH);
      setBtnActions();
      enableUndoRedo(false, false);
      enableCutCopy(false);
   }

   private JToolBar toolbar(JButton closeBt) {
      JButton[] bts = new JButton[] {
         undoBt, redoBt, cutBt, copyBt, pasteBt,
         indentBt, outdentBt, clearBt, closeBt
      };
      String[] toolTips = new String[] {
         "Undo", "Redo", "Cut selection", "Copy selection", "Paste",
         "Increase indentation", "Reduce indentation",
         "Clear the text area", "Close the exchange editor"
      };
      undoBt.setEnabled(false);
      redoBt.setEnabled(false);
      cutBt.setEnabled(false);
      copyBt.setEnabled(false);
      JToolBar tb = UiComponents.lastBtRightToolbar(bts, toolTips);
      return tb;
   }

   private JPanel controlsPnl() {
      JPanel pnl = new JPanel(new GridLayout(2, 1));
      pnl.add(buttonPnl());
      pnl.add(checkBoxPnl());
      return pnl;
   }

   private JPanel buttonPnl() {
      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      JButton[] bts = new JButton[] {
         setTextBt, insertTextBt, takeLangBt
      };
      String[] toolTips = new String[] {
         "Fetch text that is selected in the selected file",
         "Insert or replace selected text in the selected file",
         "Adopt language and indentation from the selected file"
      };
      for (int i = 0; i < bts.length; i++) {
         bts[i].setFocusable(false);
         bts[i].setToolTipText(toolTips[i]);
         pnl.add(bts[i]);
      }
      return pnl;
   }

   private JPanel checkBoxPnl() {
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
      JCheckBox replBx = new JCheckBox(
            "Replace fetched text");
      replBx.setFocusable(false);
      replBx.addItemListener(e ->
         exch.setReplace(e.getStateChange() == ItemEvent.SELECTED));
      pnl.add(replBx);
      JCheckBox selectBx = new JCheckBox(
            "Insert only selected text");
      selectBx.setFocusable(false);
      selectBx.addItemListener(e ->
         exch.setInsertSelection(e.getStateChange() == ItemEvent.SELECTED));
      pnl.add(selectBx);
      JPanel holdPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
      holdPnl.add(pnl);
      return holdPnl;
   }

   private void setBtnActions() {
      setTextBt.addActionListener(e -> exch.setTextFromDoc());
      insertTextBt.addActionListener(e -> exch.replaceTextInDoc());
      takeLangBt.addActionListener(e -> exch.changeCodeEditing());

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

      copyBt.setAction(new FunctionalAction(null, IconFiles.COPY_ICON,
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
   }

   private void setKeyBinding(JButton bt, KeyStroke ks, String key) {
      int isInput = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
      exchPnl.getInputMap(isInput).put(ks, key);
      exchPnl.getActionMap().put(key, bt.getAction());
   }
}
