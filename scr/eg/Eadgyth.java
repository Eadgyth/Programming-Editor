package eg;

import java.io.IOException;

import java.util.Locale;

import javax.swing.UIManager;

import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;

//--Eadgyth--//
import eg.console.*;
import eg.ui.MainWin;
import eg.ui.Menu;
import eg.ui.Toolbar;
import eg.ui.filetree.FileTree;
import eg.projects.ProjectFactory;
import eg.ui.ViewSettings;
import eg.plugin.PluginStarter;
import java.awt.event.ActionEvent;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Contains the main method.
 * <p>
 * @version 1.0 beta
 * @author Malte Bussiek, m.bussiek@web.de
 */
public class Eadgyth {

   public static void main(String[] arg) {

      Locale.setDefault(Locale.US);
      setLaf();
      
      ConsolePanel   cw        = new ConsolePanel();
      ProcessStarter proc      = new ProcessStarter(cw);
      FileTree       fileTree  = new FileTree();
      Menu           menu      = new Menu();
      Toolbar        tBar      = new Toolbar();
      MainWin        mw        = new MainWin(menu, tBar);
      ProjectFactory projFact  = new ProjectFactory(mw, proc, cw);
      PluginStarter  plugStart = new PluginStarter(mw);
      Edit           edit      = new Edit();
      TabActions     ta        = new TabActions(mw, edit, fileTree, projFact,
              plugStart);
      FontSetting    fontSet   = new FontSetting(ta.getTextDocument());
      ViewSettings   viewSet   = new ViewSettings(mw, ta.getTextDocument());
      
      fileTree.addObserver(ta);
      mw.addFileView(fileTree.fileTreePnl());
      mw.addConsoleView(cw.consolePnl());

      registerEditActions(edit, mw, menu, tBar);
      registerTabActions(ta, mw, menu, tBar);
      menu.openViewSettingsAct(e -> viewSet.makeSetWinVisible());
      menu.fontAct(e -> fontSet.makeFontSetWinVisible(true));
      menu.selectPlugAct((ActionEvent e) -> {
         try {
            plugStart.startPlugin(menu.getPluginIndex(e));
            if (!menu.isFunctionPnlSelected()) {
               mw.showFunctionPnl();
            }
         }
         catch (IOException ioe) {
            ioe.printStackTrace();
         }
      });
      fileTree.closeAct(e -> mw.hideFileView());
      cw.closeAct(e -> mw.hideConsole());
      
      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         ta.focusInSelectedTab();
      });
   }
   
   private static void setLaf() {
      Preferences prefs = new Preferences();
      prefs.readPrefs();
      int topTabInset = 0;

      if ("System".equals(prefs.prop.getProperty("LaF"))) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } 
         catch (ClassNotFoundException | IllegalAccessException 
                 | InstantiationException 
                 | UnsupportedLookAndFeelException e) {
            e.getMessage();
         }
      }
      if ("Windows".equals(Constants.CURR_LAF_STR)) {
         topTabInset = -2;
      }
      uiManagerSettings(topTabInset);
   }
   
   private static void uiManagerSettings(int topTabInset) {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("MenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.getDefaults().put("TabbedPane.contentBorderInsets",
            new Insets(topTabInset, 0, 0, 0));
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      if ("Metal".equals(Constants.CURR_LAF_STR)) {
         UIManager.put("TabbedPane.selected", 
               new javax.swing.plaf.ColorUIResource(Color.WHITE));
         UIManager.put("TabbedPane.contentAreaColor",
               new javax.swing.plaf.ColorUIResource(Color.WHITE));
      }
   }
   
   private static void registerEditActions(Edit edit, MainWin mw, Menu menu,
         Toolbar tBar) {
      menu.undoAct(e -> edit.undo());
      tBar.undoAct(e -> edit.undo());
      menu.redoAct(e -> edit.redo());
      tBar.redoAct(e -> edit.redo());
      menu.selectAllAct(e -> edit.selectAll());
      menu.copyAct(e -> edit.setClipboard());  
      menu.pasteAct(e -> edit.pasteText());   
      menu.indentAct(e -> edit.indentSelection());
      tBar.indentAct(e -> edit.indentSelection());   
      menu.outdentAct(e -> edit.outdentSelection());
      tBar.outdentAct(e -> edit.outdentSelection());
      menu.clearSpacesAct(e -> edit.clearSpaces());
      menu.changeIndentAct(e -> edit.setNewIndentUnit());
      menu.languageAct(e -> edit.changeLanguage(menu.getNewLanguage(e)));
   }
   
   private static void registerTabActions(TabActions ta, MainWin mw, Menu menu,
         Toolbar tBar) {
      menu.newFileAct(e -> ta.newEmptyTab());
      menu.openAct(e -> ta.openFileByChooser());
      tBar.openAct(e -> ta.openFileByChooser());
      menu.closeAct(e -> ta.tryClose());
      menu.saveAct(e -> ta.saveOrSaveAs());
      tBar.saveAct(e -> ta.saveOrSaveAs());      
      menu.saveAllAct(e -> ta.saveAll());
      menu.saveAsAct(e -> ta.saveAs());      
      menu.exitAct(e -> ta.tryExit());
      menu.openJavaSetWinAct(e -> ta.openProjectSetWin());
      menu.compileAct(e -> ta.saveAndCompile());
      tBar.compileAct(e -> ta.saveAndCompile());
      menu.runAct(e -> ta.runProj());
      tBar.runAct(e -> ta.runProj());
      menu.buildAct(e -> ta.buildProj());
   }
}