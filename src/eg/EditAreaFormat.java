package eg;

import eg.ui.EditArea;
import eg.ui.ViewSettingWin;
import eg.ui.FontSettingWin;

/**
 * The formatting for <code>EditArea</code> objects
 */
public class EditAreaFormat {
   
   private final ViewSettingWin viewSetWin;
   private final FontSettingWin fontSetWin;
   private final Preferences prefs = Preferences.readProgramPrefs();

   private EditArea[] editArea;
   private int iCurr; 
   private boolean isWordwrap;
   private boolean isShowLineNr;
   private String font;
   private int fontSize;
   
   /**
    * @param viewSetWin  the reference to {@link ViewSettingWin}
    */
   public EditAreaFormat(ViewSettingWin viewSetWin) {
      this.viewSetWin = viewSetWin;
      isShowLineNr = viewSetWin.isShowLineNumbers();
      isWordwrap = "enabled".equals(prefs.getProperty("wordWrap"));
      initFont();
      fontSetWin = new FontSettingWin(font, fontSize);
      fontSetWin.okAct(e -> setFont());
   }
   
   /**
    * Sets the array of type {@code EditArea}
    *
    * @param editArea  the {@link EditArea} array
    */
   public void setEditAreaArr(EditArea[] editArea) {
      this.editArea = editArea;
   }

  /**
    * Selects an element from this <code>EditArea</code> array
    *
    * @param i  the index of the array element
    */
   public void setEditAreaAt(int i) {
      iCurr = i;
   }
   
   /** 
    * Returns a new {@code EditArea} that is initialized with the
    * current formatting
    *
    * @return  a new {@link EditArea}
    */
   public EditArea createEditArea() {
      return new EditArea(isWordwrap, isShowLineNr, font, fontSize);
   }
   
   /**
    * Makes the dialog for setting the font visible
    */
   public void makeFontSettingWinVisible() {
      fontSetWin.makeVisible(true);
   }
   
   /**
    * Changes the wordwrap state of the selected <code>EditArea</code>
    *
    * @param isWordwrap  if wordwrap is enabled
    */
   public void changeWordWrap(boolean isWordwrap) {
      if (isWordwrap) {
         editArea[iCurr].enableWordwrap();
      }
      else {
         editArea[iCurr].disableWordwrap(isShowLineNr);
      }
      String state = isWordwrap ? "enabled" : "disabled";
      prefs.storePrefs("wordWrap", state);
      this.isWordwrap = isWordwrap;
   }

   /**
    * Applies the selection in this <code>ViewSettingWin</code> to
    * show or hide line numbers
    */
   public void applySetWinOk() {
      boolean show = viewSetWin.isShowLineNumbers();
      if (isShowLineNr != show) {
         isShowLineNr = show;
         for (EditArea ea : editArea) {
            if (ea != null && !ea.isWordwrap()) {
               ea.showLineNumbers(isShowLineNr);
            }
         }
         String state = isShowLineNr ? "show" : "hide";
         prefs.storePrefs("lineNumbers", state);
      }
   }
   
   //
   //--private--//
   //
   
   private void setFont() {
      font = fontSetWin.fontComboBxRes();
      fontSize = fontSetWin.sizeComboBxRes();
      for (EditArea ea : editArea) {
         if (ea != null) {
             ea.setFont(font, fontSize);
         }
      }
      prefs.storePrefs("fontSize", String.valueOf(fontSize));
      prefs.storePrefs("font", font);
      fontSetWin.makeVisible(false);
   }
   
   private void initFont() {
      font = prefs.getProperty("font");
      try {
         fontSize = Integer.parseInt(prefs.getProperty("fontSize"));
      }
      catch (NumberFormatException e) {
         fontSize = 10;
      }
   }
}
