package eg;

//--Eadgyth--//

import eg.ui.EditArea;
import eg.ui.FontSettingWin;

/**
 * The formatting of <code>EditArea</code> objects
 *
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
    * <code>EditArea</code> objects
    *
    * @param n  the number of EditArea objects
    * @param keyPrefix  A prefix for the keys that are used for the
    * format properties set in {@link Prefs}. Can be the empty string
    */
   public Formatter (int n, String keyPrefix) {
      setPropertyKeys(keyPrefix);
      getFormatProperties();
      fontWin = new FontSettingWin(font, fontSize);
      fontWin.okAct(e -> setFont());
      editArea = new EditArea[n];
      if (n == 1) {
         editArea[0] = formattedEditArea();
      }
   }

   /**
    * Returns this single formatted <code>EditArea</code>.
    * <p>
    * Throws an exception if the specified number of EditArea objects
    * is larger than one.
    *
    * @return  this EditArea
    */
   public EditArea editArea() {
      if (editArea.length > 1) {
         throw new IllegalStateException(
               "Formatting more than one EditArea is specified.");
      }
      return editArea[0];
   }

   /**
    * Returns this <code>EditArea</code> array.
    * <p>
    * Throws an exception if the specified number of EditArea objects
    * is not minimum two.
    *
    * @return  this EditArea array
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
    * array at the specified index.
    * <p>
    * Throws an exception if the specified number of EditArea objects
    * is not minimum two or if the specified index is too large.
    *
    * @param i  the index of the array element
    */
   public void createEditAreaAt(int i) {
      if (i > editArea.length - 1) {
         throw new IllegalArgumentException(
               "i is larger than the maximum possible index");
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
    * Throws an exception if the array element at the specified index
    * is null or if the index is too large.
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
    * Makes the dialog for setting the font visible and applies
    * the selected font and font size to all <code>EditArea</code>
    * objects
    */
   public void openSetFontDialog() {
      fontWin.setVisible(true);
   }

   /**
    * Sets the boolean that specifies if wordwrap is enabled and
    * applies the wordwrap state to the selected or the single
    * <code>EditArea</code>.
    * <p>
    * Enabling also hides line numbers whereas disabling shows line
    * numbers depending on the previous selection.
    *
    * @see #showLineNumbers(boolean)
    * @param wordwrap  true to enable, false to disable
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
    * Sets the boolean that specifies if line numbers are shown and
    * applies showing line numbers to objects of <code>EditArea</code>
    * in which wordwrap is disabled.
    *
    * @param b  true to show, false to hide line numbers
    */
   public void showLineNumbers(boolean b) {
      if (isShowLineNr == b) {
         return;
      }
      for (EditArea ea : editArea) {
         if (ea != null && !ea.isWordwrap()) {
            ea.showLineNumbers(b);
         }
      }
      isShowLineNr = b;
   }

   /**
    * Stores the current values for the properties wordwrap, font and
    * font size in <code>Prefs</code>
    */
   public void storeProperties() {
      prefs.setYesNoProperty(wordwrapKey, isWordwrap);
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
      wordwrapKey = prefix + Prefs.WORDWRAP_KEY;
      fontKey = prefix + Prefs.FONT_KEY;
      fontSizeKey = prefix + Prefs.FONT_SIZE_KEY;
   }

   private void getFormatProperties() {
      isWordwrap = prefs.yesNoProperty(wordwrapKey);
      font = prefs.property(fontKey);
      try {
         fontSize = Integer.parseInt(prefs.property(fontSizeKey));
      }
      catch (NumberFormatException e) {
         fontSize = 9;
      }
   }
}
