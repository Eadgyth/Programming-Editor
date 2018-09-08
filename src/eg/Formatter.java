package eg;

import eg.ui.EditArea;
import eg.ui.FontSettingWin;

/**
 * The formatting of <code>EditArea</code> objects. The properties
 * 'Wordwrap', 'Font' and 'FontSize' are initially read from {@link Prefs}.
 * Setting the boolean that specifies if line numbers are shown is
 * required in this class too because showing line numbers depends on
 * whether wordwrap is disabled.
 * @see EditArea
 */
public class Formatter {

   private final FontSettingWin fontWin;
   private final Prefs prefs = new Prefs();
   private final EditArea[] editArea;

   private String fontKey;
   private String fontSizeKey;
   private String wordwrapKey;

   private int index = 0;
   private boolean isWordwrap;
   private String font;
   private int fontSize;
   private boolean isShowLineNr = false;

   /**
    * Creates a <code>Formatter</code> for a given number of
    * <code>EditArea</code> objects that need to be formatted. Only
    * if <code>number</code> is equal to 1 a formatted EditArea is
    * created and is accessed through {@link #editArea()}. If
    * <code>number</code> is minimum 2 an initially empty array is
    * created.
    *
    * @param number  the number of EditArea objects
    * @param keyPrefix  A prefix for the keys for the format
    * properties set in {@link Prefs}. Can be the empty string
    */
   public Formatter (int number, String keyPrefix) {           
      setPropertyKeys(keyPrefix);
      getFormatProperties();
      fontWin = new FontSettingWin(font, fontSize);
      fontWin.okAct(e -> setFont());
      editArea = new EditArea[number];
      if (number == 1) {
         editArea[0] = formattedEditArea();
      }
   }

   /**
    * Gets this formatted <code>EditArea</code> if the number of EditArea
    * objects is equal to 1
    *
    * @return  this {@link EditArea}
    */
   public EditArea editArea() {
      if (editArea.length > 1) {
         throw new IllegalStateException(
               "Formatting more than one EditArea objects is set.");
      }
      return editArea[0];
   }

   /**
    * Gets this <code>EditArea</code> array if the number of EditArea
    * objects is minumum 2
    *
    * @return  this {@link EditArea} array
    */
   public EditArea[] editAreaArray() {
      if (editArea.length < 2) {
         throw new IllegalStateException(
               "Formatting only one EditArea is set.");
      }
      return editArea;
   }

   /**
    * Assigns a new formatted <code>EditArea</code> to this EditArea
    * array at the specified position
    *
    * @param i  the index of the array element
    */
   public void createEditAreaAt(int i) {
      if (i > editArea.length - 1) {
         throw new IllegalArgumentException(
               "The index is larger than the maximum possible index");
      }
      if (editArea.length < 2) {
         throw new IllegalStateException(
               "Formatting only one EditArea is set.");
      }
      editArea[i] = formattedEditArea();
   }

   /**
    * Selects the element in this <code>EditArea</code> array in which
    * the wordwrap state may be changed.
    *
    * @param i  the index of the array element
    */
   public void setIndex(int i) {
      if (editArea[i] == null) {
         throw new IllegalStateException(
               "The EditArea at index is null");
      }
      if (i > editArea.length - 1) {
         throw new IllegalArgumentException(
               "i is larger than the maximum possible index");
      }
      index = i;
   }

   /**
    * Makes the dialog for setting the font and font size visible
    */
   public void openSetFontDialog() {
      fontWin.setVisible(true);
   }

   /**
    * Sets the boolean that specifies if wordwrap is enabled. Enabling
    * wordwrap also hides line numbers whereas disabling wordwrap shows
    * line numbers if the corresponding boolean is set to true in
    * {@link #showLineNumbers(boolean)}.
    * <p>
    * The wordwrap state is changed only in the element specified in
    * {@link #setIndex(int)}
    *
    * @param wordwrap  the boolean value; true to enable
    */
   public void enableWordWrap(boolean wordwrap) {
      if (wordwrap) {
         editArea[index].enableWordwrap();
      }
      else {
         editArea[index].disableWordwrap(isShowLineNr);
      }
      isWordwrap = wordwrap;
   }

   /**
    * Sets the boolean that specifies if line numbers are shown.
    * <p>
    * Line numbers are not shown in {@link EditArea} objects in which
    * wordwrap is enabled.
    *
    * @param lineNumbers  the boolean value, true to show
    */
   public void showLineNumbers(boolean lineNumbers) {
      if (isShowLineNr == lineNumbers) {
         return;
      }
      for (EditArea ea : editArea) {
         if (ea != null && !ea.isWordwrap()) {
            ea.showLineNumbers(lineNumbers);
         }
      }
      isShowLineNr = lineNumbers;
   }

   /**
    * Sets the current values for the properties wordwrap, font and
    * font size in <code>Prefs</code>
    */
   public void setProperties() {
      String state = isWordwrap ? "enabled" : "disabled";
      prefs.setProperty(wordwrapKey, state);
      prefs.setProperty(fontKey, font);
      prefs.setProperty(fontSizeKey, String.valueOf(fontSize));
   }

   //
   //--private--/
   //

   private EditArea formattedEditArea() {
      return new EditArea(isWordwrap, isShowLineNr, font, fontSize);
   }

   private void setFont() {
      font = fontWin.font();
      fontSize = fontWin.size();
      for (EditArea ea : editArea) {
         if (ea != null) {
             ea.setFont(font, fontSize);
         }
      }
      fontWin.setVisible(false);
   }

   private void setPropertyKeys(String prefix) {
      wordwrapKey = prefix + "Wordwrap";
      fontKey = prefix + "Font";
      fontSizeKey = prefix + "FontSize";
   }

   private void getFormatProperties() {
      isWordwrap = "enabled".equals(prefs.getProperty(wordwrapKey));
      font = prefs.getProperty(fontKey);
      if (font.length() == 0) {
         font = "Consolas";
      }
      try {
         fontSize = Integer.parseInt(prefs.getProperty(fontSizeKey));
      }
      catch (NumberFormatException e) {
         fontSize = 10;
      }
   }
}
