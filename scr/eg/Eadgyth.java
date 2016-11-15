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
      
      tBar.registerTabActions(ta);
      tBar.registerEdit(edit);
      menu.registerTabActions(ta);
      menu.registerEdit(edit);
      menu.openViewSettingsAct(e -> viewSet.makeSetWinVisible());
      menu.fontAct(e -> fontSet.makeFontSetWinVisible(true));
      fileTree.closeAct(e -> mw.hideFileView());
      cw.closeAct(e -> mw.hideConsole());
      startPlugin(plugStart, mw, menu);
      
      fileTree.addObserver(ta);
      mw.addFileView(fileTree.fileTreePnl());
      mw.addConsoleView(cw.consolePnl());
      
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
      UIManager.put("Tree.rowHeight", 20);
   }
   
   private static void startPlugin(PluginStarter plugStart, MainWin mw, Menu menu) {
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
   }
}