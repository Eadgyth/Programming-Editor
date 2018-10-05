package eg.edittools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.Constants;
import eg.Languages;
import eg.Edit;
import eg.Formatter;
import eg.FunctionalAction;
import eg.Prefs;
import eg.ui.EditArea;
import eg.ui.menu.FormatMenu;
import eg.document.EditableDocument;
import eg.document.EditingStateReadable;

/**
 * An editor to edit and view text in a separate text area
 */
public class ExchangeEditor implements AddableEditTool {

   private final JPanel content = new JPanel(new BorderLayout());

   private final JMenuItem loadItm      = new JMenuItem("Load file content...");
   private final JMenuItem copyFromItm  = new JMenuItem();
   private final JMenuItem copyToItm    = new JMenuItem();
   private final JMenuItem languageItm  = new JMenuItem();
   private final JMenuItem indentLenItm = new JMenuItem("Indent length");
   private final JMenuItem undoItm      = new JMenuItem();
   private final JMenuItem redoItm      = new JMenuItem();
   private final JMenuItem cutItm       = new JMenuItem();
   private final JMenuItem copyItm      = new JMenuItem();
   private final JMenuItem pasteItm     = new JMenuItem();
   private final JMenuItem indentItm    = new JMenuItem();
   private final JMenuItem outdentItm   = new JMenuItem();
   private final JMenuItem clearItm     = new JMenuItem("Clear");
   private final FormatMenu formatMenu  = new FormatMenu();

   private final Formatter format = new Formatter(1, "Exchg");
   private final Prefs prefs = new Prefs();
   private final JPanel editorPnl;
   private final TextExchange exch;
   private final Edit edit = new Edit();

   private JMenuBar bar;

   public ExchangeEditor() {
      EditArea ea = format.editArea();
      editorPnl = ea.content();
      formatMenu.selectWordWrapItm(ea.isWordwrap());
      EditableDocument ed = new EditableDocument(ea, Languages.NORMAL_TEXT);
      ed.setEditingStateReadable(editReadable);
      String indentUnit = prefs.getProperty("IndentUnit");
      ed.setIndentUnit(indentUnit);
      String recentDir = prefs.getProperty("RecentPath");
      exch = new TextExchange(ed, recentDir);
      edit.setDocument(ed);
      initContentPnl();
   }

   @Override
   public void addClosingAction(JButton closeBt) {
      bar.add(Box.createGlue());
      bar.add(closeBt);
      closeBt.setContentAreaFilled(false);
      closeBt.setBorder(new EmptyBorder(5, 7, 5, 7));
      closeBt.setToolTipText("Close the exchange editor");
      closeBt.setFocusable(false);
   }

   @Override
   public Component toolContent() {
      return content;
   }

   @Override
   public void setEditableDocument(EditableDocument edtDoc) {
      exch.setSourceDocument(edtDoc);
   }

   /**
    * Saves the content in the exchange editor to the file
    * 'exchangeContent.txt' in the program folder and stores
    * formatting preferences
    */
   @Override
   public void end() {
      format.setProperties();
      exch.save();
   }

   //
   //--private--/
   //

   private void initContentPnl() {
      enableUndoRedo(false, false);
      enableCutCopy(false);
      editorPnl.setBorder(Constants.MATTE_TOP_GREY);
      initMenuBar();
      content.add(bar, BorderLayout.NORTH);
      content.add(editorPnl, BorderLayout.CENTER);
      content.setMinimumSize(new Dimension(
            eg.utils.ScreenParams.scaledSize(150), 0));
            
      setActions();
   }

   private void initMenuBar() {
      bar = new JMenuBar();
      bar.setOpaque(false);
      bar.setBorder(null);
      bar.setPreferredSize(new Dimension(0, Constants.BAR_HEIGHT));
      bar.add(textMenu());
      bar.add(adoptMenu());
      bar.add(editMenu());
      bar.add(formatMenu.getMenu());
   }

   private JMenu textMenu() {
      JMenu menu  = new JMenu("Text");
      menu.add(loadItm);
      menu.add(copyFromItm);
      menu.add(copyToItm);
      return menu;
   }

