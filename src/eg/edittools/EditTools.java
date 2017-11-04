package eg.edittools;

/**
 * Constants for edit tools
 */
public enum EditTools {
   
   FINDER("Find", "Finder"),
   EDIT_TEXT_PASSAGE("Exchange editor", "ExchangeEditor");
   
   private final String display;
   private final String className;
   
   private EditTools(String display, String className) {
      this.display = display;
      this.className = className;
   }
   
   /**
    * Returns the display value associated with the edit tool constants
    *
    * @return  the display value
    */
   public String display() {
      return display;
   }
   
   
   /**
    * Returns, for the edit tool contants, the name the class that implements
    * an <code>AddableEditTool</code>
    *
    * @return the class name
    */
   public String className() {
      return className;
   }
}
