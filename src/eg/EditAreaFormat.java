package eg;

import eg.ui.EditArea;
import eg.ui.ViewSettingWin;
import eg.ui.FontSettingWin;

/**
 * The formatting for <code>EditArea</code> objects which are associated
 * with the objects of <code>FileDocument</code>.
 * <p>The font and visibility of line numbersis applied to all editors,
 * whereas wordwrap is applied only to the currently selected editor.
 * <p>The initial parameters are read from the prefs file.
 */
public class EditAreaFormat {
   
   private final ViewSettingWin viewSetWin;
   private final FontSettingWin fontSetWin;
   private final Preferences prefs = new Preferences();

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
      prefs.readPrefs();
      isWordwrap = "enabled".equals(prefs.getProperty("wordWrap"));
      font = prefs.getProperty("font");
      fontSize = Integer.parseInt(prefs.getProperty("fontSize"));
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
    * Makes this window to set the font visible
    */
   public void makeFontSettingWinVisible() {
      fontSetWin.makeVisible(true);
   }
   
   /** 
    * Returns a new {@code EditArea} that is initialized with the
    * current settings
    *
    * @return  a new {@link EditArea}
    */
   public EditArea createEditArea() {
      return new EditArea(isWordwrap, isShowLineNr, font, fontSize);
   }
   
   /**
    * Changes the wordwrap state of the selected
    * <code>EditArea</code>
    *
    * @param isWordwrap  if wordwrap is enabled
    */
   public void changeWordWrap(boolean isWordwrap) {
      if (isWordwrap) {
         editArea[iCurr].enableWordwrap();
      }
      else {
         showLineNumbers(editArea[iCurr]);
      }
      String state = isWordwrap ? "enabled" : "disabled";
      prefs.storePrefs("wordWrap", state);
      this.isWordwrap = isWordwrap;
   }

   /**
    * Applies the selection in this {@link ViewSettingWin} to
    * show or hide line numbers
    */
   public void applySetWinOk() {
      boolean show = viewSetWin.isShowLineNumbers();
      if (isShowLineNr != show) {
         isShowLineNr = show;
         for (EditArea ea : editArea) {
            if (ea != null && !ea.isWordwrap()) {
               showLineNumbers(ea);
            }
         }
         String state = isShowLineNr ? "show" : "hide";
         prefs.storePrefs("lineNumbers", state);
      }
   }
   
   //
   //--private--/
   //
   
   private void showLineNumbers(EditArea ea) {
      if (isShowLineNr) {
         ea.showLineNumbers();
      } else {
         ea.hideLineNumbers();
      }
   }
   
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
}
