package eg.edittools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.InputEvent;
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
import eg.utils.SystemParams;

/**
 * An editor panel to edit and view text in a separate text area
 */
public class ExchangeEditor implements AddableEditTool {

   private final JPanel content = new JPanel(new BorderLayout());
   private final JMenuItem loadItm = new JMenuItem("Load file content ...");
   private final JMenuItem copyFromItm
         = new JMenuItem("Copy selection to from editor");
   private final JMenuItem copyToItm
         = new JMenuItem("Copy selection to main editor");
   private final JMenuItem adoptLangItm = new JMenuItem("Language");
   private final JMenuItem adoptIndentItm = new JMenuItem("Indentation settings");
   private final JMenuItem undoItm = new JMenuItem();
   private final JMenuItem redoItm = new JMenuItem();
   private final JMenuItem cutItm = new JMenuItem();
   private final JMenuItem copyItm = new JMenuItem();
   private final JMenuItem pasteItm = new JMenuItem();
   private final JMenuItem selectAllItm = new JMenuItem();
   private final JMenuItem selectLineItm = new JMenuItem();
   private final JMenuItem selectLineTextItm = new JMenuItem();
   private final JMenuItem selectLineFromCursorItm = new JMenuItem();
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
   private final Edit edit = new Edit(false);

   public ExchangeEditor() {
      EditArea ea = format.editArea();
      editorPnl = ea.content();
      formatMenu.selectWordWrapItm(ea.isWordwrap());
      Languages lang = Languages.valueOf(
            prefs.property(Prefs.EXCHG_PREFIX + Prefs.LANG_KEY));

      languageMenu.selectLanguageItm(lang);
      EditableDocument edtDoc = new EditableDocument(ea, lang);
      edtDoc.setEditingStateReadable(editReadable);
      edit.setDocument(edtDoc);
      String recentDir = prefs.property(Prefs.RECENT_DIR_KEY);
      exch = new TextExchange(edtDoc, recentDir);
      setActions();
      initContentPnl();
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
      format.storeProperties();
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
      menu.add(adoptIndentItm);
      return menu;
   }

   private JMenu editMenu() {
      JMenu menu = new JMenu("Edit");
      menu.add(undoItm);
      menu.add(redoItm);
      menu.addSeparator();
      menu.add(cutItm);
      menu.add(copyItm);
      menu.add(pasteItm);
      menu.addSeparator();
      JMenu selectMenu = new JMenu("Select");
      menu.add(selectMenu);
      selectMenu.add(selectAllItm);
      selectMenu.add(selectLineItm);
      selectMenu.add(selectLineTextItm);
      selectMenu.add(selectLineFromCursorItm);
      menu.addSeparator();
      JMenu indentMenu = new JMenu("Indentation");
      menu.add(indentMenu);
      indentMenu.add(indentItm);
      indentMenu.add(outdentItm);
      menu.add(clearItm);
      menu.addSeparator();
      menu.add(languageMenu.menu());
      return menu;
   }

   private void enableUndoRedo(boolean isUndo, boolean isRedo) {
      undoItm.getAction().setEnabled(isUndo);
      redoItm.getAction().setEnabled(isRedo);
   }

   private void enableCutCopy(boolean b) {
      cutItm.getAction().setEnabled(b);
      copyItm.getAction().setEnabled(b);
   }

   private void setActions() {
      loadItm.addActionListener(e -> exch.loadFile(languageMenu));

      copyFromItm.addActionListener(e -> exch.copyTextFromSource());
      copyFromItm.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_F, SystemParams.MODIFIER_MASK));

      copyToItm.addActionListener(e -> exch.copyTextToSource());
      copyToItm.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_T, SystemParams.MODIFIER_MASK));

      adoptIndentItm.addActionListener(e -> edit.changeIndentationMode(
            exch.sourceDocIndentUnit(), exch.sourceDocIndentTab()));

      adoptLangItm.addActionListener(e -> exch.adoptLanguage(languageMenu));
      adoptLangItm.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_G, SystemParams.MODIFIER_MASK));

      undoItm.setAction(edit.undoAction());
      pseudoShortCut(undoItm,
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, SystemParams.MODIFIER_MASK));

      redoItm.setAction(edit.redoAction());
      pseudoShortCut(redoItm,
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, SystemParams.MODIFIER_MASK));

      cutItm.setAction(new FunctionalAction("Cut", null,
            e -> edit.cut()));
      setKeyBinding(cutItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_X, SystemParams.MODIFIER_MASK), "X_pressed");

      copyItm.setAction(new FunctionalAction("Copy", null,
             e -> edit.setClipboard()));
      setKeyBinding(copyItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_C, SystemParams.MODIFIER_MASK), "C_pressed");

      pasteItm.setAction(edit.pasteAction());
      pseudoShortCut(pasteItm,
            KeyStroke.getKeyStroke(KeyEvent.VK_V, SystemParams.MODIFIER_MASK));

      selectAllItm.setAction(new FunctionalAction("All", null,
            e -> edit.selectAll()));
      setKeyBinding(selectAllItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_A, SystemParams.MODIFIER_MASK), "A_pressed");

      selectLineItm.setAction(new FunctionalAction("Line", null,
            e -> edit.selectLine()));
      setKeyBinding(selectLineItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_L, SystemParams.MODIFIER_MASK), "L_pressed");

      selectLineTextItm.setAction(new FunctionalAction(
            "Line from beginning of text", null, e -> edit.selectLineText()));
      setKeyBinding(selectLineTextItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_B, SystemParams.MODIFIER_MASK), "B_pressed");

      selectLineFromCursorItm.setAction(new FunctionalAction(
            "Line from cursor postion", null, e -> edit.selectLineFromCursor()));
      setKeyBinding(selectLineFromCursorItm, KeyStroke.getKeyStroke(
            KeyEvent.VK_I, SystemParams.MODIFIER_MASK), "I_pressed");

      indentItm.setAction(edit.indentAction());
      pseudoShortCut(indentItm,
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));

      outdentItm.setAction(edit.outdentAction());
      pseudoShortCut(outdentItm,
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK));

      clearItm.addActionListener(e -> exch.clear());
      languageMenu.setChangeLanguageActions(exch::changeLanguage);
      formatMenu.setFontAction(e -> format.openSetFontDialog());
      formatMenu.setChangeWordWrapAct(
            e -> format.enableWordWrap(formatMenu.isWordWrapItmSelected()));
   }

   private void setKeyBinding(JMenuItem itm, KeyStroke ks, String key) {
      itm.getAction().putValue(Action.ACCELERATOR_KEY, ks);
      content.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ks, key);
      content.getActionMap().put(key, itm.getAction());
   }

   private void pseudoShortCut(JMenuItem itm, KeyStroke ks) {
      itm.setAccelerator(ks);
      itm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "none");
   }

   private final EditingStateReadable editReadable = new EditingStateReadable() {

      @Override
      public void updateChangedState(boolean isChange) {
    	   // not used
      }

      @Override
      public void updateUndoableState(boolean canUndo, boolean canRedo) {
         enableUndoRedo(canUndo, canRedo);
      }

      @Override
      public void updateSelectionState(boolean isSelection) {
         enableCutCopy(isSelection);
      }

      @Override
      public void updateCursorState(int line, int col) {
    	   // not used
      }
   };
}
