package eg;

import java.io.File;

import java.util.Locale;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;
import eg.utils.FileUtils;
import eg.utils.SystemParams;
import eg.utils.ScreenParams;

/**
 * Contains the main method
 *
 * @author Malte Bussiek, m.bussiek@web.de
 */
public class Eadgyth {

   public static void main(String[] arg) {
      Locale.setDefault(Locale.US);
      uiManagerSettings();
      createEadgythDataDir();
      Prefs prefs = new Prefs();
      String laf = prefs.property(Prefs.LAF_KEY);
      setLaf(laf);

      MainWin mw = new MainWin();
      ViewSettingWin viewSetWin = new ViewSettingWin();
      Formatter f = new Formatter(15, "");
      ViewSetter viewSet = new ViewSetter(mw, viewSetWin, f);
      TabbedDocuments tabDocs = new TabbedDocuments(mw, f);

      mw.setFileActions(tabDocs);
      mw.setViewSettingWinAction(viewSetWin);
      mw.setFormatActions(f);
      viewSetWin.setOkAct(e -> {
         viewSet.applySettings();
         viewSetWin.setVisible(false);
      });
      EventQueue.invokeLater(() -> {
         mw.makeVisible();
      });
   }

   private static void uiManagerSettings() {
      //
      // The dpi scaling is disabled when the Java version is 9+
      // because the positioning of the caret in the text with the
      // mouse is not working properly on a high dpi screen. The
      // following 'if' statement may be commented out for testing.
      // Then, the methods 'scaledSize' and 'invertedScaledSize'
      // in eg.utils.ScreenParams should be modified as mentioned
      // in the comments there.
      //
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         System.setProperty("sun.java2d.uiScale", "1.0");
         UIManager.put("Button.font", ScreenParams.SANSSERIF_PLAIN_8);
      }
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("Menu.font", ScreenParams.SANSSERIF_PLAIN_9);
      UIManager.put("MenuItem.font", ScreenParams.SANSSERIF_PLAIN_9);
      UIManager.put("CheckBoxMenuItem.font", ScreenParams.SANSSERIF_PLAIN_9);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", ScreenParams.scaledSize(14));
   }

   private static void setLaf(String laf) {
      if (laf.equals("System")) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
         catch (ClassNotFoundException
              | IllegalAccessException
              | InstantiationException
              | UnsupportedLookAndFeelException e) {

            FileUtils.log(e);
         }
      }
   }

   private static void createEadgythDataDir() {
      File newDir = new File(SystemParams.EADGYTH_DATA_DIR);
      newDir.mkdir();
   }
}
