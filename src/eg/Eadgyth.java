package eg;

import java.util.Locale;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;
import eg.utils.FileUtils;

/**
 * Contains the main method
 * <p>
 * @author Malte Bussiek, m.bussiek@web.de
 */
public class Eadgyth {

   public static void main(String[] arg) {
      Locale.setDefault(Locale.US);
      uiManagerSettings();   
      Prefs prefs = new Prefs();
      prefs.load();
      String laf = prefs.getProperty("LaF");
      setLaf(laf);

      MainWin mw = new MainWin();
      ViewSettingWin viewSetWin = new ViewSettingWin();
      Formatter format = new Formatter(15, "");
      ViewSetter viewSet = new ViewSetter(mw, viewSetWin, format);
      TabbedDocuments tabDocs = new TabbedDocuments(mw, format);

      mw.setFileActions(tabDocs);
      mw.setViewSettingWinAction(viewSetWin);
      mw.setFormatActions(format);
      viewSetWin.setOkAct(e -> {
         viewSet.applySettings();
         viewSetWin.setVisible(false);
      });
      EventQueue.invokeLater(() -> {
         mw.makeVisible();
         tabDocs.createBlankDocument();
      });
   }

   private static void uiManagerSettings() {
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", Constants.SANSSERIF_PLAIN_9);
      UIManager.put("MenuItem.font", Constants.SANSSERIF_PLAIN_9);
      UIManager.put("CheckBoxMenuItem.font", Constants.SANSSERIF_PLAIN_9);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", eg.utils.ScreenParams.scaledSize(14));
   }

   private static void setLaf(String laf) {
      if (laf.length() > 0 && laf.equals("System")) {
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
