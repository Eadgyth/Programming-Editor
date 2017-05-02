package eg;

import java.util.Locale;

import java.awt.EventQueue;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

//--Eadgyth--//
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;
import eg.ui.menu.Menu;
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
      setLaf();
      FileUtils.emptyLog();

      MainWin         mw         = new MainWin();
      CurrentProject  currProj   = new CurrentProject(mw);
      PluginStarter   plugStart  = new PluginStarter(mw.functionPanel());
      ViewSettingWin  viewSetWin = new ViewSettingWin();
      EditAreaFormat  format     = new EditAreaFormat(viewSetWin, mw.menu().formatMenu());
      ViewSetter      viewSet    = new ViewSetter(viewSetWin, mw);
      Edit            edit       = new Edit();
      DocumentUpdate  docUpdate  = new DocumentUpdate(edit, plugStart);
      TabbedFiles     tabFiles   = new TabbedFiles(mw, format, currProj, docUpdate);

      mw.winListen(tabFiles.closeWindow);
      mw.registerFileAct(tabFiles);
      mw.registerProjectAct(currProj);
      mw.registerEditAct(edit, tabFiles);
      mw.registerPlugAct(plugStart);
      mw.menu().viewMenu().openSettingWinItmAct(e ->
            viewSetWin.makeVisible(true));
      viewSetWin.okAct(e -> {
         format.applySetWinOk();
         viewSet.applySetWinOk();
         viewSetWin.makeVisible(false);
      });

      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         tabFiles.focusInSelectedTab();
      });
   }
   
   private static void uiManagerSettings() {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("MenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("CheckBoxMenuItem.font", Constants.SANSSERIF_PLAIN_12);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", 20);
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
}
