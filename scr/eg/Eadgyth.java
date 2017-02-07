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
      setLaf();
      FileUtils.emptyLog();
      
      ConsolePanel    consPnl   = new ConsolePanel();   
      FileTree        fileTree  = new FileTree();
      Menu            menu      = new Menu();
      Toolbar         tBar      = new Toolbar();
      TabbedPane      tabPane   = new TabbedPane();
      MainWin         mw        = new MainWin(menu.menubar(), tBar.toolbar(),
                                      tabPane.tabbedPane(), fileTree.fileTreePnl(),
                                      consPnl.consolePnl());
      DisplaySetter   displSet  = new DisplaySetter(mw, menu, tBar);
      ProcessStarter  proc      = new ProcessStarter(consPnl);
      CurrentProject  currProj  = new CurrentProject(displSet, proc, consPnl, fileTree);
      Edit            edit      = new Edit();
      PluginStarter   plugStart = new PluginStarter(mw);
      DocumentUpdate  docUpdate = new DocumentUpdate(displSet, edit, plugStart);
      TabbedFiles     tabFiles  = new TabbedFiles(tabPane, mw, currProj, docUpdate);
      FontSetter      fontSet   = new FontSetter(tabFiles.getEditArea());

      WindowListener winListener = new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent we) {
            tabFiles.tryExit();
         }
      };
      mw.winListen(winListener);     
      tBar.registerFileActions(tabFiles);
      tBar.registerProjectActions(currProj);
      tBar.registerEdit(edit);
      menu.getFileMenu().registerAct(tabFiles);
      menu.getProjectMenu().registerAct(currProj);
      menu.getEditMenu().registerAct(edit);
      menu.getFormatMenu().registerAct(fontSet, displSet);
      menu.getViewMenu().registerAct(displSet);
      consPnl.closeAct(e -> displSet.setShowConsoleState(false));
      fileTree.closeAct(e -> displSet.setShowFileViewState(false));
      mw.closeFunctPnlAct(e -> displSet.setShowFunctionState(false)); 
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
      int topTabInset = 0;
      if ("Windows".equals(UIManager.getLookAndFeel().getName())) {
         topTabInset = -2;
      }
      uiManagerSettings(topTabInset);
   }
   
   private static void uiManagerSettings(int topTabInset) {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("MenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("CheckBoxMenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.getDefaults().put("TabbedPane.contentBorderInsets",
            new Insets(topTabInset, 0, 0, 0));
      if ("Metal".equals(UIManager.getLookAndFeel().getName())) {
         UIManager.put("TabbedPane.selected", 
               new javax.swing.plaf.ColorUIResource(Color.WHITE));
         UIManager.put("TabbedPane.contentAreaColor",
               new javax.swing.plaf.ColorUIResource(Color.WHITE));
      }
      UIManager.put("Tree.rowHeight", 20);
   }
}
