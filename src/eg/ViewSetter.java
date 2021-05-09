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
   private int themeIndex;
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
      vsw.setDefaultCloseAction(defaultClosing);
   }

   /**
    * Applies the selections made in <code>ViewSettingWin</code> and sets
    * the corresponding properties in <code>Prefs</code>
    */
   public void applySettings() {
      boolean show;
      show = vsw.isShowLineNumbers();
      if (show != isShowLineNumbers) {
         f.showLineNumbers(show);
         isShowLineNumbers = show;
         prefs.setYesNoProperty(Prefs.LINE_NR_KEY, isShowLineNumbers);
      }
      show = vsw.isShowToolbar();
      if (show != isShowToolbar) {
         mw.showToolbar(show);
         isShowToolbar = show;
         prefs.setYesNoProperty(Prefs.TOOLBAR_KEY, isShowToolbar);
      }
      show = vsw.isShowStatusbar();
      if (show != isShowStatusbar) {
         mw.showStatusbar(show);
         isShowStatusbar = show;
         prefs.setYesNoProperty(Prefs.STATUSBAR_KEY, isShowStatusbar);
      }
      int index;
      index = vsw.themeIndex();
      if (index != themeIndex) {
         themeIndex = index;
         prefs.setProperty(Prefs.THEME_KEY,
             ViewSettingWin.THEME_OPT[themeIndex]);
      }
      index = vsw.iconSizeIndex();
      if (index != iconSizeIndex) {
         iconSizeIndex = index;
         prefs.setProperty(Prefs.ICON_SIZE_KEY,
               ViewSettingWin.ICON_SIZES[iconSizeIndex]);
      }
      index = vsw.lafIndex();
      if (index != lafIndex) {
         lafIndex = index;
         prefs.setProperty("LaF", ViewSettingWin.LAF_OPT[lafIndex]);
      }
   }

   //
   //--private--/
   //

   private void initSettings() {
      isShowLineNumbers = prefs.yesNoProperty(Prefs.LINE_NR_KEY);
      vsw.setShowLineNumbers(isShowLineNumbers);
      f.showLineNumbers(isShowLineNumbers);

      isShowToolbar = prefs.yesNoProperty(Prefs.TOOLBAR_KEY);
      vsw.setShowToolbar(isShowToolbar);
      mw.showToolbar(isShowToolbar);

      isShowStatusbar = prefs.yesNoProperty(Prefs.STATUSBAR_KEY);
      vsw.setShowStatusbar(isShowStatusbar);
      mw.showStatusbar(isShowStatusbar);

      vsw.setIconSize(prefs.property(Prefs.ICON_SIZE_KEY));
      iconSizeIndex = vsw.iconSizeIndex();

      String laf = prefs.property(Prefs.LAF_KEY);
      vsw.setLaf(laf);
      lafIndex = vsw.lafIndex();

      String theme = prefs.property(Prefs.THEME_KEY);
      vsw.setTheme(theme);
      themeIndex = vsw.themeIndex();
   }

   private void undoSettings() {
      vsw.setShowLineNumbers(isShowLineNumbers);
      vsw.setShowToolbar(isShowToolbar);
      vsw.setShowStatusbar(isShowStatusbar);
      vsw.setTheme(themeIndex);
      vsw.setIconSize(iconSizeIndex);
      vsw.setLaf(lafIndex);
      vsw.setVisible(false);
   }

   private final WindowAdapter defaultClosing = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
         undoSettings();
      }
   };
}
