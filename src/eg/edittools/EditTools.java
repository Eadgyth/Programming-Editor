package eg.edittools;

/**
 * Constants that identify implementations of <code>AddableEditTool</code>
 */
public enum EditTools {

   FINDER("Find/replace", "Finder"),
   EXCHANGE_EDITOR("Exchange editor", "ExchangeEditor");

   private final String display;
   private final String className;

   private EditTools(String display, String className) {
      this.display = display;
      this.className = className;
   }

   /**
    * Returns the display value associated with this constants
    *
    * @return  the display value
    */
   public String display() {
      return display;
   }

   /**
    * Returns, for this constants, the name of the classes that implement
    * <code>AddableEditTool</code>
    *
    * @return the class name
    */
   public String className() {
      return className;
   }
}
