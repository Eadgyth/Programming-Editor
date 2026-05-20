package eg;

import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

//--Eadgyth--/
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;
import eg.utils.FileUtils;
import eg.utils.ScreenParams;
import eg.utils.SystemParams;

/**
 * Contains the main method
 *
 * @author Malte Bussiek, m.bussiek@web.de
 */
public class Eadgyth {

   public static void main(String[] arg) {
      try {
         Class.forName("eg.utils.SystemParams");
      }
      catch (ClassNotFoundException e) {
         throw new IllegalStateException("SystemParams was not found", e);
      }
      Locale.setDefault(Locale.US);
      createEadgythDataDir();
      Prefs prefs = new Prefs();
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
          System.setProperty("sun.java2d.uiScale", "1.0");
      }
      String laf = prefs.property(Prefs.LAF_KEY);
      setLaf(laf);
      uiManagerSettings();

      MainWin mw = new MainWin();
      ViewSettingWin viewSetWin = new ViewSettingWin();
      Formatter f = new Formatter(TabbedDocuments.MAX_TABS, "");
      ViewSetter viewSet = new ViewSetter(mw, viewSetWin, f);
      TabbedDocuments tabDocs = new TabbedDocuments(mw, f);
      mw.setFileActions(tabDocs, tabDocs::setFallbackCharset);
      mw.setViewSettingWinAction(viewSetWin);
      mw.setFormatActions(f);
      viewSetWin.setOkAct(e -> {
         viewSet.applySettings();
         viewSetWin.setVisible(false);
      });
      EventQueue.invokeLater(mw::makeVisible);
   }

   private static void uiManagerSettings() {
      if (SystemParams.IS_JAVA_9_OR_HIGHER) {
         scaleFont("OptionPane.messageFont", 9);
         scaleFont("OptionPane.font", 9);
         scaleFont("Button.font", 8);
         scaleFont("ComboBox.font", 8);
      }
      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
      UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
      UIManager.put("Tree.rowHeight", ScreenParams.scaledSize(13));
      scaleFont("Menu.font", 9);
      scaleFont("CheckBoxMenuItem.font", 9);
      scaleFont("MenuItem.font", 9);
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
      File dir = new File(SystemParams.EADGYTH_DATA_DIR);
      dir.mkdir();
   }

   private static void scaleFont(String uiKey, int size) {
      Font font = UIManager.getFont(uiKey);
      if (font == null) {
         return;
      }
      UIManager.put(uiKey, font.deriveFont((float) ScreenParams.scaledSize(size)));
   }
}
