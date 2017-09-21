package eg;

import java .util.Locale;

import java.awt.EventQueue;

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
      ViewSettingWin  viewSetWin = new ViewSettingWin();
      ViewSetter      viewSet    = new ViewSetter(viewSetWin, mw);
      EditAreaFormat  format     = new EditAreaFormat(viewSetWin, mw.menu().formatMenu());
      TabbedFiles     tabFiles   = new TabbedFiles(mw, format);

      mw.registerFileAct(tabFiles);
      mw.menu().editMenu().registerChangeLanguageAct(tabFiles);
      mw.menu().viewMenu().openSettingWinItmAct(e ->
            viewSetWin.makeVisible(true));
      viewSetWin.okAct(e -> {
         format.applySetWinOk();
         viewSet.applySetWinOk();
         viewSetWin.makeVisible(false);
      });

      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         tabFiles.createEmptyTab();
      });
   }
   
   private static void uiManagerSettings() {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Constants.SANSSERIF_PLAIN_9);
      UIManager.put("MenuItem.font", Constants.SANSSERIF_PLAIN_9);
      UIManager.put("CheckBoxMenuItem.font", Constants.SANSSERIF_PLAIN_9);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", (int) (15 * Constants.SCREEN_RES_RATIO));
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
