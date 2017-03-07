package eg;

import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;

import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//--Eadgyth--//
import eg.console.*;
import eg.ui.MainWin;
import eg.ui.Toolbar;
import eg.ui.TabbedPane;
import eg.ui.menu.Menu;
import eg.ui.filetree.FileTree;
import eg.utils.FileUtils;
import eg.plugin.PluginStarter;

/**
 * Contains the main method
 * <p>
 * @version 1.0 beta
 * @author Malte Bussiek, m.bussiek@web.de
 */
public class Eadgyth {

   public static void main(String[] arg) {
      Locale.setDefault(Locale.US);
      uiManagerSettings();
      TabbedPane      tabPane   = new TabbedPane();
      setLaf();
      FileUtils.emptyLog();
      Toolbar         tBar      = new Toolbar();
      ConsolePanel    consPnl   = new ConsolePanel();   
      FileTree        fileTree  = new FileTree();
      Menu            menu      = new Menu();
      MainWin         mw        = new MainWin(menu.menubar(), tBar.toolbar(),
                                      tabPane.tabbedPane(), fileTree.fileTreePnl(),
                                      consPnl.consolePnl());
      DisplaySetter   displSet  = new DisplaySetter(mw, menu, tBar,
                                      fileTree, tabPane);
      ProcessStarter  proc      = new ProcessStarter(consPnl);
      CurrentProject  currProj  = new CurrentProject(displSet, proc, consPnl, fileTree);
      Edit            edit      = new Edit();
      PluginStarter   plugStart = new PluginStarter(mw);
      DocumentUpdate  docUpdate = new DocumentUpdate(displSet, edit, plugStart);
      TabbedFiles     tabFiles  = new TabbedFiles(tabPane, displSet, currProj, docUpdate);

      WindowListener winListener = new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent we) {
            tabFiles.tryExit();
         }
      };
      mw.winListen(winListener);
          
      tBar.registerFileAct(tabFiles);
      tBar.registerProjectAct(currProj);
      tBar.registerEditAct(edit);
      menu.getFileMenu().registerAct(tabFiles);
      menu.getProjectMenu().registerAct(currProj);
      menu.getEditMenu().registerAct(edit, tabFiles);
      menu.getFormatMenu().registerAct(tabFiles, displSet);
      menu.getViewMenu().registerAct(displSet);
      consPnl.closeAct(e -> displSet.setShowConsoleState(false));
      fileTree.addObserver(tabFiles);
      menu.getPluginMenu().startPlugin(plugStart, displSet); 
      
      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         tabFiles.focusInSelectedTab();
      });
   }
   
   private static void setLaf() {
      Preferences prefs = new Preferences();
      prefs.readPrefs();
      if ("System".equals(prefs.getProperty("LaF"))) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } 
         catch (ClassNotFoundException 
              | IllegalAccessException 
              | InstantiationException 
              | UnsupportedLookAndFeelException e) {
            FileUtils.logStack(e);
         }
      }
   }
   
   private static void uiManagerSettings() {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("MenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("CheckBoxMenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", 20);
   } 
}
