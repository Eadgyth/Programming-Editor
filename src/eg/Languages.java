package eg;

/**
 * Constants for languages
 */
public enum Languages {

   NORMAL_TEXT("Normal text"),
   HTML("Html"),
   JAVA("Java"),
   JAVASCRIPT("Javascript"),
   PERL("Perl");
   
   private String display;
   
   private Languages(String display) {
      this.display = display;
   }
   
   /**
    * Returns the display value associated with a language
    * constant
    *
    * @return  the display value for a language
    */
   public String display() {
      return display;
   }
}
