package eg;

/**
 * Constants for languages
 */
public enum Languages {

   NORMAL_TEXT("Normal text"),
   CSS("CSS"),
   HTML("HTML"),
   JAVA("Java"),
   JAVASCRIPT("Javascript"),
   PERL("Perl"),
   R("R"),
   XML("XML");
   
   private final String display;
   
   /**
    * Returns the display value associated with a language
    * constant
    *
    * @return  the display value
    */
   public String display() {
      return display;
   }
   
   private Languages(String display) {
      this.display = display;
   }
}
