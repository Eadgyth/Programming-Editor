package eg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//--Eadgyth--/
import eg.ui.MainWin;
import eg.ui.ViewSettingWin;

/**
 * The applying of view settings made in <code>ViewSettingsWin</code>
 */
public class ViewSetter {

   private final MainWin mw;
   private final ViewSettingWin vsw;
   private final Formatter f;
   private final Prefs prefs = new Prefs();

   private boolean isShowLineNumbers;
   private boolean isShowToolbar;
   private boolean isShowStatusbar;
   private int backgroundIndex;
   private int iconSizeIndex;
   private int lafIndex;

   /**
    * @param mw  the reference to MainWin
    * @param vsw  the reference to ViewSettingWin
    * @param f  the reference to Formatter
    */
   public ViewSetter(MainWin mw, ViewSettingWin vsw, Formatter f) {
      this.mw = mw;
      this.vsw = vsw;
      this.f = f;
      initSettings();
      vsw.setCancelAct(e -> undoSettings());
      vsw.setDefaultCloseAction(DefaultClosing);
   }

   /**
    * Applies the selections made in <code>ViewSettingWin</code> and sets
    * the corresponding properties in <code>Prefs</code>
    */
   public void applySettings() {
      boolean show;
      String state;
      show = vsw.isShowLineNumbers();
      if (show != isShowLineNumbers) {
         f.showLineNumbers(show);
         isShowLineNumbers = show;
         state = show ? "show" : "hide";
         prefs.setProperty("LineNumbers", state);
      }
      show = vsw.isShowToolbar();
      if (show != isShowToolbar) {
         mw.showToolbar(show);
         isShowToolbar = show;
         state = isShowToolbar ? "show" : "hide";
         prefs.setProperty("Toolbar", state);
      }
      show = vsw.isShowStatusbar();
      if (show != isShowStatusbar) {
         mw.showStatusbar(show);
         isShowStatusbar = show;
         state = isShowStatusbar ? "show" : "hide";
         prefs.setProperty("Statusbar", state);
      }
      int index;
      index = vsw.backgroundIndex();
      if (index != backgroundIndex) {
         backgroundIndex = index;
         prefs.setProperty("Background",
             ViewSettingWin.BKGRD_OPT[backgroundIndex]);
      }
      index = vsw.iconSizeIndex();
      if (index != iconSizeIndex) {
         iconSizeIndex = index;
         prefs.setProperty("IconSize",
               ViewSettingWin.ICON_SIZES[iconSizeIndex]);
      }
      index = vsw.lafIndex();
      if (index != lafIndex) {
         lafIndex = index;
         prefs.setProperty("LaF", ViewSettingWin.LAF_OPT[lafIndex]);
      }
   }

   private void initSettings() {
      isShowLineNumbers = "show".equals(prefs.getProperty("LineNumbers"));
      vsw.setShowLineNumbers(isShowLineNumbers);
      f.showLineNumbers(isShowLineNumbers);

      isShowToolbar =  "show".equals(prefs.getProperty("Toolbar"));
      vsw.setShowToolbar(isShowToolbar);
      mw.showToolbar(isShowToolbar);

      isShowStatusbar =  "show".equals(prefs.getProperty("Statusbar"));
      vsw.setShowStatusbar(isShowStatusbar);
      mw.showStatusbar(isShowStatusbar);

      vsw.setIconSize(prefs.getProperty("IconSize"));
      iconSizeIndex = vsw.iconSizeIndex();

      String laf = prefs.getProperty("LaF");
      if (laf.length() > 0) {
         vsw.setLaf(laf);
      }
      else {
         vsw.setLaf(ViewSettingWin.LAF_OPT[1]);
      }
      lafIndex = vsw.lafIndex();

      String bkgrd = prefs.getProperty("Background");
      if (bkgrd.length() > 0) {
         vsw.setBackground(bkgrd);
      }
      else {
         vsw.setBackground(ViewSettingWin.BKGRD_OPT[0]);
      }
      backgroundIndex = vsw.backgroundIndex();
   }

   private void undoSettings() {
      vsw.setShowLineNumbers(isShowLineNumbers);
      vsw.setShowToolbar(isShowToolbar);
      vsw.setShowStatusbar(isShowStatusbar);
      vsw.setBackground(backgroundIndex);
      vsw.setIconSize(iconSizeIndex);
      vsw.setLaf(lafIndex);
      vsw.setVisible(false);
   }

   private final WindowAdapter DefaultClosing = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         undoSettings();
      }
   };
}
