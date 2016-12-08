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
import eg.ui.MainWin;
import eg.ui.menu.Menu;
import eg.ui.Toolbar;
import eg.ui.filetree.FileTree;
import eg.ui.ViewSettings;
import eg.ui.TabbedPane;

import eg.console.*;

import eg.projects.ProjectFactory;

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
      
      ConsolePanel   cw        = new ConsolePanel();   
      FileTree       fileTree  = new FileTree();
      Menu           menu      = new Menu();
      Toolbar        tBar      = new Toolbar();
      TabbedPane     tabPane   = new TabbedPane();
      MainWin        mw        = new MainWin(menu.menubar(), tBar.toolbar(), tabPane.tabbedPane(),
                                     fileTree.fileTreePnl(), cw.consolePnl());
      ViewSettings   viewSet   = new ViewSettings(mw, menu.getViewMenu(), menu.getFormatMenu());

      ProcessStarter proc      = new ProcessStarter(cw);
      ProjectFactory projFact  = new ProjectFactory(viewSet, proc, cw, fileTree);
      CurrentProject currProj  = new CurrentProject(projFact, mw, fileTree, menu, tBar);
      Edit           edit      = new Edit();
      PluginStarter  plugStart = new PluginStarter(mw);
      DocumentUpdate docUpdate = new DocumentUpdate(viewSet, edit, plugStart);
      TabbedFiles    tabFiles  = new TabbedFiles(tabPane, mw, currProj, docUpdate);
      FontSetting    fontSet   = new FontSetting(tabFiles.getEditArea());
      
      // register handlers
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
      menu.getProjectMenu().registerAct(currProj, tabFiles);
      menu.getEditMenu().registerAct(edit);
      menu.getFormatMenu().registerAct(fontSet, viewSet);
      menu.getViewMenu().registerAct(viewSet);
      cw.closeAct(e -> viewSet.setShowConsoleState(false));
      fileTree.closeAct(e -> viewSet.setShowFileViewState(false));
      mw.closeFunctAct(e -> viewSet.setShowFunctionState(false)); 
      fileTree.addObserver(tabFiles);
      menu.getPluginMenu().startPlugin(plugStart, viewSet); 
      
      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         tabFiles.focusInSelectedTab();
      });
   }
   
   private static void setLaf() {
      Preferences prefs = new Preferences();
      prefs.readPrefs();
      if ("System".equals(prefs.prop.getProperty("LaF"))) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } 
         catch (ClassNotFoundException 
              | IllegalAccessException 
              | InstantiationException 
              | UnsupportedLookAndFeelException e) {
            System.out.println(e.getMessage());
         }
      }
      int topTabInset = 0;
      if ("Windows".equals(Constants.CURR_LAF_STR)) {
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
      if ("Metal".equals(Constants.CURR_LAF_STR)) {
         UIManager.put("TabbedPane.selected", 
               new javax.swing.plaf.ColorUIResource(Color.WHITE));
         UIManager.put("TabbedPane.contentAreaColor",
               new javax.swing.plaf.ColorUIResource(Color.WHITE));
      }
      UIManager.put("Tree.rowHeight", 20);
   }
}