   private JMenu adoptMenu() {
      JMenu menu  = new JMenu("Adopt");
      menu.add(languageItm);
      menu.add(indentLenItm);
      return menu;
   }

   private JMenu editMenu() {
      JMenu menu  = new JMenu("Edit");
      menu.add(undoItm);
      menu.add(redoItm);
      menu.add(cutItm);
      menu.add(copyItm);
      menu.add(pasteItm);
      menu.add(indentItm);
      menu.add(outdentItm);
      menu.add(clearItm);
      return menu;
   }

   private void enableUndoRedo(boolean isUndo, boolean isRedo) {
      undoItm.setEnabled(isUndo);
      redoItm.setEnabled(isRedo);
   }

   private void enableCutCopy(boolean b) {
      cutItm.setEnabled(b);
      copyItm.setEnabled(b);
   }

   private void setActions() {
      loadItm.addActionListener(e -> exch.loadFile());

      copyFromItm.setAction(new FunctionalAction("Copy selection from main editor",
           null, e -> exch.copyTextFromSource()));
      setKeyBinding(copyFromItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_F, ActionEvent.CTRL_MASK), "F_pressed");

      copyToItm.setAction(new FunctionalAction("Copy selection to main editor",
           null, e -> exch.copyTextToSource()));
      setKeyBinding(copyToItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_T, ActionEvent.CTRL_MASK), "T_pressed");

      languageItm.setAction(new FunctionalAction("Language", null,
            e -> exch.adoptLanguage()));
      setKeyBinding(languageItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_G, ActionEvent.CTRL_MASK), "G_pressed");
            
      indentLenItm.addActionListener(e -> exch.adoptIndentUnit());

      undoItm.setAction(new FunctionalAction("Undo", null,
            e -> edit.undo()));
      setKeyBinding(undoItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_Z, ActionEvent.CTRL_MASK), "Z_pressed");

      redoItm.setAction(new FunctionalAction("Redo", null,
            e -> edit.redo()));
      setKeyBinding(redoItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_Y, ActionEvent.CTRL_MASK), "Y_pressed");

      cutItm.setAction(new FunctionalAction("Cut", null,
            e -> edit.cut()));
      setKeyBinding(cutItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_X, ActionEvent.CTRL_MASK), "X_pressed");

      copyItm.setAction(new FunctionalAction("Copy", null,
             e -> edit.setClipboard()));
      setKeyBinding(copyItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_C, ActionEvent.CTRL_MASK), "C_pressed");

      pasteItm.setAction(new FunctionalAction("Paste", null,
            e -> edit.pasteText()));
      setKeyBinding(pasteItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_V, ActionEvent.CTRL_MASK), "V_pressed");

      indentItm.setAction(new FunctionalAction("Increase indentation", null,
            e -> edit.indent()));
      setKeyBinding(indentItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_M, ActionEvent.CTRL_MASK), "M_pressed");

      outdentItm.setAction(new FunctionalAction("Reduce indentation", null,
            e -> edit.outdent()));
      setKeyBinding(outdentItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_L, ActionEvent.CTRL_MASK), "L_pressed");

      clearItm.addActionListener(e -> exch.clear());

      formatMenu.setFontAction(e -> format.openSetFontDialog());
      formatMenu.setChangeWordWrapAct(
            e -> format.enableWordWrap(formatMenu.isWordWrapItmSelected()));
   }

   private void setKeyBinding(JMenuItem itm, KeyStroke ks, String key) {
      itm.getAction().putValue(Action.ACCELERATOR_KEY, ks);
      int isInput = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
      content.getInputMap(isInput).put(ks, key);
      content.getActionMap().put(key, itm.getAction());
   }

   private final EditingStateReadable editReadable = new EditingStateReadable() {

      @Override
      public void updateInChangeState(boolean isChange) {}

      @Override
      public void updateUndoableState(boolean canUndo, boolean canRedo) {
         enableUndoRedo(canUndo, canRedo);
      }

      @Override
      public void updateSelectionState(boolean isSelection) {
         enableCutCopy(isSelection);
      }

      @Override
      public void updateCursorState(int line, int col) {}
   };
}
