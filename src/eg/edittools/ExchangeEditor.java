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

//--Eadgyth--/
import eg.Languages;
import eg.Edit;
import eg.Formatter;
import eg.FunctionalAction;
import eg.Prefs;
import eg.ui.EditArea;
import eg.ui.UIComponents;
import eg.ui.menu.FormatMenu;
import eg.ui.menu.LanguageMenu;
import eg.document.EditableDocument;
import eg.document.EditingStateReadable;
import eg.utils.ScreenParams;

/**
 * An editor panel to edit and view text in a separate text area
 */
public class ExchangeEditor implements AddableEditTool {

   private final JPanel content = new JPanel(new BorderLayout());
   private final JMenuItem loadItm = new JMenuItem("Load file content...");
   private final JMenuItem copyFromItm = new JMenuItem();
   private final JMenuItem copyToItm = new JMenuItem();
   private final JMenuItem adoptLangItm = new JMenuItem();
   private final JMenuItem adoptIndentLenItm = new JMenuItem("Indent length");
   private final JMenuItem undoItm = new JMenuItem();
   private final JMenuItem redoItm = new JMenuItem();
   private final JMenuItem cutItm = new JMenuItem();
   private final JMenuItem copyItm = new JMenuItem();
   private final JMenuItem pasteItm = new JMenuItem();
   private final JMenuItem indentItm = new JMenuItem();
   private final JMenuItem outdentItm = new JMenuItem();
   private final JMenuItem clearItm = new JMenuItem("Clear");
   private final LanguageMenu languageMenu = new LanguageMenu();
   private final FormatMenu formatMenu = new FormatMenu();
   private final JButton closeBt = UIComponents.undecoratedButton();

   private final Formatter format = new Formatter(1, Prefs.EXCHG_PREFIX);
   private final Prefs prefs = new Prefs();
   private final JPanel editorPnl;
   private final TextExchange exch;
   private final Edit edit = new Edit();

   public ExchangeEditor() {
      EditArea ea = format.editArea();
      editorPnl = ea.content();
      initContentPnl();   
      formatMenu.selectWordWrapItm(ea.isWordwrap());   
      Languages lang = Languages.valueOf(
            prefs.property(Prefs.EXCHG_PREFIX + Prefs.LANG_KEY));
      
      languageMenu.selectLanguageItm(lang);          
      EditableDocument edtDoc = new EditableDocument(ea, lang);
      edtDoc.setEditingStateReadable(editReadable);
      String indentUnit = prefs.property(Prefs.INDENT_UNIT_KEY);
      edtDoc.setIndentUnit(indentUnit);
      edit.setDocument(edtDoc);
      String recentDir = prefs.property(Prefs.RECENT_DIR_KEY);
      exch = new TextExchange(edtDoc, recentDir);
   }

   @Override
   public void addClosingAction(FunctionalAction act) {
      closeBt.setAction(act);
   }

   @Override
   public int width() {
      if (content.getWidth() == 0) {
         return ScreenParams.scaledSize(200);
      }
      else {
         return content.getWidth();
      }
   }

   @Override
   public boolean resize() {
      return true;
   }

   @Override
   public Component content() {
      return content;
   }

   @Override
   public void setDocument(EditableDocument edtDoc) {
      exch.setSourceDocument(edtDoc);
   }

   /**
    * Saves the text content in the exchange editor to the file
    * 'exchangeContent.txt' in the program folder and stores
    *  preferences
    */
   @Override
   public void end() {
      format.setProperties();
      prefs.setProperty(Prefs.EXCHG_PREFIX + Prefs.LANG_KEY,
            exch.language().toString());

      exch.save();
   }

   //
   //--private--/
   //

   private void initContentPnl() {
      editorPnl.setBorder(null);
      content.add(menuBar(), BorderLayout.NORTH);
      content.add(editorPnl, BorderLayout.CENTER);
      content.setMinimumSize(new Dimension(ScreenParams.scaledSize(150), 0));
      setActions();
      enableUndoRedo(false, false);
      enableCutCopy(false);
   }

   private JMenuBar menuBar() {
      JMenuBar mb = UIComponents.menuBar();
      mb.add(textMenu());
      mb.add(adoptMenu());
      mb.add(editMenu());
      mb.add(formatMenu.getMenu());
      mb.add(Box.createHorizontalGlue());
      mb.add(closeBt);
      mb.add(Box.createRigidArea(ScreenParams.scaledDimension(5, 0)));
      return mb;
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
      menu.add(adoptLangItm);
      menu.add(adoptIndentLenItm);
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
      menu.addSeparator();
      menu.add(languageMenu.menu());
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
      loadItm.addActionListener(e -> exch.loadFile(languageMenu));

      copyFromItm.setAction(new FunctionalAction("Copy selection from main editor",
           null, e -> exch.copyTextFromSource()));
      setKeyBinding(copyFromItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_F, ActionEvent.CTRL_MASK), "F_pressed");

      copyToItm.setAction(new FunctionalAction("Copy selection to main editor",
           null, e -> exch.copyTextToSource()));
      setKeyBinding(copyToItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_T, ActionEvent.CTRL_MASK), "T_pressed");

      adoptIndentLenItm.addActionListener(
            e -> edit.setIndentUnit(exch.sourceDocIndentUnit()));

      adoptLangItm.setAction(new FunctionalAction("Language", null,
           e -> exch.adoptLanguage(languageMenu)));
      setKeyBinding(adoptLangItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_G, ActionEvent.CTRL_MASK), "G_pressed");

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

      languageMenu.setChangeLanguageActions((l) -> exch.changeLanguage(l));

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
